package oceanic_dust.content.blocks;

import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import oceanic_dust.content.world.items.*;

public class ODWorldBlocks {
    public static Block
        //ores
        orePhosphorus,oreCorallite,oreSulfur,oreIridium;

    public static void load() {
        orePhosphorus = new OreBlock(ODItems.spaclanium){{
            oreDefault = false;
            oreThreshold = 0.81f;
            oreScale = 23.47619f;
        }};
        oreCorallite = new OreBlock(ODItems.corallite){{
            oreDefault = false;
            oreThreshold = 0.83f;
            oreScale = 23.17619f;
        }};
        oreSulfur = new OreBlock(ODItems.sulfur){{
            oreDefault = false;
            oreThreshold = 0.83f;
            oreScale = 22.17619f;
        }};
        oreIridium = new OreBlock(ODItems.iridium){{
            oreDefault = false;
            oreThreshold = 0.41f;
            oreScale = 12.42614f;
        }};
    }
}