package subvoyage.type.block.defense;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import subvoyage.core.SvSettings;

import static arc.Core.atlas;
import static arc.Core.settings;
import static mindustry.Vars.*;

public class PowerWall extends Wall {

    public TextureRegion
            corner1, corner2, corner3, corner4,
            light,dark,
            corner,
            decal,decalLarge,decalDark,decalLargeDark,
            full;

    public float hitGenerationTime = 0f;
    public float hitPower_pt = 0f;

    public PowerWall(String name) {
        super(name);
        conductivePower = true;
        hasPower = true;
        outputsPower = true;
        consumesPower = false;
        sync = true;
        update = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        if(hasPower && outputsPower){
            addBar("power", (PhosphideWallBuild entity) -> new Bar(() ->
                    Core.bundle.format("bar.poweroutput",
                            Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
                    () -> Pal.powerBar,
                    () -> entity.warmup));
        }
    }

    @Override
    public void load() {
        super.load();
        corner1 = txr("1");
        corner2 = txr("2");
        corner3 = txr("3");
        corner4 = txr("4");
        light = txr("light");
        dark = txr("dark");
        corner = txr("corner");
        decal = txr("decal"); decalLarge = txr("decal-large");
        decalDark = txr("decal-dark"); decalLargeDark = txr("decal-large-dark");
        full = txr("full");
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.basePowerGeneration, hitPower_pt * 60.0f, StatUnit.powerSecond);
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{full};
    }

    public class PhosphideWallBuild extends WallBuild {

        float generationTime = 0f;

        public void drawSelf() {
            if(!SvSettings.wallTiling()) {
                Draw.rect(full,x,y,drawrot());
                return;
            }
            Draw.rect(region,x,y,drawrot());

            boolean right = nearbyWall(0);
            boolean top = nearbyWall(1);
            boolean left = nearbyWall(2);
            boolean bot = nearbyWall(3);

            boolean upLeft = !top && !left;
            boolean upRight = !top && !right;
            boolean botLeft = !bot && !left;
            boolean botRight = !bot && !right;

            for (int i = 0; i < 4; i++) {
                if(!nearbyCornerWall(i)) continue;
                Draw.rect(corner,x,y,i*90f-90f);
            }

            drawShadeAndDecal(right,0,!botRight && !upRight);
            drawShadeAndDecal(top,1,!upLeft && !upRight);
            drawShadeAndDecal(left,2, !botLeft && !upLeft);
            drawShadeAndDecal(bot,3,!botLeft && !botRight);

            if(upLeft) Draw.rect(corner1,x,y,0);
            if(upRight) Draw.rect(corner2,x,y,0);
            if(botRight) Draw.rect(corner3,x,y,0);
            if(botLeft) Draw.rect(corner4,x,y,0);
        }
        float warmup = 0f;
        @Override
        public void damage(float damage) {
            super.damage(damage);
            generationTime += hitGenerationTime;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            warmup = Mathf.lerp(warmup,generationTime > 0 ? 1f : 0f,Time.delta/3f);
            if(generationTime >= 0) generationTime-=Time.delta;
            else if(generationTime < 0) generationTime = 0;
        }

        @Override
        public float getPowerProduction() {
            return hitPower_pt*warmup;
        }

        public void drawShadeAndDecal(boolean side, int rotation, boolean decal) {
            float rot = rotation*90f-90f;
            if(!side) {
                Draw.rect(rot >= 90f ? dark : light,x,y,rot);
                if(decal) Draw.rect(rot >= 90f ? decalLargeDark : decalLarge,x,y,rot);
            }
        }

        public boolean nearbyWall(int rotation) {
            Tmp.v1.trns(rotation*90f,12f,4f).add(x,y);
            Tmp.v2.trns(rotation*90f,12f,-4f).add(x,y);

            int x1 = Mathf.round(Tmp.v1.getX()/8f);
            int y1 = Mathf.round(Tmp.v1.getY()/8f);

            int x2 = Mathf.round(Tmp.v2.getX()/8f);
            int y2 = Mathf.round(Tmp.v2.getY()/8f);

            Building b1 = world.build(x1,y1);
            Building b2 = world.build(x2,y2);
            return b2 instanceof PhosphideWallBuild && (b1 == b2);
        }

        public boolean nearbyCornerWall(int rotation) {
            Tmp.v1.trns(rotation*90f,12f,12f).add(x,y);

            int x1 = Mathf.round(Tmp.v1.getX()/8f);
            int y1 = Mathf.round(Tmp.v1.getY()/8f);

            Building b1 = world.build(x1,y1);

            return b1 instanceof PhosphideWallBuild;
        }


        @Override
        public void draw() {
            drawSelf();
            if(flashHit){
                if(hit < 0.0001f) return;

                Draw.color(flashColor);
                Draw.alpha(hit * 0.5f);
                Draw.blend(Blending.additive);
                Fill.rect(x, y, tilesize * size, tilesize * size);
                Draw.blend();
                Draw.reset();

                if(!state.isPaused()){
                    hit = Mathf.clamp(hit - Time.delta / 10f);
                }
            }
        }
    }

    public TextureRegion txr(String name,String def) {
        return txr(name,txr(def));
    }
    public TextureRegion txr(String name,TextureRegion def) {
        return atlas.find(this.name+"-"+name,def);
    }
    public TextureRegion txr(String name) {
        return atlas.find(this.name+"-"+name);
    }
}
