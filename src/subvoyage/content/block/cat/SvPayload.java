package subvoyage.content.block.cat;

import arc.struct.IntSeq;
import arc.struct.Seq;
import mindustry.type.Category;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.PayloadConveyor;
import mindustry.world.blocks.payloads.PayloadLoader;
import mindustry.world.blocks.payloads.PayloadRouter;
import mindustry.world.blocks.payloads.PayloadUnloader;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitAssemblerModule;
import mindustry.world.blocks.units.UnitFactory;
import subvoyage.Subvoyage;
import subvoyage.type.block.distribution.PayloadLaunchPad;
import subvoyage.type.block.laser.blocks.*;
import subvoyage.type.block.unit.Fabricator;
import subvoyage.type.block.unit.Refabricator;

import static arc.Core.atlas;
import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvUnits.*;
import static subvoyage.content.block.SvBlocks.atl;

public class SvPayload {
    public static Block
        helicopterFabricator, hydromechFabricator,
        helicopterRefabricator, hydromechRefabricator,
        laserRefabricator,
        helicopterAssembler, hydromechAssembler, assemblyModule,
        fortifiedPayloadConveyor, fortifiedPayloadRouter,
        payloadLoader,payloadUnloader,
        payloadLaunchPad
    ;
    public static void load() {
        helicopterFabricator = new Fabricator("helicopter-factory"){
            {
                requirements(Category.units, atl(), with(iridium, 60, clay, 70));

                researchCost = with(iridium, 400, clay, 500);
                regionSuffix = "-fortified";
                consumeLiquid(argon, 0.3f);
                configurable = false;
                plans = Seq.with(
                        new UnitPlan(lapetus, 60f * 25, with(iridium, 15))
                );
                size = 3;
                consumePower(1.2f);
                consumeLiquid(hydrogen,8/60f).boost();
            }

            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };

        hydromechFabricator = new Fabricator("hydromech-factory") {{
            requirements(Category.units, atl(), with(iridium, 60, clay, 70, chrome, 30));
            regionSuffix = "-fortified";
            researchCost = with(iridium,600,clay,600, chrome,120);

            consumeLiquid(helium,0.25f);
            configurable = false;
            plans = Seq.with(
                    new UnitPlan(leeft, 60f * 25, with(iridium, 20))
            );
            size = 3;
            consumePower(1f);
            consumeLiquid(hydrogen,8/60f).boost();
        }

            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };

        helicopterRefabricator = new Refabricator("helicopter-refabricator") {{
            requirements(Category.units, atl(), with(iridium,100,clay,200,spaclanium,100));
            regionSuffix = "-fortified";
            researchCost = with(iridium,1200,clay,1200,spaclanium,1670);
            constructTime = 60f * 30f;
            size = 3;
            upgrades.addAll(
                    new UnitType[]{lapetus,skath}
            );
            consumePower(2.5f);
            consumeLiquid(argon, 25f / 60f);
            consumeItems(with(iridium, 30, sulfur,10));
            consumeLiquid(hydrogen,8/60f).boost();
        }

            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };


        hydromechRefabricator = new Refabricator("hydromech-refabricator") {{
            requirements(Category.units, atl(), with(iridium,100, chrome,100,corallite,200));
            regionSuffix = "-fortified";
            researchCost = with(iridium,1400, chrome,1000,corallite,1670);
            constructTime = 60f * 30f;
            size = 3;
            upgrades.addAll(
                    new UnitType[]{leeft,flagshi}
            );
            consumePower(2.5f);
            consumeLiquid(helium, 25f / 60f);
            consumeItems(with(iridium, 30, spaclanium,10));
            consumeLiquid(hydrogen,8/60f).boost();
        }

            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };

        laserRefabricator = new LaserReconstructor("laser-refabricator") {{
            requirements(Category.units, atl(), with(nitride, 180, phosphide, 180, chrome, 250, clay, 300));
            regionSuffix = "-fortified";
            researchCost = with(nitride, 80, phosphide, 80, chrome, 400, clay, 1000);
            constructTime = 60f * 40f;
            size = 5;

            inputRange = 16;
            maxSuppliers = 1;
            laserMaxEfficiency = 1.5f;
            inputs = IntSeq.with(1,2,3);
            laserRequirement = 10f;

            upgrades.addAll(
                    new UnitType[]{flagshi, vanguard},
                    new UnitType[]{skath, charon}
            );

            hasLiquids = true;
            liquidCapacity = 5*60f*2f;

            consumePower(5f);
            consumeLiquid(helium, 10f / 60f);
            consumeLiquid(argon, 10f / 60f);
            consumeItems(with(nitride, 10, phosphide, 10, chrome, 20));
        }
            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };

        helicopterAssembler = new LaserUnitAssembler("helicopter-assembler") {{
            requirements(Category.units, atl(), with(nitride,1000,quartzFiber,500,iridium,750, chrome,600));
            regionSuffix = "-fortified";
            size = 5;
            plans.add(
                    new UnitAssembler.AssemblerUnitPlan(callees, 60f * 70f, PayloadStack.list(skath, 4)),
                    new UnitAssembler.AssemblerUnitPlan(ganymede, 60f * 60f * 3f, PayloadStack.list(charon, 4))
            );
            areaSize = 13;
            itemCapacity = 40;

            consumePower(3.5f);
            consumeLiquid(argon, 96f / 60f);
            consumeItem(nitride, 40);
            consumeItem(iridium,40);

            inputRange = 16;
            maxSuppliers = 1;
            laserMaxEfficiency = 1.25f;
            inputs = IntSeq.with(1,2,3);
            consumeLaserTier0 = 240f;
            consumeLaserTier1 = 400f;
            capacity = 1000f;

            researchCostMultiplier = 0.4f;

            dronesCreated = 4;
            droneType = pisun;
        }
            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };
        hydromechAssembler = new LaserUnitAssembler("hydromech-assembler") {{
            requirements(Category.units, atl(), with(phosphide,1000,quartzFiber,500,iridium,750, chrome,600));
            regionSuffix = "-fortified";
            size = 5;
            plans.add(
                    new UnitAssembler.AssemblerUnitPlan(squadron, 60f * 70f, PayloadStack.list(flagshi, 4)),
                    new UnitAssembler.AssemblerUnitPlan(armada, 60f * 60f * 3f, PayloadStack.list(vanguard, 4))
            );
            areaSize = 13;
            itemCapacity = 40;

            consumeLaserTier0 = 240f;
            consumeLaserTier1 = 400f;

            inputRange = 16;
            maxSuppliers = 1;
            laserMaxEfficiency = 1.25f;
            inputs = IntSeq.with(1,2,3);

            capacity = 1000f;

            consumePower(3.5f);
            consumeLiquid(helium, 80f / 60f);
            consumeItem(phosphide, 40);
            consumeItem(iridium,40);

            researchCostMultiplier = 0.4f;
            droneType = ((LaserUnitAssembler) helicopterAssembler).droneType;
            dronesCreated = ((LaserUnitAssembler) helicopterAssembler).dronesCreated;
        }
            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };

        assemblyModule = new UnitAssemblerModule("assembly-module") {{
            requirements(Category.units, atl(), with(iridium,1200,quartzFiber,1200,spaclanium,1200, chrome,1200));
            consumePower(4f);
            regionSuffix = "-fortified";
            researchCostMultiplier = 0.75f;

            size = 5;
        }

            @Override
            public void load() {
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID +"factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID +"factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID +"factory-in-" + size + regionSuffix);
            }
        };

        fortifiedPayloadConveyor = new PayloadConveyor("fortified-payload-conveyor"){{
            requirements(Category.units, atl(), with(iridium, 5, chrome, 10));
            moveTime = 32f;
            canOverdrive = false;
            health = 1200;
            researchCostMultiplier = 4f;
            underBullets = true;
        }};

        fortifiedPayloadRouter = new PayloadRouter("fortified-payload-router"){{
            requirements(Category.units, atl(), with(iridium, 5, chrome, 15));
            moveTime = 32f;
            health = 1200;
            canOverdrive = false;
            researchCostMultiplier = 4f;
            underBullets = true;
        }};

        payloadLoader = new PayloadLoader("payload-loader"){{
            requirements(Category.units,atl(), with(iridium,50, chrome,30,clay,50));
            regionSuffix = "-fortified";
            hasPower = true;
            consumePower(2f);
            size = 3;
            fogRadius = 5;
        }

            @Override
            public void load() {
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID+"-"+"factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID+"-"+"factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID+"-"+"factory-in-" + size + regionSuffix);
            }
        };

        payloadUnloader = new PayloadUnloader("payload-unloader"){{
            requirements(Category.units,atl(), with(iridium,50, chrome,30,clay,50));
            regionSuffix = "-fortified";
            researchCostMultiplier = 0f;
            hasPower = true;
            consumePower(2f);
            size = 3;
            fogRadius = 5;
        }
            @Override
            public void load() {
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID+"-"+"factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID+"-"+"factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID+"-"+"factory-in-" + size + regionSuffix);
            }
        };

        payloadLaunchPad = new PayloadLaunchPad("payload-launch-pad") {{
            requirements(Category.units, atl(), with(iridium,50, chrome,30,clay,50));

            researchCost = with(iridium,200,chrome,50,clay,50);

            size = 3;
            health = 1600;
            maxPayloadSize = 3;
            range = 60*8f;
            launchTime = 180f;
            transportationTime = 300f;
            squareSprite = false;
            consumePower(0.5f);
        }};
    }
}
