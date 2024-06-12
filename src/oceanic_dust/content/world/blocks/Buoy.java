package oceanic_dust.content.world.blocks;

import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.input.Placement;
import mindustry.world.*;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class Buoy extends Block {

    public Buoy(String name) {
        super(name);
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
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, lightRadius * tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.floor() == Blocks.water || tile.block() == Blocks.water || tile.block() == Blocks.darksandWater ||
                tile.floor() == Blocks.darksandWater;
    }
}
