package subvoyage.type.unit.ai;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.ai.types.BoostAI;
import mindustry.ai.types.CommandAI;
import subvoyage.type.unit.entity.HelicopterUnitEntity;

public class HelicopterBoostAI extends BoostAI {

    @Override
    public void updateUnit(){
        if(unit.controller() instanceof CommandAI ai) {
            ai.defaultBehavior();
            unit.updateBoosting(true);
        }
        //updateMovement();
    }

    @Override
    public void updateMovement() {
        super.updateMovement();
        if (!(unit instanceof HelicopterUnitEntity heli)) return;
        if (heli.vel.len() < 0.3f) {
            float steerX = Mathf.sinDeg(Time.time) * 12f;
            float steerY = Mathf.cosDeg(Time.time) * 12f;

            float len = 1f - Mathf.clamp(heli.vel.len(), 0f, 0.3f) / 0.3f;

            heli.movePref(new Vec2(steerX, steerY).times(new Vec2(len, len)));
        }
    }
}
