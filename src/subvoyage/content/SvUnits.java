package subvoyage.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootHelix;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.type.unit.*;
import mindustry.type.weapons.*;
import mindustry.world.meta.*;
import subvoyage.*;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.other.*;
import subvoyage.content.sound.*;
import subvoyage.draw.part.*;
import subvoyage.draw.visual.*;
import subvoyage.type.ai.*;
import subvoyage.type.shoot.*;
import subvoyage.type.shoot.bullet.*;
import subvoyage.type.unit.ability.LegionfieldAbility;
import subvoyage.type.unit.helicopter.*;
import subvoyage.type.unit.hydromech.*;
import subvoyage.type.unit.hydromech.custom.*;
import subvoyage.type.unit.hydromech.weapons.*;
import subvoyage.type.unit.rover.RoverUnitType;
import subvoyage.type.unit.type.*;
import subvoyage.type.unit.weapons.*;

import static arc.Core.atlas;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Draw.z;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.tilesize;
import static subvoyage.BalanceStates.*;

public class SvUnits{
    public static UnitType
    // core
    shift,distort,commute,
    cryptal,
    //helicopters
    lapetus, skath, charon, callees,ganymede,
    //hydromechs
    leeft, flagshi, vanguard, squadron, armada,
    //rovers
    stunt, zeal, gambit,
    //cargo,payload
    bulker,
    pisun; //shh, don't tell anyone

    public static int mapHelicopter = 0;
    public static int mapHMech = 0;
    public static void load(){
        helicopter("lapetus", "skath", "charon", "callees", "ganymede");
        hmech("leeft", "flagshi", "vanguard", "squadron", "armada");

        //core
        shift = new AtlacianUnitType("shift"){{
            aiController = BuilderAI::new;
            constructor =  PayloadUnit::create;
            isEnemy = false;
            coreUnitDock = true;
            lowAltitude = true;
            flying = true;

            payloadCapacity = 2f * 2f * tilesize * tilesize;
            pickupUnits = false;
            vulnerableWithPayloads = true;

            targetPriority = -2;
            targetable = false;
            hittable = false;

            mineWalls = true;
            mineFloor = true;

            mineSpeed = 8f;
            mineTier = 1;
            buildSpeed = 1f;
            drag = 0.05f;
            speed = 4.6f;
            rotateSpeed = 15f;
            accel = 0.1f;
            fogRadius = 0f;
            itemCapacity = 30;
            health = 400f;
            engineOffset = 6.5f;
            engineSize = 0;
            hitSize = 8f;

            setEnginesMirror(
                    new UnitEngine(19 / 4f, -24 / 4f, 2.5f, 315f),
                    new UnitEngine(22 / 4f, 20 / 4f, 2.5f, -315f)
            );

            ammoType = new PowerAmmoType(900);

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
                autoTarget = false;
                controllable = true;
                laserColor = Pal.accent;
                healColor = Pal.accent;

                bullet = new BulletType(){{
                    maxRange = 60f;
                }};
            }});
        }};

        distort = new AtlacianUnitType("distort"){{
            aiController = BuilderAI::new;
            constructor =  PayloadUnit::create;
            isEnemy = false;
            coreUnitDock = true;
            lowAltitude = true;
            flying = true;

            targetPriority = -2;
            targetable = false;
            hittable = false;

            payloadCapacity = 2f * 2f * tilesize * tilesize;
            pickupUnits = false;
            vulnerableWithPayloads = true;

            mineWalls = true;
            mineFloor = true;

            itemCapacity = 60;
            mineSpeed = 10f;
            mineTier = 2;
            buildSpeed = 2f;
            drag = 0.05f;
            speed = 5.6f;
            rotateSpeed = 20f;
            accel = 0.1f;
            fogRadius = 0f;
            health = 600f;
            engineOffset = 6.5f;
            engineSize = 0;
            hitSize = 10f;

            setEnginesMirror(
                    new UnitEngine(20 / 4f, -28 / 4f, 2.5f, 315f),
                    new UnitEngine(24 / 4f, 12 / 4f, 2.5f, -315f)
            );

            ammoType = new PowerAmmoType(900);

            weapons.add(new RepairBeamWeapon(){{
                widthSinMag = 0.11f;
                reload = 20f;
                x = 0f;
                y = 6.5f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.7f;
                repairSpeed = 3.3f;
                fractionRepairSpeed = 0.06f;
                aimDst = 0f;
                shootCone = 15f;
                mirror = false;

                targetUnits = false;
                targetBuildings = true;
                autoTarget = false;
                controllable = true;
                laserColor = Pal.accent;
                healColor = Pal.accent;

                bullet = new BulletType(){{
                    maxRange = 60f;
                }};
            }});
        }};

        commute = new AtlacianUnitType("commute"){{
            aiController = BuilderAI::new;
            constructor = PayloadUnit::create;
            isEnemy = false;
            coreUnitDock = true;
            lowAltitude = true;
            flying = true;

            targetPriority = -2;
            targetable = false;
            hittable = false;

            payloadCapacity = 2f * 2f * tilesize * tilesize;
            pickupUnits = false;
            vulnerableWithPayloads = true;

            mineWalls = true;
            mineFloor = true;

            itemCapacity = 90;
            mineSpeed = 10f;
            mineTier = 2;
            buildSpeed = 3f;
            drag = 0.05f;
            speed = 6f;
            rotateSpeed = 20f;
            accel = 0.1f;
            fogRadius = 0f;
            health = 600f;
            engineOffset = 6.5f;
            engineSize = 0;
            hitSize = 10f;

            setEnginesMirror(
                    new UnitEngine(30 / 4f, -12 / 4f, 3f, 315f),
                    new UnitEngine(20 / 4f, -36 / 4f, 3f, 315f)
            );

            ammoType = new PowerAmmoType(900);

            weapons.add(new RepairBeamWeapon(){{
                widthSinMag = 0.11f;
                reload = 20f;
                x = 2f;
                y = 6.5f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.7f;
                repairSpeed = 3.3f;
                fractionRepairSpeed = 0.06f;
                aimDst = 0f;
                shootCone = 15f;
                mirror = true;

                targetUnits = true;
                targetBuildings = true;
                autoTarget = false;
                controllable = true;
                laserColor = Pal.accent;
                healColor = Pal.accent;

                bullet = new BulletType(){{
                    maxRange = 90f;
                }};
            }});
        }};

        cryptal = new AtlacianUnitType("cryptal") {{
            constructor = UnitEntity::create;
            playerControllable = false;
            logicControllable = false;
            controller = (e) -> new CryptalAI();

            health = 750;

            //aiController = CryptalAI::new;
            circleTarget = true;
            isEnemy = false;
            speed = 2f;
            rotateSpeed = 5f;
            omniMovement = false;
            lowAltitude = true;
            flying = true;
            engineSize = 0f;

            float haloY = 3.2f;
            parts.addAll(
                    new ShapePart() {{
                        circle = true;
                        hollow = true;
                        color = Pal.accent;
                        layer = Layer.effect;
                        radius = 2.5f;
                        radiusTo = 5f;
                        stroke = 1f;
                        strokeTo = 1.5f;
                        y = haloY;
                    }},
                    new HaloPart(){{
                             progress = PartProgress.constant(1f);
                             color = Pal.accent;
                             layer = Layer.effect;
                             y = haloY;
                             haloRotateSpeed = -1f;

                             shapes = 4;
                             shapeRotation = 180f;
                             triLength = 0f;
                             triLengthTo = 2f;
                             haloRotation = 45f;
                             haloRadius = 2.5f;
                             tri = true;
                             radius = 1f;
                         }},

                    new HaloPart(){{
                        progress = PartProgress.constant(1f);
                        color = Pal.accent;
                        layer = Layer.effect;
                        y = haloY;
                        haloRotateSpeed = -1f;

                        shapes = 4;
                        shapeRotation = 180f;
                        triLength = 0f;
                        triLengthTo = 2f;
                        haloRotation = 45f;
                        haloRadius = 2.5f;
                        tri = true;
                        radius = 1.5f;
                    }},

                    new HaloPart(){{
                        progress = PartProgress.constant(1f);
                        color = Pal.accent;
                        layer = Layer.effect;
                        y = haloY;

                        shapes = 4;
                        triLength = 1.5f;
                        triLengthTo = 1.5f;
                        haloRadius = 2.8f;
                        haloRotation = 45f;
                        haloRotateSpeed = 2f;
                        sides = 4;
                        shapeRotation = 90f;
                        shapeMoveRot = 10f;
                        tri = true;
                        radius = 1.5f;
                    }},
                    new FlarePart(){{
                        progress = PartProgress.warmup;
                        color1 = Pal.accent;
                        stroke = 3f;
                        radius = 0f;
                        radiusTo = 12f;
                        layer = Layer.effect;
                        y = haloY;
                        x = 0f;
                        followRotation = true;
                        sides = 6;
                    }}
            );

            weapons.add(new OffloadDestroyWeapon() {{
                y = haloY;
                x = 0;
                alternate = false;
                mirror = false;

                rotate = true;
                shootWarmupSpeed = 0.01f;
                minWarmup = 0.8f;

                bullet = new LaserBoltBulletType(1f,0f) {
                    @Override
                    public void draw(Bullet b) {
                        super.draw(b);
                    }

                    @Override
                    public void update(Bullet b) {
                        b.remove();
                        super.update(b);
                    }
                };
            }

                @Override
                protected void handleBullet(Unit unit, WeaponMount mount, Bullet bullet) {
                    super.handleBullet(unit, mount, bullet);
                    mount.warmup = 0f;
                }
            });
            setEnginesMirror(
                    new UnitEngine(16 / 4f, -28 / 4f, 1.8f, 315f),
                    new UnitEngine(58 / 4f /2f, -40 / 4f / 2f, 1.5f, 315f));
        }};

        //helicopter
        lapetus = new HelicopterUnitType("lapetus"){{
            aiController = FlyingAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.05f;
            speed = 1.4f;
            rotateSpeed = 4f;
            accel = 0.1f;
            health = HELIO_T1_HU;
            engineSize = 0;
            hitSize = 20f;
            researchCostMultiplier = 0;
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

            float BPS = 2f * 0.5f;

            parts.add(copter);
            weapons.add(new Weapon(Subvoyage.ID + "-marine-weapon"){{
                x = 5f;
                layerOffset = -2;
                reload = 60f;
                recoil = 2f;
                shootSound = Sounds.missileLaunch;
                velocityRnd = 0f;
                inaccuracy = 0f;
                top = false;
                alternate = false;

                bullet = new BasicBulletType(3f, HELIO_T1_DPS/BPS) {{
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
                    splashDamage = 25f;

                    trailEffect = SvFx.missileTrailSmokeSmall;
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
            health = HELIO_T2_HU;

            researchCostMultiplier = 0;

            engineOffset = -7.5f;
            engineSize = 0;
            hitSize = 20f;
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

            float BPS = 2f * 1.12f;

            weapons.add(new Weapon(Subvoyage.ID + "-missile-launcher"){{
                x = 7f;
                y = -2f;
                reload = 160f;
                recoil = 2f;
                shootSound = Sounds.mediumCannon;

                top = false;
                alternate = true;

                shoot = new ShootUpsurge();
                shoot.shots = 6;
                shoot.shotDelay = 15f;

                bullet = new BasicBulletType(){{
                    sprite = "shell";
                    width = height = 12f;
                    maxRange = 50f;
                    ignoreRotation = true;

                    hitColor = trailColor = SvPal.heatGlow;
                    frontColor = Color.white;
                    trailWidth = 4f;
                    trailLength = 8;
                    hitEffect = despawnEffect = Fx.blastExplosion;
                    smokeEffect = Fx.shootSmokeDisperse;

                    rotationOffset = 90f;
                    trailRotation = true;
                    trailEffect = SvFx.skathTrail;
                    trailChance = 0.44f;

                    weaveMag = 10f;
                    weaveScale = 60f;
                    weaveRandom = false;

                    hitSound = Sounds.plasmaboom;

                    backColor = SvPal.heatGlow;

                    mixColorFrom = SvPal.heatGlow;
                    mixColorTo = Color.white;

                    ejectEffect = Fx.none;
                    hitSize = 22f;

                    collidesAir = true;
                    lifetime = 30f;

                    pierce = true;
                    pierceCap = 1;

                    hitEffect = new MultiEffect(Fx.blastExplosion, Fx.smokeCloud);
                    keepVelocity = false;
                    weaveMag = 2f;
                    weaveScale = 1f;
                    speed = 1.1f;
                    drag = -0.020f;
                    homingPower = 0.05f;

                    damage = splashDamage = HELIO_T2_DPS/BPS;
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
            health = HELIO_T3_HU;

            researchCostMultiplier = 0f;

            hitSize = 20f;

            engineOffset = -7.5f;
            engineSize = 0;
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

            float BPS = 60f/10f;

            float abilityDamage = HELIO_T3_DPS/BPS*0.05f;
            float weaponDamage = HELIO_T3_DPS/BPS*0.95f;

            parts.add(copter);
            abilities.add(
            new MoveEffectAbility(3, engineOffset - 4, Pal.sapBulletBack, SvFx.missileTrailShort, 1.5f){{
                teamColor = true;
            }},

            new MoveEffectAbility(-3, engineOffset - 4, Pal.sapBulletBack, SvFx.missileTrailShort, 1.5f){{
                teamColor = true;
            }},

            new MoveLightningAbility(abilityDamage,8,0.1f,0f,1f,2f, SvPal.heatGlow)
            );

            weapons.add(new Weapon(Subvoyage.ID + "-rocket-launcher"){{
                top = false;
                alternate = false;
                x = 6f;
                y = 2f;
                reload = 10f;
                recoil = 2f;
                shootSound = Sounds.missileLaunch;
                shoot = new ShootBarrel() {{
                    barrels = new float[] {
                            -4f, 1f, -10f,
                            -2f, 0.5f, 10f,
                            0f, 0f, 0f,
                            2f, 0.5f, -10f,
                            4f, 1f, 10f
                    };
                }};

                bullet = new RailBulletType() {{
                    length = 8*10f;
                    damage = weaponDamage;

                    hitColor = SvPal.heatGlow;
                    hitEffect = endEffect = Fx.hitBulletColor;
                    pierceDamageFactor = 1.05f;
                    pointEffectSpace = 60f;

                    smokeEffect = Fx.colorSpark;

                    endEffect = new Effect(14f, e -> {
                        color(e.color);
                        z(Layer.bullet);
                        Drawf.tri(e.x, e.y, e.fout() * 1.5f, 5f, e.rotation);
                    });

                    shootEffect = new Effect(10, e -> {
                        color(e.color);
                        z(Layer.bullet);
                        float w = 1.2f + 7 * e.fout();

                        Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
                        color(e.color);

                        for(int i : Mathf.signs){
                            Drawf.tri(e.x, e.y, w * 0.9f, 18f * e.fout(), e.rotation + i * 90f);
                        }

                        Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
                    });

                    lineEffect = new Effect(20f, e -> {
                        if(!(e.data instanceof Vec2 v)) return;
                        z(Layer.bullet);
                        color(e.color);
                        stroke(e.fout() * 1.5f + 1f);

                        Fx.rand.setSeed(e.id);
                        for(int i = 0; i < 7; i++){
                            Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                            Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                        }

                        e.scaled(14f, b -> {
                            stroke(b.fout() * 3f);
                            color(e.color);
                            Lines.line(e.x, e.y, v.x, v.y);
                        });
                    });
                }};
            }});
        }};

        callees = new HelicopterUnitType("callees"){{
            aiController = FlyingAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.16f;
            speed = 1.6f;
            rotateSpeed = 4f;
            researchCostMultiplier = 0f;
            accel = 0.45f;
            health = HELIO_T4_HU;

            engineOffset = -7.5f;
            engineSize = 0;
            hitSize = 45f;
            RotatorRegionPart copter = new RotatorRegionPart(Subvoyage.ID + "-medium-rotator"){{
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
            abilities.add(new ShieldArcAbility() {{
                radius = hitSize;
                regen = 0.1f;
                max = 200f;
                cooldown = 10f * 60f;
                angle = 80f;
                angleOffset = 100f;
                whenShooting = true;
                width = 10f;
            }},new ShieldArcAbility() {{
                radius = hitSize;
                regen = 0.1f;
                max = 200f;
                cooldown = 10f * 60f;
                angle = 80f;
                angleOffset = -100f;
                whenShooting = true;
                width = 10f;
            }});

            trailLength = 20;
            trailScl = 1.9f;
            setEnginesMirror(
                new UnitEngine(7.2f, -15, 2.25f, -90)
            );

            float BPS = 2f * 2f;
            float bulletDamage = HELIO_T4_DPS/BPS;

            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                x = 13f;
                y = 8f;

                reload = 90f;
                recoil = 2f;

                rotate = true;
                rotateSpeed = 0.4f;
                layerOffset = -2f;
                rotationLimit = 22f;

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

                bullet = new BasicBulletType(4f, bulletDamage*0.2f){{
                    knockback = 4f;
                    width = 25f;
                    hitSize = 20f;
                    height = 20f;
                    sprite = "shell";
                    shootEffect = Fx.shootBigColor;
                    smokeEffect = Fx.shootSmokeSquareSparse;
                    ammoMultiplier = 1;
                    hitColor = backColor = trailColor = SvPal.heatGlow;
                    frontColor = SvPal.heatGlow;
                    trailWidth = 8f;
                    trailLength = 10;
                    trailInterp = Interp.fade;
                    hitEffect = despawnEffect = Fx.hitSquaresColor;
                    buildingDamageMultiplier = 0.2f;

                    fragBullets = 1;
                    fragVelocityMin = fragSpread = fragRandomSpread = fragVelocityMax = 0;

                    pierce = true;

                    fragBullet = new ExplosionBulletType(bulletDamage*0.75f, 50f){{
                        collidesAir = true;
                        suppressionRange = 10f;

                        killShooter = false;

                        despawnSound = SvSounds.flashExplosion;
                        despawnEffect = new MultiEffect(
                                SvFx.colorRadExplosion.get(new Object[] {SvPal.heatGlow,50f}),
                                new ExplosionEffect(){{
                                    lifetime = 50f;
                                    waveStroke = 5f;
                                    waveLife = 8f;
                                    waveColor = Color.white;
                                    sparkColor = smokeColor = SvPal.heatGlow;
                                    waveRad = 35f;
                                    smokeSize = 4f;
                                    smokes = 7;
                                    smokeSizeBase = 0f;
                                    sparks = 10;
                                    sparkRad = 40f;
                                    sparkLen = 6f;
                                    sparkStroke = 2f;
                                }});
                    }};

                    lifetime = 40f;
                }};

                shoot = new ShootSpreadForwardBackwards(6, 5f);
            }});
        }};

        ganymede = new HelicopterUnitType("ganymede"){{
            aiController = FlyingAI::new;
            constructor = HelicopterUnitEntity::create;
            drag = 0.16f;
            speed = 1.8f;
            rotateSpeed = 3f;
            accel = 0.45f;
            health = HELIO_T5_HU;
            researchCostMultiplier = 0f;

            engineOffset = -7.5f;
            engineSize = 0;
            hitSize = 64f;
            RotatorRegionPart copter = new RotatorRegionPart(Subvoyage.ID + "-medium-rotator"){{
                mirror = true;
                layer = Layer.flyingUnitLow;
                xScl = 1.6f;
                yScl = 1.6f;
                x = 23.5f;
                y = -2.75f;
                rotationSpeed = 400f;
            }};

            RotatorRegionPart tail = new RotatorRegionPart(Subvoyage.ID + "-medium-rotator"){{
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

            float mainWeaponBPS = 0.25f*6f;
            float mainWeaponDamage = (HELIO_T5_DPS*0.75f)/mainWeaponBPS;

            float subWeaponBPS = 3f*2f;
            float subWeaponDamage = (HELIO_T5_DPS*0.25f)/subWeaponBPS;

            trailLength = 20;
            trailScl = 1.9f;
            setEnginesMirror(
            new UnitEngine(14f, -20, 2.75f, -90)
            );

            weapons.add(new Weapon(Subvoyage.ID + "-beam-weapon"){{
                shadow = 20f;
                mirror = false;
                shootY = 7f;
                rotate = true;
                x = 0;
                y = 4f;

                targetInterval = 20f;
                targetSwitchInterval = 35f;

                rotateSpeed = 3.5f;
                reload = 4*60f;
                recoil = 1f;
                shootSound = Sounds.beam;

                bullet = new BasicBulletType(5f,mainWeaponDamage*0.1f) {{
                    sprite = "circle-bullet";
                    backColor = SvPal.heatGlow;
                    frontColor = Color.white;
                    width = height = 10f;
                    shrinkY = 0f;
                    trailLength = 20;
                    trailWidth = 7f;
                    trailColor = SvPal.heatGlow;
                    trailInterval = 3f;

                    shake = 5f;

                    fragBullets = 5;
                    fragBullet = new BulletType(){{
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

                            lifetime = 20f;

                            hitEffect = despawnEffect = Fx.blastExplosion;
                            smokeEffect = SvFx.shootLauncher;
                            trailEffect = SvFx.missileTrailSmokeSmall;
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
                                bullet = new EmpBulletType(){{
                                    float rad = 8*3f;

                                    fragBullets = 1;
                                    fragBullet = new EmpBulletType(){{
                                        float rad = 8*4f;

                                        scaleLife = true;
                                        lightOpacity = 0.7f;
                                        unitDamageScl = 1.1f;
                                        healPercent = 0.2f;
                                        timeIncrease = 3f;
                                        timeDuration = 60f * 20f;
                                        powerDamageScl = 1.1f;
                                        damage = mainWeaponDamage*0.5f;
                                        hitColor = lightColor = SvPal.heatGlow;
                                        lightRadius = 8*4f;
                                        clipSize = 8*5f;
                                        shootEffect = new Effect(40, e -> {
                                            color(SvPal.heatGlow);
                                            stroke(e.fout() * 1.6f);

                                            randLenVectors(e.id, 18, e.finpow() * 27f, e.rotation, 360f, (x, y) -> {
                                                float ang = Mathf.angle(x, y);
                                                lineAngle(e.x + x, e.y + y, ang, e.fout() * 6 + 1f);
                                            });
                                        });
                                        smokeEffect = Fx.shootBigSmoke2;
                                        lifetime = 30f;
                                        sprite = "circle-bullet";
                                        backColor = SvPal.heatGlow;
                                        frontColor = Color.white;
                                        width = height = 8f;
                                        shrinkY = 0f;
                                        speed = 0f;
                                        trailLength = 20;
                                        trailWidth = 3f;
                                        trailColor = SvPal.heatGlow;
                                        trailInterval = 3f;
                                        splashDamage = 20f;
                                        radius = rad;
                                        splashDamageRadius = rad;
                                        hitShake = 4f;
                                        trailRotation = true;
                                        trailInterp = Interp.fastSlow;
                                        status = StatusEffects.electrified;
                                        hitSound = Sounds.plasmaboom;

                                        trailEffect = new Effect(16f, e -> {
                                            color(SvPal.heatGlow);
                                            for(int s : Mathf.signs){
                                                Drawf.tri(e.x, e.y, 4f, 10f * e.fslope(), e.rotation + 90f*s);
                                            }
                                        });

                                        hitEffect = new Effect(50f, 100f, e -> {
                                            e.scaled(7f, b -> {
                                                color(SvPal.heatGlow, b.fout());
                                                Fill.circle(e.x, e.y, rad);
                                            });

                                            color(SvPal.heatGlow);
                                            stroke(e.fout() * 3f);
                                            Lines.circle(e.x, e.y, rad);

                                            int points = 10;
                                            float offset = Mathf.randomSeed(e.id, 360f);
                                            for(int i = 0; i < points; i++){
                                                float angle = i* 360f / points + offset;
                                                //for(int s : Mathf.zeroOne){
                                                Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 5f, 10f * e.fout(), angle/* + s*180f*/);
                                                Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 5f, -20f * e.fout(), angle/* + s*180f*/);
                                                //}
                                            }

                                            Fill.circle(e.x, e.y, 12f * e.fout());
                                            color();
                                            Fill.circle(e.x, e.y, 6f * e.fout());
                                            Drawf.light(e.x, e.y, rad * 1.6f, SvPal.heatGlow, e.fout());
                                        });
                                    }};

                                    scaleLife = true;
                                    lightOpacity = 0.7f;
                                    unitDamageScl = 1.1f;
                                    healPercent = 0.2f;
                                    timeIncrease = 3f;
                                    timeDuration = 60f * 20f;
                                    powerDamageScl = 1.1f;
                                    damage = mainWeaponDamage*0.4f;
                                    hitColor = lightColor = SvPal.heatGlow;
                                    lightRadius = 8*4f;
                                    clipSize = 8*5f;
                                    shootEffect = new Effect(40, e -> {
                                        color(SvPal.heatGlow);
                                        stroke(e.fout() * 1.6f);

                                        randLenVectors(e.id, 18, e.finpow() * 27f, e.rotation, 360f, (x, y) -> {
                                            float ang = Mathf.angle(x, y);
                                            lineAngle(e.x + x, e.y + y, ang, e.fout() * 6 + 1f);
                                        });
                                    });
                                    smokeEffect = Fx.shootBigSmoke2;
                                    lifetime = 0f;
                                    sprite = "circle-bullet";
                                    backColor = SvPal.heatGlow;
                                    frontColor = Color.white;
                                    width = height = 8f;
                                    shrinkY = 0f;
                                    speed = 0f;
                                    trailLength = 20;
                                    trailWidth = 3f;
                                    trailColor = SvPal.heatGlow;
                                    trailInterval = 3f;
                                    splashDamage = 20f;
                                    radius = rad;
                                    splashDamageRadius = rad;
                                    hitShake = 4f;
                                    trailRotation = true;
                                    trailInterp = Interp.fastSlow;
                                    status = StatusEffects.electrified;
                                    hitSound = Sounds.plasmaboom;

                                    trailEffect = new Effect(16f, e -> {
                                        color(SvPal.heatGlow);
                                        for(int s : Mathf.signs){
                                            Drawf.tri(e.x, e.y, 4f, 10f * e.fslope(), e.rotation + 90f*s);
                                        }
                                    });

                                    hitEffect = new Effect(50f, 100f, e -> {
                                        e.scaled(7f, b -> {
                                            color(SvPal.heatGlow, b.fout());
                                            Fill.circle(e.x, e.y, rad);
                                        });

                                        color(SvPal.heatGlow);
                                        stroke(e.fout() * 3f);
                                        Lines.circle(e.x, e.y, rad);

                                        int points = 10;
                                        float offset = Mathf.randomSeed(e.id, 360f);
                                        for(int i = 0; i < points; i++){
                                            float angle = i* 360f / points + offset;
                                            //for(int s : Mathf.zeroOne){
                                            Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 5f, 10f * e.fout(), angle/* + s*180f*/);
                                            Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 5f, -20f * e.fout(), angle/* + s*180f*/);
                                            //}
                                        }

                                        Fill.circle(e.x, e.y, 12f * e.fout());
                                        color();
                                        Fill.circle(e.x, e.y, 6f * e.fout());
                                        Drawf.light(e.x, e.y, rad * 1.6f, SvPal.heatGlow, e.fout());
                                    });
                                }};
                            }});
                        }};
                    }};
                }};
            }});

            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                alternate = false;

                x = 23f;
                y = 12f;

                cooldownTime = 0f;
                reload = 10f;
                recoil = 2f;

                rotate = true;
                rotateSpeed = 0.4f;
                layerOffset = -2f;
                rotationLimit = 22f;
                shootCone = 360f;

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

                shoot = new ShootBarrel() {{
                    barrels = new float[] {
                            -4f, 1f, -10f,
                            -2f, 0.5f, 10f,
                            0f, 0f, 0f,
                            2f, 0.5f, -10f,
                            4f, 1f, 10f
                    };
                }};

                bullet = new RailBulletType() {{
                    length = 8*10f;
                    damage = subWeaponDamage;

                    hitColor = SvPal.heatGlow;
                    hitEffect = endEffect = Fx.hitBulletColor;
                    pierceDamageFactor = 1.05f;
                    pointEffectSpace = 60f;

                    smokeEffect = Fx.colorSpark;

                    endEffect = new Effect(14f, e -> {
                        color(e.color);
                        z(Layer.bullet);
                        Drawf.tri(e.x, e.y, e.fout() * 1.5f, 5f, e.rotation);
                    });

                    shootEffect = new Effect(10, e -> {
                        color(e.color);
                        z(Layer.bullet);
                        float w = 1.2f + 7 * e.fout();

                        Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
                        color(e.color);

                        for(int i : Mathf.signs){
                            Drawf.tri(e.x, e.y, w * 0.9f, 18f * e.fout(), e.rotation + i * 90f);
                        }

                        Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
                    });

                    lineEffect = new Effect(20f, e -> {
                        if(!(e.data instanceof Vec2 v)) return;
                        z(Layer.bullet);
                        color(e.color);
                        stroke(e.fout() * 1.5f + 1f);

                        Fx.rand.setSeed(e.id);
                        for(int i = 0; i < 7; i++){
                            Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                            Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                        }

                        e.scaled(14f, b -> {
                            stroke(b.fout() * 3f);
                            color(e.color);
                            Lines.line(e.x, e.y, v.x, v.y);
                        });
                    });
                }};
            }});
        }};

        //hydromech
        leeft = new HydromechUnitType("leeft") {{
            constructor = HydromechUnitEntity::create;
            drag = 0.07f;
            rotateSpeed = 8f;
            armor = 4;
            health = HYDRO_T1_HU;
            hitSize = 15f;
            withStates(
                    HydromechState.GROUND,new UnitStatState() {{
                        speed = 1.1f;
                        inwardsDamageMul = 1.2f;
                    }},
                    HydromechState.WATER,new UnitStatState() {{
                        speed = 1.8f;
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

            float BPS = 3f;
            float weaponDamage = HYDRO_T1_DPS/BPS;

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
                    damage = weaponDamage;
                    lifetime = 40f;
                }};
                waterStat = new WeaponStatState() {{
                    damage = weaponDamage;
                    lifetime = 68f;
                }};
                bullet = new DecayingBulletType(4f,weaponDamage,2f) {{
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
            health = HYDRO_T2_HU;
            hitSize = 20f;
            researchCostMultiplier = 0;
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
                repairSpeed = 0.03f;
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

            float BPS = 2f * 2.25f;
            float weaponDamage = HYDRO_T2_DPS/BPS;

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
                    damage = weaponDamage;
                }};
                waterStat = new WeaponStatState() {{
                    lifetime = 78;
                    damage = weaponDamage*0.85f;
                }};

                bullet = new DecayingBulletType(4f,weaponDamage,4f) {{
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
            researchCostMultiplier = 0f;
            health = HYDRO_T3_HU;
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

            legCount = 4;
            legLength = 24f;
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
                        moveRot = 30f;
                        moveX = 1f;
                        x = 0;
                        y = 0;
                        progress = PartProgress.heat;
                        heatColor = Color.red;
                        mirror = true;
                    }});

            float BPSWater = 0.25f;
            float BPSGround = 0.6f;

            float waterDamage = HYDRO_T3_DPS/BPSWater*0.6f;
            float groundDamage = HYDRO_T3_DPS/BPSGround;

            weapons.add(new HydromechWeapon() {{
                reload = 240f;
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
                            whenShooting = false;
                            drawArc = false;

                            radius = 360f;
                            regen = 0.6f;
                            max = 2000f;
                            y = -20f;
                            width = 0f;
                        }});

                        hitEffect = despawnEffect = Fx.blastExplosion;
                        smokeEffect = SvFx.shootLauncher;
                        trailEffect = SvFx.missileTrailSmokeSmall;

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
                            bullet = new ExplosionBulletType(waterDamage, 50f){{
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

                        abilities.add(new MoveEffectAbility(){{
                            effect = SvFx.missileTrailSmokeSmall;

                            rotation = 180f;
                            y = -9f;
                            color = Color.grays(0.6f).lerp(Color.white, 0.5f).a(0.4f);
                            interval = 1.5f;
                        }});
                    }};
                }};
            }});

            weapons.add(new HydromechWeapon() {{
                reload = 100f;
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
                    smokeEffect = Fx.shootSmokeTitan;

                    hitColor = Pal.suppress;
                    shake = 1f;
                    speed = 0f;
                    keepVelocity = false;
                    collidesAir = true;

                    shootEffect = Fx.rocketSmokeLarge;

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
                        trailEffect = SvFx.missileTrailSmokeMedium;

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

                        abilities.add(new MoveEffectAbility(){{
                            effect = SvFx.missileTrailSmokeMedium;

                            rotation = 180f;
                            y = -9f;
                            color = Color.grays(0.6f).lerp(Color.white, 0.5f).a(0.4f);
                            interval = 1.5f;
                        }});
                        abilities.add(new MoveEffectAbility(){{
                            effect = SvFx.hitLaserOrange;

                            rotation = 180f;
                            y = -9f;
                            color = Color.grays(0.6f).lerp(Color.white, 0.5f).a(0.4f);
                            interval = 1.5f;
                        }});

                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            shootOnDeath = true;
                            bullet = new ExplosionBulletType(groundDamage, 50f){{
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
                    }

                        @Override
                        public void draw(Unit unit) {
                            Draw.scl(1.5f);
                            super.draw(unit);
                            Draw.scl();
                        }

                        @Override
                        public void drawBody(Unit unit) {
                            Draw.scl(1.5f);
                            super.drawBody(unit);
                            Draw.scl();
                        }
                    };
                }};
            }});
        }};

        squadron = new HydromechUnitType("squadron"){{
            constructor = HydromechUnitEntity::create;
            drag = 0.14f;
            rotateSpeed = 2f;
            health = HYDRO_T4_HU;
            researchCostMultiplier = 0f;
            hitSize = 32f;

            bodyScale = 0.9f;

            waveTrailX = 8f;
            waveTrailY = -8f;

            trailScl = 12;
            trailLength = 25;

            accel = 1f;

            withStates(
            HydromechState.GROUND, new UnitStatState(){{
                speed = 0.5f;
                inwardsDamageMul = 1.1f;
            }},
            HydromechState.WATER, new UnitStatState(){{
                speed = 0.6f;
                inwardsDamageMul = 1f;
            }}
            );
            float BPSWater = 3f;
            float BPSGround = 2f;

            float damageWater = HYDRO_T4_DPS/BPSWater;
            float damageGround = HYDRO_T4_DPS/BPSGround;

            weapons.add(new HydromechWeapon(name + "-weapon"){
                {
                    activationState = HydromechState.GROUND;
                    activationBasedDraw = true;

                    warmupSpeedModifier = 0.5f;
                    warmupReloadModifier = 15 / 300f;
                    shootSound = SvSounds.poweredMissileShoot;
                    shootWarmupSpeed = 0.001f;

                    top = true;

                    heatColor = Color.red;
                    layerOffset = 0.015f;

                    bullet = new BulletType(){{
                        smokeEffect = Fx.shootSmokeTitan;

                        hitColor = Pal.suppress;
                        shake = 1f;
                        speed = 0f;
                        keepVelocity = false;
                        collidesAir = true;

                        shootEffect = Fx.rocketSmokeLarge;

                        spawnUnit = new MissileUnitType("squadron-missile"){{
                            controller = u -> new StraightMissileAI();
                            outlineColor = SvPal.outline;
                            trailRotation = true;
                            targetAir = true;
                            physics = false;
                            lowAltitude = true;

                            lifetime = 30f;

                            hitEffect = despawnEffect = Fx.blastExplosion;
                            smokeEffect = SvFx.shootLauncher;
                            trailEffect = SvFx.missileTrailSmokeMedium;

                            trailInterval = 3f;
                            trailWidth = 1f;
                            trailLength = 6;

                            speed = 4.6f;
                            maxRange = 3f;
                            health = 40;

                            engineSize = 3f;
                            hitColor = engineColor = trailColor = SvPal.hydromech;
                            engineLayer = Layer.effect;
                            deathExplosionEffect = Fx.none;
                            loopSoundVolume = 0.1f;
                            deathSound = SvSounds.flashExplosion;
                            parts.add(new FlarePart(){
                                {
                                    progress = PartProgress.constant(1f);
                                    y = 8f;
                                    followRotation = true;
                                    spinSpeed = 0f;
                                    color1 = SvPal.hydromech;
                                    layer = Layer.effect;
                                    radius = 1f;
                                    radiusTo = 8f;
                                }
                                @Override
                                public void draw(PartParams params){
                                    Draw.blend(Blending.additive);
                                    super.draw(params);
                                    Draw.blend(Blending.normal);
                                }
                            });
                            abilities.add(new MoveEffectAbility(){{
                                effect = SvFx.hitLaserColor.get(SvPal.hydromech);

                                rotation = 180f;
                                y = -9f;
                                color = Color.grays(0.6f).lerp(Color.white, 0.5f).a(0.4f);
                                interval = 1.5f;
                            }});

                            weapons.add(new Weapon(){{
                                shootCone = 360f;
                                mirror = false;
                                reload = 1f;
                                shootOnDeath = true;
                                bullet = new ExplosionBulletType(damageGround, 50f){{
                                    collidesAir = true;
                                    suppressionRange = 80f;
                                    shootSound = SvSounds.flashExplosion;
                                    shootEffect = new MultiEffect(
                                    SvFx.colorRadExplosion.get(new Object[] {SvPal.hydromech,50f}),
                                    new ExplosionEffect(){{
                                        lifetime = 50f;
                                        waveStroke = 5f;
                                        waveLife = 8f;
                                        waveColor = Color.white;
                                        sparkColor = smokeColor = SvPal.hydromech;
                                        waveRad = 40f;
                                        smokeSize = 4f;
                                        smokes = 7;
                                        smokeSizeBase = 0f;
                                        sparks = 10;
                                        sparkRad = 40f;
                                        sparkLen = 6f;
                                        sparkStroke = 2f;
                                    }});
                                }};
                            }});
                        }

                            @Override
                            public void draw(Unit unit) {
                                Draw.scl(1.5f);
                                super.draw(unit);
                                Draw.scl();
                            }

                            @Override
                            public void drawBody(Unit unit) {
                                Draw.scl(1.5f);
                                super.drawBody(unit);
                                Draw.scl();
                            }
                        };
                    }};

                    parts.add(new RegionPart("-blade"){{
                        outlineLayerOffset = -0.001f;
                        layerOffset = 0.01f;
                        outline = true;
                        mirror = true;
                        moveRot = -40f;
                        moveY = 2f;
                        progress = PartProgress.smoothReload.inv().mul(PartProgress.warmup.mul(1f).clamp());
                        under = false;
                        moves.add(new PartMove(PartProgress.reload, 1f, 0f, 0));
                        heatColor = Color.red;
                        cooldownTime = 60f;
                    }});
                    parts.add(new FlarePart(){
                        {
                            progress = PartProgress.smoothReload.inv().mul(PartProgress.warmup.mul(1.3f).clamp());
                            y = 8f;
                            followRotation = true;
                            spinSpeed = 0f;
                            color1 = SvPal.hydromech;
                            layer = Layer.effect;
                            radius = 1f;
                            radiusTo = 8f;
                        }
                        @Override
                        public void draw(PartParams params){
                            Draw.blend(Blending.additive);
                            super.draw(params);
                            Draw.blend(Blending.normal);
                        }
                    });

                    mirror = false;
                    x = 0;
                    y = 0f;

                    warmupToHeat = true;
                    reload = 300f;
                }
            });

            weapons.add(new HydromechWeapon(name + "-weapon"){{
                activationState = HydromechState.WATER;
                activationBasedDraw = true;

                warmupSpeedModifier = 0f;
                warmupReloadModifier = 3/300f;
                shootWarmupSpeed = 0.0004f;
                shootSound = SvSounds.poweredMissileShoot;
                warmupToHeat = true;
                reload = 300f;

                top = true;

                heatColor = Color.red;
                layerOffset = 0.015f;

                bullet = new BulletType(){{
                    smokeEffect = Fx.shootSmokeTitan;

                    hitColor = Pal.suppress;
                    shake = 1f;
                    speed = 0f;
                    keepVelocity = false;
                    collidesAir = true;

                    shootEffect = Fx.rocketSmokeLarge;

                    spawnUnit = new MissileUnitType("squadron-missile-water"){{
                        controller = u -> new StraightMissileAI();
                        outlineColor = SvPal.outline;
                        trailRotation = true;
                        targetAir = true;
                        physics = false;
                        lowAltitude = true;

                        lifetime = 20f;

                        hitEffect = despawnEffect = Fx.blastExplosion;
                        smokeEffect = SvFx.shootLauncher;
                        trailEffect = SvFx.missileTrailSmokeMedium;

                        trailInterval = 3f;
                        trailWidth = 1f;
                        trailLength = 6;

                        speed = 5f;
                        maxRange = 3f;
                        health = 40;

                        engineSize = 3f;
                        hitColor = engineColor = trailColor = SvPal.hydromech;
                        engineLayer = Layer.effect;
                        deathExplosionEffect = Fx.none;
                        loopSoundVolume = 0.1f;
                        deathSound = SvSounds.flashExplosion;
                        parts.add(new FlarePart(){
                            {
                                progress = PartProgress.constant(1f);
                                y = 8f;
                                followRotation = true;
                                spinSpeed = 0f;
                                color1 = SvPal.hydromech;
                                layer = Layer.effect;
                                radius = 1f;
                                radiusTo = 8f;
                            }
                            @Override
                            public void draw(PartParams params){
                                Draw.blend(Blending.additive);
                                super.draw(params);
                                Draw.blend(Blending.normal);
                            }
                        });
                        abilities.add(new MoveEffectAbility(){{
                            effect = SvFx.hitLaserColor.get(SvPal.hydromech);

                            rotation = 180f;
                            y = -9f;
                            color = Color.grays(0.6f).lerp(Color.white, 0.5f).a(0.4f);
                            interval = 1.5f;
                        }});

                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            shootOnDeath = true;
                            bullet = new ExplosionBulletType(damageWater, 30f){{
                                collidesAir = true;
                                suppressionRange = 10f;
                                shootSound = SvSounds.flashExplosion;
                                shootEffect = new MultiEffect(
                                        SvFx.colorRadExplosion.get(new Object[] {SvPal.hydromech,30f}),
                                        new ExplosionEffect(){{
                                            lifetime = 50f;
                                            waveStroke = 5f;
                                            waveLife = 8f;
                                            waveColor = Color.white;
                                            sparkColor = smokeColor = SvPal.hydromech;
                                            waveRad = 40f;
                                            smokeSize = 4f;
                                            smokes = 7;
                                            smokeSizeBase = 0f;
                                            sparks = 10;
                                            sparkRad = 40f;
                                            sparkLen = 6f;
                                            sparkStroke = 2f;
                                        }});
                            }};
                        }});
                    }

                        @Override
                        public void draw(Unit unit) {
                            Draw.scl(1.5f);
                            super.draw(unit);
                            Draw.scl();
                        }

                        @Override
                        public void drawBody(Unit unit) {
                            Draw.scl(1.5f);
                            super.drawBody(unit);
                            Draw.scl();
                        }
                    };
                }};
                parts.add(new RegionPart("-blade"){{
                    outlineLayerOffset = -0.001f;
                    layerOffset = 0.01f;
                    outline = true;
                    mirror = true;
                    moveRot = -30f;
                    moveY = 2f;
                    progress = PartProgress.smoothReload.inv().mul(PartProgress.warmup.mul(1f).clamp());
                    under = false;
                    moves.add(new PartMove(PartProgress.reload, 1f, 0f, 0));
                    heatColor = Color.red;
                    cooldownTime = 60f;
                }});
                parts.add(new FlarePart(){
                    {
                        progress = PartProgress.smoothReload.inv().mul(PartProgress.warmup.mul(1.3f).clamp());
                        y = 8f;
                        followRotation = true;
                        spinSpeed = 0f;
                        color1 = SvPal.hydromech;
                        layer = Layer.effect;
                        radius = 1f;
                        radiusTo = 8f;
                    }
                    @Override
                    public void draw(PartParams params){
                        Draw.blend(Blending.additive);
                        super.draw(params);
                        Draw.blend(Blending.normal);
                    }
                });

                mirror = false;
                x = 0;
                y = 0f;
            }});


            bodyHeat = true;
            heatColor = Color.red;
            lockLegBase = true;
            legContinuousMove = true;
            legMinLength = 0.2f;
            legLengthScl = 0.95f;
            legSplashDamage = 1.1f;
            legStraightness = 0.2f;

            legCount = 6;
            legLength = 30f;
            legForwardScl = 2.1f;
            legMoveSpace = 1f;
            rippleScale = 1.2f;
            stepShake = 1.5f;
            legGroupSize = 2;
            legExtension = -6f;
            legBaseOffset = 15f;
            legStraightLength = 0.9f;
            legMaxLength = 1.2f;

            allowLegStep = true;
            mechSideSway = 0.9f;
            mechFrontSway = 0.9f;
            legPhysicsLayer = false;

            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit - 1f;
            targetAir = true;
        }};

        armada = new HydromechUnitType("armada"){{
            constructor = HydromechUnitEntity::create;
            drag = 0.14f;
            rotateSpeed = 2f;
            health = HYDRO_T5_HU;
            hitSize = 32f;
            researchCostMultiplier = 0f;

            bodyScale = 0.8f;

            waveTrailX = 8f;
            waveTrailY = -8f;

            trailScl = 12;
            trailLength = 25;
            legPhysicsLayer = false;
            abilities.add(new ShieldArcAbility(){{
                region = "subvoyage-armada-shield";
                radius = 42f;
                angle = 82f;
                regen = 0.6f;
                cooldown = 60f * 8f;
                max = 2000f;
                y = -20f;
                width = 6f;
                whenShooting = false;
            }});

            float BPS1 = 3*2f;
            float BPS2 = 0.15f*5f*2f;

            float damage1 = HYDRO_T5_DPS*0.2f/BPS1;
            float damage2 = HYDRO_T5_DPS*0.8f/BPS2;

            weapons.add(new HydromechWeapon(leeft.name+"-weapon") {{
                shoot = new ShootLeeft() {{shots = 2;}};
                reload = 20f;
                recoil = 3f;
                inaccuracy = 10f;
                shootY = 0;
                x = 16f;
                y = 0.25f;
                alternate = true;
                top = true;
                mirror = true;
                rotate = true;
                rotateSpeed = 90f/60f;
                shootSound = Sounds.blaster;
                soundPitchMin = 0.5f;
                soundPitchMax = 0.55f;

                groundStat = new WeaponStatState() {{
                    damage = 18f;
                    lifetime = damage1;
                }};
                waterStat = new WeaponStatState() {{
                    damage = 18f;
                    lifetime = damage1;
                }};
                bullet = new BasicBulletType(4f,damage1) {{
                    shootEffect = SvFx.pulverize;
                    smokeEffect = Fx.none;
                    hitColor = backColor = trailColor = SvPal.hydromech;
                    frontColor = Color.white;
                    lifetime = 40f;
                    trailWidth = 5f;
                    trailLength = 8;
                    trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                }};
            }});
            weapons.add(new HydromechWeapon(name+"-weapon") {{
                layerOffset = -0.001f;
                x = 58/4f;
                y = 8/4f;
                rotate = true;
                rotateSpeed = 0.4f;

                warmupSpeedModifier = 0.6f;
                warmupReloadModifier = 110/200f;
                shootWarmupSpeed = 0.05f;
                shootSound = SvSounds.poweredMissileShoot;
                warmupToHeat = true;
                reload = 200f;

                top = true;

                heatColor = Color.red;
                bullet = new BulletType(){{
                    smokeEffect = Fx.shootSmokeTitan;

                    hitColor = Pal.suppress;
                    shake = 1f;
                    speed = 0f;
                    keepVelocity = false;
                    collidesAir = true;

                    shootEffect = Fx.rocketSmokeLarge;

                    spawnUnit = new MissileUnitType("armada-missile"){{
                        controller = u -> new MissileAI();
                        outlineColor = SvPal.outline;
                        trailRotation = true;
                        targetAir = true;
                        physics = false;
                        lowAltitude = true;

                        lifetime = 60f;

                        hitEffect = despawnEffect = Fx.blastExplosion;
                        smokeEffect = SvFx.shootLauncher;
                        trailEffect = SvFx.missileTrailSmokeMedium;

                        trailInterval = 3f;
                        trailWidth = 1f;
                        trailLength = 6;

                        speed = 4.6f;
                        maxRange = 3f;
                        health = 40;

                        engineSize = 3f;
                        hitColor = engineColor = trailColor = SvPal.hydromech;
                        engineLayer = Layer.effect;
                        deathExplosionEffect = Fx.none;
                        loopSoundVolume = 0.1f;
                        deathSound = SvSounds.flashExplosion;
                        parts.add(new FlarePart(){
                            {
                                progress = PartProgress.constant(1f);
                                y = 8f;
                                followRotation = true;
                                spinSpeed = 0f;
                                color1 = SvPal.hydromech;
                                layer = Layer.effect;
                                radius = 1f;
                                radiusTo = 8f;
                            }
                            @Override
                            public void draw(PartParams params){
                                Draw.blend(Blending.additive);
                                super.draw(params);
                                Draw.blend(Blending.normal);
                            }
                        });
                        abilities.add(new MoveEffectAbility(){{
                            effect = SvFx.hitLaserColor.get(SvPal.hydromech);

                            rotation = 180f;
                            y = -9f;
                            color = Color.grays(0.6f).lerp(Color.white, 0.5f).a(0.4f);
                            interval = 1.5f;
                        }});

                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            shootOnDeath = true;
                            bullet = new ExplosionBulletType(damage2, 50f){{
                                collidesAir = true;
                                suppressionRange = 80f;
                                shootSound = SvSounds.flashExplosion;
                                fragBullets = 4;
                                fragRandomSpread = 5;
                                fragSpread = 90f;
                                fragVelocityMin = fragVelocityMax = 1f;
                                fragBullet = new BulletType() {{
                                    smokeEffect = Fx.shootSmokeTitan;

                                    hitColor = Pal.suppress;
                                    shake = 1f;
                                    speed = 0f;
                                    keepVelocity = false;
                                    collidesAir = true;

                                    shootEffect = Fx.rocketSmokeLarge;
                                    spawnUnit = new MissileUnitType("armada-missile-frag") {{
                                        controller = u -> new MissileAI();
                                        outlineColor = SvPal.outline;
                                        trailRotation = true;
                                        targetAir = true;
                                        physics = false;
                                        lowAltitude = true;

                                        lifetime = 15f;

                                        hitEffect = despawnEffect = Fx.blastExplosion;
                                        smokeEffect = SvFx.shootLauncher;
                                        trailEffect = SvFx.missileTrailSmokeMedium;

                                        trailInterval = 3f;
                                        trailWidth = 1f;
                                        trailLength = 6;

                                        speed = 4.6f;
                                        maxRange = 3f;
                                        health = 40;

                                        engineSize = 3f;
                                        hitColor = engineColor = trailColor = SvPal.hydromech;
                                        engineLayer = Layer.effect;
                                        deathExplosionEffect = Fx.none;
                                        loopSoundVolume = 0.1f;
                                        deathSound = SvSounds.flashExplosion;
                                        parts.add(new FlarePart(){
                                            {
                                                progress = PartProgress.constant(1f);
                                                y = 8f;
                                                followRotation = true;
                                                spinSpeed = 0f;
                                                color1 = SvPal.hydromech;
                                                layer = Layer.effect;
                                                radius = 1f;
                                                radiusTo = 8f;
                                            }
                                            @Override
                                            public void draw(PartParams params){
                                                Draw.blend(Blending.additive);
                                                super.draw(params);
                                                Draw.blend(Blending.normal);
                                            }
                                        });
                                        abilities.add(new MoveEffectAbility(){{
                                            effect = SvFx.hitLaserColor.get(SvPal.hydromech);

                                            rotation = 180f;
                                            y = -9f;
                                            color = Color.grays(0.6f).lerp(Color.white, 0.5f).a(0.4f);
                                            interval = 1.5f;
                                        }});
                                        weapons.add(new Weapon(){{
                                            shootCone = 360f;
                                            mirror = false;
                                            reload = 1f;
                                            shootOnDeath = true;
                                            bullet = new ExplosionBulletType(damage2, 40f){{
                                                collidesAir = true;
                                                suppressionRange = 80f;
                                                shootSound = SvSounds.flashExplosion;
                                                shootEffect = new MultiEffect(
                                                        SvFx.colorRadExplosion.get(new Object[] {SvPal.hydromech,40f}),
                                                        new ExplosionEffect(){{
                                                            lifetime = 50f;
                                                            waveStroke = 5f;
                                                            waveLife = 8f;
                                                            waveColor = Color.white;
                                                            sparkColor = smokeColor = SvPal.hydromech;
                                                            waveRad = 40f;
                                                            smokeSize = 4f;
                                                            smokes = 7;
                                                            smokeSizeBase = 0f;
                                                            sparks = 10;
                                                            sparkRad = 40f;
                                                            sparkLen = 6f;
                                                            sparkStroke = 2f;
                                                        }});
                                            }};
                                        }});
                                    }

                                        @Override
                                        public void draw(Unit unit) {
                                            Draw.scl(1.5f);
                                            super.draw(unit);
                                            Draw.scl();
                                        }

                                        @Override
                                        public void drawBody(Unit unit) {
                                            Draw.scl(1.5f);
                                            super.drawBody(unit);
                                            Draw.scl();
                                        }
                                    };
                                }};
                                shootEffect = new MultiEffect(
                                        SvFx.colorRadExplosion.get(new Object[] {SvPal.hydromech,50f}),
                                        new ExplosionEffect(){{
                                            lifetime = 50f;
                                            waveStroke = 5f;
                                            waveLife = 8f;
                                            waveColor = Color.white;
                                            sparkColor = smokeColor = SvPal.hydromech;
                                            waveRad = 40f;
                                            smokeSize = 4f;
                                            smokes = 7;
                                            smokeSizeBase = 0f;
                                            sparks = 10;
                                            sparkRad = 40f;
                                            sparkLen = 6f;
                                            sparkStroke = 2f;
                                        }});
                            }};
                        }});
                    }

                        @Override
                        public void draw(Unit unit) {
                            Draw.scl(1.5f);
                            super.draw(unit);
                            Draw.scl();
                        }

                        @Override
                        public void drawBody(Unit unit) {
                            Draw.scl(1.5f);
                            super.drawBody(unit);
                            Draw.scl();
                        }
                    };
                }};
                parts.add(new RegionPart("-blade") {{
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
            }});

            accel = 1f;
            withStates(
            HydromechState.GROUND, new UnitStatState(){{
                speed = 0.5f;
                inwardsDamageMul = 1.2f;
            }},
            HydromechState.WATER, new UnitStatState(){{
                speed = 0.7f;
                inwardsDamageMul = 1f;
            }}
            );

            bodyHeat = true;
            heatColor = Color.red;
            lockLegBase = true;
            legContinuousMove = true;
            legMinLength = 0.2f;
            legLengthScl = 0.95f;
            legSplashDamage = 1.1f;
            legStraightness = 0.2f;

            legCount = 8;
            legLength = 36f;
            legForwardScl = 0.25f;
            legMoveSpace = 1.5f;
            rippleScale = 1.2f;
            stepShake = 1.6f;
            legGroupSize = 3;
            legExtension = -6f;
            legBaseOffset = 15f;
            legStraightLength = 0.9f;
            legMaxLength = 1.4f;

            allowLegStep = true;
            mechSideSway = 0.9f;
            mechFrontSway = 0.9f;
            legPhysicsLayer = false;

            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit + 1f;
            targetAir = true;
        }};
        //rover
        stunt = new RoverUnitType("stunt") {{
            constructor = TankUnit::create;
            itemCapacity = 5;

            health = ROVER_T1_HU;
            armor = 1f;
            researchCostMultiplier = 0;
            hitSize = 12;

            abilities.add(new LegionfieldAbility() {{
                radius = 2f;
            }});

            speed = 1f;

            treadPullOffset = 14;
            treadRects = new Rect[] {
                    new Rect(6-32f,14-32f,18,47)
            };

            float BPS = 1f*1.5f;
            float damageMain = ROVER_T1_DPS/BPS*1f;
            float damageSub = ROVER_T1_DPS/BPS*0f;

            weapons.add(new Weapon(name+"-weapon") {{
                reload = 120f;
                mirror = false;
                linearWarmup = false;
                minWarmup = 0.8f;

                top = true;

                x = 0f;
                y = 0f;

                rotate = true;
                rotateSpeed = 5f;

                shoot = new ShootStunt() {{
                    shots = 2;
                }};

                range = 60;

                bullet = new BasicBulletType(4f,damageSub) {{
                    width = 12f;
                    height = 12f;
                    lifetime = 30f;
                    hitSize = 12f;

                    hitColor = backColor = trailColor = SvPal.phosphide;
                    frontColor = SvPal.spaclanium;
                    trailWidth = 5f;
                    trailLength = 8;

                    hitEffect = despawnEffect = Fx.trailFade;
                    smokeEffect = SvFx.shootLauncher;

                    hittable = false;
                    collides = false;
                    damage = 0;

                    fragOnHit = true;
                    fragOnAbsorb = false;
                    fragAngle = 0f;
                    fragSpread = 0;
                    fragRandomSpread = 0f;
                    fragVelocityMax = fragVelocityMin = 0;
                    fragLifeMin = 1;
                    fragBullets = 1;

                    shootSound = Sounds.shootAlt;

                    fragBullet = new BombBulletType(damageMain,20f) {{
                        width = 12f;
                        height = 12f;
                        sprite = "large-bomb";
                        despawnEffect = new Effect(40f, 100f, e -> {
                            color(SvPal.phosphide);
                            stroke(e.fout() * 2f);
                            float circleRad = 4f + e.finpow() * 15f;
                            Lines.circle(e.x, e.y, circleRad);
                            color(SvPal.phosphide);
                            for(int i = 0; i < 4; i++){
                                Drawf.tri(e.x, e.y, 6f, 20f * e.fout(), i*90+e.rotation);
                            }

                            color();
                            for(int i = 0; i < 4; i++){
                                Drawf.tri(e.x, e.y, 3f, 10f * e.fout(), i*90+e.rotation);
                            }

                            Drawf.light(e.x, e.y, circleRad * 1.6f, SvPal.phosphide, e.fout());
                        });;
                        shootEffect = Fx.none;
                        smokeEffect = Fx.none;

                        keepVelocity = false;

                        shootSound = Sounds.laser;

                        hitColor = backColor = trailColor = Pal.sap.cpy().a(0.7f);
                        frontColor = SvPal.spaclanium.cpy().a(0.7f);

                        status = StatusEffects.blasted;
                        statusDuration = 60f;
                        shrinkY = shrinkX = 0.5f;
                    }};

                    trailEffect = SvFx.missileTrailSmokeSmall;
                    trailRotation = true;
                    trailInterval = 3f;
                }};
            }});
        }};

        zeal = new RoverUnitType("zeal") {{
            constructor = TankUnit::create;
            itemCapacity = 5;

            health = ROVER_T2_HU;
            armor = 2f;
            researchCostMultiplier = 0;
            hitSize = 18;

            abilities.add(new LegionfieldAbility() {{
                radius = 3f;
            }});
            abilities.add(new EnergyFieldAbility(10f, 65f, 5*8f) {{
                statusDuration = 60f * 2f;
                maxTargets = 5;
                healPercent = 0.05f;
                sameTypeHealMult = 1.25f;
                effectRadius = 1f;
                y = -4f;
                color = SvPal.spaclanium;
            }});

            speed = 0.8f;

            treadPullOffset = 3;

            float BPS = 0.75f;
            float damageMain = ROVER_T2_DPS/BPS;
            weapons.add(new Weapon(name+"-weapon") {{
                reload = 80f;
                alternate = false;
                mirror = false;
                linearWarmup = false;
                minWarmup = 0.3f;

                shootY = 12f;

                top = true;

                x = 0f;
                y = 0f;

                rotate = true;
                rotateSpeed = 0.7f;

                bullet = new EmpBulletType(){{
                    float rad = 8*3f;

                    scaleLife = true;
                    lightOpacity = 0.7f;
                    unitDamageScl = 1.1f;
                    healPercent = 0.2f;
                    timeIncrease = 3f;
                    timeDuration = 60f * 20f;
                    powerDamageScl = 1.1f;
                    damage = damageMain;
                    hitColor = lightColor = SvPal.spaclanium;
                    lightRadius = 8*4f;
                    clipSize = 8*5f;
                    shootEffect = new Effect(40, e -> {
                        color(SvPal.phosphide);
                        stroke(e.fout() * 1.6f);

                        randLenVectors(e.id, 18, e.finpow() * 27f, e.rotation, 360f, (x, y) -> {
                            float ang = Mathf.angle(x, y);
                            lineAngle(e.x + x, e.y + y, ang, e.fout() * 6 + 1f);
                        });
                    });
                    smokeEffect = Fx.shootBigSmoke2;
                    lifetime = 15f;
                    sprite = "circle-bullet";
                    backColor = SvPal.phosphide;
                    frontColor = Color.white;
                    width = height = 8f;
                    shrinkY = 0f;
                    speed = 4f;
                    trailLength = 20;
                    trailWidth = 3f;
                    trailColor = SvPal.phosphide;
                    trailInterval = 3f;
                    splashDamage = damageMain;
                    radius = rad;
                    splashDamageRadius = rad;
                    hitShake = 4f;
                    trailRotation = true;
                    trailInterp = Interp.fastSlow;
                    status = StatusEffects.electrified;
                    hitSound = Sounds.plasmaboom;

                    trailEffect = new Effect(16f, e -> {
                        color(SvPal.phosphide);
                        for(int s : Mathf.signs){
                            Drawf.tri(e.x, e.y, 4f, 10f * e.fslope(), e.rotation + 90f*s);
                        }
                    });

                    hitEffect = new Effect(50f, 100f, e -> {
                        e.scaled(7f, b -> {
                            color(SvPal.phosphide, b.fout());
                            Fill.circle(e.x, e.y, rad);
                        });

                        color(SvPal.spaclanium);
                        stroke(e.fout() * 3f);
                        Lines.circle(e.x, e.y, rad);

                        int points = 10;
                        float offset = Mathf.randomSeed(e.id, 360f);
                        for(int i = 0; i < points; i++){
                            float angle = i* 360f / points + offset;
                            //for(int s : Mathf.zeroOne){
                            Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 5f, 10f * e.fout(), angle/* + s*180f*/);
                            Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 5f, -20f * e.fout(), angle/* + s*180f*/);
                            //}
                        }

                        Fill.circle(e.x, e.y, 12f * e.fout());
                        color();
                        Fill.circle(e.x, e.y, 6f * e.fout());
                        Drawf.light(e.x, e.y, rad * 1.6f, SvPal.spaclanium, e.fout());
                    });
                }};
            }});

            treadRects = new Rect[] {
                    new Rect(13-48f,5-48f,17,88)
            };
        }};

        gambit = new RoverUnitType("gambit") {{
            constructor = TankUnit::create;
            itemCapacity = 10;

            health = ROVER_T3_HU;
            armor = 4f;
            researchCostMultiplier = 0;
            hitSize = 26;

            abilities.add(new LegionfieldAbility() {{
                radius = 4f;
            }});

            speed = 0.7f;
            rotateSpeed = 1.8f;

            treadPullOffset = 3;

            canBoost = true;
            boostMultiplier = 0.7f;

            engineSize = 8f;
            engineOffset = 40f/4f;

            setEnginesMirror(new UnitEngine() {{
                radius = 8f;
                x = 0;
                y = 40f/4f;
            }});

            float BPS = 0.5f*4f;
            float damageMain = ROVER_T3_DPS/BPS;
            weapons.add(new Weapon(name+"-weapon") {{
                reload = 120f;
                alternate = false;
                mirror = false;
                linearWarmup = false;
                minWarmup = 0.3f;

                shootY = 12f;

                top = true;

                x = 0f;
                y = 0f;

                rotate = true;
                rotateSpeed = 0.5f;

                inaccuracy = 5f;

                bullet = new BasicBulletType(4f,0f) {{
                    collides = false;
                    hittable = false;

                    lifetime = 30f;
                    fragBullets = 4;
                    fragVelocityMin = fragVelocityMax = 1f;
                    fragSpread = 360f/4f;
                    fragRandomSpread = 0f;

                    scaleLife = true;

                    width = height = 16f;
                    trailLength = 8;
                    trailWidth = 6f;

                    hitEffect = Fx.none;

                    backColor = trailColor = hitColor = SvPal.phosphide;
                    frontColor = Color.white;

                    despawnEffect = new Effect(90f, 100f, e -> {
                        color(SvPal.phosphide);
                        stroke(e.fout() * 2f);
                        float circleRad = 4f + e.finpow() * 30f;


                        color(SvPal.phosphide);
                        for(int i = 0; i < 4; i++){
                            Drawf.tri(e.x, e.y, 8f, 60f * Mathf.lerp(e.fin(),0,e.time > 80 ? (e.time-80)/10f : 0), i*90+e.rotation);
                        }

                        Drawf.light(e.x, e.y, circleRad * 1.6f, SvPal.phosphide, e.fout());
                    });

                    fragBullet = new BasicBulletType(){{
                        width = height = 0f;

                        maxRange = 30f;

                        backColor = trailColor = hitColor = SvPal.phosphide;
                        frontColor = Color.white;

                        hitSound = Sounds.plasmaboom;

                        shootCone = 180f;
                        ejectEffect = Fx.none;
                        hitShake = 4f;

                        collidesAir = false;

                        lifetime = 80f;

                        despawnEffect = new Effect(40f, 100f, e -> {
                            color(SvPal.phosphide);
                            stroke(e.fout() * 2f);
                            float circleRad = 4f + e.finpow() * 30f;
                            Lines.circle(e.x, e.y, circleRad);
                            color(SvPal.phosphide);
                            for(int i = 0; i < 4; i++){
                                Drawf.tri(e.x, e.y, 6f, 40f * e.fout(), i*90+e.rotation);
                            }

                            color();
                            for(int i = 0; i < 4; i++){
                                Drawf.tri(e.x, e.y, 3f, 20f * e.fout(), i*90+e.rotation);
                            }

                            Drawf.light(e.x, e.y, circleRad * 1.6f, SvPal.phosphide, e.fout());
                        });
                        hitEffect = Fx.massiveExplosion;
                        keepVelocity = false;

                        shrinkX = 0.7f;
                        shrinkInterp = Interp.pow3Out;

                        speed = 0.5f;
                        collides = false;

                        splashDamage = damageMain;
                        splashDamageRadius = 40f;
                    }};
                }};

                ejectEffect = Fx.none;
                recoil = 2.5f;
                shootSound = Sounds.spark;
            }});

            treadRects = new Rect[] {
                    new Rect(24-64f,7-64f,20,112)
            };
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
        }};

        pisun = new AtlacianUnitType("assembler-drone"){{
            controller = u -> new AssemblerAI();
            constructor = BuildingTetherPayloadUnit::create;
            flying = true;
            drag = 0.06f;
            accel = 0.11f;
            speed = 1.3f;
            health = 90;
            engineSize = 2f;
            engineOffset = 6.5f;
            payloadCapacity = 0f;
            targetable = false;
            bounded = false;

            outlineColor = SvPal.outline;
            isEnemy = false;
            hidden = true;
            useUnitCap = false;
            logicControllable = false;
            playerControllable = false;
            allowedInPayloads = false;
            createWreck = false;
            envEnabled = Env.any;
            envDisabled = Env.none;
        }};
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


    public static void loadUwu(boolean isUwu) {
        leeft.region = atlas.find(leeft.name+(isUwu ? "-uwu" :""));
        leeft.weapons.first().layerOffset = isUwu ? -1 : 0;
        leeft.drawCell = !isUwu;

        flagshi.region = atlas.find(flagshi.name+(isUwu ? "-uwu" :""));
        flagshi.drawCell = !isUwu;
        ((HydromechUnitType) flagshi).bodyHeat = !isUwu;

        vanguard.region = atlas.find(vanguard.name+(isUwu ? "-uwu" :""));
        vanguard.drawCell = !isUwu;
        //vanguard.weapons.get(2).layerOffset = isUwu ? -1 : 0;
        ((HydromechUnitType) vanguard).bodyHeat = !isUwu;

        squadron.region = atlas.find(squadron.name+(isUwu ? "-uwu" :""));
        squadron.drawCell = !isUwu;
        squadron.weapons.first().layerOffset = isUwu ? -1 : 0;
        ((HydromechUnitType)squadron).bodyHeat = !isUwu;

        armada.region = atlas.find(armada.name+(isUwu ? "-uwu" :""));
        armada.drawCell = !isUwu;
        ((HydromechUnitType) armada).bodyHeat = !isUwu;

        lapetus.region = atlas.find(lapetus.name+(isUwu ? "-uwu" : ""));
        lapetus.drawCell = !isUwu;
        if(lapetus.parts.first() instanceof RotatorRegionPart part) {
            part.xScl = isUwu ? 0 : 1;
        }
    }
}
