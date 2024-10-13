package subvoyage.type.block;

import mindustry.world.blocks.logic.LogicBlock;

import static arc.Core.atlas;

public class LogicHidden extends LogicBlock {
    public LogicHidden(String name) {
        super(name);
    }

    public class LogicHiddenBuild extends LogicBuild {
        @Override
        public void draw() {
            region = atlas.find("error");
            for (int i = 0; i < 4; i++) {
                if(tile.nearby(i).build != null) tile.nearby(i).block().drawBase(tile);
            }
        }
    }
}
