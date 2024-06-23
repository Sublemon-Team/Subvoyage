package subvoyage.content.world.blocks.energy;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import arc.util.noise.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.type.Weather.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;

import static mindustry.Vars.*;

// TODO: rework rotations
public class WindTurbine extends SolarGenerator {
    public TextureRegion rotator;
    public TextureRegion bottom;

    public float weatherBoost = 0.25f;
    public int spacing = 6;
    public float smoothProgress = 0;
    public WindTurbine(String name) {
        super(name);
    }

    @Override
    public void load(){
        super.load();
        rotator = Core.atlas.find(this.name + "-rotator");
        bottom = Core.atlas.find(this.name + "-bottom");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{bottom, region, rotator};
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("efficiency", (WindTurbineBuild e) ->
                new Bar(() -> Core.bundle.format("bar.efficiency", Strings.fixed(e.productionEfficiency*100, 1)), () -> Pal.powerBar, () -> e.productionEfficiency));
    }

    public static void select(float x, float y, float radius, float size, Color color){
        Lines.stroke(size, color);
        Lines.square(x, y, radius - 14);
        Draw.reset();
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        select(x * tilesize + offset, y * tilesize + offset, tilesize * (size + spacing), size, Pal.placing);
    }

    // Redo this
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        int off = 1 - size % 2;
        for(int x = tile.x - spacing + off; x <= tile.x + spacing; x++){
            for(int y = tile.y - spacing + off; y <= tile.y + spacing; y++){
                Tile other = world.tile(x, y);
                if(other != null && other.block() instanceof WindTurbine turbine && (turbine == this || turbine.intersectsSpacing(other.build.tile, tile))) return false;
            }
        }

        return true;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        super.drawPlanRegion(plan, list);
        Draw.mixcol();
        int off = 1 - size % 2;
        Tile tile = plan.tile();
        if(spacing < 1 || tile == null) return;
        for(int x = tile.x - spacing + off; x <= tile.x + spacing; x++){
            for(int y = tile.y - spacing + off; y <= tile.y + spacing; y++){
                Tile t = world.tile(x, y);
                if(t != null && t.block() instanceof WindTurbine s && (s == this || s.intersectsSpacing(t.build.tile, tile))){
                    Drawf.selected(t.build, Pal.remove);
                }
            }
        }
    }

    public boolean intersectsSpacing(int sx, int sy, int ox, int oy, int ext){
        if(spacing < 1) return true;
        int spacingOffset = spacing + ext;
        int sizeOffset = 1 - (size & 1);

        return ox >= sx + sizeOffset - spacingOffset && ox <= sx + spacingOffset &&
        oy >= sy + sizeOffset - spacingOffset && oy <= sy + spacingOffset;
    }

    public boolean intersectsSpacing(Tile self, Tile other){
        return intersectsSpacing(self.x, self.y, other.x, other.y, 0);
    }
    // end

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        if(spacing >= 1) Placement.calculateNodes(points, this, rotation, (point, other) -> intersectsSpacing(point.x, point.y, other.x, other.y, 1));
    }

    public class WindTurbineBuild extends SolarGenerator.SolarGeneratorBuild{
        public float boost;
        public float rot;
        public float totalTime;

        @Override
        public void draw(){
            super.draw();
            Draw.rect(bottom, x, y);
            Drawf.spinSprite(rotator, x, y, getRot());
            Draw.rect(region, x, y);
        }

        @Override
        public void updateTile(){
            if(enabled){
                smoothProgress = Mathf.lerpDelta(smoothProgress, efficiency / (powerProduction - 20f), 0.1f);
                int sectorId = state.rules.sector == null ? 0 : state.rules.sector.id;
                float value = Mathf.clamp(Simplex.noise3d(sectorId, 2, 0.6, 1 / 100f, x, Time.time / 60 / 2, y) * 2);
                for(WeatherEntry weather : state.rules.weather){
                    value += weather.weather.sound == Sounds.wind ? 1f : 0;
                }

                boost = Mathf.lerpDelta(boost, !Groups.weather.isEmpty() ? 1.1f : 0.0f, 0.05f);
                productionEfficiency = value + (weatherBoost * boost);
            }
        }

        public float getRot(){
            WeatherState w = Groups.weather.find(ws -> ws.weather() != null && ws.weather().sound == Sounds.wind);
            totalTime += efficiency / (powerProduction - 20f) * delta();

            float rotation = Mathf.clamp(efficiency, w != null ? totalTime * w.windVector.angle() : totalTime * 90, totalTime * 90);
            return rot = rotation;
        }

        public void drawSelect(){
            drawPlace(tileX(), tileY(), 0, true);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(boost);
            write.f(rot);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            boost = read.f();
            rot = read.f();
        }
    }
}
