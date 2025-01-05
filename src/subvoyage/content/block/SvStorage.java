package subvoyage.content.block;

import arc.graphics.g2d.TextureRegion;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import subvoyage.content.other.SvTeam;
import subvoyage.type.block.storage.core.AtlacianCore;

import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvUnits.commute;
import static subvoyage.content.SvBlocks.atl;

public class SvStorage {
    public static Block
        corePuffer,

        container, largeVault, unloader, liquidContainer, liquidTank
            ;

    public static void load() {
        corePuffer = new AtlacianCore("core-puffer"){{
            requirements(Category.effect,atl(), with(spaclanium,600,corallite,600,clay,300,sulfur,300));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.editorOnly;
            isFirstTier = true;
            unitType = commute;
            health = 4000;
            itemCapacity = 3000;
            size = 4;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 12;

            placeableLiquid = true;
            bannedItems.addAll(crude);
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[SvTeam.melius.id]};
            }
        };


        container = new StorageBlock("vault"){
            {
                requirements(Category.effect,atl(), with(chrome, 20, iridium, 85));

                researchCost = with(chrome,80,iridium,200);

                size = 2;
                itemCapacity = 80;
                scaledHealth = 55;
                squareSprite = false;
            }

            @Override
            protected TextureRegion[] icons() {
                return new TextureRegion[]{region, teamRegions[SvTeam.melius.id]};
            }
        };

        unloader = new Unloader("unloader"){{
            requirements(Category.effect,atl(), with(chrome, 25, clay, 30));

            researchCost = with(chrome,100,clay,300);

            speed = 60f / 11f;
            group = BlockGroup.transportation;
            squareSprite = false;
        }
            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[SvTeam.melius.id]};
            }
        };

        liquidTank = new LiquidRouter("liquid-tank"){{
            requirements(Category.liquid,atl(), with(corallite,80, clay, 140, iridium, 30));
            researchCost = with(corallite,1040,clay,870,iridium,500);
            liquidCapacity = 1800f;
            health = 500;
            size = 3;
            liquidPadding = 6f / 4f;

            solid = true;
            squareSprite = false;
        }};
    }
}
