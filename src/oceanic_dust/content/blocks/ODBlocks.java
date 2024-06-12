package oceanic_dust.content.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import oceanic_dust.content.unit.*;
import oceanic_dust.content.world.*;
import oceanic_dust.content.world.blocks.*;
import oceanic_dust.content.world.blocks.energy.EnergyCross;
import oceanic_dust.content.world.blocks.energy.EnergyDock;
import oceanic_dust.content.world.draw.*;
import oceanic_dust.entities.shoot.*;
import oceanic_dust.entities.part.*;
import oceanic_dust.content.liquids.*;

import static mindustry.type.ItemStack.with;
import static oceanic_dust.content.world.items.ODItems.*;

public class ODBlocks {
    public static Block
            //DRILLS
            submersibleDrill, tectonicDrill,
            //DEFENSE
            whirl, rupture,
            //CRAFTERS
            waterMetallizer, ceramicBurner, argonCentrifuge,
            //LIQUIDS
            waterDiffuser,waterSifter, lowTierPump, clayConduit, conduitRouter, conduitBridge,
            //ENERGY
            energyDock, energyDistributor,sulfurator,
            //TRANSPORTATION
            duct,ductRouter,ductBridge,ductSorter, ductUnderflow, ductOverflow, ductDistributor,
            //EXPLORATION
            buoy,beacon,
            //CORES
            corePuffer;

    public static void load() {
        //drills
        submersibleDrill = new SubmersibleDrill("submersible-drill") {{
            requirements(Category.production, with(corallite, 50, spaclanium, 10, iridium, 10));
            tier = 2;
            drillTime = 400;
            size = 2;
            itemCapacity = 20;
            blockedItem = Items.sand;
            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.orangeSpark, 20f));
            fogRadius = 2;
            squareSprite = false;

            consumeLiquid(ODLiquids.polygen, 5/60f);
        }};

        tectonicDrill = new AttributeCrafter("tectonic-drill") {{
            requirements(Category.production, with(corallite, 50, spaclanium, 10, iridium, 10));
            craftTime = 400;
            size = 3;
            itemCapacity = 20;
            craftEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.orangeSpark, 20f));
            drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawBlurSpin("-rotator", 4),
            new DrawPistons() {{
                sideOffset = 1.25F;
            }},

            new DrawGlowRegion(){{
                alpha = 0.75f;
                glowScale = 3f;
                color = Color.valueOf("feb380");
            }},

            new DrawDefault(),
            new DrawRegion("-top")
            );

            fogRadius = 4;
            squareSprite = false;
            consumeLiquid(ODLiquids.polygen, 5/60f);
        }};

        //defense
        whirl = new ItemTurret("whirl"){{
            requirements(Category.turret, with(corallite, 85, clay, 45, sulfur, 10));
            outlineColor = Pal.darkOutline;
            squareSprite = false;
            ammo(
            spaclanium, new BasicBulletType(3f, 36){{
                width = 7f;
                height = 12f;
                lifetime = 60f;
                shootEffect = ODFx.pulverize;
                smokeEffect = Fx.none;
                hitColor = backColor = trailColor = Pal.suppress;
                frontColor = Color.white;
                trailWidth = 3f;
                trailLength = 5;
                hitEffect = despawnEffect = Fx.hitBulletColor;

                homingPower = 0.08f;
                homingRange = 50f;
            }}
            );

            size = 3;
            drawer = new DrawTurretCallbacked("atlacian-"){{
                DrawTurret draw = (DrawTurret)drawer;
                ODRegionPart liquidPart = new ODRegionPart(draw,"-blade"){{
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
            reload = 35f;
            shootY = 5f;
            recoil = 1f;
            rotate = false;
            shoot = new ShootWhirl(){{
                barrels = new float[]{
                -6, 2.5f, 0,
                6, 2.5f, 0
                };
                shotDelay = 5f;
            }};

            priority = 0;
            range = 170f;
            scaledHealth = 200;
            coolant = consumeCoolant(0.2f);
            researchCostMultiplier = 0.05f;

            limitRange(2);
        }};

        rupture = new ItemTurret("rupture"){{
            requirements(Category.turret, with(corallite, 45, clay, 25));
            outlineColor = Pal.darkOutline;

            size = 2;
            shootCone = 360f;
            fogRadius = 6;
            targetGround = false;
            targetAir = true;
            squareSprite = false;
            ammo(
            corallite, new BasicBulletType(6f, 16){{
                inaccuracy = 2.5f;
                width = 6f;
                height = 12f;
                lifetime = 120f;
                shootEffect = ODFx.pulverize;
                smokeEffect = Fx.none;
                hitColor = backColor = trailColor = Pal.plastaniumFront;
                frontColor = Color.white;
                trailWidth = 2f;
                trailLength = 4;
                hitEffect = despawnEffect = Fx.hitBulletColor;
            }},

            sulfur, new BasicBulletType(3f, 50){{
                reloadMultiplier = 0.3f;
                width = 6f;
                height = 12f;
                lifetime = 85f;
                shootEffect = ODFx.pulverize;
                smokeEffect = Fx.none;
                hitColor = backColor = trailColor = Pal.missileYellow;
                frontColor = Color.white;
                trailWidth = 2f;
                trailLength = 6;
                trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                hitEffect = despawnEffect = Fx.hitBulletColor;

                status = StatusEffects.slow;
                statusDuration = 60f;
            }}
            );

            drawer = new DrawTurret("atlacian-"){{
                parts.add(new RegionPart("-blade"){{
                    mirror = true;
                    under = true;
                    moveX = 1.5f;
                    moveRot = -4;
                    progress = PartProgress.recoil;
                }});

                parts.add(new RegionPart("-blade-mid"){{
                    progress = PartProgress.recoil;
                    moveY = -1.25f;
                }});
            }};

            shootSound = Sounds.railgun;
            reload = 15f;
            shootY = 5f;
            recoil = 0.5f;
            priority = 0;
            range = 260f;
            scaledHealth = 200;
            coolant = consumeCoolant(0.5f);
            coolantMultiplier = 1f;
            researchCostMultiplier = 0.05f;
            limitRange(6);
        }};

        //exploration
        buoy = new Buoy("buoy") {{
            requirements(Category.effect,with(spaclanium,20));
            alwaysUnlocked =true;
            lightRadius = 10f;
            fogRadius = Math.max(fogRadius, (int)(lightRadius / 8f * 3f) + 13);
            envDisabled |= Env.scorching;
            destructible = true;
            destroyBullet = new BasicBulletType(2f,10f) {{
                width = 11f;
                height = 20f;
                lifetime = 25f;
                shootEffect = Fx.shootBig;
                lightning = 2;
                lightningLength = 6;
                lightningColor = Pal.surge;
                //standard bullet damage is far too much for lightning
                lightningDamage = 20;
            }};

            priority = 0;
            health = 120;
        }};

        beacon = new Beacon("beacon") {{
            requirements(Category.effect,with(spaclanium,300,clay, 50,sulfur,200));
            lightRadius = 70f;
            fogRadius = Math.max(fogRadius, (int)(lightRadius / 8f * 3f) + 13);
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
            super.acceptCoolant = true;

            placeEffect = Fx.healWaveDynamic;
            health = 450;
        }};

        //liquids
        lowTierPump = new Pump("lead-pump") {{
            requirements(Category.liquid, with(spaclanium, 8));
            researchCost = with(
                    spaclanium, 5
            );

            squareSprite = false;
            envDisabled |= Env.scorching;
            pumpAmount = 8f / 60f;
        }};

        conduitBridge = new DirectionLiquidBridge("bridge-conduit"){{
            requirements(Category.liquid, with(corallite, 4, clay, 8));
            range = 4;
            hasPower = false;

            envDisabled |= Env.scorching;
        }};

        clayConduit = new Conduit("clay-conduit") {{
            requirements(Category.liquid, with(clay, 1));
            bridgeReplacement = conduitBridge;
            envDisabled |= Env.scorching;
            botColor = Color.valueOf("54333c");

            health = 45;
        }};

        conduitRouter = new LiquidRouter("liquid-router") {{
            requirements(Category.liquid, with(corallite, 4, clay, 2));
            liquidCapacity = 20f;
            underBullets = true;
            solid = false;

            envDisabled |= Env.scorching;
        }};

        waterDiffuser = new Separator("water-diffuser") {{
            requirements(Category.liquid, with(spaclanium, 20, corallite, 5));
            craftTime = 60f*2.5f;
            itemCapacity = 50;
            researchCost = with(
                    spaclanium, 20,
                    corallite , 10
            );

            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawLiquidRegion(Liquids.water)
            );
            consumeLiquid(Liquids.water, 1/60f);
            envDisabled |= Env.scorching;
            results = with(
                    spaclanium,3,
                    corallite,3,
                    fineSand,2
            );
        }};

        waterSifter = new Separator("water-sifter") {{
            requirements(Category.liquid, with(spaclanium,50, corallite, 60,clay,30));
            craftTime = 20f;
            itemCapacity = 50;
            size = 2;
            consumeLiquid(Liquids.water, 12/60f);
            envDisabled |= Env.scorching;
            squareSprite = false;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawLiquidRegion(Liquids.water),
                new WarmupDrawRegion("-top", true)
            );

            results = with(
                    spaclanium,3,
                    corallite, 2,
                    fineSand, 4,
                    sulfur, 2
            );
        }};

        //energy
        energyDock = new EnergyDock("energy-dock") {{
            requirements(Category.power,with(iridium,3,corallite, 2));
            size = 2;
            maxNodes = 10;
            range = 10;
            transferTime = 40;
            squareSprite = false;
            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawEnergyGlow()
            );
        }};

        energyDistributor = new EnergyCross("energy-distributor") {{
            requirements(Category.power, with(iridium, 18));
            consumesPower = outputsPower = true;
            health = 90;
            range = 5;
            researchCost = with(iridium,5);
            squareSprite = false;

            consumePowerBuffered(1000f);
        }};

        sulfurator = new ConsumeGenerator("sulfurator") {{
            requirements(Category.power, with(corallite, 20, clay, 30, iridium, 25));
            powerProduction = 1f;
            itemDuration = 120f;
            envDisabled |= Env.scorching;

            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.03f;

            consumeEffect = Fx.redgeneratespark;
            generateEffect = Fx.pulverizeSmall;

            drawer = new DrawMulti(new DrawDefault(), new DrawWarmupRegion());
            consumeItem(sulfur);
        }};

        //core
        corePuffer = new CoreBlock("core-puffer"){{
            requirements(Category.effect, with(spaclanium,600,corallite,600,clay,300,sulfur,300));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.editorOnly;
            isFirstTier = true;
            unitType = ODUnits.marine;
            health = 6000;
            itemCapacity = 5000;
            size = 5;

            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 12;
        }};

        //transport
        ductBridge = new DuctBridge("duct-bridge") {{
            requirements(Category.distribution, with(corallite, 4,spaclanium,2));
            envDisabled |= Env.scorching;
            health = 90;
            speed = 4f;
        }};

        duct = new Duct("duct"){{
            requirements(Category.distribution, with(corallite, 1));
            bridgeReplacement = ODBlocks.ductBridge;
            health = 90;
            speed = 4f;
            researchCost = with(corallite, 5);
            envDisabled |= Env.scorching;
        }};

        ductRouter = new Router("duct-router") {{
            requirements(Category.distribution, with(corallite, 3));
            envDisabled |= Env.scorching;
        }};
        
        ductSorter = new Sorter("duct-sorter"){{
            requirements(Category.distribution, with(corallite, 2, spaclanium, 2));
            buildCostMultiplier = 3f;
            envDisabled |= Env.scorching;
        }};

        ductDistributor = new Router("duct-distributor"){{
            requirements(Category.distribution, with(corallite, 4, spaclanium, 4));
            buildCostMultiplier = 3f;
            size = 2;
            squareSprite = false;
        }};

        ductOverflow = new OverflowGate("duct-overflow-gate"){{
            requirements(Category.distribution, with(corallite, 2, spaclanium, 4));
            buildCostMultiplier = 3f;
        }};

        ductUnderflow = new OverflowGate("duct-underflow-gate"){{
            requirements(Category.distribution, with(corallite, 2, spaclanium, 4));
            buildCostMultiplier = 3f;
            invert = true;
        }};

        //crafters
        ceramicBurner = new GenericCrafter("ceramic-burner") {{
            requirements(Category.crafting,with(spaclanium,30,corallite,70,fineSand,30));
            craftEffect = ODFx.smokePuff;
            craftTime = 60f*2;

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
            consumeLiquid(Liquids.water,1);
            consumeItem(fineSand, 1);
        }};

        argonCentrifuge = new GenericCrafter("argon-centrifuge") {{
           requirements(Category.crafting, with(spaclanium,60,corallite,200,sulfur,30));
           itemCapacity = 10;
           size = 2;
           craftEffect = Fx.smokePuff;
           craftTime = 60f;
           envDisabled |= Env.scorching;

           consumeItem(sulfur,1);
           consumeItem(corallite, 2);
           consumePower(0.8f);
           outputLiquid = new LiquidStack(ODLiquids.argon, 8/60f);
           hasLiquids = true;
           drawer = new DrawMulti(
                   new DrawDefault(),
                   new DrawLiquidRegion(ODLiquids.argon)
           );
        }};

        waterMetallizer = new GenericCrafter("water-metallizer") {{
            requirements(Category.crafting, with(spaclanium,100,corallite,60));
            outputLiquid = new LiquidStack(ODLiquids.polygen, 1);
            craftTime = 20f;

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

            consumeLiquid(Liquids.water,1);
            consumeItem(corallite,1);
        }};
    }
}
