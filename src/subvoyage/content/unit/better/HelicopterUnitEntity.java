package subvoyage.content.unit.better;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.EntityCollisions;
import subvoyage.content.unit.systematical.BaseUnit;

public class HelicopterUnitEntity extends BaseUnit {
    public float acceleration = 0;
    private boolean isAccelerating = false;

    @Override
    public void moveAt(Vec2 vector, float acceleration) {
        Vec2 t = tmp1.set(vector);
        tmp2.set(t).sub(this.vel).limit(acceleration * vector.len() * Time.delta);

        this.vel.add(accelerate(tmp2));
    }

    @Override
    public void move(float cx, float cy) {
        EntityCollisions.SolidPred check = this.solidity();

        accelerate();

        cx*=acceleration();
        cy*=acceleration();

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
        tryDecelerate();
    }

    public static int classId = 3; @Override public int classId() {return classId;}


    public void tryDecelerate() {
        if(isAccelerating) {isAccelerating = false; return;}
        decelerate();
    }

    public float acceleration() {return acceleration;}
    public void acceleration(float acceleration) {this.acceleration = acceleration;}
    public void accelerate() {
        isAccelerating = true;
        float step = Time.delta/(type().accelerationTime);
        float newAcceleration = acceleration()*(1+step*type().accelerationInertia);
        acceleration(newAcceleration);
    }
    public void decelerate() {
        float step = Time.delta/(type().accelerationTime*2);
        acceleration(acceleration()-step);
    }
    public Vec2 accelerate(Vec2 vec) {
        accelerate();
        return vec.times(new Vec2(acceleration(),acceleration()));
    }


    public HelicopterUnitType type() {return (HelicopterUnitType) type;}
    public static HelicopterUnitEntity create() {return new HelicopterUnitEntity();}

}
