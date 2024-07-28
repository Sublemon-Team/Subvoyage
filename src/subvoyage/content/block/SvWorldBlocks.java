package subvoyage.content.block;

import arc.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;
import subvoyage.content.*;
import subvoyage.draw.visual.*;
import subvoyage.type.block.environment.decoration.TreeBlock;
import subvoyage.type.block.environment.vapor.*;

import static mindustry.type.ItemStack.with;

public class SvWorldBlocks{
    public static Block
    //ores
    oreSpaclanium,oreCorallite,oreSulfur,oreIridium,oreChromium,
    wallOreSpaclanium,wallOreCorallite,wallOreIridium,wallOreChromium,
    // floors
    legartyteStone, darkLegartyteStone, archalyteStone, darkArchalyteStone, agaryteStone, sodilate, hardWater, darkHardWater,
    crudesQuarry,
    // walls
    legartyteWall, agaryteWall, archalyteWall, sodilateWall,
    // boulders
    agaryteBoulder, agaryteBlocks, hauntedTree,
    //editor
    vapor;

    public static void load() {
        vapor = new VaporFloor("vapor") {{
            requirements(Category.logic, BuildVisibility.editorOnly, with());
        }};

        hardWater = new Floor("hard-water"){
            public Block parent = Blocks.air;
            {
                speedMultiplier = 0.9f;
                variants = 0;
                status = StatusEffects.wet;
                statusDuration = 90f;
                liquidDrop = SvLiquids.hardWater;
                isLiquid = true;
                cacheLayer = SvShaders.hardWaterLayer;
                albedo = 0.9f;
                supportsOverlay = true;
                hasShadow = false;
                parent = blendGroup = Blocks.water;
                liquidMultiplier = 0.5f;
                attributes.set(SvAttribute.hardMetals, 1f);
            }

            @Override
            public void load() {
                super.load();
                edgeRegion = Core.atlas.find(name + "-edge");
            }

            @Override
            public void drawBase(Tile tile) {
                super.drawBase(tile);
            }
        };
        darkHardWater = new Floor("dark-hard-water"){
            public Block parent = Blocks.air;
            {
                speedMultiplier = 0.9f;
                variants = 0;
                status = StatusEffects.wet;
                statusDuration = 90f;
                liquidDrop = SvLiquids.hardWater;
                liquidMultiplier = 0.45f;
                isLiquid = true;
                cacheLayer = SvShaders.hardWaterLayer;
                albedo = 0.9f;
                supportsOverlay = true;
                hasShadow = false;
                parent = blendGroup = Blocks.water;
                attributes.set(SvAttribute.hardMetals, 1f);
            }

            @Override
            public void load() {
                super.load();
                edgeRegion = Core.atlas.find(name + "-edge");
            }

            @Override
            public void drawBase(Tile tile) {
                super.drawBase(tile);
            }
        };
        ((Floor) Blocks.water).supportsOverlay = true;

        oreSpaclanium = new OreBlock(SvItems.spaclanium){{
            oreDefault = false;
            oreThreshold = 0.81f;
            oreScale = 23.47619f;
        }};
        oreCorallite = new OreBlock(SvItems.corallite){{
            oreDefault = false;
            oreThreshold = 0.83f;
            oreScale = 23.17619f;
        }};
        oreSulfur = new OreBlock(SvItems.sulfur){{
            oreDefault = false;
            oreThreshold = 0.83f;
            oreScale = 22.17619f;
        }};
        oreIridium = new OreBlock(SvItems.iridium){{
            oreDefault = false;
            oreThreshold = 0.85f;
            oreScale = 12.42614f;
        }};
        oreChromium = new OreBlock(SvItems.chromium) {{
            oreDefault = false;
            variants = 5;
            oreThreshold = 0.9f;
            oreScale = 10.42614f;
        }};

        wallOreSpaclanium = new OreBlock("ore-wall-subvoyage-spaclanium", SvItems.spaclanium){{
            wallOre = true;
            needsSurface = false;
        }};
        wallOreCorallite = new OreBlock("ore-wall-subvoyage-corallite", SvItems.corallite){{
            wallOre = true;
            needsSurface = false;
        }};
        wallOreIridium = new OreBlock("ore-wall-subvoyage-iridium", SvItems.iridium){{
            wallOre = true;
            needsSurface = false;
        }};
        wallOreChromium = new OreBlock("ore-wall-subvoyage-chromium", SvItems.chromium){{
            wallOre = true;
            needsSurface = false;
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
        archalyteStone = new Floor("archalyte-stone"){{
            attributes.set(Attribute.water, -1f);
            variants = 3;
        }};
        darkArchalyteStone = new Floor("dark-archalyte-stone"){{
            attributes.set(Attribute.water, -1f);
            variants = 3;
        }};
        archalyteWall = new StaticWall("archalyte-wall"){{
            archalyteStone.asFloor().wall = this;
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
        sodilate = new Floor("sodilate"){{
            attributes.set(Attribute.water, -1f);
            variants = 3;
        }};
        sodilateWall = new StaticWall("sodilate-wall"){{
            sodilate.asFloor().wall = this;
            variants = 3;
        }};

        crudesQuarry = new SteamVent("crudes-quarry") {{
            parent = blendGroup = legartyteStone;
            attributes.set(SvAttribute.crude, 1f);
            effect = Fx.none;
            variants = 2;
        }};

        agaryteBoulder = new Prop("agaryte-boulder"){{
            variants = 2;
            agaryteStone.asFloor().decoration = this;
            forceDark = true;
        }};

        agaryteBlocks = new TallBlock("agaryte-blocks"){{
            variants = 2;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
        }};

        hauntedTree = new TreeBlock("haunted-tree"){{
            clipSize = 128f;
            shadowOffset = -2.5f;
        }};
    }
}