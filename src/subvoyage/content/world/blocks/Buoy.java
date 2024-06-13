package subvoyage.content.world.blocks;

import arc.graphics.g2d.*;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.input.Placement;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class Buoy extends Radar {
    public Buoy(String name) {
        super(name);
        swapDiagonalPlacement = true;
        allowDiagonal = true;
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
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.floor() == Blocks.water || tile.block() == Blocks.water || tile.block() == Blocks.darksandWater ||
                tile.floor() == Blocks.darksandWater;
    }

    public class BuoyBuild extends RadarBuild{
        @Override
        public void draw(){
            Draw.rect(region, x, y);
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
