package subvoyage.content.block;

import arc.*;
import mindustry.content.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.*;
import subvoyage.content.SvItems;
import subvoyage.content.other.SvAttribute;
import subvoyage.core.draw.shader.SvShaders;
import subvoyage.core.draw.block.Draw3DSprite;
import subvoyage.core.draw.block.Draw3DStem;
import subvoyage.type.block.environment.DrawerBlock;

import java.util.HashMap;

public class SvEnvironment {
    public static HashMap<Block,Block> floorToWallOre = new HashMap<>();
    public static Block
    //ores
    oreSpaclanium,oreCorallite,oreSulfur,oreIridium,oreChromium,
    wallOreSpaclanium,wallOreCorallite,wallOreIridium,wallOreChromium,
    // floors
    legartyteStone, darkLegartyteStone, archalyteStone, darkArchalyteStone, agaryteStone, sodilateStone, darkSodilateFloor, hardWater, darkHardWater,
    crudesQuarry,
    // walls
    legartyteWall, agaryteWall, archalyteWall, sodilateWall,
    // boulders
    agaryteBoulder, agaryteBlocks, legartyteBoulder, darkLegaryteBoulder, archalyteBoulder, sodilateBoulder, sodilateBlocks, archalyteSpikes, hauntedTree;

    public static void load() {
        hardWater = new Floor("hard-water"){
            public Block parent;
            {
                speedMultiplier = 0.9f;
                variants = 0;
                status = StatusEffects.wet;
                statusDuration = 90f;
                liquidDrop = SvItems.hardWater;
                isLiquid = true;
                cacheLayer = SvShaders.hardWaterLayer;
                albedo = 0.9f;
                supportsOverlay = true;
                hasShadow = false;
                parent = blendGroup = Blocks.water;
                liquidMultiplier = 0.5f;
                attributes.set(SvAttribute.sodilate, 1f);
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
            public Block parent;
            {
                speedMultiplier = 0.9f;
                variants = 0;
                status = StatusEffects.wet;
                statusDuration = 90f;
                liquidDrop = SvItems.hardWater;
                liquidMultiplier = 0.45f;
                isLiquid = true;
                cacheLayer = SvShaders.hardWaterLayer;
                albedo = 0.9f;
                supportsOverlay = true;
                hasShadow = false;
                parent = blendGroup = Blocks.water;
                attributes.set(SvAttribute.sodilate, 1f);
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
        oreChromium = new OreBlock(SvItems.chrome) {{
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
        wallOreChromium = new OreBlock("ore-wall-subvoyage-chromium", SvItems.chrome){{
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
            darkLegartyteStone.asFloor().wall = this;
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
        sodilateStone = new Floor("sodilate"){{
            attributes.set(Attribute.water, -1f);
            attributes.set(SvAttribute.sodilate, 1f);
            variants = 3;
        }};
        darkSodilateFloor = new Floor("dark-sodilate") {{
            attributes.set(Attribute.water, -1f);
            attributes.set(SvAttribute.sodilate, 1f);
            variants = 3;
        }};
        sodilateWall = new StaticWall("sodilate-wall"){{
            sodilateStone.asFloor().wall = this;
            attributes.set(SvAttribute.sodilate, 1f);
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

        legartyteBoulder = new Prop("legartyte-boulder"){{
            variants = 2;
            legartyteStone.asFloor().decoration = this;
            forceDark = true;
        }};

        darkLegaryteBoulder = new Prop("dark-legartyte-boulder"){{
            variants = 2;
            darkLegartyteStone.asFloor().decoration = this;
            forceDark = true;
        }};

        archalyteBoulder = new Prop("archalyte-boulder"){{
            variants = 3;
            archalyteStone.asFloor().decoration = this;
            darkArchalyteStone.asFloor().decoration = this;
            forceDark = true;
        }};

        sodilateBoulder = new Prop("sodilate-boulder"){{
            variants = 2;
            sodilateStone.asFloor().decoration = this;
            darkSodilateFloor.asFloor().decoration = this;
            forceDark = true;
        }};

        sodilateBlocks = new TallBlock("sodilate-blocks") {{
            variants = 3;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
            forceDark = true;
        }};
        archalyteSpikes = new TallBlock("archalyte-spikes") {{
            variants = 2;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
            forceDark = true;
        }};

        agaryteBlocks = new TallBlock("agaryte-blocks"){{
            variants = 2;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
        }};

        hauntedTree = new DrawerBlock("haunted-tree"){{
            clipSize = 128f;
            //shadowOffset = -2.5f;
            drawer = new DrawMulti(
                    new Draw3DSprite("-shadow") {{
                        surfaceTime = 0f;
                        camOffset = 0.001f;
                        enableZOffset = false;
                    }},
                    new DrawRegion("-root"),
                    new Draw3DStem("-stem") {{
                        camOffset = 0.15f;
                        segments = 4;
                    }},
                    new Draw3DSprite("-top") {{
                        surfaceTime = 0f;
                        camOffset = 0.15f;
                    }}
            );
        }};

        floorToWallOre.put(oreSpaclanium,wallOreSpaclanium);
        floorToWallOre.put(oreCorallite,wallOreCorallite);
        floorToWallOre.put(oreIridium,wallOreIridium);
        floorToWallOre.put(oreChromium,wallOreChromium);
    }
}