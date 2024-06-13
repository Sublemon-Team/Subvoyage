package subvoyage.content.unit;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.EntityCollisions;
import mindustry.gen.UnitEntity;

public class HelicopterUnitEntity extends UnitEntity {

    float localAcceleration = 0;
    boolean isAccelerating = false;

    @Override
    public float speed() {
        return super.speed()*localAcceleration;
    }

    @Override
    public void moveAt(Vec2 vector, float acceleration) {
        Vec2 t = tmp1.set(vector);
        tmp2.set(t).sub(this.vel).limit(acceleration * vector.len() * Time.delta);

        localAcceleration +=Time.delta/4;
        isAccelerating = true;
        localAcceleration = Mathf.clamp(localAcceleration,0,1);
        this.vel.add(tmp2.times(new Vec2(localAcceleration,localAcceleration)));
    }

    @Override
    public void move(float cx, float cy) {
        EntityCollisions.SolidPred check = this.solidity();

        localAcceleration +=Time.delta/4;
        isAccelerating = true;
        localAcceleration = Mathf.clamp(localAcceleration,0,1);

        cx*=localAcceleration;
        cy*=localAcceleration;

        if (check != null) {
            Vars.collisions.move(this, cx, cy, check);
        } else {
            this.x += cx;
            this.y += cy;
        }
    }

    @Override
    public void update() {
        super.update();
        if(!isAccelerating) localAcceleration-=Time.delta/4;
        isAccelerating = false;
    }
}
