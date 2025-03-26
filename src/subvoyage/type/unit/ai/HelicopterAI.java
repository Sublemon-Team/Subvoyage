package subvoyage.type.unit.ai;

import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.Units;
import mindustry.gen.Entityc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.BlockFlag;
import subvoyage.type.unit.entity.HelicopterUnitEntity;

import static mindustry.Vars.state;

public class HelicopterAI extends FlyingAI {
    public boolean keepFlying = true;
    public Unit troop = null;
    public Unit head = null;

    @Override
    public void updateMovement() {
        unloadPayloads();

        if (!(unit instanceof HelicopterUnitEntity heli)) return;

        if (target != null && unit.hasWeapons()) {
            if (unit.type.circleTarget) {
                circleAttack(120f);
            } else {
                moveTo(target, unit.type.range * 0.8f);
                unit.lookAt(target);
            }
        }


        if (unit.hitTime() > 0f && head != null) {
            moveTo(head, head.hitSize, 1000f);
        }

        if (target == null && state.rules.waves && unit.team == state.rules.defaultTeam) {
            moveTo(getClosestSpawner(), state.rules.dropZoneRadius + 130f);
        }

        if (heli.vel.len() < 0.3f && keepFlying) {
            float steerX = Mathf.sinDeg(Time.time) * 12f;
            float steerY = Mathf.cosDeg(Time.time) * 12f;

            float len = 1f - Mathf.clamp(heli.vel.len(), 0f, 0.3f) / 0.3f;

            heli.movePref(new Vec2(steerX, steerY).times(new Vec2(len, len)));
        }
        if (!keepFlying) {
            //heli.vel.lerp(0f,0f,Time.delta/60f);
        }

        if (troop != null) {
            moveTo(troop, Math.max(troop.hitSize + 4f, unit.hitSize + 4f), 100f);
        }
        if (head != null) {
            target = head.controller() instanceof HelicopterAI ai ? (ai.target == null ? target : ai.target) : null;
        }
    }

    public HelicopterAI getHeadAi() {
        return head.controller() instanceof HelicopterAI ai ? ai : null;
    }

    @Override
    public void updateTargeting() {
        super.updateTargeting();

        boolean groundTurret = targetGroundTurret(unit.getX(), unit.getY(), unit.type.fogRadius*8f*1.5f) != null;
        boolean airTurret = targetAirTurret(unit.getX(), unit.getY(), unit.type.fogRadius*8f*4f) != null;

        keepFlying = true;
        if(groundTurret && !airTurret) keepFlying = true;
        if(airTurret && !groundTurret) keepFlying = false; // air turret, get down!

        if(troop == null || Mathf.dst(troop.x(),troop.y(),unit.x(),unit.y()) < unit().hitSize*1.2f)
            troop = findTroop(unit.getX(), unit.getY(),
                    unit.type.fogRadius*8f*1.2f, unit().hitSize*1.2f); // we found an ally to follow


        var newHead = findMain(unit.getX(), unit.getY(), unit.type.fogRadius*8f);

        if(head == null || head.maxHealth < newHead.health || head.hitSize < newHead.hitSize || (head.maxHealth < newHead.health && head.health < newHead.health))
            head = newHead; // we found a better leader
        if(head != null && getHeadAi() != null && getHeadAi().head != null) head = getHeadAi().head; // we found a better leader
        if(head != null && (head.maxHealth == unit.maxHealth || head.hitSize == unit.hitSize)) head = null; //we're equal
        if(head != null && getHeadAi() != null && getHeadAi().head == unit) head = null; // we don't want loops
    }

    public Unit findMain(float x, float y, float range) {
        return Units.closest(unit.team,x,y,range,u -> u instanceof HelicopterUnitEntity,(u,xo,yo) -> -u.hitSize -u.health);
    }
    public Unit findTroop(float x, float y, float range, float minRange) {
        return Units.closest(unit.team,x,y,range,u -> u instanceof HelicopterUnitEntity && Mathf.dst(x,y,u.getX(),u.getY()) > minRange,(u,xo,yo) -> -u.hitSize -u.health);
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {

        var core = targetFlag(x, y, BlockFlag.core, true);

        if(core != null && Mathf.within(x, y, core.getX(), core.getY(), range)){
            return core;
        }

        Teamc result = target(x, y, range, !keepFlying && air, keepFlying && ground);
        if(result != null) return result;
        result = target(x, y, range, air, ground);
        if(result != null) return result;

        return core;
    }

    @Override
    public Teamc target(float x, float y, float range, boolean air, boolean ground) {
        return Units.closestTarget(unit.team, x, y, range, u -> u.checkTarget(air, ground), t -> ground);
    }

    public Teamc targetGroundTurret(float x, float y, float range) {
        return Units.closestTarget(unit.team, x, y, range, u -> false, t -> t.block instanceof Turret b && t.isValid() && t.isAdded() && b.targetGround && !b.targetAir);
    }
    public Teamc targetAirTurret(float x, float y, float range) {
        return Units.closestTarget(unit.team, x, y, range, u -> false, t -> t.block instanceof Turret b && t.isValid() && t.isAdded() && b.targetAir && !b.targetGround);
    }
}
