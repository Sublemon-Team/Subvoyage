package subvoyage.content.blocks;

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
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import subvoyage.content.*;
import subvoyage.content.blocks.crude_smelter.*;
import subvoyage.content.blocks.editor.*;
import subvoyage.content.blocks.editor.offload_core.*;
import subvoyage.content.blocks.energy.*;
import subvoyage.content.blocks.fog.*;
import subvoyage.content.blocks.production.*;
import subvoyage.content.liquids.*;
import subvoyage.content.world.*;
import subvoyage.content.world.draw.*;
import subvoyage.content.world.planets.*;
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
            whirl, rupture, awe, resonance, burden, cascade, inspiration,
            finesandWall, finesandWallLarge,
            clayWall,clayWallLarge,
            tugSheetWall, tugSheetWallLarge,
            coreDecoder, coreDecrypter,
            regenerator, regenProjector,
            //CRAFTERS
            waterMetallizer, poweredEnhancer, ceramicBurner, terracottaBlaster, argonCentrifuge, argonCondenser,
                    propanePyrolyzer, heliumCompressor,
            crudeSmelter, crudeCrucible,
            quartzScutcher, tugRoller,
            //LIQUIDS
            waterDiffuser,waterSifter, lowTierPump, centrifugalPump, clayConduit, highPressureConduit, conduitRouter, conduitBridge,
            //ENERGY
            energyDock, energyDistributor, accumulator, largeAccumulator, spaclaniumHydrolyzer, windTurbine, hydrocarbonicGenerator, chromiumReactor,
            //TRANSPORTATION
            duct,highPressureDuct,ductRouter,ductBridge,ductSorter, ductUnderflow, ductOverflow, ductDistributor,
            shipCargoStation, shipUnloadPoint,
            //PAYLOAD
            helicopterFactory,hydromechFactory,
            //EXPLORATION
            buoy,tower,beacon,
            //STORAGE
            corePuffer,coreShore,coreReef, vault, largeVault, unloader, liquidContainer, liquidTank;

    public static void load() {
        //non-user
        offloadCore = new OffloadCore("offload-core") {{
            requirements(Category.logic, BuildVisibility.editorOnly, with());
            health = 3000;
            size = 3;

            lowTierUnits = new UnitType[] {lapetus,leeft};
            midTierUnits = new UnitType[] {skath,flagshi};
            highTierUnits = new UnitType[] {charon,callees,vanguard,squadron};

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
            requirements(Category.units, atl(), with(iridium, 60, clay, 70));

            researchCost = with(iridium,400,clay,500);

            consumeLiquid(argon,0.2f);
            plans = Seq.with(
                    new UnitPlan(lapetus, 60f * 15, with(iridium, 15))
            );
            size = 3;
            consumePower(1.2f);
        }};

        hydromechFactory = new UnitFactory("hydromech-factory") {{
            requirements(Category.units, atl(), with(iridium, 60, clay, 70, chromium, 30));

            researchCost = with(iridium,600,clay,600,chromium,120);

            consumeLiquid(helium,1.2f);
            plans = Seq.with(
                    new UnitPlan(leeft, 60f * 10, with(iridium, 20))
            );
            size = 3;
            consumePower(1f);
        }};

        //drills
        submersibleDrill = new SubmersibleDrill("submersible-drill") {{
            requirements(Category.production,atl(), with(corallite, 50, spaclanium, 10));
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
            requirements(Category.production,atl(), with(corallite, 100, chromium, 50, iridium, 100, clay, 200));

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

            consumeLiquid(argon, 0.2f);
            consumeCoolant(1.2f);
        }};
        tectonicDrill = new AttributeCrafter("tectonic-drill") {{
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

            boostScale = 1f / 9f;

            craftEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.orangeSpark, 20f));
            drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawGlowRegion(){{
                alpha = 0.75f;
                glowScale = 6f;
                color = SvPal.heatGlow;
            }},

            new DrawBlurSpin("-rotator", 4),
            new DrawDefault(),
            new DrawRegion("-top")
            );

            fogRadius = 4;
            squareSprite = false;
            consumePower(0.2f);
        }};

        //defense
        whirl = new ItemTurret("whirl"){{
            requirements(Category.turret,atl(), with(corallite, 85, clay, 45));
            outlineColor = Pal.darkOutline;
            squareSprite = false;

            researchCost = with(corallite,10,clay,10);

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
            coolant = consume(new ConsumeLiquid(nitrogen, 20f / 60f));

            limitRange(2);
        }};

        rupture = new ItemTurret("rupture"){{
            requirements(Category.turret,atl(), with(corallite, 145, clay, 125, iridium, 30));
            size = 3;
            outlineColor = Pal.darkOutline;
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
            reload = 80f;
            shootY = 12f;
            recoil = 0.5f;
            priority = 0;
            range = 260f;
            scaledHealth = 200;

            coolant = consume(new ConsumeLiquid(nitrogen, 20f / 60f));

            limitRange(6);
        }};

        awe = new PowerTurret("awe") {{
            requirements(Category.turret,atl(), with(corallite, 85, iridium, 40, spaclanium, 20));
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
            coolant = consume(new ConsumeLiquid(nitrogen, 20f / 60f));
        }};

        resonance = new PowerTurret("resonance") {{
            requirements(Category.turret,atl(), with(corallite, 185, iridium, 140,chromium,80));

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

                fragBullet = new BasicBulletType(6f, 6) {{
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
            coolant = consume(new ConsumeLiquid(nitrogen, 20f / 60f));
        }};
        inspiration = new TractorBeamTurret("inspiration"){{
            requirements(Category.turret,atl(), with(corallite,200,iridium,150,chromium,35));

            researchCost = with(corallite,1400,iridium,1200,chromium,300);

            targetAir = true;
            targetGround = false;

            hasPower = true;
            hasLiquids = true;
            size = 2;
            force = 20f;
            scaledForce = 6f;
            range = 240f;
            damage = 2f;
            scaledHealth = 160;
            rotateSpeed = 10;

            laserColor = SvPal.inspirationLaser;

            consumePower(3f);
            consumeLiquid(argon,0.3f);
            consumeLiquid(helium,0.4f);

            coolant = consume(new ConsumeLiquid(nitrogen, 20f / 60f));
        }};

        burden = new LiquidTurret("burden") {{
            outlineColor = Pal.darkOutline;
            requirements(Category.turret,atl(), with(clay,200,iridium,140,chromium,120));
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
            requirements(Category.turret,atl(), with(clay,300,iridium,150,chromium,50,spaclanium,80));

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

                hitColor = backColor = trailColor = SvPal.chromiumLightish;
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

                hitColor = backColor = trailColor = SvPal.tugSheetLightish;
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
                    colorFrom = colorTo = SvPal.tugSheetLightish;
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
            reload = 15f;
            shootY = 8f;

            recoil = 0.5f;
            priority = 0;
            range = 180f;
            scaledHealth = 200;

            coolantMultiplier = 1.2f;
            coolant = consume(new ConsumeLiquid(nitrogen, 20f / 60f));

            limitRange(6);
        }};


        int wallHealthMultiplier = 4;
        int largeWallHealthMultiplier = 20;
        finesandWall = new Wall("finesand-wall"){{
            requirements(Category.defense,atl(), with(fineSand, 8));

            researchCost = with(fineSand,10);

            health = 60 * wallHealthMultiplier;
            envDisabled |= Env.scorching;
        }};

        finesandWallLarge = new Wall("finesand-wall-large"){{
            requirements(Category.defense,atl(), mult(finesandWall.requirements, 5));

            researchCost = with(fineSand,80);

            health = 60 * largeWallHealthMultiplier;
            size = 2;
            envDisabled |= Env.scorching;
        }};

        clayWall = new Wall("clay-wall"){{
            requirements(Category.defense,atl(), with(clay, 8));

            researchCost = with(clay,15);

            health = 160 * wallHealthMultiplier;
            envDisabled |= Env.scorching;
        }};

        clayWallLarge = new Wall("clay-wall-large"){{
            requirements(Category.defense,atl(), mult(clayWall.requirements, 4));

            researchCost = with(clay,100);

            health = 160 * largeWallHealthMultiplier;
            size = 2;
            envDisabled |= Env.scorching;
        }};

        tugSheetWall = new ShieldWall("tug-sheet-wall") {{
            requirements(Category.defense,atl(), with(tugSheet, 8));
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
            armor = 15f;
            health = 200 * wallHealthMultiplier;
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
            armor = 15f;
            health = 200 * largeWallHealthMultiplier;
            envDisabled |= Env.scorching;
            size = 2;
        }};

        coreDecoder = new CoreDecoder("core-decoder") {{
            requirements(Category.effect,atl(),with(iridium,400,chromium,300,quartzFiber,250));

            researchCost = with(iridium,800,chromium,500,corallite,400);

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

        regenerator = new MendProjector("regenerator"){{
            requirements(Category.effect,atl(), with(spaclanium, 60));
            researchCost = with(spaclanium,300);

            consumePower(0.15f);
            consumeLiquid(polygen,0.3f);

            size = 1;
            reload = 200f;
            range = 40f;
            healPercent = 4f;
            phaseBoost = 4f;
            phaseRangeBoost = 20f;
            health = 80;
        }};

        regenProjector = new MendProjector("regen-projector"){{
            requirements(Category.effect,atl(), with(spaclanium, 60, clay, 80, iridium, 10));
            researchCost = with(spaclanium,500,clay,280,iridium,100);

            consumePower(0.3f);
            consumeLiquid(polygen,0.5f);

            size = 2;
            reload = 100f;
            range = 40f*2;
            healPercent = 16f;
            phaseBoost = 4f;
            phaseRangeBoost = 20f;
            health = 400;
        }};

        //exploration
        buoy = new Buoy("buoy") {{
            requirements(Category.effect,atl(BuildVisibility.fogOnly), with(spaclanium,20));
            alwaysUnlocked = true;
            fogRadius = 32;
            envDisabled |= Env.scorching;
            destructible = true;

            priority = 0;
            health = 120;

            researchCost = with(spaclanium,8);
        }};

        tower = new Buoy("tower") {{
            requirements(Category.effect,atl(BuildVisibility.fogOnly), with(chromium,10,clay,10));
            fogRadius = 40;

            envDisabled |= Env.scorching;
            destructible = true;
            isWater = false;
            outlineIcon = true;

            discoveryTime *= 1.5f;

            consumeLiquid(polygen,0.3f);

            priority = 0;
            health = 360;

            researchCost = with(chromium,50,clay,50);
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
            discover = consumeLiquid(argon,2f);
        }};

        //liquids
        lowTierPump = new Pump("lead-pump") {{
            requirements(Category.liquid,atl(), with(spaclanium, 8));

            squareSprite = false;
            envDisabled |= Env.scorching;
            pumpAmount = 15f / 60f;

            researchCost = with(spaclanium,5);
        }};

        centrifugalPump = new Pump("centrifugal-pump") {{
            requirements(Category.liquid,atl(), with(spaclanium, 32, clay, 30, iridium, 10));

            researchCost = with(spaclanium,300,clay,320,iridium,400);

            size = 2;

            squareSprite = false;
            envDisabled |= Env.scorching;
            pumpAmount =20f / 60f;
            consumePower(2.5f/60f);

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
            requirements(Category.liquid,atl(), with(clay, 1));

            researchCost = with(clay,3);

            envDisabled |= Env.scorching;
            botColor = SvPal.clayDarkish;

            health = 45;
        }};

        highPressureConduit = new Conduit("high-pressure-conduit") {{
            requirements(Category.liquid,atl(), with(chromium, 1, clay, 1));

            researchCost = with(clay,400,chromium,100);

            envDisabled |= Env.scorching;
            botColor = SvPal.clayDarkish;
            liquidCapacity = 16f;
            liquidPressure = 1.225f;

            health = 80;
        }};

        conduitBridge = new DirectionLiquidBridge("bridge-conduit"){{
            requirements(Category.liquid,atl(), with(corallite, 4, clay, 8));

            researchCost = with(corallite,80,clay,40);
            ((Conduit) clayConduit).rotBridgeReplacement = this;
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

        waterDiffuser = new Separator("water-diffuser") {{
            requirements(Category.liquid,atl(), with(spaclanium, 30, corallite, 10));
            size = 2;
            craftTime = 60f;
            itemCapacity = 50;

            researchCost = with(spaclanium,3,corallite,1);

            squareSprite = false;
            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawLiquidRegion(water)
            );
            consumeLiquid(water, 6/60f);
            envDisabled |= Env.scorching;
            results = with(
                    spaclanium,3,
                    corallite,3,
                    fineSand,2,
                    sulfur,1
            );
        }};

        waterSifter = new WaterSifter("water-sifter") {{
            requirements(Category.liquid,atl(), with(spaclanium,50, corallite, 60,clay,30));
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
                new WarmupDrawRegion("-top", true)
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
            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawEnergyLinksGlow()
            );
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
            requirements(Category.power,atl(), with(iridium, 5, spaclanium, 20));
            consumePowerBuffered(4000f);

            researchCost = with(iridium,80,spaclanium,100);

            baseExplosiveness = 1f;
            drawer = new DrawMulti(new DrawDefault(), new DrawEnergyGlow());
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
            requirements(Category.power,atl(),with(corallite,300,clay,100,iridium,200, chromium,10));

            researchCost = with(corallite,700,clay,350,iridium,350,chromium,50);

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
            requirements(Category.power,atl(),with(chromium, 300, tugSheet, 50, corallite, 80, iridium, 100));

            researchCost = with(chromium,1800,tugSheet,500,corallite,3000,iridium,1950);

            size = 3;
            squareSprite = false;
            health = 900;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.24f;
            powerProduction = 60;

            consumePower(12f);
            consumeItem(chromium,2);
            consumeLiquid(polygen, heating / coolantPower).update(false);
        }};

        //storage

        vault = new StorageBlock("vault"){
            {
                requirements(Category.effect,atl(), with(chromium, 250, iridium, 125));

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
                requirements(Category.effect,atl(), with(chromium, 500, iridium, 345));
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
            requirements(Category.effect,atl(), with(chromium, 25, clay, 30));

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
            requirements(Category.effect,atl(), with(spaclanium,1500,corallite,1200,chromium,1300,iridium,800));

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
            requirements(Category.effect,atl(), with(spaclanium,3500,corallite,2200,chromium,1300,iridium,1000,quartzFiber,1000,tugSheet,800));

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
            requirements(Category.distribution,atl(), with(corallite, 1));
            health = 90;
            speed = 4f;
            envDisabled |= Env.scorching;

            researchCost = with(corallite,2);
        }};

        highPressureDuct = new Duct("high-pressure-duct") {{
            requirements(Category.distribution,atl(),with(chromium,1,corallite,1));
            researchCost = with(chromium,500,corallite,900);
            health = 180;
            speed = 3.2f;
            envDisabled |= Env.scorching;
        }};

        ductBridge = new DuctBridge("duct-bridge") {{
            requirements(Category.distribution,atl(), with(corallite, 4,spaclanium,2));
            researchCost = with(corallite, 16, spaclanium, 4);

            ((Duct) duct).bridgeReplacement = this;
            ((Duct) highPressureDuct).bridgeReplacement = this;

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
            requirements(Category.crafting,atl(),with(spaclanium,30,corallite,70,fineSand,30));
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
            requirements(Category.crafting,atl(),with(spaclanium,100,corallite,200,fineSand,120,iridium,40));

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
            consumeLiquid(water, 0.8f);
            consumeItem(fineSand,2);
            consumeLiquid(propane, 0.5f);
            consumePower(2.3f);
        }};

        waterMetallizer = new GenericCrafter("water-metallizer") {{
            requirements(Category.crafting,atl(), with(spaclanium,100,corallite,60));
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
            requirements(Category.crafting,atl(), with(chromium, 200, iridium, 100));
            craftTime = 10f;

            researchCost = with(chromium,1600,iridium,3200);

            itemCapacity = 30;
            size = 3;

            regionRotated1 = 3;
            outputLiquids = LiquidStack.with(SvLiquids.polygen, 2f, nitrogen, .4f);
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

            consumeLiquid(water,0.5f);
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
            outputLiquid = new LiquidStack(argon, 16/60f);
            hasLiquids = true;
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawLiquidRegion(argon)
            );
        }};

        argonCondenser = new GenericCrafter("argon-condenser") {{
            requirements(Category.crafting,atl(), with(chromium,120,iridium,60,quartzFiber,10));

            researchCost = with(chromium,3500,iridium,5310,quartzFiber,3210);

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
            outputLiquid = new LiquidStack(argon, 50/60f);
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
            }},
            new DrawGlowRegion()
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
                    new DrawLiquidRegion(propane),
                    new DrawHeatGlow(),
                    new DrawArcSmelt(){{
                        flameColor = SvPal.propane;
                        midColor = SvPal.propane;
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

            outputLiquid = new LiquidStack(propane,1.5f);
            hasLiquids = true;
        }};

        heliumCompressor = new GenericCrafter("helium-compressor") {{
            requirements(Category.crafting,atl(),with(chromium,120,iridium,120,corallite,300,clay,100));
            researchCost = with(chromium,500,iridium,600,corallite,1200,clay,800);

            itemCapacity = 20;
            size = 3;
            craftEffect = Fx.smokeCloud;
            craftTime = 30f;
            envDisabled |= Env.scorching;

            consumeLiquid(water,0.15f);
            consumeLiquid(propane,0.2f);
            consumePower(0.6f);

            outputLiquid = new LiquidStack(helium,1.35f);
            hasLiquids = true;
        }};


        crudeSmelter = new CrudeSmelter("crude-smelter") {{
            requirements(Category.crafting,atl(),with(spaclanium,100,iridium,50,clay,30));
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
            requirements(Category.crafting,atl(), with(spaclanium, 500, iridium, 510, clay, 530,chromium,350));
            researchCost = with(spaclanium, 5000, iridium, 5600, clay, 3600,chromium,2750);

            squareSprite = false;

            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.smokePuff;
            recipes = recipes(spaclanium, 4, 60, corallite, 3, 80, iridium, 2, 90, chromium, 1, 120);

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

        quartzScutcher = new AttributeCrafter("quartz-scutcher") {{
            requirements(Category.crafting,atl(),with(chromium,220,spaclanium,120,fineSand,80,iridium,30));

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
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawColorWeave(SvPal.quartzWeave), new DrawDefault());
            outputItem = new ItemStack(quartzFiber,2);

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};

        tugRoller = new TugRoller("tug-roller"){{
            requirements(Category.crafting,atl(),with(chromium,220,iridium,140,quartzFiber,60,sulfur,120));

            researchCost = with(chromium,5000,iridium,7000,quartzFiber,5000,sulfur,3200);

            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.generatespark;
            craftTime = 100f;
            envDisabled |= Env.scorching;

            consumeItem(chromium,6);
            consumeItem(sulfur,8);
            consumeLiquid(polygen,0.8f);
            consumeLiquid(helium,1.3f);
            consumePower(9f);

            outputItem = new ItemStack(tugSheet,1);
            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};
    }

    static BuildVisibility atl(BuildVisibility v) {
        return new BuildVisibility(() -> v.visible() &&  Vars.state.rules.planet == SvPlanets.atlacian);
    }
    static BuildVisibility atl() {
        return atl(BuildVisibility.shown);
    }
}
