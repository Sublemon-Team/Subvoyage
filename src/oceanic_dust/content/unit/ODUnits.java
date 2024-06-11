package oceanic_dust.content.unit;

import arc.graphics.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import oceanic_dust.content.world.*;

public class ODUnits {
    public static UnitType marine;
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
                bullet = new ArtilleryBulletType(3f, 9){{
                    trailSize = 1;
                    homingPower = 0.08f;
                    weaveMag = 4;
                    weaveScale = 1;
                    lifetime = 42f;
                    keepVelocity = false;
                    smokeEffect = ODFx.hitLaserOrange;
                    hitEffect = despawnEffect = ODFx.hitLaserOrange;
                    frontColor = Color.white;
                    hitSound = Sounds.none;
                    collidesTeam = true;
                    backColor = Pal.lightOrange;
                }};
            }});
        }};
    }
}
