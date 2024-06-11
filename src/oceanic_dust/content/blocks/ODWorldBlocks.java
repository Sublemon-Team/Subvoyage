package oceanic_dust.content.blocks;

import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;
import oceanic_dust.content.world.items.*;

public class ODWorldBlocks {
    public static Block
    //ores
            oreSpaclanium,oreCorallite,oreSulfur,oreIridium,
    // floors
    legartyteStone, darkLegartyteStone, agaryteStone,
    // walls
    legartyteWall, agaryteWall;


    public static void load() {
        oreSpaclanium = new OreBlock(ODItems.spaclanium){{
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
        }};
        darkLegartyteStone = new Floor("dark-legartyte-stone"){{
            attributes.set(Attribute.water, -1f);
            variants = 3;
        }};
        legartyteWall = new StaticWall("legartyte-wall"){{
            legartyteStone.asFloor().wall = this;
            variants = 3;
        }};
        agaryteStone = new Floor("agaryte-stone"){{
            attributes.set(Attribute.water, -1f);
            variants = 3;
        }};
        agaryteWall = new StaticWall("agaryte-wall"){{
            agaryteStone.asFloor().wall = this;
            variants = 3;
        }};
    }
}