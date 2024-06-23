package subvoyage.content.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import subvoyage.content.liquids.*;
import subvoyage.content.world.*;
import subvoyage.content.world.blocks.*;
import subvoyage.content.world.blocks.crude_smelter.*;
import subvoyage.content.world.blocks.energy.*;
import subvoyage.content.world.blocks.offload_core.*;
import subvoyage.content.world.draw.*;
import subvoyage.entities.part.*;
import subvoyage.entities.shoot.*;

import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.*;
import static subvoyage.content.liquids.SvLiquids.*;
import static subvoyage.content.unit.SvUnits.*;
import static subvoyage.content.world.items.SvItems.*;

public class SvBlocks{
    /*
        featherDrill (перовой бур),awe (трепет), swiftHydralizer (Электро-обогащатель), argonCondenser (Сгущатель аргона),
        windTurbine (ветрогенератор), chromiumReactor (Хромовый реактор), coreDecrypter (Ядровый Декриптор),
        regenerator (Регенератор), repairProjector (Регенерирующий проектор), shieldProjector (Защитный проектор),
        accumulator (Аккумулятор), largeAccumulator (Большой аккумулятор), crudeSmelter (Сырьевая плавильня),
        tower(Вышка), coreShore (Ядро Берег), coreReef (Ядро Риф), highPressureConduit,Duct (Канал/трубопровод высокого давления),
        resonance (Резонанс), burden (Бремя), cascade (Каскад)
     */
    
    public static Block
            //NON-USER
            offloadCore, offloadCoreGuardian,
            //DRILLS
            submersibleDrill, featherDrill, tectonicDrill,
            //DEFENSE
            whirl, rupture, awe, resonance, burden, cascade,
            finesandWall, finesandWallLarge,
            clayWall,clayWallLarge,
            tugSheetWall, tugSheetWallLarge,
            coreDecoder, coreDecrypter,
            regenerator, regenProjector,
            //CRAFTERS
            waterMetallizer, poweredEnhancer, ceramicBurner, terracottaBlaster, argonCentrifuge, argonCondenser,
            crudeSmelter,
            quartzScutcher, tugRoller,
            //LIQUIDS
            waterDiffuser,waterSifter, lowTierPump, centrifugalPump, clayConduit, highPressureConduit, conduitRouter, conduitBridge,
            //ENERGY
            energyDock, energyDistributor, accumulator, largeAccumulator, spaclaniumHydrolyzer, windTurbine, chromiumReactor,
            //TRANSPORTATION
            duct,highPressureDuct,ductRouter,ductBridge,ductSorter, ductUnderflow, ductOverflow, ductDistributor,
            shipCargoStation, shipUnloadPoint,
            //PAYLOAD
            helicopterFactory,
            //EXPLORATION
            buoy,tower,beacon,
            //STORAGE
            corePuffer,coreShore,coreReef, vault, largeVault, unloader, liquidContainer, liquidTank;

    public static void load() {
        //non-user
        offloadCore = new OffloadCore("offload-core") {{
            requirements(Category.logic, BuildVisibility.editorOnly, with());
            health = 400;
            size = 3;

            unitType = marine;
        }};


        offloadCoreGuardian = new OffloadCoreGuardian("offload-core-guardian") {{
            requirements(Category.logic, BuildVisibility.editorOnly, with());
            health = 4000;
            size = 4;

            unitType = marine;
        }};;

        //payload
        helicopterFactory = new UnitFactory("helicopter-factory") {{
            requirements(Category.units, with(iridium, 60, clay, 70));
            consumeLiquid(argon,1f);
            plans = Seq.with(
                    new UnitPlan(lapetus, 60f * 15, with(iridium, 15))
            );
            size = 3;
            consumePower(1.2f);
        }};

        //drills
        submersibleDrill = new SubmersibleDrill("submersible-drill") {{
            requirements(Category.production, with(corallite, 50, spaclanium, 10, iridium, 10));
            tier = 2;
            hardnessDrillMultiplier = 0.9f;
            drillTime = 470;
            size = 2;
            itemCapacity = 20;
            blockedItem = Items.sand;
            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.orangeSpark, 20f));
            fogRadius = 2;
            squareSprite = false;

            consumeLiquid(SvLiquids.polygen, 5/60f);
        }};

        featherDrill = new SubmersibleDrill("feather-drill") {{
            requirements(Category.production, with(corallite, 100, chromium, 50, iridium, 100, clay, 200));
            tier = 3;
            hardnessDrillMultiplier = 1.1f;
            drillTime = 250;
            size = 3;
            itemCapacity = 30;
            blockedItem = Items.sand;
            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.orangeSpark, 20f));
            fogRadius = 3;
            squareSprite = false;
            scaleTop = false;

            consumeLiquid(argon, 1.8f);
            consumeCoolant(1.2f);
        }};

        tectonicDrill = new AttributeCrafter("tectonic-drill") {{
            requirements(Category.production, with(corallite, 50, spaclanium, 10, iridium, 10));
            craftTime = 400;
            size = 3;
            itemCapacity = 20;
            outputItem = new ItemStack(crude, 8);
            hasPower = true;
            hasLiquids = false;
            displayEfficiency = false;
            ambientSound = Sounds.drill;
            ambientSoundVolume = 0.15f;

            craftEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.orangeSpark, 20f));
            drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawGlowRegion(){{
                alpha = 0.75f;
                glowScale = 6f;
                color = Color.valueOf("feb380");
            }},

            new DrawBlurSpin("-rotator", 4),
            new DrawDefault(),
            new DrawRegion("-top")
            );

            fogRadius = 4;
            squareSprite = false;
            consumeItems(with(sulfur, 4));
            consumePower(2f);
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
                shootEffect = SvFx.pulverize;
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
                shootEffect = SvFx.pulverize;
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
                shootEffect = SvFx.pulverize;
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

            limitRange(6);
        }};


        awe = new PowerTurret("awe") {{
            requirements(Category.turret, with(corallite, 85, iridium, 40, spaclanium, 20));
            size = 2;
            outlineColor = Pal.darkOutline;
            shootCone = 360f;
            targetGround = true;
            targetAir = false;
            fogRadius = 6;
            health = 260;
            shootSound = Sounds.dullExplosion;
            range = 8*10f;
            velocityRnd = 0;
            reload = 60f;
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

            shootType = new ExplosionBulletType(26f,range) {{
                collidesAir = false;
                buildingDamageMultiplier = 1.1f;
                ammoMultiplier = 1f;
                speed = 0;
                lifetime = 1f;
                killShooter = false;
            }};
            consumePower(3.3f);
            coolant = consumeCoolant(0.1f);
        }};

        resonance = new PowerTurret("resonance") {{
            requirements(Category.turret, with(corallite, 185, iridium, 140,chromium,80));
            size = 3;
            outlineColor = Pal.darkOutline;
            shootCone = 360f;
            targetGround = true;
            targetAir = true;
            fogRadius = 9;
            health = 540;
            shootSound = Sounds.cannon;
            range = 10*10f;
            velocityRnd = 0;
            reload = 120f;
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

            shootType = new ExplosionBulletType(54f,range) {{
                collidesAir = false;
                buildingDamageMultiplier = 1.1f;
                ammoMultiplier = 1f;
                speed = 0;
                lifetime = 1f;
                killShooter = false;
            }};
            consumePower(3.3f);
            coolant = consumeCoolant(0.1f);
        }};

        burden = new LiquidTurret("burden") {{
            requirements(Category.turret, with(clay,200,iridium,140,chromium,120));
            ammo(
                    Liquids.water, new LiquidBulletType(Liquids.water){{
                        lifetime = 49f;
                        speed = 4f;
                        knockback = 1.7f;
                        puddleSize = 8f;
                        orbSize = 4f;
                        drag = 0.001f;
                        ammoMultiplier = 0.4f;
                        statusDuration = 60f * 4f;
                        damage = 0.2f;
                        layer = Layer.bullet - 2f;
                    }},
                    polygen, new LiquidBulletType(polygen){{
                        lifetime = 68f;
                        speed = 6f;
                        knockback = 3f;
                        puddleSize = 12f;
                        orbSize = 5f;
                        drag = 0.001f;
                        ammoMultiplier = 0.3f;
                        statusDuration = 60f * 4f;
                        damage = 0.3f;
                        layer = Layer.bullet - 2f;
                    }}
            );
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
            requirements(Category.turret, with(clay,300,iridium,150,chromium,50,spaclanium,80));
            outlineColor = Pal.darkOutline;

            size = 3;
            shootCone = 360f;
            fogRadius = 6;
            targetGround = true;
            targetAir = true;
            squareSprite = false;
            ammo(
                    chromium, new BasicBulletType(6f, 40){{
                        inaccuracy = 0.5f;
                        ammoMultiplier = 1f;
                        width = 6f;
                        height = 12f;
                        lifetime = 120f;
                        shootEffect = SvFx.pulverize;
                        smokeEffect = Fx.none;
                        hitColor = backColor = trailColor = Pal.plastaniumFront;
                        frontColor = Color.white;
                        trailWidth = 6f;
                        trailLength = 4;
                        shoot = new ShootBarrel();
                        hitEffect = despawnEffect = Fx.hitBulletColor;
                    }},

                    tugSheet, new BasicBulletType(12f, 300){{
                        reloadMultiplier = 1.1f;
                        ammoMultiplier = 1f;
                        width = 6f;
                        ammoPerShot = 4;
                        height = 12f;
                        lifetime = 85f;
                        shootEffect = SvFx.pulverize;
                        smokeEffect = Fx.none;
                        hitColor = backColor = trailColor = Pal.missileYellow;
                        frontColor = Color.white;
                        trailWidth = 8f;
                        trailLength = 6;
                        homingPower = 0.08f;
                        homingRange = 8f;
                        shoot = new ShootBarrel();
                        trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                        hitEffect = despawnEffect = Fx.hitBulletColor;
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
            range = 180f;
            scaledHealth = 200;
            coolant = consumeCoolant(0.5f);
            coolantMultiplier = 1.2f;

            limitRange(6);

        }};


        int wallHealthMultiplier = 4;
        int largeWallHealthMultiplier = 20;
        finesandWall = new Wall("finesand-wall"){{
            requirements(Category.defense, with(fineSand, 8));
            health = 60 * wallHealthMultiplier;
            envDisabled |= Env.scorching;
        }};

        finesandWallLarge = new Wall("finesand-wall-large"){{
            requirements(Category.defense, mult(finesandWall.requirements, 5));
            health = 60 * largeWallHealthMultiplier;
            size = 2;
            envDisabled |= Env.scorching;
        }};

        clayWall = new Wall("clay-wall"){{
            requirements(Category.defense, with(clay, 8));
            health = 60 * wallHealthMultiplier;
            envDisabled |= Env.scorching;
        }};

        clayWallLarge = new Wall("clay-wall-large"){{
            requirements(Category.defense, mult(clayWall.requirements, 4));
            health = 60 * largeWallHealthMultiplier;
            size = 2;
            envDisabled |= Env.scorching;
        }};

        tugSheetWall = new ShieldWall("tug-sheet-wall") {{
            requirements(Category.defense, with(tugSheet, 8));
            consumePower(3f / 60f);

            glowColor = Color.valueOf("bee8d7").a(0.5f);
            glowMag = 0.8f;
            glowScl = 12f;

            hasPower = true;
            outputsPower = false;
            consumesPower = true;
            conductivePower = true;
            chanceDeflect = 10f;
            armor = 15f;
            health = 200 * wallHealthMultiplier;
            envDisabled |= Env.scorching;
        }};

        tugSheetWallLarge = new ShieldWall("tug-sheet-wall-large") {{
            requirements(Category.defense, mult(tugSheetWall.requirements, 4));
            consumePower(3*4f / 60f);

            glowColor = Color.valueOf("bee8d7").a(0.5f);
            glowMag = 0.8f;
            glowScl = 12f;

            hasPower = true;
            outputsPower = false;
            consumesPower = true;
            conductivePower = true;
            chanceDeflect = 20f;
            armor = 15f;
            health = 200 * largeWallHealthMultiplier;
            envDisabled |= Env.scorching;
            size = 2;
        }};

        coreDecoder = new CoreDecoder("core-decoder") {{
            requirements(Category.effect, with(iridium,300,chromium,200,corallite,20));
            health = 560;

            priority = TargetPriority.core-0.2f;
            fogRadius = 10;
            size = 2;
            consumePower(5f);

            destructible = true;

            envDisabled |= Env.scorching;
        }};

        coreDecrypter = new CoreDecoder("core-decrypter") {{
            requirements(Category.effect,with(iridium,400,chromium,300,quartzFiber,250));
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
        }};

        regenerator = new MendProjector("regenerator"){{
            requirements(Category.effect, with(spaclanium, 60));
            consumePower(0.3f);
            size = 1;
            reload = 200f;
            range = 40f;
            healPercent = 4f;
            phaseBoost = 4f;
            phaseRangeBoost = 20f;
            health = 80;
            consumeLiquid(polygen,0.3f).boost();
        }};

        regenProjector = new MendProjector("repair-projector"){{
            requirements(Category.effect, with(spaclanium, 60, clay, 80, iridium, 10));
            consumePower(0.3f);
            size = 2;
            reload = 100f;
            range = 40f*2;
            healPercent = 16f;
            phaseBoost = 4f;
            phaseRangeBoost = 20f;
            health = 400;
            consumeLiquid(polygen,1f).boost();
        }};

        //exploration
        buoy = new Buoy("buoy") {{
            requirements(Category.effect, BuildVisibility.fogOnly, with(spaclanium,20));
            alwaysUnlocked = true;
            fogRadius = 32;
            envDisabled |= Env.scorching;
            destructible = true;

            priority = 0;
            health = 120;
        }};

        tower = new Buoy("tower") {{
            requirements(Category.effect, BuildVisibility.fogOnly, with(chromium,10,clay,10));
            fogRadius = 28;

            envDisabled |= Env.scorching;
            destructible = true;
            isWater = false;

            priority = 0;
            health = 360;
        }};

        beacon = new Beacon("beacon") {{
            requirements(Category.effect, BuildVisibility.fogOnly, with(spaclanium,300,clay, 50,sulfur,200));
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
            super.acceptCoolant = true;

            placeEffect = Fx.healWaveDynamic;
            health = 450;

            consumePower(0.15f);
        }};

        //liquids
        lowTierPump = new Pump("lead-pump") {{
            requirements(Category.liquid, with(spaclanium, 8));

            squareSprite = false;
            envDisabled |= Env.scorching;
            pumpAmount = 8f / 60f;
        }};

        centrifugalPump = new Pump("centrifugal-pump") {{
            requirements(Category.liquid, with(spaclanium, 8));
            size = 2;

            squareSprite = false;
            envDisabled |= Env.scorching;
            pumpAmount = 16f / 60f;
            consumePower(0.35f);

            drawer = new DrawMulti(
            new DrawPistons() {{
                lenOffset = -4.25f;
                sideOffset = Mathf.PI / 2f;
                angleOffset = 90.0F;
                sides = 2;

                sinMag = 2.75f;
                sinScl = 5f;
            }},
            new DrawDefault(),
            new DrawPumpLiquid()
            );
        }};

        clayConduit = new Conduit("clay-conduit") {{
            requirements(Category.liquid, with(clay, 1));
            bridgeReplacement = conduitBridge;
            envDisabled |= Env.scorching;
            botColor = Color.valueOf("54333c");

            health = 45;
        }};

        highPressureConduit = new Conduit("high-pressure-conduit") {{
            requirements(Category.liquid, with(chromium, 1, clay, 1));
            bridgeReplacement = conduitBridge;
            envDisabled |= Env.scorching;
            botColor = Color.valueOf("54333c");
            liquidCapacity = 16f;
            liquidPressure = 1.225f;

            health = 80;
        }};

        conduitBridge = new DirectionLiquidBridge("bridge-conduit"){{
            requirements(Category.liquid, with(corallite, 4, clay, 8));
            range = 4;
            hasPower = false;

            envDisabled |= Env.scorching;
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

            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawLiquidRegion(water)
            );
            consumeLiquid(water, 1/60f);
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
            consumeLiquid(water, 12/60f);
            envDisabled |= Env.scorching;
            squareSprite = false;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawLiquidRegion(water),
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
            new DrawEnergyLinksGlow()
            );
        }};

        energyDistributor = new EnergyCross("energy-distributor") {{
            requirements(Category.power, with(iridium, 18));
            consumesPower = outputsPower = true;
            health = 90;
            range = 5;
            squareSprite = false;

            consumePowerBuffered(1000f);
        }};

        accumulator = new Battery("accumulator"){{
            requirements(Category.power, with(iridium, 5, spaclanium, 20));
            consumePowerBuffered(4000f);
            baseExplosiveness = 1f;
            drawer = new DrawMulti(new DrawDefault(), new DrawEnergyGlow());
        }};

        largeAccumulator = new Battery("large-accumulator"){{
            requirements(Category.power, mult(accumulator.requirements,4));
            consumePowerBuffered(4000f*5);
            baseExplosiveness = 3f;
            drawer = new DrawMulti(new DrawDefault(), new DrawEnergyGlow());
            size = 2;
        }};

        spaclaniumHydrolyzer = new ConsumeGenerator("spaclanium-hydrolyzer") {{
            requirements(Category.power, with(corallite, 20, clay, 30, iridium, 25));
            powerProduction = 1.2f;
            itemDuration = 120f;
            envDisabled |= Env.scorching;

            ambientSound = Sounds.extractLoop;
            ambientSoundVolume = 0.03f;

            size = 2;

            consumeEffect = Fx.generatespark;
            generateEffect = Fx.pulverizeSmall;

            drawer = new DrawMulti(new DrawDefault(), new DrawWarmupRegion());
            consumeItem(spaclanium);
            consumeLiquid(water,0.5f);
        }};

        windTurbine = new WindTurbine("wind-turbine") {{
            requirements(Category.power,with(corallite,60,clay,15,iridium,30));

            ambientSound = Sounds.wind;
            ambientSoundVolume = 0.05f;

            powerProduction = 0.2f;
            size = 2;
        }};

        chromiumReactor = new ChromiumReactor("chromium-reactor"){{
            requirements(Category.power,with(chromium, 300, tugSheet, 50, corallite, 80, iridium, 100));
            size = 3;
            squareSprite = false;
            health = 900;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.24f;
            powerProduction = 6;

            consumePower(20f/30);
            consumeItem(chromium,2);
            consumeLiquid(polygen, heating / coolantPower).update(false);
        }};

        //storage
        corePuffer = new SubvoyageCoreBlock("core-puffer"){{
            requirements(Category.effect, with(spaclanium,600,corallite,600,clay,300,sulfur,300));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.editorOnly;
            isFirstTier = true;
            unitType = marine;
            health = 6000;
            itemCapacity = 5000;
            size = 4;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 12;

            bannedItems.addAll(crude);
        }};

        coreShore = new SubvoyageCoreBlock("core-shore"){{
            requirements(Category.effect, with(spaclanium,1500,corallite,1200,chromium,1300,iridium,800));
            unitType = marine;
            health = 12000;
            itemCapacity = 8000;
            size = 5;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 18;

            bannedItems.addAll(crude);
        }};

        coreReef = new SubvoyageCoreBlock("core-reef"){{
            requirements(Category.effect, with(spaclanium,3500,corallite,2200,chromium,1300,iridium,1000,quartzFiber,1000,tugSheet,800));
            unitType = marine;
            health = 24000;
            itemCapacity = 12000;
            size = 6;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 24;

            bannedItems.addAll(crude);
        }};

        vault = new StorageBlock("vault"){
            {
                requirements(Category.effect, with(chromium, 250, iridium, 125));
                size = 2;
                itemCapacity = 300;
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
                requirements(Category.effect, with(chromium, 500, iridium, 345));
                size = 3;
                itemCapacity = 1000;
                scaledHealth = 155;
                squareSprite = false;
            }

            @Override
            protected TextureRegion[] icons() {
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };

        unloader = new Unloader("unloader"){{
            requirements(Category.effect, with(chromium, 25, clay, 30));
            speed = 60f / 11f;
            group = BlockGroup.transportation;
            squareSprite = false;
        }};

        liquidContainer = new LiquidRouter("liquid-container"){{
            requirements(Category.liquid, with(corallite, 30, clay, 35));
            liquidCapacity = 700f;
            size = 2;
            liquidPadding = 3f / 4f;

            solid = true;
            squareSprite = false;
        }};

        liquidTank = new LiquidRouter("liquid-tank"){{
            requirements(Category.liquid, with(corallite,80, clay, 140, iridium, 30));
            liquidCapacity = 1800f;
            health = 500;
            size = 3;
            liquidPadding = 6f / 4f;

            solid = true;
            squareSprite = false;
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
            bridgeReplacement = SvBlocks.ductBridge;
            health = 90;
            speed = 4f;
            envDisabled |= Env.scorching;
        }};

        highPressureDuct = new Duct("high-pressure-duct") {{
            requirements(Category.distribution,with(chromium,1,corallite,1));
            bridgeReplacement = SvBlocks.ductBridge;
            health = 180;
            speed = 2f;
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
            requirements(Category.distribution,with(iridium,100,clay,200));

            size = 3;
            itemCapacity = 250;

            buildTime = 6f*60f;

            consumePower(12f/60f);
            consumeLiquid(argon,8f/60f);

            unitType = bulker;
        }};

        shipUnloadPoint = new UnitCargoUnloadPoint("ship-unload-point") {{
            requirements(Category.distribution,with(iridium,50,clay,80));

            size = 2;
            itemCapacity = 50;
        }};


        //crafters
        ceramicBurner = new GenericCrafter("ceramic-burner") {{
            requirements(Category.crafting,with(spaclanium,30,corallite,70,fineSand,30));
            craftEffect = SvFx.smokePuff;
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
            consumeLiquid(water,1);
            consumeItem(fineSand, 1);
        }};

        terracottaBlaster = new GenericCrafter("terracotta-blaster") {{
            requirements(Category.crafting,with(spaclanium,100,corallite,200,fineSand,120,iridium,40));
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
            outputItem = new ItemStack(clay,5);
            itemCapacity = 12;

            size = 3;
            envDisabled |= Env.scorching;
            consumeLiquid(water, 2);
            consumeItem(fineSand,2);
            consumePower(2.3f);
        }};

        waterMetallizer = new GenericCrafter("water-metallizer") {{
            requirements(Category.crafting, with(spaclanium,100,corallite,60));
            outputLiquid = new LiquidStack(SvLiquids.polygen, 1);
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

            consumeLiquid(water,1);
            consumeItem(corallite,1);
        }};

        poweredEnhancer = new GenericCrafter("powered-enhancer") {{
            requirements(Category.crafting, with(chromium, 200, iridium, 100));
            outputLiquid = new LiquidStack(SvLiquids.polygen, 2);
            craftTime = 1f;

            itemCapacity = 30;
            size = 3;

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

            consumeLiquid(water,0.5f);
            consumeItem(corallite, 1);
            consumePower(0.8f);
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
            outputLiquid = new LiquidStack(argon, 8/60f);
            hasLiquids = true;
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawLiquidRegion(argon)
            );
        }};

        argonCondenser = new GenericCrafter("argon-condenser") {{
            requirements(Category.crafting, with(chromium,120,iridium,60,tugSheet,10));
            itemCapacity = 10;
            size = 3;
            craftEffect = Fx.smokePuff;
            craftTime = 20f;
            envDisabled |= Env.scorching;

            consumeItem(sulfur,2);
            consumeItem(corallite, 3);
            consumePower(1.3f);
            outputLiquid = new LiquidStack(argon, 30/60f);
            hasLiquids = true;
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawLiquidRegion(argon)
            );
        }};

        crudeSmelter = new CrudeSmelter("crude-smelter") {{
            requirements(Category.crafting,with(spaclanium,100,iridium,50,clay,30));
            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.smokePuff;
            recipes = recipes(spaclanium,2,60,corallite,2,60,iridium,1,90,chromium,1,120);

            consumeItem(crude,2);
            consumeLiquid(water,0.5f);
            consumePower(0.8f);

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};

        quartzScutcher = new AttributeCrafter("quartz-scutcher") {{
            requirements(Category.crafting,with(chromium,220,spaclanium,120,fineSand,80,iridium,30));
            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.smeltsmoke;
            craftTime = 100f;
            envDisabled |= Env.scorching;

            consumeItem(spaclanium,6);
            consumeItem(fineSand,8);
            consumeLiquid(argon,1.2f);
            consumePower(6f);

            outputItem = new ItemStack(quartzFiber,2);

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};

        tugRoller = new AttributeCrafter("tug-roller") {{
            requirements(Category.crafting,with(chromium,220,iridium,140,quartzFiber,60,sulfur,120));
            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.smeltsmoke;
            craftTime = 100f;
            envDisabled |= Env.scorching;

            consumeItem(chromium,6);
            consumeItem(sulfur,8);
            consumeLiquid(polygen,1.3f);
            consumePower(9f);

            outputItem = new ItemStack(tugSheet,1);

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};
    }
}
