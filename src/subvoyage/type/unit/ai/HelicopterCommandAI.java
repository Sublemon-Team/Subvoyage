package subvoyage.type.unit.ai;

import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.UnitCommand;
import mindustry.ai.UnitStance;
import mindustry.ai.types.CommandAI;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.*;
import static mindustry.Vars.tilesize;

public class HelicopterCommandAI extends CommandAI {
    @Override
    public void defaultBehavior() {
        if(!net.client() && unit instanceof Payloadc pay){
            payloadPickupCooldown -= Time.delta;

            //auto-drop everything
            if(command == UnitCommand.unloadPayloadCommand && pay.hasPayload()){
                Call.payloadDropped(unit, unit.x, unit.y);
            }

            //try to pick up what's under it
            if(command == UnitCommand.loadUnitsCommand){
                tryPickupUnit(pay);
            }

            //try to pick up a block
            if(command == UnitCommand.loadBlocksCommand && (targetPos == null || unit.within(targetPos, 1f))){
                Building build = world.buildWorld(unit.x, unit.y);

                if(build != null && state.teams.canInteract(unit.team, build.team)){
                    //pick up block's payload
                    Payload current = build.getPayload();
                    if(current != null && pay.canPickupPayload(current)){
                        Call.pickedBuildPayload(unit, build, false);
                        //pick up whole building directly
                    }else if(build.block.buildVisibility != BuildVisibility.hidden && build.canPickup() && pay.canPickup(build)){
                        Call.pickedBuildPayload(unit, build, true);
                    }
                }
            }
        }

        if(!net.client() && command == UnitCommand.enterPayloadCommand && unit.buildOn() != null && (targetPos == null || (world.buildWorld(targetPos.x, targetPos.y) != null && world.buildWorld(targetPos.x, targetPos.y) == unit.buildOn()))){
            var build = unit.buildOn();
            tmpPayload.unit = unit;
            if(build.team == unit.team && build.acceptPayload(build, tmpPayload)){
                Call.unitEnteredPayload(unit, build);
                return; //no use updating after this, the unit is gone!
            }
        }

        updateVisuals();
        updateTargeting();

        if(attackTarget != null && invalid(attackTarget)){
            attackTarget = null;
            targetPos = null;
        }

        //move on to the next target
        if(attackTarget == null && targetPos == null){
            finishPath();
        }

        if(attackTarget != null){
            if(targetPos == null){
                targetPos = new Vec2();
                lastTargetPos = targetPos;
            }
            targetPos.set(attackTarget);
        }

        boolean alwaysArrive = false;

        float engageRange = unit.type.range - 10f;
        boolean withinAttackRange = attackTarget != null && unit.within(attackTarget, engageRange) && stance != UnitStance.ram;

        if(targetPos != null){
            boolean move = true, isFinalPoint = commandQueue.size == 0;
            vecOut.set(targetPos);
            vecMovePos.set(targetPos);

            //the enter payload command requires an exact position
            if(group != null && group.valid && groupIndex < group.units.size && command != UnitCommand.enterPayloadCommand){
                vecMovePos.add(group.positions[groupIndex * 2], group.positions[groupIndex * 2 + 1]);
            }

            Building targetBuild = world.buildWorld(targetPos.x, targetPos.y);

            //TODO: should the unit stop when it finds a target?
            if(
                    (stance == UnitStance.patrol && target != null && unit.within(target, unit.type.range - 2f) && !unit.type.circleTarget) ||
                            (command == UnitCommand.enterPayloadCommand && unit.within(targetPos, 4f) || (targetBuild != null && unit.within(targetBuild, targetBuild.block.size * tilesize/2f * 0.9f))) ||
                            (command == UnitCommand.loopPayloadCommand && unit.within(targetPos, 10f))
            ){
                move = false;
            }

            vecOut.set(vecMovePos);

            if(move){
                if(unit.type.circleTarget && attackTarget != null){
                    target = attackTarget;
                    circleAttack(80f);
                }else{
                    moveTo(vecOut,
                            withinAttackRange ? engageRange :
                                    attackTarget != null && stance != UnitStance.ram ? engageRange : 0f,
                            unit.isFlying() ? 40f : 100f, false, null, isFinalPoint || alwaysArrive);
                }
            }

            //if stopAtTarget is set, stop trying to move to the target once it is reached - used for defending
            if(attackTarget != null && stopAtTarget && unit.within(attackTarget, engageRange - 1f)){
                attackTarget = null;
            }

            if(move && (attackTarget == null || !unit.within(attackTarget, unit.type.range))){
                unit.lookAt(vecMovePos);
            }else{
                faceTarget();
            }

            //reached destination, end pathfinding
            if(attackTarget == null && unit.within(vecMovePos, command.exactArrival && commandQueue.size == 0 ? 1f : Math.max(5f, unit.hitSize / 2f))){
                finishPath();
            }

            if(stopWhenInRange && targetPos != null && unit.within(vecMovePos, engageRange * 0.9f)){
                finishPath();
                stopWhenInRange = false;
            }

        }else if(target != null){
            faceTarget();
        }
    }

    void finishPath(){
        //the enter payload command never finishes until they are actually accepted
        if(command == UnitCommand.enterPayloadCommand && commandQueue.size == 0 && targetPos != null && world.buildWorld(targetPos.x, targetPos.y) != null && world.buildWorld(targetPos.x, targetPos.y).block.acceptsUnitPayloads){
            return;
        }

        if(!net.client() && command == UnitCommand.loopPayloadCommand && unit instanceof Payloadc pay){

            if(transferState == transferStateNone){
                transferState = pay.hasPayload() ? transferStateUnload : transferStateLoad;
            }

            if(payloadPickupCooldown > 0f) return;

            if(transferState == transferStateUnload){
                //drop until there's a failure
                int prev = -1;
                while(pay.hasPayload() && prev != pay.payloads().size){
                    prev = pay.payloads().size;
                    Call.payloadDropped(unit, unit.x, unit.y);
                }

                //wait for everything to unload before running code below
                if(pay.hasPayload()){
                    return;
                }
                payloadPickupCooldown = 60f;
            }else if(transferState == transferStateLoad){
                //pick up units until there's a failure
                int prev = -1;
                while(prev != pay.payloads().size){
                    prev = pay.payloads().size;
                    tryPickupUnit(pay);
                }

                //wait to load things before running code below
                if(!pay.hasPayload()){
                    return;
                }
                payloadPickupCooldown = 60f;
            }

            //it will never finish
            if(commandQueue.size == 0){
                return;
            }
        }

        transferState = transferStateNone;

        Vec2 prev = targetPos;
        targetPos = null;

        if(commandQueue.size > 0){
            var next = commandQueue.remove(0);
            if(next instanceof Teamc target){
                commandTarget(target, this.stopAtTarget);
            }else if(next instanceof Vec2 position){
                commandPosition(position);
            }

            if(prev != null && (stance == UnitStance.patrol || command == UnitCommand.loopPayloadCommand)){
                commandQueue.add(prev.cpy());
            }

            //make sure spot in formation is reachable
            if(group != null){
                group.updateRaycast(groupIndex, next instanceof Vec2 position ? position : Tmp.v3.set(next));
            }
        }else{
            if(group != null){
                group = null;
            }
        }
    }

    void tryPickupUnit(Payloadc pay){
        Unit target = Units.closest(unit.team, unit.x, unit.y, unit.type.hitSize * 2f, u -> u.isAI() && u != unit && u.isGrounded() && pay.canPickup(u) && u.within(unit, u.hitSize + unit.hitSize));
        if(target != null){
            Call.pickedUnitPayload(unit, target);
        }
    }
}
