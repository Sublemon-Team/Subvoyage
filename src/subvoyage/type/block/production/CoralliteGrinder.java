package subvoyage.type.block.production;

import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Tile;

import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Pump;

public class CoralliteGrinder extends GenericCrafter {
    public int maxLiquidTiles = 3;
    public CoralliteGrinder(String name) {
        super(name);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.getLinkedTilesAs(this, tempTiles).sum((other) -> other.floor().isLiquid ? 1 : 0) <= maxLiquidTiles;
    }

    public class CoralliteRefinerBuild extends GenericCrafterBuild {
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if(source instanceof Pump.PumpBuild) return super.acceptLiquid(source,liquid);
            return false;
        }
        @Override
        public boolean acceptItem(Building source, Item item) {
            return false;
        }
    }
}
