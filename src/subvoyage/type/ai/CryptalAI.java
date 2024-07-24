package subvoyage.type.ai;

import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.ai.types.FlyingAI;
import mindustry.content.Fx;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.world.meta.BlockFlag;
import subvoyage.type.block.core.offload_core.IOffload;
import subvoyage.type.block.core.offload_core.OffloadCore;
import subvoyage.draw.visual.SvFx;

import java.util.HashMap;

import static mindustry.Vars.*;

public class CryptalAI extends FlyingAI {
    public HashMap<Unit,Float> timePassed = new HashMap<>();
    @Override
    public void updateMovement() {
        unloadPayloads();
        if(target == null) timePassed.put(unit,timePassed.getOrDefault(unit,0f)+ Time.delta);
        if(timePassed.getOrDefault(unit,0f) > 60*15f) unit.kill();
        if(target != null && unit.hasWeapons()){
            if(unit.type.circleTarget){
                circleAttack(120f);
                for (WeaponMount mount : unit.mounts) {
                    if(mount.warmup >= (mount.weapon.minWarmup-0.1f) && target instanceof OffloadCore.OffloadCoreBuilding of) {
                        if(of.tryBreakLayer()) {
                            Sounds.pulseBlast.at(unit.x,unit.y);
                            new MultiEffect(Fx.drillSteam, SvFx.decoderWave).create(unit.x, unit.y, 0, Pal.accent, 8f * tilesize);
                            SvFx.point.create(unit.x,unit.y,0,Pal.redLight,new Object());
                            SvFx.point.create(of.x,of.y,0,Pal.redLight,new Object());
                            SvFx.beam.create(of.x,of.y,0,Pal.redLight,new float[] {unit.x,unit.y,of.x,of.y});
                        };
                        unit.kill();
                        mount.warmup = 0f;
                    }
                }
                unit.lookAt(target);
            }else{
                moveTo(target, unit.type.range * 0.8f);
                unit.lookAt(target);
            }
        }

        if(target == null && state.rules.waves && unit.team == state.rules.defaultTeam){
            moveTo(getClosestSpawner(), state.rules.dropZoneRadius + 130f);
        }
    }

    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        return findMainTarget(x, y, range, air, ground);
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        var core = targetFlag(x, y, BlockFlag.core, true, true);
        return core;
    }

    public Teamc targetFlag(float x, float y, BlockFlag flag, boolean enemy, boolean offload) {
        if (unit.team == Team.derelict) return null;
        Seq<Building>   found = enemy ? indexer.getEnemy(unit.team, flag) : indexer.getFlagged(unit.team, flag);
                        found.filter((e) -> !offload || e instanceof IOffload);
        return Geometry.findClosest(x, y, found);
    }
}
