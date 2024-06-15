package subvoyage.content.unit.better;

import arc.math.*;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.EntityCollisions;
import subvoyage.content.unit.systematical.BaseUnit;

public class HelicopterUnitEntityNew extends BaseUnit {
    public float acceleration = 0;
    private boolean isAccelerating = false;

    @Override
    public void moveAt(Vec2 vector, float acceleration) {
        Vec2 t = tmp1.set(vector);
        tmp2.set(t).sub(this.vel).limit(acceleration * vector.len() * Time.delta);
        if(t.len() >= 0.2f) accelerate();
        this.vel.add(tmp2.times(new Vec2(acceleration(),acceleration())));
    }

    @Override
    public void move(float cx, float cy) {
        EntityCollisions.SolidPred check = this.solidity();

        if(cx*cy >= 0.1f) accelerate();

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
        if(!isAccelerating) decelerate();
        else isAccelerating = false;
    }

    public float acceleration() {return acceleration;}
    public void acceleration(float acceleration) {this.acceleration = Mathf.clamp(acceleration);}
    public void accelerate() {
        isAccelerating = true;
        float step = Time.delta/(type().accelerationTime);
        float newAcceleration = acceleration()*(1+step)+type().accelerationInertia;
        acceleration(newAcceleration);
    }
    public void decelerate() {
        isAccelerating = false;
        float step = Time.delta/(type().accelerationTime)*2;
        acceleration(acceleration()-step);
    }
    public Vec2 accelerate(Vec2 vec) {
        accelerate();
        return vec.times(new Vec2(acceleration(),acceleration()));
    }


    public HelicopterUnitTypeNew type() {return (HelicopterUnitTypeNew) type;}
    public static HelicopterUnitEntityNew create() {return new HelicopterUnitEntityNew();}

}
