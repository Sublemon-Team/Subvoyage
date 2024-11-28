package subvoyage.type.unit.ai;

import arc.func.Boolf;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.types.DefenderAI;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.state;
import static mindustry.Vars.tree;

public class DefenderDroneAI extends DefenderAI {
    public Boolf<Unit> filter = (u) -> true;

    public float maxOwnerDistance = 8f*20f;
    public Teamc enemyTarget = null;

    public float noTargetTime = 0f;
    public float aliveTime = 20f*60f;

    @Override
    public void updateMovement() {
        unloadPayloads();

        if(enemyTarget != null && (target != null && Mathf.dst(unit.x,unit.y,target.x(),target.y()) >= maxOwnerDistance)) {
            enemyTarget = null;
        }
        if(enemyTarget != null && (target != null && Mathf.dst(unit.x,unit.y,target.x(),target.y()) < maxOwnerDistance)) {
            moveTo(enemyTarget, (enemyTarget instanceof Sized s ? s.hitSize()/2f * 1.1f : 0f) + unit.hitSize/2f + 15f, 50f);
            unit.lookAt(enemyTarget);
        }
        else if(target != null){
            moveTo(target, (target instanceof Sized s ? s.hitSize()/2f * 1.1f : 0f) + unit.hitSize/2f + 15f, 50f);
            unit.lookAt(target);
            noTargetTime = 0f;
        } else {
            noTargetTime += Time.delta;
        }
        if(noTargetTime >= 5*60f) {
            unit.kill();
        }
    }

    @Override
    public void updateUnit() {
        super.updateUnit();
        unit.health -= unit.type.health / aliveTime * Time.delta;
    }

    @Override
    public void updateTargeting() {
        if(retarget()) {
            target = findTarget(unit.x, unit.y, unit.range(), true, true);
            enemyTarget = findEnemyTarget();
        }
        updateWeapons();
    }

    @Override
    public void updateWeapons() {
        float rotation = unit.rotation - 90;
        var target = enemyTarget;

        noTargetTime += Time.delta;

        if(invalid(target)){
            target = null;
        }else{
            noTargetTime = 0f;
        }

        unit.isShooting = false;

        for(var mount : unit.mounts){
            Weapon weapon = mount.weapon;
            float wrange = weapon.range();

            //let uncontrollable weapons do their own thing
            if(!weapon.controllable || weapon.noAttack) continue;

            if(!weapon.aiControllable){
                mount.rotate = false;
                continue;
            }

            float mountX = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y),
                    mountY = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y);

            if(unit.type.singleTarget){
                mount.target = target;
            }else{
                if(checkTarget(mount.target, mountX, mountY, wrange)){
                    mount.target = null;
                }
            }

            boolean shoot = false;

            if(mount.target != null){
                shoot = mount.target.within(mountX, mountY, wrange + (mount.target instanceof Sized s ? s.hitSize()/2f : 0f)) && shouldShoot();

                Vec2 to = Predict.intercept(unit, mount.target, weapon.bullet.speed);
                mount.aimX = to.x;
                mount.aimY = to.y;
            }

            unit.isShooting |= (mount.shoot = mount.rotate = shoot);

            if(mount.target == null && !shoot && !Angles.within(mount.rotation, mount.weapon.baseRotation, 0.01f) && noTargetTime >= rotateBackTimer){
                mount.rotate = true;
                Tmp.v1.trns(unit.rotation + mount.weapon.baseRotation, 5f);
                mount.aimX = mountX + Tmp.v1.x;
                mount.aimY = mountY + Tmp.v1.y;
            }

            if(shoot){
                unit.aimX = mount.aimX;
                unit.aimY = mount.aimY;
            }
        }
    }

    public Teamc findEnemyTarget() {
        if(target == null) return null;
        var result = Units.closestEnemy(unit.team, target.x(), target.y(), maxOwnerDistance, u -> !u.dead() && u.type != unit.type && u.targetable(unit.team) && u.type.playerControllable);

        if(result != null) return result;

        for(var flag : unit.type.targetFlags){
            Teamc res = targetFlag(target.x(), target.y(), flag, true);
            if(res != null && Mathf.dst(res.x(),res.y(),target.x(),target.y()) < maxOwnerDistance) return res;
        }

        return result;
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        return enemyTarget;
    }

    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        //Sort by max health and closer target.
        var result = Units.closest(unit.team, x, y, maxOwnerDistance, u -> filter.get(u) && !u.dead() && u.type != unit.type && u.targetable(unit.team) && u.type.playerControllable,
                (u, tx, ty) -> -u.maxHealth + Mathf.dst2(u.x, u.y, tx, ty) / 6400f);

        return result;
    }
}
