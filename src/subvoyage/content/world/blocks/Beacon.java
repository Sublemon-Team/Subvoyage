package subvoyage.content.world.blocks;

import arc.graphics.g2d.*;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.*;
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
    public void init() {
        super.init();
        updateClipRadius((lightRadius + 1) * tilesize);
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

        @Override
        public float range() {
            return repairRadius;
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
    }
}
