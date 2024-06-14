package subvoyage.content.unit;

import arc.graphics.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import subvoyage.content.world.*;
import subvoyage.entities.part.*;

public class SvUnits{
    public static UnitType marine, helio;
    public static void load() {
        marine = new AtlacianUnitType("marine") {{
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

        helio = new HelicopterUnitType("helio") {{
            aiController = FlyingAI::new;
            constructor = HelicopterUnitEntity::create;
            isEnemy = false;
            coreUnitDock = true;
            lowAltitude = true;
            flying = true;

            drag = 0.05f;
            speed = 1.6f;
            rotateSpeed = 12f;
            accel = 0.1f;
            fogRadius = 0.5f;
            health = 800f;

            engineOffset = 6.5f;
            engineSize = 0;
            setEnginesMirror(
            new UnitEngine(-16 / 4 , -engineOffset, 1.25f, -90)
            );

            RotatorRegionPart copter = new RotatorRegionPart(){{
                outline = false;
                layerOffset = Layer.flyingUnitLow + 1;
                xScl = 0.6f;
                yScl = 0.6f;
                y = -0.15f;

                moveRot = 90f;
                rotation = 360f;
            }};

            hitSize = 8f;
            onUpdate = (e) -> copter.moveRot = 90f+(90f*e.localAcceleration);
            parts.add(copter);

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
    }

    static {
        EntityMapping.register("helio",HelicopterUnitEntity::new);
    }
}
