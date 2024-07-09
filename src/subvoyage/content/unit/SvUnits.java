package subvoyage.content.unit;

import arc.graphics.*;
import arc.math.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.type.unit.*;
import mindustry.type.weapons.*;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import subvoyage.*;
import subvoyage.content.SvPal;
import subvoyage.content.unit.ai.StraightMissileAI;
import subvoyage.content.unit.bullet.*;
import subvoyage.content.unit.entity.*;
import subvoyage.content.unit.type.*;
import subvoyage.content.unit.weapons.*;
import subvoyage.content.world.*;
import subvoyage.entities.part.*;
import subvoyage.entities.shoot.*;

import java.security.cert.Certificate;

public class SvUnits{
    public static UnitType
    // core
    marine,
    //helicopters
    lapetus, skath, charon, callees,ganymede,
    //hydromechs
    leeft, flagshi, vanguard,
    //cargo
    bulker;

    public static int mapHelicopter = 0;
    public static int mapHMech = 0;
    public static void load(){
        helicopter("lapetus", "skath", "charon", "callees", "ganymede");
        hmech("leeft", "flagshi", "vanguard");
        //core
        marine = new AtlacianUnitType("marine"){{
            aiController = BuilderAI::new;
            constructor = UnitEntity::create;
            isEnemy = false;
            coreUnitDock = true;
            lowAltitude = true;
            flying = true;

            mineSpeed = 8f;
            mineTier = 1;
            buildSpeed = 1f;
            drag = 0.05f;
            speed = 2.6f;
            rotateSpeed = 15f;
            accel = 0.1f;
            fogRadius = 0f;
            itemCapacity = 30;
            health = 400f;
            engineOffset = 6.5f;
            engineSize = 0;
            hitSize = 8f;

            setEnginesMirror(
                    new UnitEngine(19 / 4f, -24 / 4f, 2.5f, 315f)
            );

            ammoType = new PowerAmmoType(900);
            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                y = -1.25f;
                x = 6.5f;
                reload = 15f;
                ejectEffect = Fx.casing1;
                recoil = 2f;
                shootSound = Sounds.bolt;
                velocityRnd = 0f;
                inaccuracy = 0f;
                alternate = true;
                fogRadius = 0;
                lightRadius = 8;
                bullet = new LaserBulletType(10){{
                    colors = new Color[]{Pal.accent.cpy().a(0.4f), Pal.accent, Color.white};
                    //TODO merge
                    chargeEffect = new MultiEffect(Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin);

                    buildingDamageMultiplier = 0f;
                    hitEffect = Fx.hitLancer;
                    hitSize = 4;
                    lifetime = 16f;
                    drawSize = 400f;
                    collidesAir = false;
                    length = 40f;
                    ammoMultiplier = 1f;
                    pierceCap = 4;
                }};
            }});

            weapons.add(new RepairBeamWeapon(){{
                widthSinMag = 0.11f;
                reload = 20f;
                x = 0f;
                y = 6.5f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.7f;
                repairSpeed = 3.1f;
                fractionRepairSpeed = 0.06f;
                aimDst = 0f;
                shootCone = 15f;
                mirror = false;

                targetUnits = false;
                targetBuildings = true;
                autoTarget = true;
                controllable =false;
                laserColor = Pal.accent;
                healColor = Pal.accent;

                bullet = new BulletType(){{
                    maxRange = 60f;
                }};
            }});
        }};

        //helicopter
        lapetus = new HelicopterUnitType("lapetus"){{
            aiController = FlyingAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.05f;
            speed = 1.6f;
            rotateSpeed = 4f;
            accel = 0.1f;
            health = 800f;
            engineSize = 0;
            hitSize = 20f;
            RotatorRegionPart copter = new RotatorRegionPart(){
                {
                    layer = Layer.flyingUnitLow;
                    xScl = 1.5f;
                    yScl = 1.5f;
                    y = -0.15f;
                    rotationSpeed = 400f;
                }
            };

            onDraw = (e) -> {
                copter.unitRot = e.rotation();
                copter.unitX = e.x;
                copter.unitY = e.y;
            };

            parts.add(copter);
            weapons.add(new Weapon(SubvoyageMod.ID + "-marine-weapon"){{
                x = 5f;
                layerOffset = -2;
                reload = 60f;
                recoil = 2f;
                shootSound = Sounds.missileLaunch;
                velocityRnd = 0f;
                inaccuracy = 0f;
                top = false;
                alternate = false;
                bullet = new BasicBulletType(3f, 8){{
                    sprite = "missile-large";
                    width = 6f;
                    height = 13f;
                    lifetime = 70f;
                    hitSize = 6f;
                    hitColor = backColor = trailColor = SvPal.heatGlow;
                    frontColor = Color.white;
                    trailWidth = 2f;
                    trailLength = 8;
                    hitEffect = despawnEffect = Fx.blastExplosion;
                    smokeEffect = SvFx.shootLauncher;
                    splashDamageRadius = 10f;
                    splashDamage = 20f;

                    trailEffect = SvFx.missileTrailSmoke;
                    trailRotation = true;
                    trailInterval = 3f;
                }};
            }});
        }};

        skath = new HelicopterUnitType("skath"){{
            aiController = FlyingAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.15f;
            speed = 1.3f;
            rotateSpeed = 3f;
            accel = 0.25f;
            health = 2250f;

            engineOffset = -7.5f;
            engineSize = 0;
            hitSize = 31f;
            RotatorRegionPart copter = new RotatorRegionPart(){{
                layer = Layer.flyingUnitLow;
                xScl = 1.27f;
                yScl = 1.27f;
                y = 2.47f;
                rotationSpeed = 400f;
            }};

            RotatorRegionPart tail = new RotatorRegionPart(){{
                layer = Layer.flyingUnitLow;
                xScl = 0.75f;
                yScl = 0.75f;
                y = -10.5f;
                rotationSpeed = 400f;
            }};

            onDraw = (e) -> {
                copter.unitRot = e.rotation();
                copter.unitX = e.x;
                copter.unitY = e.y;

                tail.unitRot = e.rotation();
                tail.unitX = e.x;
                tail.unitY = e.y;
            };

            parts.addAll(copter, tail);
            weapons.add(new Weapon(SubvoyageMod.ID + "-missile-launcher"){{
                x = 7f;
                y = -2f;
                reload = 180f;
                recoil = 2f;
                shootSound = Sounds.mediumCannon;

                top = false;
                alternate = false;
                shoot.shots = 8;
                shoot.shotDelay = 15f;
                bullet = new BasicBulletType(){{
                    sprite = "missile-large";
                    width = height = 8f;
                    maxRange = 50f;
                    ignoreRotation = true;

                    hitColor = trailColor = SvPal.heatGlow;
                    frontColor = Color.white;
                    trailWidth = 2f;
                    trailLength = 8;
                    hitEffect = despawnEffect = Fx.blastExplosion;
                    smokeEffect = SvFx.shootLauncher;
                    hitSound = Sounds.plasmaboom;

                    backColor = SvPal.heatGlow;
                    frontColor = Color.white;
                    mixColorTo = Color.white;

                    ejectEffect = Fx.none;
                    hitSize = 22f;

                    collidesAir = true;
                    lifetime = 87f;

                    hitEffect = new MultiEffect(Fx.blastExplosion, Fx.smokeCloud);
                    keepVelocity = false;
                    weaveMag = 2f;
                    weaveScale = 1f;
                    speed = 0.8f;
                    drag = -0.020f;
                    homingPower = 0.05f;

                    splashDamage = 2f;
                    splashDamageRadius = 20f;
                }};
            }});
        }};

        charon = new HelicopterUnitType("charon"){{
            aiController = FlyingAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.16f;
            speed = 1.5f;
            rotateSpeed = 2f;
            accel = 0.45f;
            health = 4220f;

            engineOffset = -7.5f;
            engineSize = 0;
            hitSize = 32f;
            RotatorRegionPart copter = new RotatorRegionPart(){{
                    layer = Layer.flyingUnitLow;
                    xScl = 2f;
                    yScl = 2f;
                    y = -0.15f;
                    rotationSpeed = 400f;
            }};

            onDraw = (e) -> {
                copter.unitRot = e.rotation();
                copter.unitX = e.x;
                copter.unitY = e.y;
            };

            parts.add(copter);
            abilities.add(
            new MoveEffectAbility(3, engineOffset - 4, Pal.sapBulletBack, SvFx.missileTrailShort, 1.5f){{
                teamColor = true;
            }},

            new MoveEffectAbility(-3, engineOffset - 4, Pal.sapBulletBack, SvFx.missileTrailShort, 1.5f){{
                teamColor = true;
            }}
            );

            weapons.add(new PointDefenseWeapon(name + "-point-defense"){{
                top = false;
                mirror = true;
                alternate = false;

                x = 4f;
                y = -9.25f;

                reload = 50f;
                targetInterval = 10f;
                targetSwitchInterval = 15f;
                bullet = new BulletType(){{
                    shootEffect = Fx.sparkShoot;
                    hitEffect = Fx.pointHit;
                    maxRange = 100f;
                    damage = 17f;
                }};
            }});

            weapons.add(new Weapon(SubvoyageMod.ID + "-rocket-launcher"){{
                top = false;
                alternate = false;
                x = 6f;
                y = 2f;
                reload = 120f;
                recoil = 2f;
                shootSound = Sounds.missileLaunch;
                bullet = new BulletType(){{
                    shootEffect = Fx.sparkShoot;
                    smokeEffect = Fx.shootSmokeTitan;
                    hitColor = Pal.suppress;
                    shake = 1f;
                    speed = 0f;
                    keepVelocity = false;
                    collidesAir = true;
                    spawnUnit = new MissileUnitType("charon-missile"){{
                        outlineColor = Pal.darkOutline;
                        trailRotation = true;
                        targetAir = true;
                        physics = true;
                        lowAltitude = true;

                        hitEffect = despawnEffect = Fx.blastExplosion;
                        smokeEffect = SvFx.shootLauncher;
                        trailEffect = SvFx.missileTrailSmoke;
                        trailInterval = 3f;
                        trailWidth = 1f;
                        trailLength = 6;

                        speed = 4.6f;
                        maxRange = 5f;
                        health = 40;
                        homingDelay = 5f;

                        engineSize = 3f;
                        hitColor = engineColor = trailColor = SvPal.heatGlow;
                        engineLayer = Layer.effect;
                        deathExplosionEffect = Fx.none;
                        loopSoundVolume = 0.1f;
                        parts.add(new ShapePart(){{
                            layer = Layer.effect;
                            circle = true;
                            y = -0.25f;
                            radius = 1.5f;
                            color = SvPal.heatGlow;
                            colorTo = Color.white;
                            progress = PartProgress.life.curve(Interp.pow5In);
                        }});

                        parts.add(new RegionPart("-fin"){{
                            mirror = true;
                            progress = PartProgress.life.mul(3f).curve(Interp.pow5In);
                            moveRot = 32f;
                            rotation = -6f;
                            moveY = 1.5f;
                            x = 3f / 4f;
                            y = -6f / 4f;
                        }});

                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            shootOnDeath = true;
                            bullet = new ExplosionBulletType(40, 15){{
                                collidesAir = true;
                                suppressionRange = 80f;
                                fragBullets = 5;
                                fragBullet = new BasicBulletType(4f,10f) {{
                                    sprite = "missile-large";
                                    width = height = 8f;
                                    maxRange = 50f;
                                    ignoreRotation = true;

                                    hitColor = trailColor = SvPal.heatGlow;
                                    frontColor = Color.white;
                                    trailWidth = 2f;
                                    trailLength = 8;
                                    hitEffect = despawnEffect = Fx.blastExplosion;
                                    smokeEffect = SvFx.shootLauncher;
                                    hitSound = Sounds.plasmaboom;

                                    backColor = SvPal.heatGlow;
                                    frontColor = Color.white;
                                    mixColorTo = Color.white;

                                    ejectEffect = Fx.none;
                                    hitSize = 22f;

                                    collidesAir = true;
                                    lifetime = 87f;

                                    hitEffect = new MultiEffect(Fx.blastExplosion, Fx.smokeCloud);
                                    keepVelocity = false;
                                    weaveMag = 2f;
                                    weaveScale = 1f;
                                    speed = 0.8f;
                                    drag = -0.020f;
                                    homingPower = 0.01f;

                                    splashDamage = 10f;
                                    splashDamageRadius = 10f;
                                }};

                                shootEffect = new ExplosionEffect(){{
                                    lifetime = 50f;
                                    waveStroke = 5f;
                                    waveLife = 8f;
                                    waveColor = Color.white;
                                    sparkColor = smokeColor = SvPal.heatGlow;
                                    waveRad = 40f;
                                    smokeSize = 4f;
                                    smokes = 7;
                                    smokeSizeBase = 0f;
                                    sparks = 10;
                                    sparkRad = 40f;
                                    sparkLen = 6f;
                                    sparkStroke = 2f;
                                }};
                            }};
                        }});
                    }};
                }};
            }});
        }};

        callees = new HelicopterUnitType("callees"){{
            aiController = FlyingAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.16f;
            speed = 1.6f;
            rotateSpeed = 4f;
            accel = 0.45f;
            health = 8820f;

            engineOffset = -7.5f;
            engineSize = 0;
            hitSize = 45f;
            RotatorRegionPart copter = new RotatorRegionPart(SubvoyageMod.ID + "-medium-rotator"){{
                    mirror = true;
                    layer = Layer.flyingUnitLow;
                    xScl = 1.2f;
                    yScl = 1.2f;
                    x = 16.5f;
                    y = -0.15f;
                    rotationSpeed = 400f;
            }};

            onDraw = (e) -> {
                copter.unitRot = e.rotation();
                copter.unitX = e.x;
                copter.unitY = e.y;
            };

            parts.add(copter);
            parts.add(new ShapePart(){{
                layer = Layer.effect;
                circle = true;
                x = -0.25f;
                y = -8;
                radius = 2f;
                color = SvPal.heatGlow;
                colorTo = Color.white;
                progress = PartProgress.life.curve(Interp.pow5In);
            }});

            abilities.add(new SuppressionFieldAbility(){{
                layer = Layer.flyingUnitLow - 1;
                orbRadius = 0.65f;
                y = 6f;

                color = SvPal.heatGlow;
                particleColor = SvPal.suppresion;
            }});

            trailLength = 20;
            trailScl = 1.9f;
            setEnginesMirror(
                new UnitEngine(7.2f, -15, 2.25f, -90)
            );

//            abilities.add(
//            new MoveEffectAbility(6, engineOffset - 5.5f, Pal.sapBulletBack, SvFx.missileTrailShort, 0.5f){{
//                teamColor = true;
//            }},
//            new MoveEffectAbility(-6, engineOffset - 5.5f, Pal.sapBulletBack, SvFx.missileTrailShort, 0.5f){{
//                teamColor = true;
//            }}
//            );

            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                x = 13f;
                y = 8f;
                shoot.shots = 3;
                shoot.shotDelay = 12f;

                reload = 160f;
                recoil = 2f;

                rotate = true;
                rotateSpeed = 0.4f;
                layerOffset = -2f;
                rotationLimit = 22f;
                minWarmup = 0.95f;
                shootWarmupSpeed = 0.1f;
                inaccuracy = 28f;
                shootCone = 40f;

                shootSound = Sounds.missileLaunch;
                parts.add(new RegionPart("-blade"){{
                    heatProgress = PartProgress.warmup;
                    progress = PartProgress.warmup.blend(PartProgress.reload, 0.15f);
                    heatColor = SvPal.heatGlow;
                    x = 5 / 4f;
                    y = 0f;
                    moveRot = -33f;
                    moveY = -1f;
                    moveX = -1f;
                    under = true;
                    mirror = true;
                }});

                bullet = new BulletType(){{
                    shootEffect = Fx.sparkShoot;
                    smokeEffect = Fx.shootSmokeTitan;
                    hitColor = Pal.suppress;
                    shake = 1f;
                    speed = 0f;
                    keepVelocity = false;
                    collidesAir = true;
                    spawnUnit = new MissileUnitType("callees-missile"){{
                        outlineColor = Pal.darkOutline;
                        trailRotation = true;
                        targetAir = true;
                        physics = true;
                        lowAltitude = true;

                        hitEffect = despawnEffect = Fx.blastExplosion;
                        smokeEffect = SvFx.shootLauncher;
                        trailEffect = SvFx.missileTrailSmoke;
                        trailInterval = 3f;
                        trailWidth = 1f;
                        trailLength = 6;

                        speed = 4.6f;
                        maxRange = 3f;
                        health = 40;
                        homingDelay = 5f;

                        engineSize = 3f;
                        hitColor = engineColor = trailColor = SvPal.heatGlow;
                        engineLayer = Layer.effect;
                        deathExplosionEffect = Fx.none;
                        loopSoundVolume = 0.1f;
                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            shootOnDeath = true;
                            bullet = new ExplosionBulletType(150, 25f){{
                                collidesAir = true;
                                suppressionRange = 80f;
                                shootEffect = new ExplosionEffect(){{
                                    lifetime = 50f;
                                    waveStroke = 5f;
                                    waveLife = 8f;
                                    waveColor = Color.white;
                                    sparkColor = smokeColor = SvPal.heatGlow;
                                    waveRad = 40f;
                                    smokeSize = 4f;
                                    smokes = 7;
                                    smokeSizeBase = 0f;
                                    sparks = 10;
                                    sparkRad = 40f;
                                    sparkLen = 6f;
                                    sparkStroke = 2f;
                                }};
                            }};
                        }});
                    }};
                }};
            }});
        }};

        ganymede = new HelicopterUnitType("ganymede"){{
            aiController = FlyingAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.16f;
            speed = 1.8f;
            rotateSpeed = 3f;
            accel = 0.45f;
            health = 24820f;

            engineOffset = -7.5f;
            engineSize = 0;
            hitSize = 64f;
            RotatorRegionPart copter = new RotatorRegionPart(SubvoyageMod.ID + "-medium-rotator"){{
                mirror = true;
                layer = Layer.flyingUnitLow;
                xScl = 1.6f;
                yScl = 1.6f;
                x = 23.5f;
                y = -2.75f;
                rotationSpeed = 400f;
            }};

            RotatorRegionPart tail = new RotatorRegionPart(SubvoyageMod.ID + "-medium-rotator"){{
                layer = Layer.flyingUnitLow;
                xScl = 0.8f;
                yScl = 0.8f;
                y = -33.25f;
                rotationSpeed = 400f;
            }};

            onDraw = (e) -> {
                copter.unitRot = e.rotation();
                copter.unitX = e.x;
                copter.unitY = e.y;

                tail.unitRot = e.rotation();
                tail.unitX = e.x;
                tail.unitY = e.y;
            };

            parts.add(copter, tail);
            abilities.add(new SuppressionFieldAbility(){{
                layer = Layer.flyingUnitLow - 1;
                orbRadius = 0.65f;
                y = 18f;

                color = SvPal.heatGlow;
                particleColor = SvPal.suppresion;
            }});

            trailLength = 20;
            trailScl = 1.9f;
            setEnginesMirror(
            new UnitEngine(14f, -20, 2.75f, -90)
            );
//            abilities.add(
//            new MoveEffectAbility(6, engineOffset - 5.5f, Pal.sapBulletBack, SvFx.missileTrailShort, 0.5f){{
//                teamColor = true;
//            }},
//            new MoveEffectAbility(-6, engineOffset - 5.5f, Pal.sapBulletBack, SvFx.missileTrailShort, 0.5f){{
//                teamColor = true;
//            }}
//            );

            weapons.add(new PointDefenseWeapon(name + "-point-defense"){{
                top = false;
                mirror = true;
                alternate = false;

                x = 12f;
                y = -12.25f;

                reload = 40f;
                targetInterval = 10f;
                targetSwitchInterval = 15f;
                bullet = new BulletType(){{
                    shootEffect = Fx.disperseTrail;
                    hitEffect = Fx.pointHit;
                    maxRange = 100f;
                    damage = 18f;
                }};
            }});

            weapons.add(new PointDefenseWeapon(name + "-point-defense"){{
                top = false;
                mirror = true;
                alternate = false;
                x = 12f;
                y = 21f;

                reload = 40f;
                targetInterval = 10f;
                targetSwitchInterval = 15f;
                bullet = new BulletType(){{
                    shootEffect = Fx.disperseTrail;
                    hitEffect = Fx.pointHit;
                    maxRange = 100f;
                    damage = 18f;
                }};
            }});

            weapons.add(new Weapon(SubvoyageMod.ID + "-beam-weapon"){{
                shadow = 20f;
                controllable = false;
                autoTarget = true;
                mirror = false;
                shake = 3f;
                shootY = 7f;
                rotate = true;
                x = 0;
                y = 4f;

                targetInterval = 20f;
                targetSwitchInterval = 35f;

                rotateSpeed = 3.5f;
                reload = 170f;
                recoil = 1f;
                shootSound = Sounds.beam;
                continuous = true;
                cooldownTime = reload;
                immunities.add(StatusEffects.burning);
                bullet = new ContinuousLaserBulletType(){{
                    maxRange = 90f;
                    damage = 22f;
                    length = 95f;
                    hitEffect = Fx.smeltsmoke;
                    drawSize = 200f;
                    lifetime = 155f;
                    shake = 1f;

                    shootEffect = Fx.shootHeal;
                    smokeEffect = Fx.none;
                    width = 4f;
                    largeHit = false;

                    incendChance = 0.03f;
                    incendSpread = 5f;
                    incendAmount = 1;
                    colors = new Color[]{SvPal.heatGlow.a(.2f), SvPal.heatGlow.a(.5f), SvPal.heatGlow.mul(1.2f), Color.white};
                }};
            }});

            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                x = 23f;
                y = 12f;
                shoot.shots = 5;
                shoot.shotDelay = 12f;

                reload = 360f;
                recoil = 2f;

                rotate = true;
                rotateSpeed = 0.4f;
                layerOffset = -2f;
                rotationLimit = 22f;
                minWarmup = 0.95f;
                shootWarmupSpeed = 0.1f;
                inaccuracy = 28f;
                shootCone = 40f;

                shootSound = Sounds.missileLaunch;
                parts.add(new RegionPart("-blade"){{
                    heatProgress = PartProgress.warmup;
                    progress = PartProgress.warmup.blend(PartProgress.reload, 0.15f);
                    heatColor = SvPal.heatGlow;
                    x = 5 / 4f;
                    y = 0f;
                    moveRot = -33f;
                    moveY = -1f;
                    moveX = -1f;
                    under = true;
                    mirror = true;
                }});

                bullet = new BulletType(){{
                    shootEffect = Fx.sparkShoot;
                    smokeEffect = Fx.shootSmokeTitan;
                    hitColor = Pal.suppress;
                    shake = 1f;
                    speed = 0f;
                    keepVelocity = false;
                    collidesAir = true;
                    spawnUnit = new MissileUnitType("ganymede-missile"){{
                        outlineColor = Pal.darkOutline;
                        trailRotation = true;
                        targetAir = true;
                        physics = true;
                        lowAltitude = true;

                        hitEffect = despawnEffect = Fx.blastExplosion;
                        smokeEffect = SvFx.shootLauncher;
                        trailEffect = SvFx.missileTrailSmoke;
                        trailInterval = 3f;
                        trailWidth = 1f;
                        trailLength = 6;

                        speed = 4.6f;
                        maxRange = 3f;
                        health = 40;
                        homingDelay = 5f;

                        engineSize = 3f;
                        hitColor = engineColor = trailColor = SvPal.heatGlow;
                        engineLayer = Layer.effect;
                        deathExplosionEffect = Fx.none;
                        loopSoundVolume = 0.1f;
                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            shootOnDeath = true;
                            bullet = new ExplosionBulletType(250, 50f){{
                                collidesAir = true;
                                suppressionRange = 80f;


                                fragBullets = 2;
                                fragBullet = new BasicBulletType(10f,60f) {{
                                    sprite = "missile-large";
                                    width = height = 8f;
                                    maxRange = 50f;
                                    ignoreRotation = true;

                                    hitColor = trailColor = SvPal.heatGlow;
                                    frontColor = Color.white;
                                    trailWidth = 2f;
                                    trailLength = 8;
                                    hitEffect = despawnEffect = Fx.blastExplosion;
                                    smokeEffect = SvFx.shootLauncher;
                                    hitSound = Sounds.plasmaboom;

                                    backColor = SvPal.heatGlow;
                                    frontColor = Color.white;
                                    mixColorTo = Color.white;

                                    ejectEffect = Fx.none;
                                    hitSize = 22f;

                                    collidesAir = true;
                                    lifetime = 30f;

                                    hitEffect = new MultiEffect(Fx.blastExplosion, Fx.smokeCloud);
                                    keepVelocity = false;
                                    weaveMag = 2f;
                                    weaveScale = 1f;
                                    speed = 0.8f;
                                    drag = -0.020f;
                                    homingPower = 0.01f;

                                    fragBullets = 10;
                                    fragBullet = new BasicBulletType(20f,30f) {{
                                        sprite = "missile-large";
                                        width = height = 8f;
                                        maxRange = 50f;
                                        ignoreRotation = true;

                                        hitColor = trailColor = SvPal.heatGlow;
                                        frontColor = Color.white;
                                        trailWidth = 2f;
                                        trailLength = 8;
                                        hitEffect = despawnEffect = Fx.blastExplosion;
                                        smokeEffect = SvFx.shootLauncher;
                                        hitSound = Sounds.plasmaboom;

                                        backColor = SvPal.heatGlow;
                                        frontColor = Color.white;
                                        mixColorTo = Color.white;

                                        ejectEffect = Fx.none;
                                        hitSize = 22f;

                                        collidesAir = true;
                                        lifetime = 30f;

                                        hitEffect = new MultiEffect(Fx.blastExplosion, Fx.smokeCloud);
                                        keepVelocity = false;
                                        weaveMag = 2f;
                                        weaveScale = 1f;
                                        speed = 0.8f;
                                        drag = -0.020f;
                                        homingPower = 0.01f;

                                        splashDamage = 20f;
                                        splashDamageRadius = 10f;
                                    }};

                                    splashDamage = 20f;
                                    splashDamageRadius = 10f;
                                }};

                                shootEffect = new ExplosionEffect(){{
                                    lifetime = 50f;
                                    waveStroke = 5f;
                                    waveLife = 8f;
                                    waveColor = Color.white;
                                    sparkColor = smokeColor = SvPal.heatGlow;
                                    waveRad = 40f;
                                    smokeSize = 4f;
                                    smokes = 7;
                                    smokeSizeBase = 0f;
                                    sparks = 10;
                                    sparkRad = 40f;
                                    sparkLen = 6f;
                                    sparkStroke = 2f;
                                }};
                            }};
                        }});
                    }};
                }};
            }});
        }};

        //hydromech
        leeft = new HydromechUnitType("leeft") {{
            constructor = HydromechUnitEntity::create;
            drag = 0.07f;
            rotateSpeed = 8f;
            health = 1500;
            hitSize = 15f;
            withStates(
                    HydromechState.GROUND,new UnitStatState() {{
                        speed = 0.8f;
                        inwardsDamageMul = 1.2f;
                    }},
                    HydromechState.WATER,new UnitStatState() {{
                        speed = 1.6f;
                    }}
            );

            bodyHeat = true;
            heatColor = Color.red;

            legCount = 4;
            legLength = 9f;
            legForwardScl = 0.6f;
            legMoveSpace = 1.4f;

            mechSideSway = 0.55f;
            mechFrontSway = 0.15f;

            allowLegStep = true;
            legPhysicsLayer = false;
            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit - 1f;
            targetAir = true;
            weapons.add(new HydromechWeapon(name + "-weapon") {{
                shoot = new ShootLeeft() {{shots = 2;}};
                reload = 40f;
                recoil = 3f;
                inaccuracy = 10f;
                shootY = 0;
                x = 0;
                y = 0.25f;

                top = true;
                mirror = false;
                shootSound = Sounds.blaster;
                soundPitchMin = 0.5f;
                soundPitchMax = 0.55f;

                groundStat = new WeaponStatState() {{
                    damage = 18f;
                    lifetime = 40f;
                }};
                waterStat = new WeaponStatState() {{
                    damage = 18f;
                    lifetime = 68f;
                }};
                bullet = new DecayingBulletType(4f,18f,9f) {{
                    shootEffect = SvFx.pulverize;
                    smokeEffect = Fx.none;
                    hitColor = backColor = trailColor = Pal.missileYellow;
                    frontColor = Color.white;
                    lifetime = 40f;
                    trailWidth = 5f;
                    trailLength = 8;
                    trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                }};
            }});
            //researchCostMultiplier = 0f;
        }};
        flagshi = new HydromechUnitType("flagshi"){{
            constructor = HydromechUnitEntity::create;
            drag = 0.07f;
            rotateSpeed = 8f;
            health = 3220;
            hitSize = 20f;
            withStates(
                    HydromechState.GROUND,new UnitStatState() {{
                        speed = 0.8f;
                    }},
                    HydromechState.WATER,new UnitStatState() {{
                        speed = 1.6f;
                        inwardsDamageMul = 1.2f;
                    }}
            );

            bodyHeat = true;
            heatColor = Color.red;

            bodyScale = 0.9f;

            legGroupSize = 3;
            lockLegBase = true;
            legContinuousMove = true;
            legMaxLength = 1.1f;
            legMinLength = 0.2f;
            legLengthScl = 0.95f;
            legSplashDamage = 1.1f;
            legStraightness = 0.4f;

            legCount = 6;
            legLength = 15f;
            legForwardScl = 0.7f;
            legMoveSpace = 2f;
            rippleScale = 2f;
            stepShake = 0.1f;
            legExtension = -5f;
            legBaseOffset = 8f;

            allowLegStep = true;
            mechSideSway = 0.9f;
            mechFrontSway = 0.9f;
            legPhysicsLayer = false;

            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit - 1f;
            targetAir = true;
            weapons.add(new HydromechRepairBeam(name + "-repair-weapon") {{
                widthSinMag = 0.11f;
                reload = 20f;
                x = 0f;
                y = 6.5f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.7f;
                repairSpeed = 3.1f;
                fractionRepairSpeed = 0.06f;
                aimDst = 0f;
                shootCone = 15f;
                mirror = false;

                activationState = HydromechState.WATER;

                targetUnits = true;
                targetBuildings = false;
                autoTarget = true;
                controllable = false;
                laserColor = Pal.heal;
                healColor = Pal.heal;

                bullet = new BulletType(){{
                    maxRange = 60f;
                }};
            }});
            weapons.add(new HydromechWeapon(name + "-weapon") {{
                shoot = new ShootLeeft() {{shots = 3;}};
                reload = 40f;
                recoil = 3f;
                inaccuracy = 10f;
                shootY = 0;
                top = true;
                mirror = true;
                flipSprite = true;
                x = 9f;
                shootSound = Sounds.blaster;
                soundPitchMin = 0.4f;
                soundPitchMax = 0.45f;

                sclX = 1.65f;
                sclY = 1.5f;

                groundStat = new WeaponStatState() {{
                    lifetime = 50;
                    damage = 25f;
                }};
                waterStat = new WeaponStatState() {{
                    lifetime = 78;
                    damage = 9f;
                }};

                bullet = new DecayingBulletType(4f,9f,4f) {{
                    shootEffect = SvFx.pulverize;
                    smokeEffect = Fx.none;
                    hitColor = backColor = trailColor = Pal.missileYellow;
                    frontColor = Color.white;
                    lifetime = 70f;
                    trailWidth = 6f;
                    trailLength = 8;
                    trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                }};
            }});
        }};

        vanguard = new HydromechUnitType("vanguard"){{
            constructor = HydromechUnitEntity::create;
            drag = 0.14f;
            rotateSpeed = 5f;
            health = 3220;
            hitSize = 20f;

            waveTrailX = 8f;
            waveTrailY = -8f;

            trailScl = 12;
            trailLength = 25;

            accel = 0.4f;

            withStates(
            HydromechState.GROUND, new UnitStatState(){{
                speed = 0.6f;
                inwardsDamageMul = 0.6f;
            }},
            HydromechState.WATER, new UnitStatState(){{
                speed = 0.9f;
                inwardsDamageMul = 0.8f;
            }}
            );


            bodyHeat = true;
            heatColor = Color.red;
            legGroupSize = 3;
            lockLegBase = true;
            legContinuousMove = true;
            legMaxLength = 1.1f;
            legMinLength = 0.2f;
            legLengthScl = 0.95f;
            legSplashDamage = 1.1f;
            legStraightness = 0.4f;

            legCount = 6;
            legLength = 15f;
            legForwardScl = 0.7f;
            legMoveSpace = 2f;
            rippleScale = 2f;
            stepShake = 0.1f;
            legExtension = -5f;
            legBaseOffset = 8f;

            allowLegStep = true;
            mechSideSway = 0.9f;
            mechFrontSway = 0.9f;
            legPhysicsLayer = false;

            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit - 1f;
            targetAir = true;


            parts.add(
                    new RegionPart("-rifle"){{
                        moveRot = 20f;
                        moveX = 1f;
                        x = 0;
                        y = 0;
                        progress = PartProgress.heat;
                        heatColor = Color.red;
                        mirror = true;
                    }});

            weapons.add(new HydromechWeapon() {{
                reload = 100f;
                recoil = 6f;
                inaccuracy = 0f;
                x = 0;
                y = 0;
                heatColor = Color.red;
                mirror = false;
                alternate = false;
                top = false;
                shootSound = Sounds.missileSmall;
                soundPitchMin = 0.4f;
                soundPitchMax = 0.25f;

                activationState = HydromechState.WATER;
                activationBasedDraw = true;
                cooldownTime = reload;

                bullet = new BulletType(){{
                    shootEffect = Fx.sparkShoot;
                    smokeEffect = Fx.shootSmokeTitan;
                    hitColor = Pal.suppress;
                    shake = 1f;
                    speed = 0f;
                    keepVelocity = false;
                    collidesAir = true;

                    spawnUnit = new MissileUnitType("vanguard-missile-water"){{
                        controller = u -> new StraightMissileAI();
                        constructor = () -> {
                            TimedKillUnit u = TimedKillUnit.create();
                            u.apply(StatusEffects.invincible,120f);
                            return u;
                        };
                        outlineColor = Pal.darkOutline;
                        trailRotation = true;
                        targetAir = true;
                        physics = false;
                        lowAltitude = true;

                        lifetime = 60f;
                        health = 1000f;

                        abilities.add(new ShieldArcAbility(){{
                            region = "subvoyage-vanguard-missile-shield";
                            radius = 18f;
                            angle = 82f;
                            regen = 0.6f;
                            cooldown = 0f;
                            max = 2000f;
                            y = -20f;
                            width = 6f;
                            whenShooting = false;
                        }});

                        hitEffect = despawnEffect = Fx.blastExplosion;
                        smokeEffect = SvFx.shootLauncher;
                        trailEffect = SvFx.missileTrailSmoke;
                        trailInterval = 3f;
                        trailWidth = 1f;
                        trailLength = 6;

                        speed = 4.6f;
                        maxRange = 3f;
                        health = 40;

                        engineSize = 3f;
                        hitColor = engineColor = trailColor = SvPal.heatGlow;
                        engineLayer = Layer.effect;
                        deathExplosionEffect = Fx.none;
                        loopSoundVolume = 0.1f;
                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            shootOnDeath = true;
                            bullet = new ExplosionBulletType(50f, 50f){{
                                collidesAir = true;
                                suppressionRange = 80f;
                                shootEffect = new ExplosionEffect(){{
                                    lifetime = 50f;
                                    waveStroke = 5f;
                                    waveLife = 8f;
                                    waveColor = Color.white;
                                    sparkColor = smokeColor = SvPal.heatGlow;
                                    waveRad = 40f;
                                    smokeSize = 4f;
                                    smokes = 7;
                                    smokeSizeBase = 0f;
                                    sparks = 10;
                                    sparkRad = 40f;
                                    sparkLen = 6f;
                                    sparkStroke = 2f;
                                }};
                            }};
                        }});
                    }};
                }};
            }});
            weapons.add(new HydromechWeapon() {{
                reload = 80f;
                recoil = 4f;
                inaccuracy = 10f;
                x = 0;
                y = 0;
                heatColor = Color.red;
                mirror = false;
                alternate = false;
                top = false;
                shootSound = Sounds.missileSmall;
                soundPitchMin = 0.4f;
                soundPitchMax = 0.25f;

                activationState = HydromechState.GROUND;
                activationBasedDraw = true;

                cooldownTime = reload;

                bullet = new BulletType(){{
                    shootEffect = Fx.sparkShoot;
                    smokeEffect = Fx.shootSmokeTitan;
                    hitColor = Pal.suppress;
                    shake = 1f;
                    speed = 0f;
                    keepVelocity = false;
                    collidesAir = true;

                    spawnUnit = new MissileUnitType("vanguard-missile"){{
                        controller = u -> new StraightMissileAI();
                        outlineColor = Pal.darkOutline;
                        trailRotation = true;
                        targetAir = true;
                        physics = false;
                        lowAltitude = true;

                        lifetime = 50f;

                        hitEffect = despawnEffect = Fx.blastExplosion;
                        smokeEffect = SvFx.shootLauncher;
                        trailEffect = SvFx.missileTrailSmoke;
                        trailInterval = 3f;
                        trailWidth = 1f;
                        trailLength = 6;

                        speed = 4.6f;
                        maxRange = 3f;
                        health = 40;

                        engineSize = 3f;
                        hitColor = engineColor = trailColor = SvPal.heatGlow;
                        engineLayer = Layer.effect;
                        deathExplosionEffect = Fx.none;
                        loopSoundVolume = 0.1f;
                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            shootOnDeath = true;
                            bullet = new ExplosionBulletType(90, 50f){{
                                collidesAir = true;
                                suppressionRange = 80f;
                                shootEffect = new ExplosionEffect(){{
                                    lifetime = 50f;
                                    waveStroke = 5f;
                                    waveLife = 8f;
                                    waveColor = Color.white;
                                    sparkColor = smokeColor = SvPal.heatGlow;
                                    waveRad = 40f;
                                    smokeSize = 4f;
                                    smokes = 7;
                                    smokeSizeBase = 0f;
                                    sparks = 10;
                                    sparkRad = 40f;
                                    sparkLen = 6f;
                                    sparkStroke = 2f;
                                }};
                            }};
                        }});
                    }};
                }};
            }});
            weapons.add(new HydromechWeapon(name+"-hydrogun") {{
                rotate = true;
                x = 0f;
                y = -2f;
                mirror = false;
                controllable = true;
                cooldownTime = 1f;
                hasHeat = false;
                rotateSpeed = 5f;
                alwaysShootWhenMoving = true;
                recoil = 0.1f;

                shootSound = Sounds.none;

                bullet = new LiquidBulletType(Liquids.water);
            }});
        }};

        //other
        bulker = new AtlacianUnitType("bulker"){{
            controller = u -> new CargoAI();
            constructor = BuildingTetherPayloadUnit::create;
            isEnemy = false;
            allowedInPayloads = false;
            logicControllable = false;
            playerControllable = false;
            envDisabled = 0;
            payloadCapacity = 0f;

            lowAltitude = false;
            flying = true;

            drag = 0.06f;
            speed = 3.5f;
            rotateSpeed = 9f;
            accel = 0.1f;
            itemCapacity = 50;
            health = 200f;
            hitSize = 11f;
            engineSize = 2.3f;
            engineOffset = 6.5f;
            hidden = true;

            setEnginesMirror(
                    new UnitEngine(24 / 4f, -24 / 4f, 2.3f, 315f)
            );
        }};;
    }

    public static void helicopter(String id) {
        mapHelicopter = EntityMapping.register(id,HelicopterUnitEntity::new);
    }
    public static void hmech(String id) {
        mapHMech = EntityMapping.register(id,HydromechUnitEntity::create);
    }

    public static void helicopter(String... ids) {
        for (String id : ids) helicopter(id);
    }
    public static void hmech(String... ids) {
        for (String id : ids) hmech(id);
    }
}
