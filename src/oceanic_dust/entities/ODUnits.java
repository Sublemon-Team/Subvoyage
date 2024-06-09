package oceanic_dust.entities;

import arc.graphics.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;

public class ODUnits {
    public static UnitType marine;
    public static void load() {
        marine = new UnitType("marine") {{
            aiController = BuilderAI::new;
            constructor = UnitEntity::create;
            isEnemy = false;

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
            engineOffset = 6f;
            hitSize = 8f;

            ammoType = new PowerAmmoType(900);
            abilities.add(new RepairFieldAbility(5f, 60f * 8, 80f));
            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                y = -1.25f;
                x = 6.5f;
                reload = 10f;
                ejectEffect = Fx.absorb;
                recoil = 2f;
                shootSound = Sounds.missile;
                velocityRnd = 0f;
                inaccuracy = 0f;
                alternate = true;
                fogRadius = 0;
                lightRadius = 8;
                bullet = new MissileBulletType(5f, 9){{
                    homingPower = 0.08f;
                    weaveMag = 4;
                    weaveScale = 1;
                    lifetime = 50f;
                    keepVelocity = false;
                    shootEffect = Fx.shootHealYellow;
                    smokeEffect = Fx.hitLaser;
                    hitEffect = despawnEffect = Fx.hitLaser;
                    frontColor = Color.white;
                    hitSound = Sounds.none;

                    healPercent = 0.5f;
                    collidesTeam = true;
                    backColor = Pal.heal;
                    trailColor = Pal.heal;
                }};
            }});
        }};
    }
}
