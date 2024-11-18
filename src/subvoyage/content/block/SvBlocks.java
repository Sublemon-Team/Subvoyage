package subvoyage.content.block;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.DrawPart.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import subvoyage.*;
import subvoyage.content.*;
import subvoyage.content.block.cat.*;
import subvoyage.content.other.*;
import subvoyage.content.sound.SvSounds;
import subvoyage.draw.block.*;
import subvoyage.draw.part.*;
import subvoyage.draw.visual.*;
import subvoyage.type.block.core.*;
import subvoyage.type.block.core.offload_core.*;
import subvoyage.type.block.defense.DrawRingTurret;
import subvoyage.type.block.defense.PowerRingTurret;
import subvoyage.type.block.distribution.*;
import subvoyage.type.block.effect.OverdriveSquareProjector;
import subvoyage.type.block.fog.*;
import subvoyage.type.block.laser.blocks.LaserGenerator;
import subvoyage.type.block.laser.nodes.LaserNode;
import subvoyage.type.block.laser.nodes.LaserSplitter;
import subvoyage.type.block.laser_old_blocks.*;
import subvoyage.type.block.laser_old_blocks.unit.*;
import subvoyage.type.block.laser.nodes.LaserAmplifier;
import subvoyage.type.block.power.generation.*;
import subvoyage.type.block.power.node.*;
import subvoyage.type.block.production.*;
import subvoyage.type.block.production.crude_smelter.*;
import subvoyage.type.block.wall.PhosphideWall;
import subvoyage.type.shoot.*;
import subvoyage.type.shoot.bullet.*;
import subvoyage.world.*;

import static arc.Core.atlas;
import static arc.graphics.g2d.Draw.color;
import static mindustry.Vars.tilesize;
import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.*;
import static subvoyage.BalanceStates.*;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvUnits.*;

public class SvBlocks{
    
    public static Block
            //NON-USER
            offloadCore, offloadCoreGuardian, productionAnchor,
            //DRILLS
            submersibleDrill, featherDrill, tectonicDrill,
            //DEFENSE
            whirl,rupture,resonance,burden,cascade,spectrum,upsurge,resistance,
            awe, inspiration, //removed
            clayWall,clayWallLarge,
            phosphideWall,phosphideWallLarge,
            tugSheetWall, tugSheetWallLarge,
            coreDecoder, coreDecrypter,
            regenerator, regenProjector,
            overdriveProjector,
            //CRAFTERS
            waterMetallizer, poweredEnhancer,
            ceramicBurner, terracottaBlaster, circularCrusher,
            argonCentrifuge, argonCondenser,
            propanePyrolyzer, heliumCompressor,
            phosphidePhotosynthesizer,
            crudeSmelter, crudeCrucible,
            quartzScutcher, tugRoller,
            //LIQUIDS
            waterDiffuser, waterSifter, distiller, lowTierPump, centrifugalPump, fortifiedConduit, isolatedConduit, highPressureConduit, conduitRouter, conduitBridge,
            //ENERGY
            energyDock, energyDistributor, accumulator, largeAccumulator, spaclaniumHydrolyzer, windTurbine, hydrocarbonicGenerator, chromiumReactor,
            //LASER
            laserProjector, luminescentProjector, laserSource, laserNode, laserAmplificator, laserSplitter, laserBlaster,
            //TRANSPORTATION
            duct,isolatedDuct,highPressureDuct,ductRouter,ductBridge,ductSorter,ductInvSorter, ductUnderflow, ductOverflow, ductDistributor, incinerator,
            shipCargoStation, shipUnloadPoint,
            //PAYLOAD
            helicopterFabricator, hydromechFabricator,
            helicopterRefabricator, hydromechRefabricator,
            laserRefabricator,
            helicopterAssembler, hydromechAssembler, assemblyModule,
            fortifiedPayloadConveyor, fortifiedPayloadRouter,
            payloadLoader,payloadUnloader,
            payloadLaunchPad,
            //EXPLORATION
            buoy,tower,beacon,
            //STORAGE
            corePuffer,coreShore,coreReef, vault, largeVault, unloader, liquidContainer, liquidTank;

    public static void loadCat() {
        SvEnvironment.load();
        SvStorage.load();
        SvProduction.load();
        SvCrafting.load();
        SvDistribution.load();
        SvPayload.load();
        SvSpecial.load();
        SvTurret.load();
        SvDefense.load();
        SvPower.load();
        SvLaser.load();
    }

    public static void load() {
        //non-user
        offloadCore = new OffloadCore("offload-core") {{
            requirements(Category.logic, BuildVisibility.editorOnly, with());
            health = 3000;
            size = 3;

            itemCapacity = 1000;

            lowTierUnits = new UnitType[] {lapetus,leeft};
            midTierUnits = new UnitType[] {skath,flagshi};
            highTierUnits = new UnitType[] {charon,callees,vanguard,squadron};

            unitType = shift;
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };


        offloadCoreGuardian = new OffloadCoreGuardian("offload-core-guardian") {{
            requirements(Category.logic, BuildVisibility.editorOnly, with());
            health = 4000;
            size = 4;
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };

        productionAnchor = new ProductionAnchor("production-anchor") {{
            requirements(Category.effect, BuildVisibility.editorOnly, with());
            health = 1500;
            size = 3;

            priority = TargetPriority.core-0.2f; // this is pretty good resource source so yes

            itemBatches = with(
                    corallite,40,
                    corallite,40,
                    spaclanium,35,
                    spaclanium,35,
                    finesand,20,
                    sulfur,15,
                    spaclanium,35,
                    spaclanium,35,
                    finesand,20,
                    sulfur,15,
                    corallite,40,
                    corallite,40
            );
        }};

        //laser

        laserProjector = new LaserGenerator("laser-projector") {{
            requirements(Category.effect, atl(), with(iridium, 300, chrome, 200, spaclanium, 150));
            laserOutput = 10f;
            outputRange = 5;

            maxSuppliers = 0;

            outputs = IntSeq.with(0);

            size = 3;
            squareSprite = false;
            consumePower(1.3f);
        }};

        luminescentProjector = new LaserGenerator("luminescent-projector") {{
            requirements(Category.effect, atl(), with(iridium, 300, chrome, 200, spaclanium, 150)); //TODO:reqs
            laserOutput = 60f;
            maxSuppliers = 0;
            size = 3;
            squareSprite = false;
            outputRange = 4;
            outputs = IntSeq.with(0);
            itemDuration = 60f*4f;
            consumeItem(phosphide,1);
            consumePower(1.3f);
        }};

        laserSource = new LaserGenerator("laser-source") {{
            requirements(Category.effect, atl(BuildVisibility.sandboxOnly), with());
            laserOutput = 1000f;
            maxSuppliers = 0;
            size = 3;
            squareSprite = false;
            outputRange = 32;
            outputs = IntSeq.with(0);
        }};

        laserNode = new LaserNode("laser-node") {{
            requirements(Category.effect, atl(), with(iridium, 30, chrome, 30));
            size = 3;
            maxSuppliers = 1;
            squareSprite = false;

            inputRange = 8;
            outputRange = 8;

            capacity = 60;

            outputs = IntSeq.with(0);
            inputs = IntSeq.with(1,2,3);
        }};

        laserSplitter = new LaserSplitter("laser-splitter") {{
            requirements(Category.effect, atl(), with(iridium, 50, chrome, 40, spaclanium, 10));
            size = 3;
            maxSuppliers = 1;

            squareSprite = false;
            inputRange = 8;
            outputRange = 16;
            outputs = IntSeq.with(1,3);
            inputs = IntSeq.with(2);
        }};

        laserAmplificator = new LaserAmplifier("laser-amplifier") {{
            requirements(Category.effect, atl(), with(iridium, 80, chrome, 80, spaclanium, 10));
            size = 3;
            squareSprite = false;

            maxSuppliers = 3;

            consumePower(4f);

            inputRange = 16;
            outputRange = 16;
            outputs = IntSeq.with(0);
            inputs = IntSeq.with(1,2,3);
        }};

        laserBlaster = new LaserBlaster("laser-blaster") {{
            requirements(Category.effect, atl(), with(iridium, 200, chrome, 200, spaclanium, 200, corallite, 100));
            consumeLaserPower(300);
            minLaserEfficiency = 0.3f;
            size = 3;
            inputRange = 16;
            drawInputs = false;
            setLaserInputs(1,2,3);

            maxSuppliers = 1;
            drawer = new DrawMulti(
                    new DrawRegion("-base"),
                    new DrawOutline("") {{
                        layer = Layer.turret;
                        buildingRotate = true;
                    }},
                    new DrawHeatGlow() {{
                        layer = Layer.turret+1f;
                    }}
            );

            // would be good if there were some effects like smoke, huh?
            bulletType = new ContiniousLaserRangedBulletType(15){{
                maxRange = range = length = 15*60f;
                hitEffect = Fx.hitMeltdown;
                hitColor = Pal.meltdownHit;
                status = StatusEffects.melting;
                drawSize = 420f;

                damageInterval = 1f;

                lifetime = shootDuration = 120f;
                shootDelay = 120f;

                incendChance = 0.4f;
                incendSpread = 5f;
                incendAmount = 1;
                ammoMultiplier = 1f;
            }};
        }};

        //payload
        helicopterFabricator = new UnitFactory("helicopter-factory"){
            {
                requirements(Category.units, atl(), with(iridium, 60, clay, 70));

                researchCost = with(iridium, 400, clay, 500);
                regionSuffix = "-fortified";
                consumeLiquid(argon, 0.2f);
                configurable = false;
                plans = Seq.with(
                new UnitPlan(lapetus, 60f * 25, with(iridium, 15))
                );
                size = 3;
                consumePower(1.2f);
            }

            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };

        hydromechFabricator = new UnitFactory("hydromech-factory") {{
            requirements(Category.units, atl(), with(iridium, 60, clay, 70, chrome, 30));
            regionSuffix = "-fortified";
            researchCost = with(iridium,600,clay,600, chrome,120);

            consumeLiquid(helium,1.2f);
            configurable = false;
            plans = Seq.with(
                    new UnitPlan(leeft, 60f * 25, with(iridium, 20))
            );
            size = 3;
            consumePower(1f);
        }

            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };

        helicopterRefabricator = new Reconstructor("helicopter-refabricator") {{
            requirements(Category.units, atl(), with(iridium,100,clay,200,spaclanium,100));
            regionSuffix = "-fortified";
            researchCost = with(iridium,1200,clay,1200,spaclanium,1670);
            constructTime = 60f * 30f;
            size = 3;
            upgrades.addAll(
                    new UnitType[]{lapetus,skath}
            );
            consumePower(2.5f);
            consumeLiquid(argon, 3f / 60f);
            consumeItems(with(iridium, 60, spaclanium,50));
        }

            @Override
            public void load(){
                super.load();
                topRegion = atlas.find(name + "-top", Subvoyage.ID + "-factory-top-" + size + regionSuffix);
                outRegion = atlas.find(name + "-out", Subvoyage.ID + "-factory-out-" + size + regionSuffix);
                inRegion = atlas.find(name + "-in", Subvoyage.ID + "-factory-in-" + size + regionSuffix);
            }
        };


        hydromechRefabricator = new Reconstructor("hydromech-refabricator") {{
            requirements(Category.units, atl(), with(iridium,100, chrome,100,corallite,200));
            regionSuffix = "-fortified";
            researchCost = with(iridium,1400, chrome,1000,corallite,1670);
            constructTime = 60f * 30f;
            size = 3;
            upgrades.addAll(
                    new UnitType[]{leeft,flagshi}
            );
            consumePower(2.5f);
            consumeLiquid(helium, 3f / 60f);
            consumeItems(with(iridium, 60, crude,50));
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
            requirements(Category.units, atl(), with(iridium,500, chrome,400,corallite,300,spaclanium,300));
            regionSuffix = "-fortified";
            researchCost = with(iridium,3000, chrome,3000,corallite,4000,spaclanium,3500);
            constructTime = 60f * 40f;
            size = 5;
            inputRange = 16;
            maxSuppliers = 1;
            minLaserEfficiency = 0.95f;
            setLaserInputs(1,2,3);

            upgrades.addAll(
                    new UnitType[]{flagshi, vanguard},
                    new UnitType[]{skath, charon}
            );

            hasLiquids = true;
            liquidCapacity = 5*60f*2f;

            consumeLaserPower(10f);
            consumePower(5f);
            consumeLiquid(helium, 5f / 60f);
            consumeLiquid(argon, 5f / 60f);
            consumeItems(with(iridium, 60, chrome, 20, crude,20));
        }};

        helicopterAssembler = new LaserUnitAssembler("helicopter-assembler") {{
            requirements(Category.units, atl(), with(iridium,1000,quartzFiber,500,corallite,750, chrome,600));
            regionSuffix = "-fortified";
            size = 5;
            plans.add(
                    new UnitAssembler.AssemblerUnitPlan(callees, 60f * 70f, PayloadStack.list(skath, 4)),
                    new UnitAssembler.AssemblerUnitPlan(ganymede, 60f * 60f * 3f, PayloadStack.list(charon, 4))
            );
            areaSize = 13;
            itemCapacity = 40;

            consumePower(3.5f);
            consumeLiquid(argon, 12f / 60f);
            consumeItem(iridium, 20);
            consumeItem(clay,20);

            setLaserInputs(1,2,3);
            maxSuppliers = 1;
            inputRange = 8;

            consumeLaserTier0 = 40f;
            consumeLaserTier1 = 140f;
            minLaserEfficiency = 0.95f;

            dronesCreated = 4;
            droneType = pisun;
        }};
        hydromechAssembler = new LaserUnitAssembler("hydromech-assembler") {{
            requirements(Category.units, atl(), with(iridium,1100,quartzFiber,600,spaclanium,850, chrome,700));
            regionSuffix = "-fortified";
            size = 5;
            plans.add(
                    new UnitAssembler.AssemblerUnitPlan(squadron, 60f * 70f, PayloadStack.list(flagshi, 4)),
                    new UnitAssembler.AssemblerUnitPlan(armada, 60f * 60f * 3f, PayloadStack.list(vanguard, 4))
            );
            areaSize = 13;
            itemCapacity = 40;

            consumeLaserTier0 = 40f;
            consumeLaserTier1 = 140f;
            minLaserEfficiency = 0.95f;

            setLaserInputs(1,2,3);
            maxSuppliers = 1;
            inputRange = 8;

            consumePower(3.5f);
            consumeLiquid(helium, 12f / 60f);
            consumeItem(iridium, 20);
            consumeItem(crude,20);
            droneType = ((LaserUnitAssembler) helicopterAssembler).droneType;
            dronesCreated = ((LaserUnitAssembler) helicopterAssembler).dronesCreated;
        }};

        assemblyModule = new LaserUnitAssemblerModule("assembly-module") {{
            requirements(Category.units, with(iridium,1200,quartzFiber,1200,spaclanium,1200, chrome,1200));
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
            size = 3;
            health = 1600;
            maxPayloadSize = 3;
            range = 60*8f;
            launchTime = 180f;
            transportationTime = 300f;
            squareSprite = false;
            consumePower(0.5f);
        }};

        //drills
        submersibleDrill = new Drill("submersible-drill"){{
            requirements(Category.production,atl(), with(corallite, 50, spaclanium, 10));
            tier = 2;
            hardnessDrillMultiplier = 0.9f;
            drillTime = 470;
            size = 2;
            itemCapacity = 20;
            blockedItem = Items.sand;
            fogRadius = 2;
            squareSprite = false;
            updateEffect = Fx.none;
            drillEffect = Fx.none;

            researchCost = with(corallite,200,spaclanium,100);

            consumeLiquid(water, 5/60f);
            consumeLiquid(hydrogen,4f/60f).boost();
        }
            @Override
            public void setStats() {
                super.setStats();
                if(findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase){
                    stats.remove(Stat.booster);
                    stats.add(Stat.booster,
                            StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                                    consBase.amount, liquidBoostIntensity, false,
                                    l -> (consumesLiquid(l) && (findConsumer(f -> f instanceof ConsumeLiquid).booster || ((ConsumeLiquid)findConsumer(f -> f instanceof ConsumeLiquid)).liquid != l)))
                    );
                }
            }
        };

        featherDrill = new Drill("feather-drill"){{
            requirements(Category.production,atl(), with(corallite, 100, chrome, 50, iridium, 100, clay, 200));
            researchCost = with(corallite,500, chrome,200,iridium,300,clay,400);

            tier = 3;
            hardnessDrillMultiplier = 1.1f;
            drillTime = 250;
            size = 3;
            itemCapacity = 30;
            blockedItem = Items.sand;
            fogRadius = 3;
            squareSprite = false;

            consumePower(0.67f);
            consumeLiquid(argon, 6f/60f);
            consumeLiquid(hydrogen,9f/60f).boost();
        }

            @Override
            public void setStats() {
                super.setStats();
                if(findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase){
                    stats.remove(Stat.booster);
                    stats.add(Stat.booster,
                            StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                                    consBase.amount, liquidBoostIntensity, false,
                                    l -> (consumesLiquid(l) && (findConsumer(f -> f instanceof ConsumeLiquid).booster || ((ConsumeLiquid)findConsumer(f -> f instanceof ConsumeLiquid)).liquid != l)))
                    );
                }
            }
        };
        tectonicDrill = new AttributeCrafterBoostable("tectonic-drill") {{
            requirements(Category.production,atl(), with(corallite, 200, spaclanium, 100, iridium, 100));
            researchCost = with(corallite,1000,spaclanium,600,iridium,400);
            attribute = SvAttribute.crude;

            minEfficiency = 9f - 0.0001f;
            baseEfficiency = 0f;
            displayEfficiency = false;

            craftTime = 200;
            size = 3;
            itemCapacity = 20;
            outputItem = new ItemStack(crude, 3);
            hasPower = true;
            hasLiquids = false;
            ambientSound = Sounds.drill;
            ambientSoundVolume = 0.15f;

            consumeLiquid(hydrogen,4f/60f).boost();

            boostScale = 1f / 9f;
            /*consumeCoolant()
            coolant = consume(new ConsumeLiquid(nitrogen, 20f / 60f));*/

            craftEffect = new MultiEffect(Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.orangeSpark, 20f));
            drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawBlurSpin("-rotator", 4),
            new DrawDefault(),
            new DrawRegion("-top")
            );

            fogRadius = 4;
            squareSprite = false;
            consumePower(0.2f);
        }

            @Override
            public void drawPlace(int x, int y, int rotation, boolean valid) {
                super.drawPlace(x, y, rotation, valid);
                int efficiency = (int)((baseEfficiency + Math.min(maxBoost, boostScale * sumAttribute(attribute, x, y))) * 100f);
                if(efficiency < 100) {
                    Draw.z(Layer.block+2);
                    //Drawf.dashCircle((x+size/4f)*tilesize,(y+size/4f)*tilesize,oreSearchRadius*tilesize, Pal.redDust);
                    WorldLabel.drawAt(Core.bundle.get("tectonic-drill.place.message"), (x + size / 4f - 0.75f) * tilesize, (y + size / 4f + size - 0.1f - 1f) * tilesize,
                            Layer.block + 3, WorldLabel.flagOutline, 0.8f);

                    color(Color.scarlet);
                    Draw.rect(Icon.cancel.getRegion(), (x+size/4f - 0.75f)*tilesize,(y+size/4f+size-0.1f+0.5f - 1f)*tilesize);
                }
            }
        };

        //defense
        whirl = new ItemTurret("whirl"){{
            requirements(Category.turret,atl(), with(corallite, 85, clay, 45));
            outlineColor = SvPal.outline;
            squareSprite = false;

            researchCost = with(corallite,10,clay,10);

            size = 3;
            drawer = new DrawTurretCallbacked("atlacian-"){{
                DrawTurret draw = (DrawTurret)drawer;
                SvRegionPart liquidPart = new SvRegionPart(draw,"-blade"){{
                    heatColor = Color.sky.cpy().a(0.42f);
                    heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                    mirror = true;
                    under = true;
                    moveX = 1.5f;
                    moveRot = -8;
                }};

                parts.add(new RegionPart("-blade-mid"){{
                    heatColor = Color.sky.cpy().a(0.42f);
                    heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                    progress = PartProgress.recoil;
                    moveY = -1.25f;
                }});

                parts.add(liquidPart);
                onDraw = (build,drawer) -> {
                    liquidPart.liquidDraw = build.liquids.current();
                    liquidPart.liquidAlpha = build.liquids.get(liquidPart.liquidDraw) / build.block.liquidCapacity;
                };
            }};

            shootSound = Sounds.blaster;
            reload = 20f;

            shoot = new ShootWhirl(){{
                barrels = new float[]{
                        -6, 2.5f, 0,
                        6, 2.5f, 0
                };
                shotDelay = 5f;
            }};

            float BPS = 60f/(reload+shoot.firstShotDelay+shoot.shots*shoot.shotDelay)*shoot.shots;

            float mainDamage = WHIRL_DPS/BPS*0.75f;
            float subDamage = WHIRL_DPS/BPS*0.25f;

            ammo(
                    spaclanium,new MissileBulletType(3.7f, mainDamage){{
                        width = 7f;
                        height = 12f;
                        shrinkY = 0f;
                        lifetime = 60f;
                        hitEffect = Fx.blastExplosion;
                        despawnEffect = Fx.blastExplosion;
                        shootEffect = SvFx.pulverize;
                        smokeEffect = Fx.none;
                        hitColor = backColor = trailColor = SvPal.spaclanium;
                        frontColor = Color.white;
                        trailWidth = 3f;
                        trailLength = 5;

                        //status = StatusEffects.electrified; //TODO: replace with custom effect
                        statusDuration = 60f;

                        ammoMultiplier = 2f;
                        lightningColor = SvPal.spaclanium;
                        lightningDamage = subDamage/2f;
                        lightning = 2;
                        lightningLength = 10;
                    }}
            );

            shootY = 5f;
            recoil = 1f;
            rotate = false;

            range = 170f;
            scaledHealth = 900/9f;

            coolantMultiplier = 1f;
            coolant = consume(new ConsumeLiquid(hydrogen, 20f / 60f));

            limitRange(2);
        }};

        rupture = new ItemTurret("rupture"){{
            requirements(Category.turret,atl(), with(corallite, 145, clay, 125, iridium, 30));
            size = 3;
            outlineColor = SvPal.outline;
            shootCone = 360f;
            fogRadius = 6;

            researchCost = with(corallite, 100, clay, 60, iridium, 30);
            targetGround = false;
            targetAir = true;
            squareSprite = false;

            cooldownTime = 60f;
            shoot = new ShootHelix() {{
                mag = 3;
            }};
            reload = 80f;

            float BPS = 60f/(reload+shoot.firstShotDelay+shoot.shots*shoot.shotDelay)*(shoot.shots*2f);
            float mainDamage = RUPTURE_DPS/BPS*0.9f;
            float subDamage = RUPTURE_DPS/BPS*0.1f/(3+3*3f);

            ammo(
            sulfur, new BasicBulletType(6f, mainDamage){{
                width = 6f;
                height = 12f;
                lifetime = 30f;
                shootEffect = SvFx.pulverize;
                smokeEffect = Fx.none;
                hitColor = backColor = trailColor = Pal.missileYellow;
                frontColor = Color.white;
                trailWidth = 6f;
                trailLength = 12;
                trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                hitEffect = despawnEffect = Fx.hitBulletColor;
                shoot = new ShootHelix();
                ammoPerShot = 3;
                fragBullet = intervalBullet = new BasicBulletType(6f, subDamage) {{
                    width = 9f;
                    hitSize = 5f;
                    height = 15f;
                    pierce = true;
                    pierceCap = 3;
                    lifetime = 15f;
                    pierceBuilding = true;
                    hitColor = backColor = trailColor = Pal.bulletYellow;
                    frontColor = Color.white;
                    trailWidth = 2.1f;
                    trailLength = 5;

                    status = StatusEffects.slow; //TODO: replaceEffect
                    statusDuration = 60f;
                    hitEffect = despawnEffect = new WaveEffect(){{
                        colorFrom = colorTo = Pal.boostFrom;
                        sizeTo = 4f;
                        strokeFrom = 4f;
                        lifetime = 10f;
                    }};
                    buildingDamageMultiplier = 0.3f;
                    homingPower = 0.2f;
                    homingRange = 30f;
                }};
                intervalRandomSpread = 0f;
                intervalSpread = 80f;
                intervalBullets = 3;
                intervalDelay = -1f;
                bulletInterval = 10f;

                fragRandomSpread = 0f;
                fragSpread = 90f;
                fragBullets = 3;
                fragVelocityMin = 1f;
            }}
            );

            drawer = new DrawTurret("atlacian-"){{
                parts.add(new RegionPart("-blade"){{
                    mirror = true;
                    under = true;
                    moveX = 1.5f;
                    moveRot = -4;
                    progress = PartProgress.recoil;
                    moves.add(new PartMove(PartProgress.warmup,1.25f,2.25f,-16));
                }});

                parts.add(new RegionPart("-blade-mid"){{
                    //progress = PartProgress.recoil;
                    progress = PartProgress.heat;
                    moveY = -1.25f;
                }});

                parts.add(new FlarePart(){{
                    progress = PartProgress.warmup.delay(0.05f);
                    color1 = SvPal.quartzFiber;
                    stroke = 3f;
                    radius = 0f;
                    radiusTo = 6f;
                    layer = Layer.effect;
                    y = -8f;
                    x = 0f;
                    followRotation = true;
                    sides = 6;
                }});

                parts.add(new ShapePart(){{
                    progress = PartProgress.warmup.delay(0.05f);
                    color = SvPal.quartzFiber;
                    stroke = 1f;
                    radius = 2.5f;
                    radiusTo = 6f;
                    layer = Layer.effect;
                    y = -8f;
                    x = 0f;
                    rotateSpeed = 1f;
                    hollow = true;
                    sides = 6;
                }});

            }};

            shootSound = Sounds.railgun;
            shootY = 12f;
            recoil = 0.5f;
            range = 260f;
            scaledHealth = 200;

            coolantMultiplier = 2f;
            coolant = consume(new ConsumeLiquid(hydrogen, 20f / 60f));

            limitRange(6);
        }};

        awe = new PowerTurret("awe") {{
            requirements(Category.turret,atl(BuildVisibility.hidden), with(corallite, 85, iridium, 40, spaclanium, 20));
            size = 2;
            outlineColor = SvPal.outline;
            shootCone = 360f;
            targetGround = true;
            targetAir = false;
            fogRadius = 6;
            health = 260;
            shootSound = Sounds.dullExplosion;
            range = 8*10f;
            velocityRnd = 0;
            reload = 20f;
            shake = 5f;
            shootY = 0f;
            rotateSpeed = 0;
            drawer = new DrawTurret("atlacian-");

            shootEffect = new MultiEffect(SvFx.aweExplosion,SvFx.aweExplosionDust, new WaveEffect(){{
                lifetime = 10f;
                strokeFrom = 3f;
                strokeTo = 0f;
                sizeTo = range;
            }});

            shootType = new ExplosionBulletType(30f,range) {{
                collidesAir = false;
                buildingDamageMultiplier = 1.1f;
                ammoMultiplier = 1f;
                speed = 0;
                lifetime = 1f;

                lightningType = new BulletType(0.0001f, 10f){{
                    lifetime = Fx.lightning.lifetime;
                    hitEffect = Fx.hitLancer;
                    despawnEffect = Fx.none;
                    status = StatusEffects.shocked;
                    statusDuration = 10f;
                    hittable = false;
                    lightColor = Color.white;
                    collidesAir = false;
                    buildingDamageMultiplier = 0.25f;
                }};

                killShooter = false;
            }};
            consumePower(3.3f);
            coolant = consume(new ConsumeLiquid(hydrogen, 20f / 60f));
        }};

        resonance = new PowerTurret("resonance") {{
            requirements(Category.turret,atl(), with(corallite, 185, iridium, 140, chrome,80));

            researchCost = with(corallite,800,iridium,500, chrome,300);

            size = 3;
            outlineColor = SvPal.outline;
            shootCone = 360f;
            targetGround = true;
            targetAir = true;
            fogRadius = 8;
            health = 540;
            shootSound = Sounds.cannon;
            range = 12*10f;
            velocityRnd = 0;
            reload = 160f;
            shake = 5f;
            chargeSound = Sounds.lasercharge;
            shootY = 0f;
            rotateSpeed = 0;
            drawer = new DrawTurret("atlacian-");

            shootEffect = new MultiEffect(SvFx.resonanceExplosion,SvFx.resonanceExplosionDust, new WaveEffect(){{
                lifetime = 10f;
                strokeFrom = 3f;
                strokeTo = 0f;
                sizeTo = range;
            }});

            float BPS = 60f/(reload+shoot.firstShotDelay+shoot.shots*shoot.shotDelay)*(shoot.shots);
            float mainDamage = RESONANCE_DPS/BPS*0.6f;
            float subDamage = RESONANCE_DPS/BPS*0.4f/8f;

            shootType = new ExplosionBulletType(mainDamage,range) {{
                collidesAir = true;
                buildingDamageMultiplier = 1.1f;
                ammoMultiplier = 1f;
                speed = 0;
                lifetime = 1f;
                killShooter = false;

                fragBullets = 8;
                fragSpread = 45f;
                fragRandomSpread = 5f;
                fragVelocityMin = 1f;

                lightningColor = Color.white;
                lightningDamage = 6;
                lightning = 8;
                lightningLength = 10;


                fragBullet = new BasicBulletType(6f, subDamage*0.7f) {{
                    width = 9f;
                    hitSize = 5f;
                    height = 15f;
                    pierce = true;
                    lifetime = 40f;
                    pierceBuilding = true;
                    hitColor = backColor = trailColor = Pal.lightishGray;
                    frontColor = Color.white;
                    trailWidth = 2.1f;
                    trailLength = 5;

                    weaveMag = 10f;
                    weaveScale = 18f;

                    lightningColor = Color.white;
                    lightningDamage = subDamage*0.3f/2f;
                    lightning = 2;
                    lightningLength = 10;

                    hitEffect = despawnEffect = new WaveEffect(){{
                        colorFrom = colorTo = Pal.lightishGray;
                        sizeTo = 4f;
                        strokeFrom = 4f;
                        lifetime = 10f;
                    }};
                    buildingDamageMultiplier = 0.8f;
                    homingPower = 0.08f;
                    homingRange = 80f;
                }};
            }};
            consumePower(3.3f);
            coolant = consume(new ConsumeLiquid(hydrogen, 20f / 60f));
            coolantMultiplier = 8f;
        }};
        inspiration = new TractorBeamTurret("inspiration"){{
            requirements(Category.turret,atl(BuildVisibility.hidden), with(corallite,200,iridium,150, chrome,35));

            researchCost = with(corallite,1400,iridium,1200, chrome,300);

            targetAir = true;
            targetGround = false;

            hasPower = true;
            hasLiquids = true;
            size = 2;
            force = 40f;
            scaledForce = 6f;
            range = 240f;
            damage = 6f/60f;
            scaledHealth = 160;
            rotateSpeed = 10;

            laserWidth = 0.25f;
            laserColor = SvPal.inspirationLaser;

            status = StatusEffects.slow;

            consumePower(3f);
            consumeLiquid(argon,0.3f);
            consumeLiquid(helium,0.4f);

            outlineColor = SvPal.outline;
            squareSprite = false;

            coolant = consume(new ConsumeLiquid(hydrogen, 20f / 60f));
        }};

        burden = new LiquidTurret("burden") {{
            outlineColor = SvPal.outline;
            requirements(Category.turret,atl(), with(clay,200,iridium,140, chrome,120));
            researchCost = with(clay,1000,iridium,600, chrome,400);
            ammo(
                    Liquids.water, new LiquidBulletType(Liquids.water){{
                        lifetime = 49f;
                        speed = 4f;
                        knockback = 2.1f;
                        puddleSize = 8f;
                        orbSize = 4f;
                        drag = 0.001f;
                        ammoMultiplier = 0.4f;
                        statusDuration = 60f * 4f;
                        damage = 0.2f;
                        layer = Layer.bullet - 2f;
                    }}
            );

            drawer = new DrawTurret("atlacian-");
            size = 3;
            reload = 3f;
            shoot.shots = 2;
            velocityRnd = 0.1f;
            inaccuracy = 4f;
            recoil = 1f;
            shootCone = 45f;
            liquidCapacity = 40f;
            shootEffect = Fx.shootLiquid;
            range = 190f;
            scaledHealth = 250;
            flags = EnumSet.of(BlockFlag.turret, BlockFlag.extinguisher);
        }};

        cascade = new ItemTurret("cascade") {{
            requirements(Category.turret,atl(), with(clay,300,iridium,150, chrome,50,spaclanium,80));

            researchCost = with(clay,1000,iridium,800, chrome,700,spaclanium,600);

            outlineColor = SvPal.outline;

            size = 3;
            shootCone = 360f;
            fogRadius = 6;
            targetGround = true;
            targetAir = true;
            squareSprite = false;



            reload = 40f;
            shoot = new ShootSpread(2,30);

            float BPS = 60f/(reload+shoot.firstShotDelay+shoot.shots*shoot.shotDelay)*(shoot.shots);
            float mainDamage = CASCADE_DPS/BPS*0.6f;
            float subDamage = CASCADE_DPS/BPS*0.4f/(5f*2f);

            ammo(
                    chrome, new BasicBulletType(6f, mainDamage){{
                sprite = "large-orb";
                inaccuracy = 1f;
                ammoMultiplier = 3f;
                ammoPerShot = 2;

                width = 12f;
                height = 12f;
                lifetime = 120f;
                shootEffect = SvFx.pulverize;
                smokeEffect = Fx.none;

                hitColor = backColor = trailColor = SvPal.chromiumLightish;
                frontColor = Color.white;

                fragBullets = intervalBullets = 5;
                fragVelocityMin = 1f;
                fragVelocityMax = 1f;
                bulletInterval = 40f;
                intervalDelay = 10f;
                fragBullet = intervalBullet = new LaserBulletType(subDamage){{
                    colors = new Color[]{SvPal.chromiumLightish.cpy().a(0.4f), SvPal.chromiumLightish, Color.white};
                    chargeEffect = new MultiEffect(Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin);

                    buildingDamageMultiplier = 0.25f;
                    hitEffect = Fx.hitLancer;
                    hitSize = 4;
                    lifetime = 16f;
                    drawSize = 400f;
                    collidesAir = false;
                    length = 5*8f;
                    ammoMultiplier = 1f;
                    pierceCap = 4;

                    status = StatusEffects.electrified;
                }};

                homingPower = 0.18f;
                homingRange = 16f;

                trailRotation = true;
                trailEffect = Fx.disperseTrail;
                trailInterval = 3f;
                trailWidth = 6f;
                trailLength = 6;
                trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);

                hitEffect = despawnEffect = Fx.hitBulletColor;
            }});

            smokeEffect = Fx.shootSmokeSmite;
            drawer = new DrawTurret("atlacian-"){{
                var heatp = PartProgress.warmup.blend(p -> Mathf.absin(2f, 1f) * p.warmup, 0.2f);
                var haloProgress = PartProgress.warmup.delay(0.5f);
                float haloY = -10;
                parts.addAll(new RegionPart("-blade"){{
                                 progress = PartProgress.warmup;
                                 heatProgress = PartProgress.warmup;
                                 heatColor = SvPal.turretHeatGlow;
                    mirror = true;
                    under = true;
                                 moveX = 2f;
                                 moveRot = -7f;
                                 moves.add(new PartMove(PartProgress.warmup, 0f, -2f, 3f));
                             }},

                new RegionPart("-barrel"){{
                    progress = PartProgress.warmup;
                    heatProgress = PartProgress.recoil;
                    under = true;
                    moveY = -4f;
                    heatColor = SvPal.turretHeatGlow;
                }},

                new RegionPart("-mid"){{
                    heatProgress = heatp;
                    progress = PartProgress.warmup;
                    heatColor = SvPal.turretHeatGlow;
                    moveY = -8f;
                    mirror = false;
                    under = true;
                }},

                new ShapePart(){{
                    progress = PartProgress.warmup.delay(0.2f);
                    color = Pal.accent;
                    circle = true;
                    hollow = true;
                    stroke = 0f;
                    strokeTo = 2f;
                    radius = 6f;
                    layer = Layer.effect;
                    y = haloY;
                    rotateSpeed = 1f;
                }},

                new HaloPart(){{
                    progress = haloProgress;
                    color = Pal.accent;
                    layer = Layer.effect;
                    y = haloY;
                    haloRotateSpeed = -1f;

                    shapes = 4;
                    shapeRotation = 180f;
                    triLength = 0f;
                    triLengthTo = 2f;
                    haloRotation = 45f;
                    haloRadius = 2f;
                    tri = true;
                    radius = 4f;
                }},

                new HaloPart(){{
                    progress = haloProgress;
                    color = Pal.accent;
                    layer = Layer.effect;
                    y = haloY;
                    haloRotateSpeed = -1f;

                    shapes = 4;
                    shapeRotation = 180f;
                    triLength = 0f;
                    triLengthTo = 2f;
                    haloRotation = 45f;
                    haloRadius = 10f;
                    tri = true;
                    radius = 4f;
                }},

                new HaloPart(){{
                    progress = haloProgress;
                    color = Pal.accent;
                    layer = Layer.effect;
                    y = haloY;

                    shapes = 4;
                    triLength = 0f;
                    triLengthTo = 4f;
                    haloRadius = 10f;
                    haloRotation = 0;
                    shapeRotation = 90f;
                    tri = true;
                    radius = 4f;
                }});
            }};

            shootSound = Sounds.railgun;
            shootY = 8f;

            recoil = 0.5f;
            range = 180f;
            scaledHealth = 200;

            coolantMultiplier = 1.2f;
            coolant = consume(new ConsumeLiquid(hydrogen, 20f / 60f));

            limitRange(6);
        }};

        spectrum = new ItemTurret("spectrum") {{
            requirements(Category.turret,atl(),with(spaclanium,1)); //TODO: reqs

            coolantMultiplier = 1.1f;
            coolant = consume(new ConsumeLiquid(hydrogen, 20f / 60f));

            outlineColor = SvPal.outline;

            size = 4;
            rotateSpeed = 1.4f;
            shootSound = Sounds.mediumCannon;
            ammoPerShot = 2;
            maxAmmo = ammoPerShot * 3;

            reload = 160f;
            shoot = new ShootMulti() {{
                shots = 4;
                shotDelay = 10f;
                source = new ShootHelix() {{
                    mag = 2f;
                    scl = 10f;
                }};
                dest = new ShootPattern[] {
                        new ShootSpread() {{
                            spread = -30f;
                            shots = 2;
                        }}
                };
            }};

            float BPS = 60f/(reload+shoot.firstShotDelay+shoot.shots*shoot.shotDelay)*(shoot.shots);
            float mainDamage = SPECTRUM_DPS/BPS;

            drawer = new DrawTurret("atlacian-") {{
                parts.addAll(
                        new FlarePart(){{
                            progress = PartProgress.warmup.mul(PartProgress.reload.min(PartProgress.constant(0.2f)).inv()).delay(0.2f).curve(Interp.smoother);
                            color1 = Pal.redLight;
                            layer = Layer.effect;
                            y = 4f;
                            radius = 0f;
                            radiusTo = 8f;
                            spinSpeed = 1f;
                            followRotation = true;
                        }},
                        new RegionPart("-blade") {{
                            heatProgress = progress = PartProgress.warmup;
                            mirror = true;
                            under = true;
                            moveX = 2f;
                            moveY = -2f;
                            moveRot = -5f;
                            moves.add(new PartMove(PartProgress.heat,0,-2f,5f));
                            moves.add(new PartMove(PartProgress.smoothReload, 0f, -2f, 3f));
                        }},
                        new RegionPart("-barrage") {{
                            heatProgress = progress = PartProgress.recoil;
                            moveX = 0f; moveY = 0f;
                            moveRot = -10f;
                            mirror = true;
                            under = true;
                            moves.add(new PartMove(PartProgress.warmup,2f,-2f,-5f));
                            moves.add(new PartMove(PartProgress.heat,0,-2f,5f));
                            moves.add(new PartMove(PartProgress.smoothReload, 0f, -2f, 3f));
                        }},
                        new RegionPart("-mid") {{
                            mirror = false;
                            moves.add(new PartMove(PartProgress.heat,0,-2f,0f));
                            moves.add(new PartMove(PartProgress.smoothReload, 0f, -2f, 0f));
                        }},
                        new RegionPart("-wing") {{
                            heatProgress = progress = PartProgress.warmup;
                            moveRot = 10f;
                            moveY = -4f;
                            mirror = true;
                            under = true;
                            moves.add(new PartMove(PartProgress.heat,0,-2f,5f));
                            moves.add(new PartMove(PartProgress.smoothReload, 0f, -2f, 3f));
                        }},
                        new RegionPart("-bottom") {{
                            mirror = false;
                            moves.add(new PartMove(PartProgress.heat,0,-2f,0f));
                            moves.add(new PartMove(PartProgress.smoothReload, 0f, -2f, 0f));
                        }}
                );
            }};
            moveWhileCharging = false;
            targetAir = false;
            targetGround = true;
            minWarmup = 0.5f;
            inaccuracy = 0f;
            predictTarget = false;
            range = 180f;
            shootY = 4f;
            ammo(nitride,new ArtilleryBulletType(2.5f, mainDamage, "shell") {{
                hitEffect = new MultiEffect(Fx.titanExplosion, Fx.titanSmoke);
                collidesAir = true;
                collidesGround = true;
                pierce = true;
                pierceCap = 1;
                despawnEffect = Fx.none;
                knockback = 2f;
                lifetime = 140f;
                height = 19f;
                width = 17f;
                splashDamageRadius = 65f;
                splashDamage = mainDamage;
                scaledSplashDamage = true;
                backColor = hitColor = trailColor = Color.valueOf("ea8878").lerp(Pal.redLight, 0.5f);
                frontColor = Color.white;
                ammoMultiplier = 1f;
                hitSound = Sounds.titanExplosion;

                status = StatusEffects.blasted;

                knockback = 5f;

                trailLength = 32;
                trailWidth = 3.35f;
                trailSinScl = 2.5f;
                trailSinMag = 0.5f;
                trailEffect = Fx.none;
                despawnShake = 7f;

                shootEffect = Fx.shootTitan;
                smokeEffect = Fx.shootSmokeTitan;
                trailInterp = Interp.slope;
                shrinkX = 0.2f;
                shrinkY = 0.1f;
                buildingDamageMultiplier = 0.3f;
            }});
        }};

        upsurge = new ItemTurret("upsurge") {{
            requirements(Category.turret,atl(),with(spaclanium,1)); //TODO: reqs

            coolantMultiplier = 1.1f;
            coolant = consume(new ConsumeLiquid(hydrogen, 20f / 60f));

            outlineColor = SvPal.outline;

            size = 4;
            rotateSpeed = 1.4f;
            shootSound = Sounds.shotgun;

            ammoPerShot = 2;
            maxAmmo = ammoPerShot * 3;
            drawer = new DrawTurret("atlacian-") {{
                parts.addAll(
                        new RegionPart("-blade") {{
                            mirror = true;
                            under = true;
                            progress = PartProgress.recoil;
                            moveY = -1f;
                            moveRot = -10f;
                        }},
                        new RegionPart("-mid") {{
                            mirror = false;
                        }},
                        new RegionPart("-wing") {{
                            mirror = true;
                            under = true;
                            progress = PartProgress.warmup;
                            moveY = 2f;
                            moveRot = -5f;
                        }}
                );
            }};
            reload = 70f;
            moveWhileCharging = true;
            targetAir = true;
            targetGround = false;
            minWarmup = 0f;
            shoot = new ShootMulti() {{
                shots = 40;
                source = new ShootAlternate(){{
                    spread = 4.7f;
                    shots = 4;
                    barrels = 4;
                }};
                dest = new ShootPattern[] {new ShootUpsurge() {{
                    shotDelay = 5f;
                    shots = 10;
                }}};
            }};
            float BPS = 60f/reload*(4f*10f);
            float mainDamage = UPSURGE_DPS/BPS;

            inaccuracy = 0f;
            predictTarget = false;
            shootY = 4f;
            ammo(phosphide,new BasicBulletType(){{
                damage = mainDamage;
                speed = 8.5f;
                width = height = 16;
                shrinkY = 0.3f;

                pierce = true;
                pierceCap = 3;

                backSprite = "large-bomb-back";
                sprite = "large-bomb";
                velocityRnd = 0.11f;
                collidesGround = false;
                collidesTiles = false;
                shootEffect = Fx.shootBig2;
                smokeEffect = Fx.shootSmokeDisperse;
                frontColor = SvPal.phosphide;
                backColor = trailColor = hitColor = SvPal.phosphide;
                trailChance = 0.44f;
                ammoMultiplier = 2f;

                lifetime = 21f;
                rotationOffset = 90f;
                trailRotation = true;
                trailEffect = SvFx.upsurgeTrail;
                inaccuracy = 1f;

                hitEffect = despawnEffect = Fx.hitBulletColor;
            }});

            range = 336f;
        }};

        resistance = new PowerRingTurret("resistance") {{
            requirements(Category.turret,atl(),with(spaclanium,1));
            range = 180*tilesize;
            fogRadius = 15;
            spacing = 160;
            fogRadiusMultiplier = 0.4F;

            bulletType = new ExplosionBulletType(RESIST_DPS*0.7f,4*tilesize) {{
                buildingDamageMultiplier = 0.4f;
                ammoMultiplier = 1f;
                speed = 0;
                lifetime = 1f;

                despawnShake = 10f;
                hitShake = 10f;

                pierce = true;
                pierceBuilding = true;
                pierceArmor = true;
                hitSound = despawnSound = SvSounds.flashExplosion;

                fragBullet = new BasicBulletType(6f, RESIST_DPS*0.2f/10f,"shell") {{
                    hitSound = despawnSound = Sounds.bang;
                    width = 9f;
                    hitSize = 5f;
                    height = 15f;
                    pierce = true;
                    lifetime = 35f;
                    weaveMag = 10f;
                    weaveScale = 18f;
                    pierceBuilding = true;
                    hitColor = backColor = trailColor = SvPal.teslaCharge;
                    frontColor = Color.white;
                    trailLength = 32;
                    trailWidth = 3.35f;
                    trailSinScl = 2.5f;
                    trailSinMag = 0.5f;
                    trailEffect = Fx.none;

                    status = StatusEffects.slow;
                    statusDuration = 60f;
                    hitEffect = despawnEffect = new WaveEffect(){{
                        colorFrom = colorTo = SvPal.teslaCharge;
                        sizeTo = 4f;
                        strokeFrom = 4f;
                        lifetime = 10f;
                    }};
                    buildingDamageMultiplier = 0.3f;
                    homingPower = 0.2f;
                    homingRange = 30f;
                }};

                fragRandomSpread = 0f;
                fragSpread = 30f;
                fragBullets = 10;
                fragVelocityMin = 1f;

                lightningColor = SvPal.teslaCharge;
                lightningDamage = RESIST_DPS*0.1f/16f;
                lightning = 16;
                lightningLength = 10;

                killShooter = false;
            }};
            consumePower(3.3f);
            coolant = (ConsumeLiquid) consume(new ConsumeLiquid(hydrogen, 120f / 60f)).boost();

            liquidCapacity = 300f;

            size = 5;

            cooldownTime = 120f;
            recoilTime = 50f;
            recoilPow = 2;
            recoil = 1f;

            outlineColor = SvPal.outline;
            heatColor = SvPal.teslaCharge;

            drawer = new DrawRingTurret("atlacian-") {{
                parts.addAll(
                    new RegionPart("-blade") {{
                        mirror = true;
                        x = 0;
                        y = 0;

                        heatProgress = progress = PartProgress.warmup;
                        moveRot = -25f;

                        moves.add(new PartMove() {{progress = PartProgress.recoil.inv(); moveY = -5f;}});
                        moves.add(new PartMove() {{progress = PartProgress.heat; moveRot = -10;}});
                    }},
                    new RegionPart("-mid") {{
                        mirror = false;
                        x = 0;
                        y = 0;
                        heatProgress = progress = PartProgress.warmup;
                        moveY = -2f;
                        moves.add(new PartMove() {{progress = PartProgress.recoil.inv(); moveY = -5f;}});
                    }},
                    new RegionPart("-arc") {{
                        mirror = false;
                        x = 0;
                        y = 0;
                        heatProgress = progress = PartProgress.warmup;
                        moveY = -4f;
                        moves.add(new PartMove() {{progress = PartProgress.recoil.inv(); moveY = -5f;}});
                    }},
                    new FlarePart(){{
                        progress = PartProgress.warmup.delay(0.09f);
                        color1 = SvPal.teslaCharge;
                        layer = Layer.effect;
                        stroke = 3f;
                        radius = 0f;
                        radiusTo = 8f;
                        y = 7f;
                        x = 0f;
                        followRotation = true;
                        sides = 6;
                    }},

                    new ShapePart(){{
                        progress = PartProgress.warmup.delay(0.08f);
                        color = SvPal.teslaCharge;
                        layer = Layer.effect;
                        stroke = 2f;
                        radius = 0f;
                        radiusTo = 8f;
                        y = 7f;
                        x = 0f;
                        rotateSpeed = 2f;
                        hollow = true;
                        sides = 6;
                    }},
                    new ShapePart(){{
                        progress = PartProgress.warmup.delay(0.05f);
                        color = SvPal.teslaCharge;
                        layer = Layer.effect;
                        stroke = 2f;
                        radius = 0f;
                        radiusTo = 12f;
                        y = 7f;
                        x = 0f;
                        rotateSpeed = -2f;
                        hollow = true;
                        sides = 6;
                    }}
                );
            }};

            minRingCount = 1;
            boostRingCount = 2;

            ringChargeTime = 140f;
            ringMovementSpeed = 1f;
            ringRadius = 24f;
            ringAccuracy = 0.8f;

            consumePower(1f);
            consumeItem(tugSheet,2);
        }};

        clayWall = new Wall("clay-wall"){{
            requirements(Category.defense,atl(), with(clay, 6));

            researchCost = with(clay,15);

            health = (int) CLAY_WALL_HP;
            envDisabled |= Env.scorching;
        }};

        clayWallLarge = new Wall("clay-wall-large"){{
            requirements(Category.defense,atl(), mult(clayWall.requirements, 4));

            researchCost = with(clay,100);

            health = (int) CLAY_WALL_LARGE_HP;
            size = 2;
            envDisabled |= Env.scorching;
        }};

        phosphideWallLarge = new PhosphideWall("phosphide-wall-large"){{
            requirements(Category.defense,atl(), with(phosphide,24));

            researchCost = with(phosphide,100);

            health = (int) PHOSPHIDE_WALL_LARGE_HP;
            size = 2;
            envDisabled |= Env.scorching;

            hitGenerationTime = 1.5f;
            hitPower_pt = 80/10f;

            armor = 4f;

            hasPower = true;
            outputsPower = true;
            consumesPower = false;
            conductivePower = true;
        }};

        tugSheetWall = new ShieldWall("tug-sheet-wall") {{
            requirements(Category.defense,atl(), with(tugSheet, 6));
            consumePower(3f / 60f);

            researchCost = with(tugSheet,150);

            glowColor = SvPal.tugSheetGlow.a(0.5f);
            glowMag = 0.8f;
            glowScl = 12f;

            hasPower = true;
            outputsPower = false;
            consumesPower = true;
            conductivePower = true;
            chanceDeflect = 10f;
            armor = 7f;
            health = (int) TUGSHEET_WALL_HP;
            envDisabled |= Env.scorching;
        }};

        tugSheetWallLarge = new ShieldWall("tug-sheet-wall-large") {{
            requirements(Category.defense,atl(), mult(tugSheetWall.requirements, 4));
            researchCost = with(tugSheet,600);

            consumePower(3*4f / 60f);

            glowColor = SvPal.tugSheetGlow.a(0.5f);
            glowMag = 0.8f;
            glowScl = 12f;

            hasPower = true;
            outputsPower = false;
            consumesPower = true;
            conductivePower = true;
            chanceDeflect = 20f;
            armor = 8f;
            health = (int) TUGSHEET_WALL_LARGE_HP;
            envDisabled |= Env.scorching;
            size = 2;
        }};

        coreDecoder = new CoreDecoder("core-decoder") {{
            requirements(Category.effect,atl(),with(iridium,150, chrome,150));

            researchCost = with(iridium,800, chrome,500,corallite,400);

            health = 2560;
            priority = TargetPriority.core;
            fogRadius = 16;
            size = 2;
            consumePower(6f);
            consumeLiquid(propane,0.95f);
            destructible = true;
            envDisabled |= Env.scorching;
        }};

        /*coreDecrypter = new CoreDecoder("core-decrypter") {{
            requirements(Category.effect,atl(),with(iridium,400,chromium,300,quartzFiber,250));

            researchCost = with(iridium,1600,chromium,1000,quartzFiber,820);

            health = 2560;
            priority = TargetPriority.core;
            fogRadius = 16;
            size = 3;
            consumePower(18f);
            destructible = true;
            envDisabled |= Env.scorching;
            minAttempts = 50;
            frequency = 80;
            hackChance = 0.02f;
        }};*/

        regenProjector = new MendProjector("regen-projector"){{
            requirements(Category.effect,atl(), with(spaclanium, 60, clay, 80, iridium, 10));
            researchCost = with(spaclanium,500,clay,280,iridium,100);

            consumePower(0.3f);
            consumeLiquid(hydrogen,13/60f);
            squareSprite = false;

            size = 2;
            reload = 100f;
            range = 48f*2;
            healPercent = 5f;
            phaseBoost = 4f;
            phaseRangeBoost = 20f;
            health = 400;
        }};

        overdriveProjector = new OverdriveSquareProjector("overdrive-projector"){{
            requirements(Category.effect, with(corallite,100,iridium,80, chrome,20,quartzFiber,10));
            consumePower(3.50f);
            researchCost = with(corallite,1000,iridium,800, chrome,1000,quartzFiber,100);
            size = 2;
            consumeItem(quartzFiber).boost();
        }};

        //exploration
        buoy = new Buoy("buoy") {{
            requirements(Category.effect,atl(BuildVisibility.fogOnly), with(spaclanium,20));
            alwaysUnlocked = true;
            fogRadius = 32;
            envDisabled |= Env.scorching;
            destructible = true;

            priority = TargetPriority.wall;
            health = 120;

            researchCost = with(spaclanium,8);
        }};

        tower = new Buoy("tower") {{
            requirements(Category.effect,atl(BuildVisibility.fogOnly), with(chrome,10,clay,10));
            fogRadius = 40;

            envDisabled |= Env.scorching;
            destructible = true;
            isWater = false;
            outlineIcon = true;

            discoveryTime *= 1.5f;

            consumeLiquid(hydrogen,4/60f);

            priority = TargetPriority.wall;
            health = 360;

            researchCost = with(chrome,50,clay,50);
        }};

        beacon = new Beacon("beacon") {{
            requirements(Category.effect,atl(BuildVisibility.fogOnly), with(spaclanium,300,clay, 50,sulfur,200,iridium,300));
            fogRadius = 75;
            size = 3;
            envDisabled |= Env.scorching;
            destructible = true;
            squareSprite = false;

            super.length = 6f;
            super.repairSpeed = 1f;
            super.repairRadius = 160f;
            super.powerUse = 5f;
            super.beamWidth = 1.5f;
            super.pulseRadius = 10f;
            super.coolantUse = 0.16f;
            super.coolantMultiplier = 2f;

            researchCost = with(spaclanium,500,clay,100,sulfur,300);

            placeEffect = Fx.healWaveDynamic;
            health = 450;


            acceptCoolant = false;
            heal = consumePower(0.15f);
            discover = consumeLiquid(argon,32/60f);
        }};

        //liquids
        lowTierPump = new Pump("lead-pump") {{
            requirements(Category.liquid,atl(), with(corallite, 8));

            squareSprite = false;
            envDisabled |= Env.scorching;
            pumpAmount = 15f / 60f;

            researchCost = with(corallite,5);
        }};

        centrifugalPump = new Pump("centrifugal-pump") {{
            requirements(Category.liquid,atl(), with(spaclanium, 32, clay, 30, iridium, 10));

            researchCost = with(spaclanium,300,clay,320,iridium,400);

            size = 2;
            squareSprite = false;
            envDisabled |= Env.scorching;
            pumpAmount =20f / 60f;
            consumePower(2.5f/60f);
        }};

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

        highPressureConduit = new Conduit("high-pressure-conduit") {{
            requirements(Category.liquid,atl(), with(chrome, 1, clay, 1));

            researchCost = with(clay,400, chrome,100);

            envDisabled |= Env.scorching;
            botColor = SvPal.veryDarkViolet;
            liquidCapacity = 16f;
            liquidPressure = 1.225f;

            health = 80;
        }};

        conduitBridge = new DirectionLiquidBridge("bridge-conduit"){{
            requirements(Category.liquid,atl(), with(corallite, 4, clay, 8));

            researchCost = with(corallite,80,clay,40);
            ((Conduit) fortifiedConduit).rotBridgeReplacement = this;
            ((Conduit) highPressureConduit).rotBridgeReplacement = this;
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

        waterDiffuser = new Diffuser("water-diffuser") {{
            requirements(Category.production, atl(), with(corallite, 10));
            size = 2;
            craftTime = 30f;
            itemCapacity = 50;

            researchCost = with(corallite,5);

            squareSprite = false;

            consumeLiquid(water, 6/60f);
            envDisabled |= Env.scorching;
            results = new Item[] {
                    spaclanium,spaclanium,
                    corallite,corallite,
                    sulfur,
                    finesand
            };
        }};

        waterSifter = new WaterSifter("water-sifter") {{
            requirements(Category.production, atl(), with(spaclanium, 50, corallite, 60, clay, 30));
            harvestTime = 80f;
            itemCapacity = 50;
            researchCost = with(spaclanium,100,corallite,60,clay,50);

            consumePower(4/60f);

            size = 2;
            envDisabled |= Env.scorching;
            squareSprite = false;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawLiquidRegion(water),
            new DrawRegion("-top")
            );
        }};

        //energy
        energyDock = new EnergyDock("energy-dock") {{
            requirements(Category.power,atl(),with(iridium,3,corallite, 2));
            researchCost = with(iridium,5,corallite,10);

            size = 2;
            maxNodes = 10;
            range = 10;
            transferTime = 40;
            squareSprite = false;
        }};

        energyDistributor = new EnergyCross("energy-distributor") {{
            requirements(Category.power,atl(), with(iridium, 18));
            researchCost = with(iridium,30);
            consumesPower = outputsPower = true;
            health = 90;
            range = 5;
            squareSprite = false;

            consumePowerBuffered(1000f);
        }};

        accumulator = new Battery("accumulator"){{
            requirements(Category.power,atl(BuildVisibility.editorOnly), with(iridium, 5, spaclanium, 20));
            consumePowerBuffered(4000f);

            researchCost = with(iridium,80,spaclanium,100);

            baseExplosiveness = 1f;
        }};

        largeAccumulator = new Battery("large-accumulator"){{
            requirements(Category.power,atl(BuildVisibility.hidden), mult(accumulator.requirements,4));
            consumePowerBuffered(4000f*5);

            researchCost = with(iridium,800,spaclanium,700);

            baseExplosiveness = 3f;
            drawer = new DrawMulti(new DrawDefault(), new DrawEnergyGlow());
            size = 2;
        }};
        spaclaniumHydrolyzer = new ConsumeGenerator("spaclanium-hydrolyzer") {{
            requirements(Category.power,atl(), with(corallite, 20, clay, 30, iridium, 25));

            researchCost = with(corallite,200,clay,150,iridium,100);

            powerProduction = 5.4f;
            itemDuration = 120f;
            envDisabled |= Env.scorching;

            ambientSound = Sounds.extractLoop;
            ambientSoundVolume = 0.03f;

            size = 2;

            consumeEffect = Fx.generatespark;
            generateEffect = Fx.pulverizeSmall;

            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawGlowRegion(){{
                alpha = 0.75f;
                color = Color.violet;
            }}
            );

            consumeItem(spaclanium);
            consumeLiquid(water,0.5f);
        }};

        windTurbine = new WindTurbine("wind-turbine") {{
            requirements(Category.power,atl(),with(corallite,60,clay,15,iridium,30));

            researchCost = with(corallite,600,clay,300,iridium,300);

            ambientSound = Sounds.wind;
            ambientSoundVolume = 0.05f;

            powerProduction = 0.2f;
            size = 2;
        }};
        hydrocarbonicGenerator = new ConsumeGenerator("hydrocarbonic-generator") {{
            requirements(Category.power,atl(),with(corallite,300,clay,100,iridium,200, chrome,10));

            researchCost = with(corallite,700,clay,350,iridium,350, chrome,50);

            size = 2;

            ambientSound = Sounds.glow;
            ambientSoundVolume = 0.05f;

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawWarmupRegion(),
                    new DrawGlowRegion()
            );

            powerProduction = 24f;
            itemDuration = 60f;
            envDisabled |= Env.scorching;
            consumeLiquid(propane,0.65f);
        }};

        chromiumReactor = new ChromiumReactor("chromium-reactor"){{
            requirements(Category.power,atl(),with(chrome, 300, tugSheet, 50, corallite, 80, iridium, 100));

            researchCost = with(chrome,1800,tugSheet,500,corallite,3000,iridium,1950);

            size = 3;
            squareSprite = false;
            health = 900;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.24f;
            powerProduction = 60;

            consumePower(12f);
            consumeItem(chrome,2);
            consumeLiquid(hydrogen, heating / coolantPower).update(false);
        }};

        //storage

        vault = new StorageBlock("vault"){
            {
                requirements(Category.effect,atl(), with(chrome, 40, iridium, 85));

                researchCost = with(chrome,300,iridium,300);

                size = 2;
                itemCapacity = 80;
                scaledHealth = 55;
                squareSprite = false;
            }

            @Override
            protected TextureRegion[] icons() {
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };
        largeVault = new StorageBlock("large-vault"){
            {
                requirements(Category.effect,atl(), with(chrome, 100, iridium, 145));
                researchCost = with(chrome,1000,iridium,1500);
                size = 3;
                itemCapacity = 300;
                scaledHealth = 155;
                squareSprite = false;
            }

            @Override
            protected TextureRegion[] icons() {
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
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
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };;

        liquidContainer = new LiquidRouter("liquid-container"){{
            requirements(Category.liquid,atl(), with(corallite, 30, clay, 35));

            researchCost = with(corallite,540,clay,350);

            liquidCapacity = 700f;
            size = 2;
            liquidPadding = 3f / 4f;

            solid = true;
            squareSprite = false;
        }};

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

        corePuffer = new SubvoyageCoreBlock("core-puffer"){{
            requirements(Category.effect,atl(), with(spaclanium,600,corallite,600,clay,300,sulfur,300));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.editorOnly;
            isFirstTier = true;
            unitType = shift;
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
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };

        coreShore = new SubvoyageCoreBlock("core-shore"){{
            requirements(Category.effect,atl(), with(spaclanium,1500,corallite,1200, chrome,1300,iridium,800));

            researchCost = with(spaclanium,3000,corallite,3000, chrome,1500,iridium,1500);

            unitType = distort;
            health = 12000;
            itemCapacity = 8000;
            size = 5;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 18;

            placeableLiquid = true;
            bannedItems.addAll(crude);
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };

        coreReef = new SubvoyageCoreBlock("core-reef"){{
            requirements(Category.effect,atl(), with(spaclanium,3500,corallite,2200, chrome,1300,iridium,1000,quartzFiber,1000,tugSheet,800));

            researchCost = with(spaclanium,8000,corallite,8000, chrome,5000,iridium,3000,quartzFiber,1000,tugSheet,1000);
            unitType = commute;
            health = 24000;
            itemCapacity = 12000;
            size = 6;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 30;

            placeableLiquid = true;
            bannedItems.addAll(crude);
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };

        //transport

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

        highPressureDuct = new Duct("high-pressure-duct") {{
            requirements(Category.distribution,atl(),with(chrome,1,iridium,1,corallite,1));
            researchCost = with(chrome,500,corallite,900);
            health = 180;
            speed = 3.2f;
            envDisabled |= Env.scorching;
        }};

        ductBridge = new DuctBridge("duct-bridge") {{
            requirements(Category.distribution,atl(), with(corallite, 4,spaclanium,2));
            researchCost = with(corallite, 16, spaclanium, 4);

            ((Duct) duct).bridgeReplacement = this;
            ((Duct) highPressureDuct).bridgeReplacement = this;

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
            requirements(Category.distribution,atl(), with(corallite, 4, spaclanium, 4));
            researchCost = with(corallite,320,spaclanium,70);
            buildCostMultiplier = 3f;
            size = 2;
            squareSprite = false;
        }};

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

        /*
        unitCargoLoader = new UnitCargoLoader("unit-cargo-loader"){{
            requirements(Category.distribution, with(Items.silicon, 80, Items.surgeAlloy, 50, Items.oxide, 20));

            size = 3;
            buildTime = 60f * 8f;

            consumePower(8f / 60f);

            //intentionally set absurdly high to make this block not overpowered
            consumeLiquid(Liquids.nitrogen, 10f / 60f);

            itemCapacity = 200;
            researchCost = with(Items.silicon, 2500, Items.surgeAlloy, 20, Items.oxide, 30);
        }};

        unitCargoUnloadPoint = new UnitCargoUnloadPoint("unit-cargo-unload-point"){{
            requirements(Category.distribution, with(Items.silicon, 60, Items.tungsten, 60));

            size = 2;

            itemCapacity = 100;

            researchCost = with(Items.silicon, 3000, Items.oxide, 20);
        }};
         */

        shipCargoStation = new UnitCargoLoader("ship-cargo-station"){{
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
        }};


        //crafters
        ceramicBurner = new GenericCrafter("ceramic-burner") {{
            requirements(Category.crafting,atl(),with(spaclanium,30,corallite,70, finesand,30));
            craftEffect = SvFx.smokePuff;
            craftTime = 60f*2;

            researchCost = with(spaclanium,100,corallite,60, finesand,10);

            hasItems = true;
            hasLiquids = true;
            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawBurner(),
            new DrawRegion("-top"),
            new DrawHeatGlow()
            );

            outputItem = new ItemStack(clay,2);
            itemCapacity = 3;
            size = 2;
            envDisabled |= Env.scorching;
            consumeLiquid(water,8/60f);
            consumeItem(finesand, 1);
        }};
        terracottaBlaster = new GenericCrafter("terracotta-blaster") {{
            requirements(Category.crafting,atl(),with(spaclanium,100,corallite,200, finesand,120,iridium,40));

            researchCost = with(spaclanium,1000,corallite,1600, finesand,700,iridium,780);

            craftEffect = SvFx.smokePuff;
            craftTime = 60f*1.3f;

            hasItems = true;
            hasLiquids = true;
            squareSprite = false;

            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawBurner(),
            new DrawRegion("-top"),
            new DrawHeatGlow()
            );
            outputItem = new ItemStack(clay,10);
            itemCapacity = 12;

            size = 3;
            envDisabled |= Env.scorching;
            consumeLiquid(water, 0.8f);
            consumeItem(finesand,3);
            consumeLiquid(propane, 0.5f);
            consumePower(2.3f);
        }};

        circularCrusher = new CircCrusher("circular-crusher") {{
            requirements(Category.crafting,atl(), with(corallite,120,iridium,20));
            size = 3;
            researchCost = with(corallite,600,iridium,400);

            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.crawlDust;
            craftTime = 40f;
            envDisabled |= Env.scorching;

            consumeItem(crude,1);
            consumePower(0.6f);

            outputItem = new ItemStack(finesand,3);
            hasItems = true;
            hasPower = true;
        }};

        distiller = new GenericCrafter("distiller"){{
            requirements(Category.crafting, atl(), with(spaclanium, 100, corallite, 60));
            outputLiquid = new LiquidStack(water, 6/60f);
            craftTime = 140f;

            researchCost = with(spaclanium, 700, corallite, 800);
            itemCapacity = 30;
            size = 2;

            squareSprite = false;
            hasItems = true;
            hasLiquids = true;
            envDisabled |= Env.scorching;
            drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawLiquidTile(hardWater, 2f),
            new DrawBubbles(Color.valueOf("7693e3")){{
                sides = 10;
                recurrence = 3f;
                spread = 6;
                radius = 1.5f;
                amount = 20;
            }},
            new DrawBlurSpin("-rotator", 6f),
            new DrawRegion("-top"),
            new DrawDefault()
            );

            consumeLiquid(hardWater, 8/60f);
        }};

        waterMetallizer = new GenericCrafter("water-metallizer") {{
            requirements(Category.crafting,atl(), with(spaclanium,100,corallite,60));
            outputLiquid = new LiquidStack(hydrogen, 18f/60f);
            craftTime = 60f;

            researchCost = with(spaclanium,700,corallite,800);

            itemCapacity = 30;
            size = 2;

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
            envDisabled |= Env.scorching;
            drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawMixer(),
            new DrawDefault(),
            new DrawRegion("-top")
            );

            consumeLiquid(water,12f/60f);
            consumeItem(corallite,1);
        }};

        poweredEnhancer = new GenericCrafter("powered-enhancer") {{
            requirements(Category.crafting,atl(), with(chrome, 200, iridium, 100));
            craftTime = 50f;

            researchCost = with(chrome,1600,iridium,3200);

            itemCapacity = 30;
            size = 3;

            regionRotated1 = 3;
            outputLiquids = LiquidStack.with(hydrogen, 38/60f, hydrogen, 12/60f);
            liquidOutputDirections = new int[]{1, 3};

            rotate = true;
            invertFlip = true;

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
            envDisabled |= Env.scorching;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawMixer(),
                    new DrawRegion(),
                    new DrawRegion("-top"),
                    new DrawLiquidOutputs()
            );

            consumeLiquid(water,12/60f);
            consumeItem(corallite, 1);
            consumePower(0.8f);
        }};

        argonCentrifuge = new GenericCrafter("argon-centrifuge") {{
            requirements(Category.crafting,atl(), with(spaclanium,60,corallite,200,sulfur,30));

            researchCost = with(spaclanium,800,corallite,700,sulfur,900);

            itemCapacity = 10;
            size = 2;
            craftEffect = Fx.smokePuff;
            craftTime = 60f;
            envDisabled |= Env.scorching;

            consumeItem(sulfur,1);
            consumeItem(corallite, 2);
            consumePower(0.8f);
            outputLiquid = new LiquidStack(argon, 36/60f);
            hasLiquids = true;
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawLiquidRegion(argon)
            );
        }};

        argonCondenser = new GenericCrafter("argon-condenser") {{
            requirements(Category.crafting,atl(), with(chrome,120,iridium,60,quartzFiber,10));

            researchCost = with(chrome,3500,iridium,5310,quartzFiber,3210);

            squareSprite = false;
            itemCapacity = 10;
            size = 3;
            craftEffect = Fx.none;
            craftTime = 20f;
            envDisabled |= Env.scorching;

            consumeItem(sulfur,1);
            consumeItem(corallite, 2);
            consumeLiquid(helium,0.1f);
            consumePower(0.6f);
            outputLiquid = new LiquidStack(argon, 134/60f);
            hasLiquids = true;
            drawer = new DrawMulti(
                    new DrawDefault(),
            new DrawLiquidRegion(argon),
            new DrawArcSmelt(){{
                flameColor = SvPal.argonFlame;
                midColor = SvPal.argonMidSmelt;
                flameRad = 1.0F;
                circleSpace = 1.0F;
                flameRadiusScl = 3.0F;
                flameRadiusMag = 0.3F;
                circleStroke = 1.25F;
                particles = 16;
                particleLife = 30.0F;
                particleRad = 5.2F;
                particleStroke = 0.8F;
                particleLen = 2.25F;
            }}
            );
        }};

        propanePyrolyzer = new GenericCrafter("propane-pyrolizer") {{
            requirements(Category.crafting,atl(),with(iridium,300,corallite,300,clay,150));
            researchCost = with(iridium,300,corallite,700,clay,400);

            itemCapacity = 20;
            size = 3;
            craftEffect = Fx.fireSmoke;
            craftTime = 30f;
            envDisabled |= Env.scorching;

            consumeItem(corallite,1);
            consumeItem(crude,1);
            consumePower(0.45f);

            drawer = new DrawMulti(
                    new DrawDefault(),
            new DrawHeatGlow()
            );

            outputLiquid = new LiquidStack(propane,1.5f);
            hasLiquids = true;
        }};

        heliumCompressor = new GenericCrafter("helium-compressor") {{
            requirements(Category.crafting,atl(),with(chrome,120,iridium,120,corallite,300,clay,100));
            researchCost = with(chrome,500,iridium,600,corallite,1200,clay,800);

            itemCapacity = 20;
            size = 3;
            craftEffect = SvFx.smokeCloud;
            craftTime = 30f;
            envDisabled |= Env.scorching;

            consumeLiquid(water,0.15f);
            consumeLiquid(propane,0.2f);
            consumePower(0.6f);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawLiquidRegion(helium)
            );

            outputLiquid = new LiquidStack(helium,1.35f);
            hasLiquids = true;
        }};

        phosphidePhotosynthesizer = new LaserCrafter("phosphide-photosynthesizer") {{
            requirements(Category.crafting,atl(),with(spaclanium,100,iridium,50,clay,30)); //TODO: reqs
            //TODO: reserach cost
            itemCapacity = 25;
            size = 3;
            craftEffect = SvFx.photosynthFlash;
            craftTime = 90f;
            envDisabled |= Env.scorching;

            consumeItem(iridium,1);
            consumeItem(spaclanium,2);
            consumePower(0.8f);
            consumeLaserPower(15f);
            laserOverpowerScale = 0.8f;
            maxLaserEfficiency = 2.50f;

            setLaserInputs(0,1,2,3);
            maxSuppliers = 1;
            inputRange = 8;
            drawInputs = false;

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawPistons(),
                    new DrawRegion("-top")
            );

            outputItem = new ItemStack(phosphide,3);
        }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[] {fullIcon};
            }
        };


        crudeSmelter = new CrudeSmelter("crude-smelter") {{
            requirements(Category.crafting,atl(),with(spaclanium,100,iridium,50,clay,30));
            researchCost = with(spaclanium,1000,iridium,1600,clay,600);

            itemCapacity = 30;
            size = 2;
            craftEffect = Fx.smokePuff;
            recipes = recipes(spaclanium, 2, 120, corallite, 2, 120, iridium, 1, 160, chrome, 1, 190);

            squareSprite = false;

            drawer = new DrawMulti(new DrawDefault(), new DrawFlame());
            consumeItem(crude,2);
            consumeLiquid(water,0.5f);
            consumePower(0.8f);

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};

        crudeCrucible = new CrudeSmelter("crude-crucible"){{
            requirements(Category.crafting,atl(), with(spaclanium, 500, iridium, 510, clay, 530, chrome,350));
            researchCost = with(spaclanium, 5000, iridium, 5600, clay, 3600, chrome,2750);

            squareSprite = false;

            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.smokePuff;
            recipes = recipes(spaclanium, 8, 60, corallite, 6, 80, iridium,5, 90, chrome, 3, 120);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawFlame(){{
                        lightRadius = 70f;
                        flameRadius = 6f;
                    }}
            );
            consumeItem(crude, 3);
            consumeLiquid(water, 0.5f);
            consumeLiquid(propane, 0.7f);
            consumePower(1.2f);

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};

        quartzScutcher = new LaserCrafter("quartz-scutcher") {{
            requirements(Category.crafting,atl(),with(chrome,220,spaclanium,120, finesand,80,iridium,30));

            researchCost = with(chrome,3000,spaclanium,5000, finesand,3500,iridium,6000);

            itemCapacity = 30;
            size = 3;
            craftEffect = SvFx.scutchFlash;
            craftTime = 100f;
            envDisabled |= Env.scorching;

            consumeItem(spaclanium,3);
            consumeItem(finesand,2);
            consumeLiquid(argon,55/60f);
            consumePower(400/60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawColorWeave(SvPal.quartzWeave), new DrawDefault());
            outputItem = new ItemStack(quartzFiber,3);

            consumeLaserPower(45f);
            laserOverpowerScale = 0.8f;
            maxLaserEfficiency = 4f;

            setLaserInputs(0,1,2,3);
            maxSuppliers = 1;
            inputRange = 8;
            drawInputs = false;

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};

        tugRoller = new TugRoller("tug-roller"){{
            requirements(Category.crafting,atl(),with(chrome,220,iridium,140,quartzFiber,60,sulfur,120));

            researchCost = with(chrome,5000,iridium,7000,quartzFiber,5000,sulfur,3200);

            itemCapacity = 30;
            size = 3;
            craftEffect = SvFx.tugPress;
            craftTime = 100f;
            envDisabled |= Env.scorching;

            consumeItem(chrome,3);
            consumeItem(phosphide,4);
            consumeLiquid(hydrogen,80/60f);
            consumeLiquid(helium,65/60f);
            consumePower(1280/60f);

            consumeLaserPower(60f);
            laserOverpowerScale = 0.75f;
            maxLaserEfficiency = 4f;

            setLaserInputs(0,1,2,3);
            maxSuppliers = 1;
            inputRange = 8;
            drawInputs = false;

            outputItem = new ItemStack(tugSheet,3);
            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};
    }

    public static BuildVisibility atl(BuildVisibility v){
        return new BuildVisibility(() -> Vars.state == null || Vars.state.isMenu() || (v.visible() && Vars.state.rules.planet == SvPlanets.atlacian || Vars.state.rules.env == Environment.any || Vars.state.rules.planet == Planets.sun));
    }

    public static BuildVisibility atl(){
        return atl(BuildVisibility.shown);
    }
}
