package oceanic_dust.blocks;

import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import oceanic_dust.items.*;

public class ODWorldBlocks {
    public static Block
        //ores
        orePhosphorus,oreCorallite,oreSulfur;

    public static void load() {
        orePhosphorus = new OreBlock(ODItems.phosphorus){{
            oreDefault = true;
            oreThreshold = 0.81f;
            oreScale = 23.47619f;
        }};
        oreCorallite = new OreBlock(ODItems.corallite){{
            oreDefault = true;
            oreThreshold = 0.83f;
            oreScale = 23.17619f;
        }};
        oreSulfur = new OreBlock(ODItems.sulfur){{
            oreDefault = true;
            oreThreshold = 0.83f;
            oreScale = 22.17619f;
        }};
    }
}
