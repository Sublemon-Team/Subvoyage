package subvoyage.content.unit;

import mindustry.Vars;
import mindustry.entities.EntityCollisions;
import mindustry.entities.Units;
import mindustry.gen.BuildingTetherPayloadUnit;
import mindustry.gen.UnitWaterMove;
import mindustry.gen.WaterMovec;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class WaterPayloadUnit extends BuildingTetherPayloadUnit implements WaterMovec {


    @Override
    public boolean canPass(int tileX, int tileY) {
        Tile tile = world.tile(tileX,tileY);
        return tile != null && tile.floor().isLiquid;
    }

    @Override
    public boolean isPathImpassable(int tileX, int tileY) {
        return Vars.world.tiles.in(tileX, tileY) && this.type.pathCost.getCost(this.team.id, Vars.pathfinder.get(tileX, tileY)) == -1;
    }

    @Override
    public int pathType() {
        return 2;
    }



    @Override
    public boolean onSolid() {
        return onLiquid();
    }
    @Override
    public boolean onLiquid() {
        Tile tile = this.tileOn();
        return tile != null && tile.floor().isLiquid;
    }



    public static WaterPayloadUnit create() {
        return new WaterPayloadUnit();
    }
}
