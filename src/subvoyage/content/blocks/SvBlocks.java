package subvoyage.content.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
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
import mindustry.ui.dialogs.PlanetDialog;
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
import subvoyage.content.blocks.editor.SubvoyageCoreBlock;
import subvoyage.content.blocks.fog.Beacon;
import subvoyage.content.blocks.fog.Buoy;
import subvoyage.content.blocks.production.SubmersibleDrill;
import subvoyage.content.blocks.production.TugRoller;
import subvoyage.content.liquids.*;
import subvoyage.content.world.*;
import subvoyage.content.blocks.crude_smelter.*;
import subvoyage.content.blocks.energy.*;
import subvoyage.content.blocks.editor.offload_core.*;
import subvoyage.content.world.draw.*;
import subvoyage.entities.part.*;
import subvoyage.entities.shoot.*;

import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.*;
import static subvoyage.content.liquids.SvLiquids.*;
import static subvoyage.content.unit.SvUnits.*;
import static subvoyage.content.world.items.SvItems.*;

public class SvBlocks{
    
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
            crudeSmelter, crudeCrucible,
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

            unitType = marine;
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };

        //payload
        helicopterFactory = new UnitFactory("helicopter-factory") {{
            requirements(Category.units, with(iridium, 60, clay, 70));

            researchCost = with(iridium,400,clay,500);

            consumeLiquid(argon,1f);
            plans = Seq.with(
                    new UnitPlan(lapetus, 60f * 15, with(iridium, 15))
            );
            size = 3;
            consumePower(1.2f);
        }};

        //drills
        submersibleDrill = new SubmersibleDrill("submersible-drill") {{
            requirements(Category.production, with(corallite, 50, spaclanium, 10));
            tier = 2;
            hardnessDrillMultiplier = 0.9f;
            drillTime = 470;
            size = 2;
            itemCapacity = 20;
            blockedItem = Items.sand;
            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.orangeSpark, 20f));
            fogRadius = 2;
            squareSprite = false;

            researchCost = with(corallite,200,spaclanium,100);

            consumeLiquid(water, 30/60f);
        }};

        featherDrill = new SubmersibleDrill("feather-drill") {{
            requirements(Category.production, with(corallite, 100, chromium, 50, iridium, 100, clay, 200));

            researchCost = with(corallite,500,chromium,200,iridium,300,clay,400);

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
            requirements(Category.production, with(corallite, 200, spaclanium, 100, iridium, 100));

            researchCost = with(corallite,1000,spaclanium,600,iridium,400);

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

            researchCost = with(corallite,10,clay,10,sulfur,5);

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

            researchCost = with(corallite,100,clay,60);

            outlineColor = Pal.darkOutline;

            size = 2;
            shootCone = 360f;
            fogRadius = 6;
            targetGround = false;
            targetAir = true;
            squareSprite = false;

            shoot = new ShootHelix() {{
                mag = 3;
            }};


            ammo(
            sulfur, new BasicBulletType(6f, 40){{
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
                fragBullet = intervalBullet = new BasicBulletType(6f, 2) {{
                    width = 9f;
                    hitSize = 5f;
                    height = 15f;
                    pierce = true;
                    lifetime = 15f;
                    pierceBuilding = true;
                    hitColor = backColor = trailColor = Pal.bulletYellow;
                    frontColor = Color.white;
                    trailWidth = 2.1f;
                    trailLength = 5;
                    shoot = new ShootHelix();

                    status = StatusEffects.slow;
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
            }},
            quartzFiber, new BasicBulletType(5f, 40){{
                        width = 12f;
                        height = 12f;
                        lifetime = 30f;
                        shootEffect = SvFx.pulverize;
                        smokeEffect = Fx.none;
                        hitColor = backColor = trailColor = Pal.thoriumPink;
                        frontColor = Color.white;
                        trailWidth = 6f;
                        trailLength = 12;
                        trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                        hitEffect = despawnEffect = Fx.hitBulletColor;
                        shoot = new ShootHelix();

                        fragBullet = intervalBullet = new BasicBulletType(9f, 12) {{
                            width = 9f;
                            hitSize = 5f;
                            height = 15f;
                            pierce = true;
                            lifetime = 35f;
                            pierceBuilding = true;
                            hitColor = backColor = trailColor = Pal.thoriumPink;
                            frontColor = Color.white;
                            trailWidth = 2.1f;
                            trailLength = 5;
                            shoot = new ShootHelix();

                            status = StatusEffects.slow;
                            statusDuration = 60f;
                            hitEffect = despawnEffect = new WaveEffect(){{
                                colorFrom = colorTo = Pal.thoriumPink;
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
                }});

                parts.add(new RegionPart("-blade-mid"){{
                    //progress = PartProgress.recoil;
                    progress = PartProgress.heat;
                    moveY = -1.25f;
                }});
            }};

            shootSound = Sounds.railgun;
            reload = 80f;
            shootY = 5f;
            recoil = 0.5f;
            priority = 0;
            range = 260f;
            scaledHealth = 200;

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

            researchCost = with(corallite,800,iridium,500,chromium,300);

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
            outlineColor = Pal.darkOutline;
            requirements(Category.turret, with(clay,200,iridium,140,chromium,120));
            researchCost = with(clay,1000,iridium,600,chromium,400);
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
            requirements(Category.turret, with(clay,300,iridium,150,chromium,50,spaclanium,80));

            researchCost = with(clay,1000,iridium,800,chromium,700,spaclanium,600);

            outlineColor = Pal.darkOutline;

            size = 3;
            shootCone = 360f;
            fogRadius = 6;
            targetGround = true;
            targetAir = true;
            squareSprite = false;
            ammo(
            chromium, new BasicBulletType(6f, 60){{
                sprite = "large-orb";
                inaccuracy = 1f;
                ammoMultiplier = 10f;
                ammoPerShot = 3;

                width = 12f;
                height = 12f;
                lifetime = 120f;
                shootEffect = SvFx.pulverize;
                smokeEffect = Fx.none;

                hitColor = backColor = trailColor = Color.valueOf("d5cba3");
                frontColor = Color.white;

                homingPower = 0.18f;
                homingRange = 16f;

                trailRotation = true;
                trailEffect = Fx.disperseTrail;
                trailInterval = 3f;
                trailWidth = 6f;
                trailLength = 6;
                trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);

                shoot = new ShootBarrel();
                hitEffect = despawnEffect = Fx.hitBulletColor;
            }},

            tugSheet, new BasicBulletType(12f, 300){{
                sprite = "large-orb";
                inaccuracy = 3f;
                reloadMultiplier = 1.1f;
                ammoMultiplier = 1f;

                width = 8f;
                ammoPerShot = 4;
                height = 12f;
                lifetime = 85f;
                shootEffect = SvFx.pulverize;
                smokeEffect = Fx.none;

                hitColor = backColor = trailColor = Color.valueOf("95b3b1");
                frontColor = Color.white;

                trailRotation = true;
                trailEffect = Fx.disperseTrail;
                trailInterval = 3f;
                trailWidth = 6f;
                trailLength = 6;
                trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);

                homingPower = 0.08f;
                homingRange = 8f;
                shoot = new ShootBarrel();

                hitEffect = Fx.hitBulletColor;
                despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                    sizeTo = 16f;
                    colorFrom = colorTo = Color.valueOf("95b3b1");
                    lifetime = 12;
                }});
            }}
            );

            smokeEffect = Fx.shootSmokeSmite;
            drawer = new DrawTurret("atlacian-"){{
                var heatp = PartProgress.warmup.blend(p -> Mathf.absin(2f, 1f) * p.warmup, 0.2f);
                var haloProgress = PartProgress.warmup.delay(0.5f);
                float haloY = -10;
                parts.addAll(new RegionPart("-blade"){{
                                 progress = PartProgress.warmup;
                                 heatProgress = PartProgress.warmup;
                                 heatColor = Color.valueOf("ffffc4");
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
                    heatColor = Color.valueOf("ffffc4");
                }},

                new RegionPart("-mid"){{
                    heatProgress = heatp;
                    progress = PartProgress.warmup;
                    heatColor = Color.valueOf("ffffc4");
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
            reload = 15f;
            shootY = 8f;

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

            researchCost = with(fineSand,10);

            health = 60 * wallHealthMultiplier;
            envDisabled |= Env.scorching;
        }};

        finesandWallLarge = new Wall("finesand-wall-large"){{
            requirements(Category.defense, mult(finesandWall.requirements, 5));

            researchCost = with(fineSand,80);

            health = 60 * largeWallHealthMultiplier;
            size = 2;
            envDisabled |= Env.scorching;
        }};

        clayWall = new Wall("clay-wall"){{
            requirements(Category.defense, with(clay, 8));

            researchCost = with(clay,15);

            health = 60 * wallHealthMultiplier;
            envDisabled |= Env.scorching;
        }};

        clayWallLarge = new Wall("clay-wall-large"){{
            requirements(Category.defense, mult(clayWall.requirements, 4));

            researchCost = with(clay,100);

            health = 60 * largeWallHealthMultiplier;
            size = 2;
            envDisabled |= Env.scorching;
        }};

        tugSheetWall = new ShieldWall("tug-sheet-wall") {{
            requirements(Category.defense, with(tugSheet, 8));
            consumePower(3f / 60f);

            researchCost = with(tugSheet,150);

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
            researchCost = with(tugSheet,600);

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

            researchCost = with(iridium,800,chromium,500,corallite,400);

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
        }};

        regenerator = new MendProjector("regenerator"){{
            requirements(Category.effect, with(spaclanium, 60));
            researchCost = with(spaclanium,300);

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

        regenProjector = new MendProjector("regen-projector"){{
            requirements(Category.effect, with(spaclanium, 60, clay, 80, iridium, 10));
            researchCost = with(spaclanium,500,clay,280,iridium,100);

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

            researchCost = with(spaclanium,8);
        }};

        tower = new Buoy("tower") {{
            requirements(Category.effect, BuildVisibility.fogOnly, with(chromium,10,clay,10));
            fogRadius = 28;

            envDisabled |= Env.scorching;
            destructible = true;
            isWater = false;
            outlineIcon = true;

            consumePower(4f/60f);

            priority = 0;
            health = 360;

            researchCost = with(chromium,50,clay,50);
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

            researchCost = with(spaclanium,500,clay,100,sulfur,300);

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

            researchCost = with(spaclanium,5);
        }};

        centrifugalPump = new Pump("centrifugal-pump") {{
            requirements(Category.liquid, with(spaclanium, 32, clay, 30, iridium, 10));

            researchCost = with(spaclanium,300,clay,320,iridium,400);

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

            researchCost = with(clay,3);

            envDisabled |= Env.scorching;
            botColor = Color.valueOf("54333c");

            health = 45;
        }};

        highPressureConduit = new Conduit("high-pressure-conduit") {{
            requirements(Category.liquid, with(chromium, 1, clay, 1));

            researchCost = with(clay,400,chromium,100);

            envDisabled |= Env.scorching;
            botColor = Color.valueOf("54333c");
            liquidCapacity = 16f;
            liquidPressure = 1.225f;

            health = 80;
        }};

        conduitBridge = new DirectionLiquidBridge("bridge-conduit"){{
            requirements(Category.liquid, with(corallite, 4, clay, 8));

            researchCost = with(corallite,80,clay,40);
            ((Conduit) clayConduit).rotBridgeReplacement = this;
            ((Conduit) highPressureConduit).rotBridgeReplacement = this;
            range = 4;
            hasPower = false;

            envDisabled |= Env.scorching;
        }};

        conduitRouter = new LiquidRouter("liquid-router") {{
            requirements(Category.liquid, with(corallite, 4, clay, 2));

            researchCost = with(corallite,40,clay,10);

            liquidCapacity = 20f;
            underBullets = true;
            solid = false;



            envDisabled |= Env.scorching;
        }};

        waterDiffuser = new Separator("water-diffuser") {{
            requirements(Category.liquid, with(spaclanium, 20, corallite, 5));
            craftTime = 60f*2.5f;
            itemCapacity = 50;

            researchCost = with(spaclanium,3,corallite,1);

            squareSprite = false;

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
            researchCost = with(spaclanium,100,corallite,60,clay,50);

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
            researchCost = with(iridium,5,corallite,10);

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
            researchCost = with(iridium,30);
            consumesPower = outputsPower = true;
            health = 90;
            range = 5;
            squareSprite = false;

            consumePowerBuffered(1000f);
        }};

        accumulator = new Battery("accumulator"){{
            requirements(Category.power, with(iridium, 5, spaclanium, 20));
            consumePowerBuffered(4000f);

            researchCost = with(iridium,80,spaclanium,100);

            baseExplosiveness = 1f;
            drawer = new DrawMulti(new DrawDefault(), new DrawEnergyGlow());
        }};

        largeAccumulator = new Battery("large-accumulator"){{
            requirements(Category.power, mult(accumulator.requirements,4));
            consumePowerBuffered(4000f*5);

            researchCost = with(iridium,800,spaclanium,700);

            baseExplosiveness = 3f;
            drawer = new DrawMulti(new DrawDefault(), new DrawEnergyGlow());
            size = 2;
        }};

        spaclaniumHydrolyzer = new ConsumeGenerator("spaclanium-hydrolyzer") {{
            requirements(Category.power, with(corallite, 20, clay, 30, iridium, 25));

            researchCost = with(corallite,200,clay,150,iridium,100);

            powerProduction = 1.2f;
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
            requirements(Category.power,with(corallite,60,clay,15,iridium,30));

            researchCost = with(corallite,600,clay,300,iridium,300);

            ambientSound = Sounds.wind;
            ambientSoundVolume = 0.05f;

            powerProduction = 0.2f;
            size = 2;
        }};

        chromiumReactor = new ChromiumReactor("chromium-reactor"){{
            requirements(Category.power,with(chromium, 300, tugSheet, 50, corallite, 80, iridium, 100));

            researchCost = with(chromium,1800,tugSheet,500,corallite,3000,iridium,1950);

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

        vault = new StorageBlock("vault"){
            {
                requirements(Category.effect, with(chromium, 250, iridium, 125));

                researchCost = with(chromium,1000,iridium,2000);

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
                researchCost = with(chromium,2000,iridium,3500);
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

            researchCost = with(chromium,100,clay,300);

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
            requirements(Category.liquid, with(corallite, 30, clay, 35));

            researchCost = with(corallite,540,clay,350);

            liquidCapacity = 700f;
            size = 2;
            liquidPadding = 3f / 4f;

            solid = true;
            squareSprite = false;
        }};

        liquidTank = new LiquidRouter("liquid-tank"){{
            requirements(Category.liquid, with(corallite,80, clay, 140, iridium, 30));
            researchCost = with(corallite,1040,clay,870,iridium,500);
            liquidCapacity = 1800f;
            health = 500;
            size = 3;
            liquidPadding = 6f / 4f;

            solid = true;
            squareSprite = false;
        }};

        corePuffer = new SubvoyageCoreBlock("core-puffer"){{
            requirements(Category.effect, with(spaclanium,600,corallite,600,clay,300,sulfur,300));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.editorOnly;
            isFirstTier = true;
            unitType = marine;
            health = 4000;
            itemCapacity = 3000;
            size = 4;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 7;

            bannedItems.addAll(crude);
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };

        coreShore = new SubvoyageCoreBlock("core-shore"){{
            requirements(Category.effect, with(spaclanium,1500,corallite,1200,chromium,1300,iridium,800));

            researchCost = with(spaclanium,3000,corallite,3000,chromium,1500,iridium,1500);

            unitType = marine;
            health = 12000;
            itemCapacity = 8000;
            size = 5;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 12;

            bannedItems.addAll(crude);
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };

        coreReef = new SubvoyageCoreBlock("core-reef"){{
            requirements(Category.effect, with(spaclanium,3500,corallite,2200,chromium,1300,iridium,1000,quartzFiber,1000,tugSheet,800));

            researchCost = with(spaclanium,8000,corallite,8000,chromium,5000,iridium,3000,quartzFiber,1000,tugSheet,1000);
            unitType = marine;
            health = 24000;
            itemCapacity = 12000;
            size = 6;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 20;

            bannedItems.addAll(crude);
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };

        //transport

        duct = new Duct("duct"){{
            requirements(Category.distribution, with(corallite, 1));
            health = 90;
            speed = 4f;
            envDisabled |= Env.scorching;

            researchCost = with(corallite,2);
        }};

        highPressureDuct = new Duct("high-pressure-duct") {{
            requirements(Category.distribution,with(chromium,1,corallite,1));
            researchCost = with(chromium,500,corallite,900);
            health = 180;
            speed = 3.2f;
            envDisabled |= Env.scorching;
        }};

        ductBridge = new DuctBridge("duct-bridge") {{
            requirements(Category.distribution, with(corallite, 4,spaclanium,2));
            researchCost = with(corallite, 16, spaclanium, 4);

            ((Duct) duct).bridgeReplacement = this;
            ((Duct) highPressureDuct).bridgeReplacement = this;

            envDisabled |= Env.scorching;
            health = 90;
            speed = 4f;
        }};

        ductRouter = new Router("duct-router") {{
            requirements(Category.distribution, with(corallite, 3));
            researchCost = with(corallite,16);
            envDisabled |= Env.scorching;
        }};
        
        ductSorter = new Sorter("duct-sorter"){{
            requirements(Category.distribution, with(corallite, 2, spaclanium, 2));
            researchCost = with(corallite,100,spaclanium,350);
            buildCostMultiplier = 3f;
            envDisabled |= Env.scorching;
        }};

        ductDistributor = new Router("duct-distributor"){{
            requirements(Category.distribution, with(corallite, 4, spaclanium, 4));
            researchCost = with(corallite,320,spaclanium,70);
            buildCostMultiplier = 3f;
            size = 2;
            squareSprite = false;
        }};

        ductOverflow = new OverflowGate("duct-overflow-gate"){{
            requirements(Category.distribution, with(corallite, 2, spaclanium, 4));
            researchCost = with(corallite,300,spaclanium,300);
            buildCostMultiplier = 3f;
        }};

        ductUnderflow = new OverflowGate("duct-underflow-gate"){{
            requirements(Category.distribution, with(corallite, 2, spaclanium, 4));
            researchCost = with(corallite,300,spaclanium,300);
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

            researchCost = with(iridium,1000,clay,700);

            size = 3;
            itemCapacity = 250;

            buildTime = 6f*60f;

            consumePower(12f/60f);
            consumeLiquid(argon,8f/60f);

            unitType = bulker;
        }};

        shipUnloadPoint = new UnitCargoUnloadPoint("ship-unload-point") {{
            requirements(Category.distribution,with(iridium,50,clay,80));

            researchCost = with(iridium,700,clay,500);

            size = 2;
            itemCapacity = 50;
        }};


        //crafters
        ceramicBurner = new GenericCrafter("ceramic-burner") {{
            requirements(Category.crafting,with(spaclanium,30,corallite,70,fineSand,30));
            craftEffect = SvFx.smokePuff;
            craftTime = 60f*2;

            researchCost = with(spaclanium,100,corallite,60,fineSand,10);

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

            researchCost = with(spaclanium,1000,corallite,1600,fineSand,700,iridium,780);

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

            consumeLiquid(water,1);
            consumeItem(corallite,1);
        }};

        poweredEnhancer = new GenericCrafter("powered-enhancer") {{
            requirements(Category.crafting, with(chromium, 200, iridium, 100));
            outputLiquid = new LiquidStack(SvLiquids.polygen, 2);
            craftTime = 1f;

            researchCost = with(chromium,1600,iridium,3200);

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

            researchCost = with(spaclanium,800,corallite,700,sulfur,900);

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

            researchCost = with(chromium,3500,iridium,5310,tugSheet,3210);

            squareSprite = false;
            itemCapacity = 10;
            size = 3;
            craftEffect = Fx.none;
            craftTime = 20f;
            envDisabled |= Env.scorching;

            consumeItem(sulfur,2);
            consumeItem(corallite, 4);
            consumePower(1.3f);
            outputLiquid = new LiquidStack(argon, 30/60f);
            hasLiquids = true;
            drawer = new DrawMulti(
                    new DrawDefault(),
            new DrawLiquidRegion(argon),
            new DrawArcSmelt(){{
                flameColor = Color.valueOf("bd4453");
                midColor = Color.valueOf("ff8c99");
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
            }},
            new DrawGlowRegion()
            );
        }};


        crudeSmelter = new CrudeSmelter("crude-smelter") {{
            requirements(Category.crafting,with(spaclanium,100,iridium,50,clay,30));
            researchCost = with(spaclanium,1000,iridium,1600,clay,600);

            itemCapacity = 30;
            size = 2;
            craftEffect = Fx.smokePuff;
            recipes = recipes(spaclanium, 2, 120, corallite, 2, 120, iridium, 1, 160, chromium, 1, 190);

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
            requirements(Category.crafting, with(spaclanium, 500, iridium, 510, clay, 530,chromium,350));
            researchCost = with(spaclanium, 5000, iridium, 5600, clay, 3600,chromium,2750);

            squareSprite = false;

            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.smokePuff;
            recipes = recipes(spaclanium, 2, 60, corallite, 2, 60, iridium, 1, 90, chromium, 1, 120);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawFlame(){{
                        lightRadius = 70f;
                        flameRadius = 6f;
                    }}
            );
            consumeItem(crude, 3);
            consumeLiquid(water, 1f);
            consumePower(1.2f);

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};

        quartzScutcher = new AttributeCrafter("quartz-scutcher") {{
            requirements(Category.crafting,with(chromium,220,spaclanium,120,fineSand,80,iridium,30));

            researchCost = with(chromium,3000,spaclanium,5000,fineSand,3500,iridium,6000);

            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.smeltsmoke;
            craftTime = 100f;
            envDisabled |= Env.scorching;

            consumeItem(spaclanium,6);
            consumeItem(fineSand,8);
            consumeLiquid(argon,1.2f);
            consumePower(6f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawColorWeave(Color.valueOf("FDE8E2")), new DrawDefault());
            outputItem = new ItemStack(quartzFiber,2);

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};

        tugRoller = new TugRoller("tug-roller"){{
            requirements(Category.crafting,with(chromium,220,iridium,140,quartzFiber,60,sulfur,120));

            researchCost = with(chromium,5000,iridium,7000,quartzFiber,5000,sulfur,3200);

            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.generatespark;
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
