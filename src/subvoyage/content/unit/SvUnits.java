package subvoyage.content.unit;

import arc.graphics.*;
import arc.math.*;
import mindustry.ai.Pathfinder;
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
import subvoyage.*;
import subvoyage.content.unit.ai.WaterCargoAI;
import subvoyage.content.unit.entity.HelicopterUnitEntity;
import subvoyage.content.unit.entity.WaterPayloadUnit;
import subvoyage.content.unit.type.AtlacianUnitType;
import subvoyage.content.unit.type.HelicopterUnitType;
import subvoyage.content.world.*;
import subvoyage.entities.part.*;

public class SvUnits{
    public static UnitType
    // core
    marine,
    //helicopters
    lapetus, skath, charon, callees,ganymede,
    //cargo
    bulker;

    public static int mapHelicopter = 0;
    public static void load(){
        helicopter("lapetus", "skath", "charon", "callees", "ganymede");
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
            hitSize = 8f;

            ammoType = new PowerAmmoType(900);
            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                y = -1.25f;
                x = 6.5f;
                reload = 10f;
                ejectEffect = Fx.casing1;
                recoil = 2f;
                shootSound = Sounds.lasershoot;
                velocityRnd = 0f;
                inaccuracy = 0f;
                alternate = true;
                fogRadius = 0;
                lightRadius = 8;
                bullet = new ArtilleryBulletType(3f, 11){{
                    collidesTiles = true;
                    collides = true;
                    collidesAir = true;

                    trailSize = 1;
                    homingPower = 0.08f;
                    weaveMag = 4;
                    weaveScale = 1;
                    lifetime = 42f;
                    keepVelocity = false;
                    smokeEffect = SvFx.hitLaserOrange;
                    hitEffect = despawnEffect = SvFx.hitLaserOrange;
                    frontColor = Color.white;
                    hitSound = Sounds.none;
                    backColor = Pal.lightOrange;
                }};
            }});
        }};

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
                    lifetime = 120f;
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
            health = 1250f;

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
                reload = 60f;
                recoil = 2f;
                shootSound = Sounds.mediumCannon;

                top = false;
                alternate = false;
                shoot.shots = 4;
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
                    splashDamage = 25f;
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
            health = 2220f;

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
            health = 2820f;

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
                            bullet = new ExplosionBulletType(90, 25f){{
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
            health = 6820f;

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
                            bullet = new ExplosionBulletType(60, 30f){{
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

        bulker = new AtlacianUnitType("bulker"){{
            controller = u -> new WaterCargoAI();
            constructor = WaterPayloadUnit::create;
            isEnemy = false;
            allowedInPayloads = false;
            logicControllable = false;
            playerControllable = false;
            envDisabled = 0;
            payloadCapacity = 0f;

            lowAltitude = false;
            flying = false;

            pathCost = Pathfinder.costTypes.get(Pathfinder.costNaval);
            drag = 0.06f;
            speed = 3.5f;
            rotateSpeed = 9f;
            accel = 0.1f;
            itemCapacity = 100;
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

    public static void helicopter(String... ids) {
        for (String id : ids) helicopter(id);
    }
}
