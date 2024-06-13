package subvoyage.content.world.blocks;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.input.Placement;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class Beacon extends RepairTurret {
    public float discoveryTime = 60f * 10f;

    public Beacon(String name) {
        super(name);
        size = 3;
        outlineColor = Pal.darkOutline;
        envDisabled |= Env.scorching;
        destructible = true;
        swapDiagonalPlacement = true;
        allowDiagonal = true;
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, lightRadius/2 * tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.floor() == Blocks.water || tile.block() == Blocks.water || tile.block() == Blocks.darksandWater ||
                tile.floor() == Blocks.darksandWater;
    }

    public class BeaconBuild extends RepairPointBuild implements Ranged {
        public float progress;
        public float lastRadius = 0f;
        public float smoothEfficiency = 1f;
        public float totalProgress;

        @Override
        public float fogRadius(){
            return fogRadius * progress * smoothEfficiency;
        }

        @Override
        public boolean canPickup(){
            return false;
        }

        @Override
        public void updateTile(){
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.05f);

            if(Math.abs(fogRadius() - lastRadius) >= 0.5f){
                Vars.fogControl.forceUpdate(team, this);
                lastRadius = fogRadius();
            }

            progress += edelta() / discoveryTime;
            progress = Mathf.clamp(progress);

            totalProgress += efficiency * edelta();
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, fogRadius() * tilesize, Pal.accent);
            Drawf.dashCircle(x, y, repairRadius, Pal.heal);
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);

            float t = Time.time / 60f;

            Draw.z(Layer.turret);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);

            if(shouldConsume()) Draw.scl(1.2f);
            Drawf.spinSprite(region, x, y, (t * 90) % 360);

            if(shouldConsume()) Draw.scl();

            drawBeam(x, y, rotation, length, id, target, team, strength,
                    pulseStroke, pulseRadius, beamWidth, lastEnd, offset, laserColor, laserTopColor,
                    laser, laserEnd, laserTop, laserTopEnd);
        }

        @Override
        public float progress(){
            return progress;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            progress = read.f();
        }
    }
}
