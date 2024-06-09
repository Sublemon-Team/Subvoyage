package oceanic_dust.blocks.content;

import mindustry.content.*;
import mindustry.game.*;
import mindustry.world.*;

public class Buoy extends Block {

    public Buoy(String name) {
        super(name);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.floor() == Blocks.water || tile.block() == Blocks.water;
    }
}
