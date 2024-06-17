package subvoyage.content.unit.ai;

import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import mindustry.ai.Pathfinder;
import mindustry.ai.types.CargoAI;
import mindustry.gen.Building;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Call;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.*;
import static mindustry.ai.Pathfinder.costNaval;
import static subvoyage.SubvoyageMod.navalCargoId;

public class WaterCargoAI extends CargoAI {

    private Building target;

    public static class WaterCargoFlowfield extends Pathfinder.Flowfield {
        public Building target;
        @Override
        protected void getPositions(IntSeq out) {
            out.clear();
            if(target != null) {
                out.add(target.tile.array());
            }
        }
    }

    public void pathfind(){

        Tile tile = unit.tileOn();
        if(tile == null) return;
        WaterCargoFlowfield flowfield = (WaterCargoFlowfield) pathfinder.getField(state.rules.waveTeam, costNaval, navalCargoId);
        flowfield.target = this.target;
        Tile targetTile = pathfinder.getTargetTile(tile, flowfield);
        if(tile == targetTile || !targetTile.floor().isLiquid || !targetTile.block().isAir()) return;

        if(!unit.within(targetTile,8f)) {
            unit.movePref(vec.trns(unit.angleTo(targetTile.worldx(), targetTile.worldy()), unit.speed()));
        }
    }

    @Override
    public void updateMovement() {

        if(!(unit instanceof BuildingTetherc tether) || tether.building() == null) return;

        var build = tether.building();

        target = unit.hasItem() ? unloadTarget : build;
        transferRange = 40f;
        pathfind();

        if(build.items == null) return;

        //empty, approach the loader, even if there's nothing to pick up (units hanging around doing nothing looks bad)
        if(!unit.hasItem()){

            //check if ready to pick up
            if(build.items.any() && unit.within(build, transferRange)){
                if(retarget()){
                    findAnyTarget(build);
                    //target has been found, grab items and go
                    if(unloadTarget != null){
                        Call.takeItems(build, itemTarget, Math.min(unit.type.itemCapacity, build.items.get(itemTarget)), unit);
                    }
                }
            }
        }else{ //the unit has an item, deposit it somewhere.

            //there may be no current target, try to find one
            if(unloadTarget == null){
                if(retarget()){
                    findDropTarget(unit.item(), 0, null);
                    //if there is not even a single place to unload, dump items.
                    if(unloadTarget == null){
                        unit.clearItem();
                    }
                }
            }else{

                //what if some prankster reconfigures or picks up the target while the unit is moving? we can't have that!
                if(unloadTarget.item != itemTarget || unloadTarget.isPayload()){
                    unloadTarget = null;
                    return;
                }

                //deposit in bursts, unloading can take a while
                if(unit.within(unloadTarget, transferRange) && timer.get(timerTarget2, dropSpacing)){
                    int max = unloadTarget.acceptStack(unit.item(), unit.stack.amount, unit);

                    //deposit items when it's possible
                    if(max > 0){
                        noDestTimer = 0f;
                        Call.transferItemTo(unit, unit.item(), max, unit.x, unit.y, unloadTarget);

                        //try the next target later
                        if(!unit.hasItem()){
                            targetIndex ++;
                        }
                    }else if((noDestTimer += dropSpacing) >= emptyWaitTime){
                        //oh no, it's out of space - wait for a while, and if nothing changes, try the next destination

                        //next targeting attempt will try the next destination point
                        targetIndex = findDropTarget(unit.item(), targetIndex, unloadTarget) + 1;

                        //nothing found at all, clear item
                        if(unloadTarget == null){
                            unit.clearItem();
                        }
                    }
                }
            }
        }
    }
}
