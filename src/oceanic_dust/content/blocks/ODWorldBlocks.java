package oceanic_dust.content.blocks;

import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;
import oceanic_dust.content.world.items.*;

public class ODWorldBlocks {
    public static Block
    //ores
    orePhosphorus,oreCorallite,oreSulfur,oreIridium,
    // floors
    legartyteStone,
    // walls
    legartyteWall;


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
        legartyteStone = new Floor("legartyte-stone"){{
            attributes.set(Attribute.water, -1f);
            variants = 3;
            wall = legartyteWall;
        }};
        legartyteWall = new StaticWall("legartyte-wall"){{
            variants = 3;
        }};
    }
}