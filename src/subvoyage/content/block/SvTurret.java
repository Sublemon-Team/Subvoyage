package subvoyage.content.block;

import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.IntSeq;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.DrawTurret;
import subvoyage.core.draw.DataEffect;
import subvoyage.core.draw.SvPal;
import subvoyage.content.ost.SvSounds;
import subvoyage.core.draw.block.DrawTurretCallbacked;
import subvoyage.core.draw.part.SvRegionPart;
import subvoyage.core.draw.SvFx;
import subvoyage.type.block.turret.LaserTurret;
import subvoyage.type.block.turret.resist.DrawResist;
import subvoyage.type.block.turret.resist.ResistTurret;
import subvoyage.type.shoot.ShootUpsurge;
import subvoyage.type.shoot.ShootWhirl;
import subvoyage.type.shoot.ShootZigZag;

import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.with;
import static subvoyage.core.ContentStates.*;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvBlocks.atl;

public class SvTurret {
    public static Block
        whirl,rupture,resonance,cascade,spectrum,upsurge,resistance
            ;

    public static void load() {
        whirl = new ItemTurret("whirl"){{
            requirements(Category.turret,atl(), with(spaclanium, 85, clay, 45, iridium, 30));
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
            reload = 60f;
            shoot = new ShootUpsurge() {{
                shots = 2;
                shotDelay = 10;
                firstShotDelay = 10;
            }};;

            float BPS = 2f;

            float mainDamage = WHIRL_DPS/BPS;

            ammo(
                    spaclanium, new BasicBulletType(3.7f, mainDamage) {{
                        width = 15f;
                        height = 15f;
                        shrinkY = 0.1f;
                        lifetime = 3.7f*10*8f;
                        hitEffect = Fx.blastExplosion;
                        despawnEffect = Fx.blastExplosion;
                        shootEffect = SvFx.pulverize;
                        smokeEffect = Fx.none;
                        hitColor = backColor = trailColor = SvPal.spaclanium;
                        frontColor = Color.white;
                        trailWidth = 3f;
                        trailLength = 5;

                        ammoMultiplier = 2f;
                    }}
            );

            shootY = 5f;
            recoil = 1f;
            rotate = false;

            range = 18*8f;
            scaledHealth = 900/9f;

            coolantMultiplier = 1f;
            coolant = consume(new ConsumeLiquid(hydrogen, 20f / 60f));

            limitRange(0);
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
            shoot = new ShootZigZag() {{
                mag = 5;
                scl = 4f;
            }};
            reload = 80f;

            float BPS = 60f/(reload+shoot.firstShotDelay+shoot.shots*shoot.shotDelay)*(shoot.shots*2f);
            float mainDamage = RUPTURE_DPS/BPS*1f;
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

                        ammoPerShot = 3;
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
        resonance = new PowerTurret("resonance") {{
            requirements(Category.turret,atl(), with(corallite, 185, iridium, 140, spaclanium,80, clay, 180));

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
        cascade = new LaserTurret("cascade") {{
            requirements(Category.turret,atl(), with(corallite,300,iridium,200,spaclanium,220,clay,150,chrome,100));

            researchCost = with(clay,1000,iridium,800, chrome,700,spaclanium,600);

            outlineColor = SvPal.outline;

            size = 3;
            shootCone = 360f;
            fogRadius = 6;
            targetGround = true;
            targetAir = true;
            squareSprite = false;

            inputs = IntSeq.with(1,2,3);
            laserRequirement = 30;
            laserMaxEfficiency = 2f;
            laserOverpowerScale = 0.5f;
            capacity = 120;

            consumePower(1.2f);

            reload = 40f;
            shoot = new ShootSpread(2,30);

            float BPS = 60f/(reload+shoot.firstShotDelay+shoot.shots*shoot.shotDelay)*(shoot.shots);
            float mainDamage = CASCADE_DPS/BPS*0.6f;
            float subDamage = CASCADE_DPS/BPS*0.4f/(5f*2f);

            shootType = new BasicBulletType(6f, mainDamage){{
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
                    }};

            smokeEffect = Fx.shootSmokeSmite;
            drawer = new DrawTurret("atlacian-"){{
                var heatp = DrawPart.PartProgress.warmup.blend(p -> Mathf.absin(2f, 1f) * p.warmup, 0.2f);
                var haloProgress = DrawPart.PartProgress.warmup.delay(0.5f);
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

            coolantMultiplier = 1f;

            limitRange(6);
        }};
        upsurge = new ItemTurret("upsurge") {{
            requirements(Category.turret,atl(),with(phosphide,100,iridium,300,spaclanium,300,clay,300,chrome,200));

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
            shoot = new ShootPattern() {{
                shots = 1;
                firstShotDelay = 10f;
            }};

            float BPS = (120f/reload);
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
            ammo(nitride,new ArtilleryBulletType(2.5f, 0, "shell") {{
                lifetime = 60f;

                intervalDelay = 30f;
                bulletInterval = 100f;

                height = width = 0f;
                collides = false;
                hittable = false;

                scaleLife = false;

                fragBullets = 1;
                fragAngle = fragSpread = fragRandomSpread = fragVelocityMin = fragVelocityMax = 0;

                fragBullet = new ExplosionBulletType(mainDamage * 0.3f,8f*5f) {{
                    killShooter = false;

                    hitEffect = new MultiEffect(new DataEffect(SvFx.spectrumExplosion,8f*5f));
                    collidesAir = true;
                    collidesGround = true;
                    pierce = true;
                    pierceCap = 1;
                    despawnEffect = Fx.none;
                    knockback = 2f;
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
                }};

                intervalBullet = new ExplosionBulletType(mainDamage * 0.7f,8f*8f) {{
                    killShooter = false;

                    hitEffect = new MultiEffect(new DataEffect(SvFx.spectrumExplosion,8f*8f));
                    collidesAir = true;
                    collidesGround = true;
                    pierce = true;
                    pierceCap = 1;
                    despawnEffect = Fx.none;
                    knockback = 2f;
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
                }};

            }});
        }};
        resistance = new ResistTurret("resistance") {{
            requirements(Category.turret,atl(),with(spaclanium,1));
            range = 180*tilesize;
            fogRadius = 15;
            spacing = 160;
            fogRadiusMultiplier = 0.4F;

            health = 5400;

            bulletType = new ExplosionBulletType(RESIST_DPS,4*tilesize) {{
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
            consumePower(2.3f);

            inputs = IntSeq.with(1,2,3);
            laserRequirement = 120;
            laserMaxEfficiency = 2f;
            laserOverpowerScale = 0.5f;
            capacity = 240;

            coolant = (ConsumeLiquid) consume(new ConsumeLiquid(hydrogen, 120f / 60f)).boost();

            liquidCapacity = 300f;

            size = 5;

            cooldownTime = 120f;
            recoilTime = 50f;
            recoilPow = 2;
            recoil = 1f;

            outlineColor = SvPal.outline;
            heatColor = SvPal.teslaCharge;

            drawer = new DrawResist("atlacian-") {{
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

            consumePower(5f);
            consumeItem(nitride,3);
        }};
    }

}
