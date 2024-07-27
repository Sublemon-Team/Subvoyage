package subvoyage.type.block.fog;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;

import static mindustry.Vars.*;

public class Buoy extends Radar {
    public boolean isWater = true;
    public Buoy(String name) {
        super(name);
        swapDiagonalPlacement = true;
        allowDiagonal = true;
        outlineIcon = false;
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, lightRadius * tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if(!isWater) return super.canPlaceOn(tile,team,rotation);
        return tile.floor().isLiquid;
    }

    public class BuoyBuild extends RadarBuild{
        @Override
        public void drawSelect() {
            super.drawSelect();
            if(fogRadius*progress < 6f) Drawf.dashCircle(x, y, 6 * tilesize, Pal.accent);
        }

        @Override
        public void updateTile() {
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.05f);
            if(Math.abs(fogRadius() - lastRadius) >= 0.5f){
                Vars.fogControl.forceUpdate(team, this);
                lastRadius = fogRadius();
            }

            progress += edelta() / discoveryTime * (fogRadius*progress < 6f ? 8 : 1);
            progress = Mathf.clamp(progress);

            totalProgress += efficiency * edelta();
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
