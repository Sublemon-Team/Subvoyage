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
import mindustry.type.UnitType.*;
import mindustry.type.ammo.*;
import mindustry.type.unit.*;
import subvoyage.*;
import subvoyage.content.world.*;
import subvoyage.entities.part.*;

public class SvUnits{
    public static UnitType
    // core
    marine,
    //helicopters
    helio, harke;

    public static int copterId = 0;
    public static void load(){
        copterId = EntityMapping.register("helio", HelicopterUnitEntity::new);
        copterId = EntityMapping.register("harke", HelicopterUnitEntity::new);
        marine = new AtlacianUnitType("marine"){{
            aiController = BuilderAI::new;
            constructor = UnitEntity::create;
            isEnemy = false;
            coreUnitDock = true;
            lowAltitude = true;
            flying = true;

            mineSpeed = 8f;
            mineTier = 1;
            buildSpeed = 0.4f;
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

        helio = new HelicopterUnitType("helio"){{
            aiController = FlyingFollowAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.05f;
            speed = 1.6f;
            rotateSpeed = 12f;
            accel = 0.1f;
            health = 800f;
            engineSize = 0;

            RotatorRegionPart copter = new RotatorRegionPart(){
                {
                    outline = false;
                    layerOffset = Layer.flyingUnitLow;
                    xScl = 1.5f;
                    yScl = 1.5f;
                    y = -0.15f;
                    moveRot = 600f;
                    onUpdate = (e) -> moveRot = 600f + (600f * e.localAcceleration);
                    rotation = 360f;
                }
            };

            parts.add(copter);
            hitSize = 8f;
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
                bullet = new BasicBulletType(3f, 40){{
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

        harke = new HelicopterUnitType("harke"){{
            aiController = FlyingFollowAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.15f;
            speed = 1.3f;
            rotateSpeed = 6f;
            accel = 0.25f;
            health = 800f;

            engineOffset = -7.5f;
            engineSize = 0;
            RotatorRegionPart copter = new RotatorRegionPart(){
                {
                    outline = false;
                    layerOffset = Layer.flyingUnitLow;
                    xScl = 1.5f;
                    yScl = 1.5f;
                    y = -0.15f;
                    moveRot = 600f;
                    onUpdate = (e) -> moveRot = 600f + (moveRot * e.localAcceleration);
                    rotation = 360f;
                }
            };

            parts.add(copter);
            hitSize = 8f;
            abilities.add(
            new MoveEffectAbility(3, engineOffset - 1, Pal.sapBulletBack, SvFx.missileTrailShort, 1.5f){{
                teamColor = true;
            }},

            new MoveEffectAbility(-3, engineOffset - 1, Pal.sapBulletBack, SvFx.missileTrailShort, 1.5f){{
                teamColor = true;
            }}
            );

            weapons.add(new Weapon(SubvoyageMod.ID + "-rocket-launcher"){{
                x = 6f;
                reload = 60f;
                recoil = 2f;
                shootSound = Sounds.missileLaunch;
                velocityRnd = 0f;
                inaccuracy = 0f;
                top = false;
                alternate = false;
                bullet = new BulletType(){{
                    shootEffect = Fx.sparkShoot;
                    smokeEffect = Fx.shootSmokeTitan;
                    hitColor = Pal.suppress;
                    shake = 1f;
                    speed = 0f;
                    keepVelocity = false;
                    collidesAir = true;
                    spawnUnit = new MissileUnitType("harke-missile"){{
                        outlineColor = Pal.darkOutline;
                        trailRotation = true;
                        targetAir = true;
                        physics = true;
                        lowAltitude = true;

                        hitEffect = despawnEffect = Fx.blastExplosion;
                        smokeEffect = SvFx.shootLauncher;
                        splashDamageRadius = 10f;
                        splashDamage = 40f;

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
                            bullet = new ExplosionBulletType(60f, 15f){{
                                collidesAir = true;
                                suppressionRange = 140f;
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
    }
}
