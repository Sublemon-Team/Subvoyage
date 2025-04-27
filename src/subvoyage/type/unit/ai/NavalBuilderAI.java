package subvoyage.type.unit.ai;

import arc.math.geom.Vec2;
import arc.struct.Queue;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.ai.UnitStance;
import mindustry.ai.types.BuilderAI;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.Build;
import mindustry.world.blocks.ConstructBlock;

import static mindustry.Vars.controlPath;
import static mindustry.Vars.world;

public class NavalBuilderAI extends BuilderAI {

    protected static final Vec2 vecOut = new Vec2(), vecMovePos = new Vec2();
    boolean found = false;
    protected static final boolean[] noFound = {false};

    @Override
    public void updateMovement() {
        if(target != null && shouldShoot()){
            unit.lookAt(target);
        }else if(!unit.type.flying){
            unit.lookAt(unit.prefRotation());
        }

        unit.updateBuilding = true;

        if(assistFollowing != null && assistFollowing.activelyBuilding()){
            following = assistFollowing;
        }

        boolean moving = false;

        if(following != null){
            //try to follow and mimic someone

            //validate follower
            if(!following.isValid() || !following.activelyBuilding()){
                following = null;
                unit.plans.clear();
                return;
            }

            //set to follower's first build plan, whatever that is
            unit.plans.clear();
            unit.plans.addFirst(following.buildPlan());
            lastPlan = null;
        }

        boolean move = false;

        if(unit.buildPlan() != null){
            //approach plan if building
            BuildPlan req = unit.buildPlan();

            //clear break plan if another player is breaking something
            if(!req.breaking && timer.get(timerTarget2, 40f)){
                for(Player player : Groups.player){
                    if(player.isBuilder() && player.unit().activelyBuilding() && player.unit().buildPlan().samePos(req) && player.unit().buildPlan().breaking){
                        unit.plans.removeFirst();
                        //remove from list of plans
                        unit.team.data().plans.remove(p -> p.x == req.x && p.y == req.y);
                        return;
                    }
                }
            }

            boolean valid =
                    !(lastPlan != null && lastPlan.removed) &&
                            ((req.tile() != null && req.tile().build instanceof ConstructBlock.ConstructBuild cons && cons.current == req.block) ||
                                    (req.breaking ?
                                            Build.validBreak(unit.team(), req.x, req.y) :
                                            Build.validPlace(req.block, unit.team(), req.x, req.y, req.rotation)));

            if(valid){
                vecMovePos.set(req.tile().getX(),req.tile().getY());
                move = controlPath.getPathPosition(unit, vecMovePos, vecMovePos, vecOut, null);
            }else{
                //discard invalid plan
                unit.plans.removeFirst();
                lastPlan = null;
            }
        }else{

            if(assistFollowing != null){
                vecMovePos.set(assistFollowing.x(),assistFollowing.y());
                move = controlPath.getPathPosition(unit, vecMovePos,vecMovePos, vecOut, null);
            }

            //follow someone and help them build
            if(timer.get(timerTarget2, 20f)){
                found = false;

                Units.nearby(unit.team, unit.x, unit.y, buildRadius, u -> {
                    if(found) return;

                    if(u.canBuild() && u != unit && u.activelyBuilding()){
                        BuildPlan plan = u.buildPlan();

                        Building build = world.build(plan.x, plan.y);
                        if(build instanceof ConstructBlock.ConstructBuild cons){
                            float dist = Math.min(cons.dst(unit) - unit.type.buildRange, 0);

                            //make sure you can reach the plan in time
                            if(dist / unit.speed() < cons.buildCost * 0.9f){
                                following = u;
                                found = true;
                            }
                        }
                    }
                });

                float minDst = Float.MAX_VALUE;
                Player closest = null;
                for(var player : Groups.player){
                    if(!player.dead() && player.isBuilder() && player.team() == unit.team){
                        float dst = player.dst2(unit);
                        if(dst < minDst){
                            closest = player;
                            minDst = dst;
                        }
                    }
                }

                assistFollowing = closest == null ? null : closest.unit();
            }

            //find new plan
            if(!unit.team.data().plans.isEmpty() && following == null && timer.get(timerTarget3, rebuildPeriod)){
                Queue<Teams.BlockPlan> blocks = unit.team.data().plans;
                Teams.BlockPlan block = blocks.first();

                //check if it's already been placed
                if(world.tile(block.x, block.y) != null && world.tile(block.x, block.y).block() == block.block){
                    blocks.removeFirst();
                }else if(Build.validPlace(block.block, unit.team(), block.x, block.y, block.rotation) && (!alwaysFlee || !nearEnemy(block.x, block.y))){ //it's valid
                    lastPlan = block;
                    //add build plan
                    unit.addBuild(new BuildPlan(block.x, block.y, block.rotation, block.block, block.config));
                    //shift build plan to tail so next unit builds something else
                    blocks.addLast(blocks.removeFirst());
                }else{
                    //shift head of queue to tail, try something else next time
                    blocks.addLast(blocks.removeFirst());
                }
            }
        }

        if(move && unit.dst(vecMovePos) > unit.type.buildRange-8f) {
            moveTo(vecOut, 1f,
                    vecMovePos.epsilonEquals(vecOut, 4.1f) ? 30f : 0f);
        }
    }
}
