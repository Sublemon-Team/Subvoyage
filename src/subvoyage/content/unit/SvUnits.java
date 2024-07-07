package subvoyage.content.unit;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.Vars;
import mindustry.ai.types.*;
import mindustry.audio.SoundLoop;
import mindustry.content.*;
import mindustry.core.World;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.type.unit.*;
import mindustry.type.weapons.*;
import mindustry.world.blocks.units.RepairTurret;
import subvoyage.*;
import subvoyage.content.unit.entity.*;
import subvoyage.content.unit.type.*;
import subvoyage.content.world.*;
import subvoyage.entities.part.*;
import subvoyage.entities.shoot.*;

import static mindustry.Vars.*;

public class SvUnits{
    public static UnitType
    // core
    marine,
    //helicopters
    lapetus, skath, charon, callees,ganymede,
    //hydromechs
    leeft, flagshi,
    //cargo
    bulker;

    public static int mapHelicopter = 0;
    public static int mapHMech = 0;
    public static void load(){
        helicopter("lapetus", "skath", "charon", "callees", "ganymede");
        hmech("leeft", "flagshi");
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
                    hitColor = backColor = trailColor = Color.valueOf("feb380");
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

                    hitColor = trailColor = Color.valueOf("feb380");
                    frontColor = Color.white;
                    trailWidth = 2f;
                    trailLength = 8;
                    hitEffect = despawnEffect = Fx.blastExplosion;
                    smokeEffect = SvFx.shootLauncher;
                    hitSound = Sounds.plasmaboom;

                    backColor = Color.valueOf("feb380");
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
                        hitColor = engineColor = trailColor = Color.valueOf("feb380");
                        engineLayer = Layer.effect;
                        deathExplosionEffect = Fx.none;
                        loopSoundVolume = 0.1f;
                        parts.add(new ShapePart(){{
                            layer = Layer.effect;
                            circle = true;
                            y = -0.25f;
                            radius = 1.5f;
                            color = Color.valueOf("feb380");
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

                                    hitColor = trailColor = Color.valueOf("feb380");
                                    frontColor = Color.white;
                                    trailWidth = 2f;
                                    trailLength = 8;
                                    hitEffect = despawnEffect = Fx.blastExplosion;
                                    smokeEffect = SvFx.shootLauncher;
                                    hitSound = Sounds.plasmaboom;

                                    backColor = Color.valueOf("feb380");
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
                                    sparkColor = smokeColor = Color.valueOf("feb380");
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
                color = Color.valueOf("feb380");
                colorTo = Color.white;
                progress = PartProgress.life.curve(Interp.pow5In);
            }});

            abilities.add(new SuppressionFieldAbility(){{
                layer = Layer.flyingUnitLow - 1;
                orbRadius = 0.65f;
                y = 6f;

                color = Color.valueOf("feb380");
                particleColor = Color.valueOf("FE6C4C");
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
                    heatColor = Color.valueOf("feb380");
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
                        hitColor = engineColor = trailColor = Color.valueOf("feb380");
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
                                    sparkColor = smokeColor = Color.valueOf("feb380");
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

                color = Color.valueOf("feb380");
                particleColor = Color.valueOf("FE6C4C");
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
                    colors = new Color[]{Color.valueOf("feb380").a(.2f), Color.valueOf("feb380").a(.5f), Color.valueOf("feb380").mul(1.2f), Color.white};
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
                    heatColor = Color.valueOf("feb380");
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
                        hitColor = engineColor = trailColor = Color.valueOf("feb380");
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

                                    hitColor = trailColor = Color.valueOf("feb380");
                                    frontColor = Color.white;
                                    trailWidth = 2f;
                                    trailLength = 8;
                                    hitEffect = despawnEffect = Fx.blastExplosion;
                                    smokeEffect = SvFx.shootLauncher;
                                    hitSound = Sounds.plasmaboom;

                                    backColor = Color.valueOf("feb380");
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

                                        hitColor = trailColor = Color.valueOf("feb380");
                                        frontColor = Color.white;
                                        trailWidth = 2f;
                                        trailLength = 8;
                                        hitEffect = despawnEffect = Fx.blastExplosion;
                                        smokeEffect = SvFx.shootLauncher;
                                        hitSound = Sounds.plasmaboom;

                                        backColor = Color.valueOf("feb380");
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
                                    sparkColor = smokeColor = Color.valueOf("feb380");
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
            speed = 1.6f;
            rotateSpeed = 8f;
            health = 1100;
            hitSize = 15f;
            flying = false;

            legCount = 4;
            legLength = 9f;
            legForwardScl = 0.6f;
            legMoveSpace = 1.4f;

            mechSideSway = 0.55f;
            mechFrontSway = 0.15f;

            allowLegStep = true;
            hovering = true;
            legPhysicsLayer = false;

            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit - 1f;
            targetAir = true;


            weapons.add(new Weapon(name + "-weapon") {{
                shoot = new ShootLeeft() {{shots = 2;}};
                reload = 40f;
                recoil = 3f;
                inaccuracy = 10f;
                shootY = 0;
                top = false;
                mirror = false;
                x = 0;
                shootSound = Sounds.blaster;
                soundPitchMin = 0.4f;
                soundPitchMax = 0.45f;
                bullet = new BasicBulletType(4f,18f) {{
                    shootEffect = SvFx.pulverize;
                    smokeEffect = Fx.none;
                    hitColor = backColor = trailColor = Pal.missileYellow;
                    frontColor = Color.white;
                    lifetime = 40f;
                    trailWidth = 3f;
                    trailLength = 8;
                    trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                }

                    @Override
                    public void update(Bullet b) {
                        super.update(b);
                        b.damage = b.type.damage - (b.fin()*9);
                    }

                    @Override
                    public void draw(Bullet b) {
                        drawTrail(b);
                        drawParts(b);
                        float shrink = shrinkInterp.apply(b.fout());
                        float height = this.height * ((1f - shrinkY) + shrinkY * shrink);
                        float width = this.width * ((1f - shrinkX) + shrinkX * shrink);
                        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;

                        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

                        Draw.mixcol(mix, mix.a);

                        if(backRegion.found()){
                            Draw.color(b.team.color);
                            Draw.rect(backRegion, b.x, b.y, width, height, b.rotation() + offset);
                        }

                        Draw.color(frontColor);
                        Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation() + offset);

                        Draw.reset();
                    }

                    @Override
                    public void drawTrail(Bullet b) {
                        if(trailLength > 0 && b.trail != null){
                            float z = Draw.z();
                            Draw.z(z - 0.0001f);
                            b.trail.draw(b.team.color, trailWidth*b.damage/b.type.damage*2);
                            Draw.z(z);
                        }
                    }
                };
            }
                @Override
                protected void handleBullet(Unit unit, WeaponMount mount, Bullet bullet) {
                    super.handleBullet(unit, mount, bullet);
                    if(unit instanceof HydromechUnitEntity hm && hm.isOnLiquid()) bullet.lifetime *= 1.7f;
                }
            });
            //researchCostMultiplier = 0f;
        }};
        flagshi = new HydromechUnitType("flagshi"){{
            constructor = HydromechUnitEntity::create;
            drag = 0.07f;
            speed = 1.6f;
            rotateSpeed = 8f;
            health = 1520;
            hitSize = 20f;
            flying = false;

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

            hovering = true;
            legPhysicsLayer = false;

            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit - 1f;
            targetAir = true;


            weapons.add(new RepairBeamWeapon(name + "-repair-weapon") {{
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

                targetUnits = true;
                targetBuildings = false;
                autoTarget = true;
                controllable = false;
                laserColor = Pal.heal;
                healColor = Pal.heal;

                bullet = new BulletType(){{
                    maxRange = 60f;
                }};
            }

                @Override
                public void update(Unit unit, WeaponMount mount) {

                    boolean can = unit.canShoot();
                    float lastReload = mount.reload;
                    mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0);
                    mount.recoil = Mathf.approachDelta(mount.recoil, 0, unit.reloadMultiplier / recoilTime);
                    if(recoils > 0){
                        if(mount.recoils == null) mount.recoils = new float[recoils];
                        for(int i = 0; i < recoils; i++){
                            mount.recoils[i] = Mathf.approachDelta(mount.recoils[i], 0, unit.reloadMultiplier / recoilTime);
                        }
                    }
                    mount.smoothReload = Mathf.lerpDelta(mount.smoothReload, mount.reload / reload, smoothReloadSpeed);
                    mount.charge = mount.charging && shoot.firstShotDelay > 0 ? Mathf.approachDelta(mount.charge, 1, 1 / shoot.firstShotDelay) : 0;

                    float warmupTarget = (can && mount.shoot) || (continuous && mount.bullet != null) || mount.charging ? 1f : 0f;
                    if(linearWarmup){
                        mount.warmup = Mathf.approachDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
                    }else{
                        mount.warmup = Mathf.lerpDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
                    }

                    //rotate if applicable
                    if(rotate && (mount.rotate || mount.shoot) && can){
                        float axisX = unit.x + Angles.trnsx(unit.rotation - 90,  x, y),
                                axisY = unit.y + Angles.trnsy(unit.rotation - 90,  x, y);

                        mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
                        mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
                        if(rotationLimit < 360){
                            float dst = Angles.angleDist(mount.rotation, baseRotation);
                            if(dst > rotationLimit/2f){
                                mount.rotation = Angles.moveToward(mount.rotation, baseRotation, dst - rotationLimit/2f);
                            }
                        }
                    }else if(!rotate){
                        mount.rotation = baseRotation;
                        mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
                    }

                    float
                            weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : baseRotation),
                            mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                            mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
                            bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
                            bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
                            shootAngle = bulletRotation(unit, mount, bulletX, bulletY);

                    //find a new target
                    if(!controllable && autoTarget){
                        if((mount.retarget -= Time.delta) <= 0f){
                            mount.target = findTarget(unit, mountX, mountY, bullet.range, bullet.collidesAir, bullet.collidesGround);
                            mount.retarget = mount.target == null ? targetInterval : targetSwitchInterval;
                        }

                        if(mount.target != null && checkTarget(unit, mount.target, mountX, mountY, bullet.range)){
                            mount.target = null;
                        }

                        boolean shoot = false;

                        if(mount.target != null){
                            shoot = mount.target.within(mountX, mountY, bullet.range + Math.abs(shootY) + (mount.target instanceof Sized s ? s.hitSize()/2f : 0f)) && can;

                            if(predictTarget){
                                Vec2 to = Predict.intercept(unit, mount.target, bullet.speed);
                                mount.aimX = to.x;
                                mount.aimY = to.y;
                            }else{
                                mount.aimX = mount.target.x();
                                mount.aimY = mount.target.y();
                            }
                        }

                        mount.shoot = mount.rotate = shoot;

                        //note that shooting state is not affected, as these cannot be controlled
                        //logic will return shooting as false even if these return true, which is fine
                    }

                    if(alwaysShooting) mount.shoot = true;

                    //update continuous state
                    if(continuous && mount.bullet != null){
                        if(!mount.bullet.isAdded() || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type != bullet){
                            mount.bullet = null;
                        }else{
                            mount.bullet.rotation(weaponRotation + 90);
                            mount.bullet.set(bulletX, bulletY);
                            mount.reload = reload;
                            mount.recoil = 1f;
                            unit.vel.add(Tmp.v1.trns(unit.rotation + 180f, mount.bullet.type.recoil * Time.delta));
                            if(shootSound != Sounds.none && !headless){
                                if(mount.sound == null) mount.sound = new SoundLoop(shootSound, 1f);
                                mount.sound.update(bulletX, bulletY, true);
                            }

                            if(alwaysContinuous && mount.shoot){
                                mount.bullet.time = mount.bullet.lifetime * mount.bullet.type.optimalLifeFract * mount.warmup;
                                mount.bullet.keepAlive = true;

                                unit.apply(shootStatus, shootStatusDuration);
                            }
                        }
                    }else{
                        //heat decreases when not firing
                        mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / cooldownTime, 0);

                        if(mount.sound != null){
                            mount.sound.update(bulletX, bulletY, false);
                        }
                    }

                    //flip weapon shoot side for alternating weapons
                    boolean wasFlipped = mount.side;
                    if(otherSide != -1 && alternate && mount.side == flipSprite && mount.reload <= reload / 2f && lastReload > reload / 2f){
                        unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
                        mount.side = !mount.side;
                    }

                    //shoot if applicable
                    if(mount.shoot && //must be shooting
                            can && //must be able to shoot
                            !(bullet.killShooter && mount.totalShots > 0) && //if the bullet kills the shooter, you should only ever be able to shoot once
                            (!useAmmo || unit.ammo > 0 || !state.rules.unitAmmo || unit.team.rules().infiniteAmmo) && //check ammo
                            (!alternate || wasFlipped == flipSprite) &&
                            mount.warmup >= minWarmup && //must be warmed up
                            unit.vel.len() >= minShootVelocity && //check velocity requirements
                            (mount.reload <= 0.0001f || (alwaysContinuous && mount.bullet == null)) && //reload has to be 0, or it has to be an always-continuous weapon
                            (alwaysShooting || Angles.within(rotate ? mount.rotation : unit.rotation + baseRotation, mount.targetRotation, shootCone)) //has to be within the cone
                    ){
                        shoot(unit, mount, bulletX, bulletY, shootAngle);

                        mount.reload = reload;

                        if(useAmmo){
                            unit.ammo--;
                            if(unit.ammo < 0) unit.ammo = 0;
                        }
                    }

                    weaponRotation = unit.rotation - 90;
                    float wx = unit.x + Angles.trnsx(weaponRotation, x, y),
                            wy = unit.y + Angles.trnsy(weaponRotation, x, y);

                    HealBeamMount heal = (HealBeamMount)mount;
                    boolean isLiquid = (unit instanceof HydromechUnitEntity hm) && hm.isOnLiquid();
                    boolean canShoot = mount.shoot && isLiquid;
                    System.out.println(isLiquid + "&" + canShoot);

                    if(!autoTarget){
                        heal.target = null;
                        if(canShoot){
                            heal.lastEnd.set(heal.aimX, heal.aimY);

                            if(!rotate && !Angles.within(Angles.angle(wx, wy, heal.aimX, heal.aimY), unit.rotation, shootCone)){
                                canShoot = false;
                            }
                        }

                        //limit range
                        heal.lastEnd.sub(wx, wy).limit(range()).add(wx, wy);

                        if(targetBuildings){
                            //snap to closest building
                            World.raycastEachWorld(wx, wy, heal.lastEnd.x, heal.lastEnd.y, (x, y) -> {
                                var build = Vars.world.build(x, y);
                                if(build != null && build.team == unit.team && build.damaged()){
                                    heal.target = build;
                                    heal.lastEnd.set(x * tilesize, y * tilesize);
                                    return true;
                                }
                                return false;
                            });
                        }
                        if(targetUnits){
                            //TODO does not support healing units manually yet
                        }
                    }

                    heal.strength = Mathf.lerpDelta(heal.strength, Mathf.num(autoTarget ? mount.target != null : canShoot), 0.2f);

                    //create heal effect periodically
                    if(canShoot && mount.target instanceof Building b && b.damaged() && (heal.effectTimer += Time.delta) >= reload){
                        healEffect.at(b.x, b.y, 0f, healColor, b.block);
                        heal.effectTimer = 0f;
                    }

                    if(canShoot && mount.target instanceof Healthc u){
                        float baseAmount = repairSpeed * heal.strength * Time.delta + fractionRepairSpeed * heal.strength * Time.delta * u.maxHealth() / 100f;
                        u.heal((u instanceof Building b && b.wasRecentlyDamaged() ? recentDamageMultiplier : 1f) * baseAmount);
                    }
                }

                @Override
                public void draw(Unit unit, WeaponMount mount) {
                    HealBeamMount heal = (HealBeamMount)mount;

                    if(unit.canShoot() && unit instanceof HydromechUnitEntity hm && hm.isOnLiquid()){
                        float
                                weaponRotation = unit.rotation - 90,
                                wx = unit.x + Angles.trnsx(weaponRotation, x, y),
                                wy = unit.y + Angles.trnsy(weaponRotation, x, y),
                                z = Draw.z();
                        RepairTurret.drawBeam(wx, wy, unit.rotation + mount.rotation, shootY, unit.id, mount.target == null || controllable ? null : (Sized)mount.target, unit.team, heal.strength,
                                pulseStroke, pulseRadius, beamWidth + Mathf.absin(widthSinScl, widthSinMag), heal.lastEnd, heal.offset, laserColor, laserTopColor,
                                laser, laserEnd, laserTop, laserTopEnd);
                        Draw.z(z);
                    }
                }
            });
            weapons.add(new Weapon(name + "-weapon") {{
                shoot = new ShootLeeft() {{shots = 3;}};
                reload = 40f;
                recoil = 3f;
                inaccuracy = 10f;
                shootY = 0;
                top = false;
                mirror = true;
                x = 6.5f;
                shootSound = Sounds.blaster;
                soundPitchMin = 0.4f;
                soundPitchMax = 0.45f;
                bullet = new BasicBulletType(4f,9f) {{
                    shootEffect = SvFx.pulverize;
                    smokeEffect = Fx.none;
                    hitColor = backColor = trailColor = Pal.missileYellow;
                    frontColor = Color.white;
                    lifetime = 70f;
                    trailWidth = 3f;
                    trailLength = 8;
                    trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                }

                    @Override
                    public void update(Bullet b) {
                        super.update(b);
                        b.damage = b.type.damage - (b.fin()*3);
                    }

                    @Override
                    public void draw(Bullet b) {
                        drawTrail(b);
                        drawParts(b);
                        float shrink = shrinkInterp.apply(b.fout());
                        float height = this.height * ((1f - shrinkY) + shrinkY * shrink);
                        float width = this.width * ((1f - shrinkX) + shrinkX * shrink);
                        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;

                        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

                        Draw.mixcol(mix, mix.a);

                        if(backRegion.found()){
                            Draw.color(b.team.color);
                            Draw.rect(backRegion, b.x, b.y, width, height, b.rotation() + offset);
                        }

                        Draw.color(frontColor);
                        Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation() + offset);

                        Draw.reset();
                    }

                    @Override
                    public void drawTrail(Bullet b) {
                        if(trailLength > 0 && b.trail != null){
                            float z = Draw.z();
                            Draw.z(z - 0.0001f);
                            b.trail.draw(b.team.color, trailWidth*b.damage/b.type.damage*2);
                            Draw.z(z);
                        }
                    }
                };
            }
                @Override
                protected void handleBullet(Unit unit, WeaponMount mount, Bullet bullet) {
                    super.handleBullet(unit, mount, bullet);
                    if(unit instanceof HydromechUnitEntity hm && hm.isOnLiquid()) {
                        bullet.lifetime *= 1.7f;
                    } else if (unit instanceof HydromechUnitEntity) {
                        bullet.damage *= 2f;
                    }
                }
            });
            //researchCostMultiplier = 0f;
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
