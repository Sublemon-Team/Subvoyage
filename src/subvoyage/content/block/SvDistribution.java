package subvoyage.content.block;

import mindustry.io.SaveVersion;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.Incinerator;
import mindustry.world.blocks.units.UnitCargoLoader;
import mindustry.world.blocks.units.UnitCargoUnloadPoint;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import subvoyage.core.draw.SvPal;
import subvoyage.type.block.distribution.ItemPipeline;
import subvoyage.type.block.distribution.LiquidPipeline;

import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvUnits.bulker;
import static subvoyage.content.SvBlocks.atl;

public class SvDistribution {
    public static Block
        duct,isolatedDuct,highPressureDuct,ductRouter,ductBridge,ductSorter,ductInvSorter, ductUnderflow, ductOverflow, ductDistributor, incinerator,
        itemPipeline,
        //shipCargoStation, shipUnloadPoint,

        fortifiedConduit, isolatedConduit, highPressureConduit, conduitRouter, conduitBridge, liquidPipeline
    ;

    public static void load() {
        duct = new Duct("duct"){{
            requirements(Category.distribution,atl(), with(corallite, 1));
            health = 90;
            speed = 4f;
            envDisabled |= Env.scorching;

            researchCost = with(corallite,2);
        }};

        isolatedDuct = new Duct("isolated-duct") {{
            requirements(Category.distribution,atl(), with(corallite, 1,iridium,1));
            health = 270;
            speed = 4f;
            armored = true;
            envDisabled |= Env.scorching;

            researchCost = with(corallite,20,iridium,20);
        }

            @Override
            public void load() {
                super.load();
                botRegions = ((Duct) duct).botRegions;
            }
        };


        ductBridge = new DuctBridge("duct-bridge") {{
            requirements(Category.distribution,atl(), with(corallite, 4,spaclanium,2));
            researchCost = with(corallite, 16, spaclanium, 4);

            ((Duct) duct).bridgeReplacement = this;

            placeableLiquid = true;
            envDisabled |= Env.scorching;
            health = 90;
            speed = 4f;
        }};

        ductRouter = new Router("duct-router") {{
            requirements(Category.distribution,atl(), with(corallite, 3));
            researchCost = with(corallite,16);
            envDisabled |= Env.scorching;
        }};

        ductSorter = new Sorter("duct-sorter"){{
            requirements(Category.distribution,atl(), with(corallite, 2, spaclanium, 2));
            researchCost = with(corallite,100,spaclanium,350);
            buildCostMultiplier = 3f;
            envDisabled |= Env.scorching;
        }};

        ductInvSorter = new Sorter("duct-inverted-sorter"){{
            requirements(Category.distribution,atl(), with(corallite, 2, spaclanium, 2));
            researchCost = with(corallite,100,spaclanium,350);
            buildCostMultiplier = 3f;
            invert = true;
            envDisabled |= Env.scorching;
        }};

        ductDistributor = new Router("duct-distributor"){{
            requirements(Category.distribution,atl(BuildVisibility.hidden), with(corallite, 4, spaclanium, 4));
            researchCost = with(corallite,320,spaclanium,70);
            buildCostMultiplier = 3f;
            size = 2;
            squareSprite = false;
        }};
        SaveVersion.modContentNameMap.put(ductDistributor.name,ductRouter.name);

        ductOverflow = new OverflowGate("duct-overflow-gate"){{
            requirements(Category.distribution,atl(), with(corallite, 2, spaclanium, 4));
            researchCost = with(corallite,300,spaclanium,300);
            buildCostMultiplier = 3f;
        }};

        ductUnderflow = new OverflowGate("duct-underflow-gate"){{
            requirements(Category.distribution,atl(), with(corallite, 2, spaclanium, 4));
            researchCost = with(corallite,300,spaclanium,300);
            buildCostMultiplier = 3f;
            invert = true;
        }};

        incinerator = new Incinerator("incinerator") {{
            requirements(Category.distribution,atl(), with(corallite,5,spaclanium,10));
            health = 90;
            envEnabled |= Env.space;
            buildCostMultiplier = 0.8f;
            consumePower(3f/60f);
        }};

        /*shipCargoStation = new UnitCargoLoader("ship-cargo-station"){{
            requirements(Category.distribution,atl(),with(iridium,100,clay,200));

            researchCost = with(iridium,1000,clay,700);

            size = 3;
            itemCapacity = 250;

            buildTime = 6f*60f;

            consumePower(12f/60f);
            consumeLiquid(argon,8f/60f);

            unitType = bulker;
        }};

        shipUnloadPoint = new UnitCargoUnloadPoint("ship-unload-point") {{
            requirements(Category.distribution,atl(),with(iridium,50,clay,80));

            researchCost = with(iridium,700,clay,500);

            size = 2;
            itemCapacity = 50;
        }};*/


        fortifiedConduit = new Conduit("clay-conduit") {{
            requirements(Category.liquid,atl(), with(corallite, 2));

            researchCost = with(corallite,3);

            envDisabled |= Env.scorching;
            botColor = SvPal.veryDarkViolet;

            health = 45;
        }};

        isolatedConduit = new ArmoredConduit("isolated-conduit") {{
            requirements(Category.liquid,atl(), with(corallite, 2,iridium,1));

            researchCost = with(corallite,30,iridium,30);
            envDisabled |= Env.scorching;
            botColor = SvPal.veryDarkViolet;
            health = 125;
        }};


        conduitBridge = new DirectionLiquidBridge("bridge-conduit"){{
            requirements(Category.liquid,atl(), with(corallite, 4, clay, 8));

            researchCost = with(corallite,80,clay,40);
            ((Conduit) fortifiedConduit).rotBridgeReplacement = this;
            range = 4;
            hasPower = false;

            envDisabled |= Env.scorching;
        }};

        conduitRouter = new LiquidRouter("liquid-router") {{
            requirements(Category.liquid,atl(), with(corallite, 4, clay, 2));

            researchCost = with(corallite,40,clay,10);

            liquidCapacity = 20f;
            underBullets = true;
            solid = false;

            envDisabled |= Env.scorching;
        }};

        itemPipeline = new ItemPipeline("item-pipeline") {{
            requirements(Category.distribution,atl(BuildVisibility.editorOnly),with());

            health = 1200;

            range = 24;

            pulse = false;
            hasPower = false;
        }};

        liquidPipeline = new LiquidPipeline("liquid-pipeline") {{
            requirements(Category.distribution,atl(BuildVisibility.editorOnly),with());

            health = 1200;

            range = 64;

            pulse = false;
            hasPower = false;
        }};
    }
}
