package subvoyage.content.world.blocks.cargo;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.world.blocks.units.UnitCargoLoader;

import static mindustry.Vars.net;
import static mindustry.Vars.tilesize;

public class ShipCargoStation extends UnitCargoLoader {
    public ShipCargoStation(String name) {
        super(name);
        rotate = true;
        rotateDraw = true;
    }

    public class ShipCargoBuild extends UnitTransportSourceBuild {

        @Override
        public void draw() {
            Draw.rect(block.region, x, y);
            if(unit == null){
                Draw.draw(Layer.blockOver, () -> {
                    //TODO make sure it looks proper
                    construct(this, unitType.fullIcon, rotation*90f-90f, buildProgress, warmup, totalProgress);
                });
            }else{
                Draw.z(Layer.bullet - 0.01f);
                Draw.color(polyColor);
                Lines.stroke(polyStroke * readyness);
                Lines.poly(x, y, polySides, polyRadius, Time.time * polyRotateSpeed);
                Draw.reset();
                Draw.z(Layer.block);
            }
        }
        public void construct(Building t, TextureRegion region, float rotation, float progress, float alpha, float time){
            construct(t, region, Pal.accent, rotation, progress, alpha, time);
        }

        public void construct(Building t, TextureRegion region, Color color, float rotation, float progress, float alpha, float time){
            construct(t, region, color, rotation, progress, alpha, time, t.block.size * tilesize - 4f);
        }

        public void construct(Building t, TextureRegion region, Color color, float rotation, float progress, float alpha, float time, float size){
            Shaders.build.region = region;
            Shaders.build.progress = progress;
            Shaders.build.color.set(color);
            Shaders.build.color.a = alpha;
            Shaders.build.time = -time / 20f;

            float x = t.x+(rotation() == 0 ? 1 : rotation() == 2 ? -1 : 0)*tilesize*block.size;
            float y = t.y+(rotation() == 1 ? 1 : rotation() == 3 ? -1 : 0)*tilesize*block.size;

            Draw.shader(Shaders.build);
            Draw.rect(region, x, y, rotation);
            Draw.shader();

            Draw.shader(Shaders.light);
            Draw.color(Pal.accent);
            Draw.blend(Blending.additive);
            Draw.rect(region,x,y,rotation);
            //Lines.lineAngleCenter(t.x + Mathf.sin(time, 20f, size / 2f), t.y+tilesize*2, 90, size);

            Draw.reset();
        }

        @Override
        public void spawned(int id){
            float x = this.x+(rotation() == 0 ? 1 : rotation() == 2 ? -1 : 0)*tilesize*size;
            float y = this.y+(rotation() == 1 ? 1 : rotation() == 3 ? -1 : 0)*tilesize*size;
            Fx.spawn.at(x, y);
            buildProgress = 0f;
            if(net.client()){
                readUnitId = id;
            }
        }

        @Override
        public void updateTile() {
            //unit was lost/destroyed
            if(unit != null && (unit.dead || !unit.isAdded())){
                unit = null;
            }

            if(readUnitId != -1){
                unit = Groups.unit.getByID(readUnitId);
                if(unit != null || !net.client()){
                    readUnitId = -1;
                }
            }

            warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
            readyness = Mathf.approachDelta(readyness, unit != null ? 1f : 0f, 1f / 60f);

            if(unit == null && Units.canCreate(team, unitType)){
                buildProgress += edelta() / buildTime;
                totalProgress += edelta();

                if(buildProgress >= 1f){
                    if(!net.client()){
                        unit = unitType.create(team);
                        if(unit instanceof BuildingTetherc bt){
                            bt.building(this);
                        }
                        float x = this.x+(rotation() == 0 ? 1 : rotation() == 2 ? -1 : 0)*tilesize*size;
                        float y = this.y+(rotation() == 1 ? 1 : rotation() == 3 ? -1 : 0)*tilesize*size;
                        unit.set(x, y);
                        unit.rotation = rotation*90f;
                        unit.add();
                        Call.unitTetherBlockSpawned(tile, unit.id);
                    }
                }
            }
        }
    }
}
