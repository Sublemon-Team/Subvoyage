package subvoyage.type.block.environment;

import arc.graphics.g2d.Draw;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OverlayFloor;

public class CrackFloor extends OverlayFloor {
    public CrackFloor(String name) {
        super(name);
    }

    @Override
    public void drawBase(Tile tile) {
        Draw.alpha(0.35f);
        super.drawBase(tile);
        Draw.alpha(1f);
    }
}
