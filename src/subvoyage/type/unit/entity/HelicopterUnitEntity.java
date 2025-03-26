package subvoyage.type.unit.entity;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ai.types.CommandAI;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ExplosionBulletType;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Hitboxc;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import subvoyage.content.SvUnits;
import subvoyage.core.draw.DataEffect;
import subvoyage.core.draw.SvFx;
import subvoyage.core.draw.SvPal;
import subvoyage.type.unit.type.HelicopterUnitType;

import java.util.Iterator;

public class HelicopterUnitEntity extends UnitEntity {

    public float localAcceleration = 0;
    public boolean isAccelerating = false;
    public float lastAccel = 0f;

    @Override
    public int classId() {
        return SvUnits.mapHelicopter;
    }

    @Override
    public void draw() {
        this.drawBuilding();
        if (this.mining()) {
            float focusLen = this.hitSize / 2.0F + Mathf.absin(Time.time, 1.1F, 0.5F);
            float swingScl = 12.0F;
            float swingMag = 1.0F;
            float flashScl = 0.3F;
            float px = this.x + Angles.trnsx(this.rotation, focusLen);
            float py = this.y + Angles.trnsy(this.rotation, focusLen);
            float ex = this.mineTile.worldx() + Mathf.sin(Time.time + 48.0F, swingScl, swingMag);
            float ey = this.mineTile.worldy() + Mathf.sin(Time.time + 48.0F, swingScl + 2.0F, swingMag);
            Draw.z(115.1F);
            Draw.color(Color.lightGray, Color.white, 1.0F - flashScl + Mathf.absin(Time.time, 0.5F, flashScl));
            Drawf.laser(Core.atlas.find("minelaser"), Core.atlas.find("minelaser-end"), px, py, ex, ey, 0.75F);
            if (this.isLocal()) {
                Lines.stroke(1.0F, Pal.accent);
                Lines.poly(this.mineTile.worldx(), this.mineTile.worldy(), 4, 4.0F * Mathf.sqrt2, Time.time);
            }

            Draw.color();
        }

        Iterator var9 = this.statuses.iterator();

        while(var9.hasNext()) {
            StatusEntry e = (StatusEntry)var9.next();
            e.effect.draw(this, e.time);
        }
        this.type.draw(this);
    }

    public float accel() {
        return accelSmooth;
    }

    @Override
    public float shadowAlpha() {
        return accel();
    }

    @Override
    public float elevation() {
        return accel();
    }

    public static HelicopterUnitEntity create() {
        return new HelicopterUnitEntity();
    }

    @Override
    public void moveAt(Vec2 vector, float acceleration) {
        Vec2 t = tmp1.set(vector);
        tmp2.set(t).sub(this.vel).limit(acceleration * vector.len() * Time.delta);

        if(t.len() > 0.2f) localAcceleration += (Time.delta/60)*(localAcceleration+0.1f);
        if(t.len() > 0.2f) isAccelerating = true;
        localAcceleration = Mathf.clamp(localAcceleration,0,1);
        this.vel.add(tmp2.times(new Vec2(localAcceleration,localAcceleration)));
    }

    @Override
    public void movePref(Vec2 movement) {
        if (this.type.omniMovement) {
            this.moveAt(movement,type.accel);
        } else {
            this.rotateMove(movement);
        }
    }

    @Override
    public void wobble() {
        this.x += Mathf.sin(Time.time + (float)(this.id() % 10 * 12), 25.0F, 0.05F) * Time.delta * this.elevation * accel();
        this.y += Mathf.cos(Time.time + (float)(this.id() % 10 * 12), 25.0F, 0.05F) * Time.delta * this.elevation * accel();
    }

    public boolean reachedFullAltitude = false;
    public float accelSmooth = 0f;


    public transient BulletType expl = null;

    @Override
    public void update() {
        physref.body.layer = 2;

        super.update();
        accelSmooth = Mathf.lerp(accelSmooth,vel.len()/type.speed,Time.delta/10f);
        if(!isAccelerating) {
            localAcceleration -= Time.delta/30f;
            localAcceleration = Mathf.clamp(localAcceleration,0,1);
        } else isAccelerating = false;
        if(type instanceof  HelicopterUnitType hType) hType.onUpdate.accept(this);

        if(accel() > 0.7f) {
            reachedFullAltitude = true;
        }

        if(lastAccel > 0.25 && accel() <= 0.25 && accel()-lastAccel < 0 && reachedFullAltitude) {
            Effect.shake(type.stepShake,type.stepShake,new Vec2(x,y));
            reachedFullAltitude = false;
        }
        lastAccel = accel();

        if(dead) {
            accelSmooth = Mathf.approachDelta(accelSmooth,0,1/60f);
            if(!Vars.state.isPaused()) rotation += Time.delta / hitSize * 80f * accel();
        }

        physref.body.layer = 2; // prevent isGrounded affecting physics
    }

    @Override
    public boolean canPass(int tileX, int tileY) {
        return true;
    }


    @Override
    public boolean isPathImpassable(int tileX, int tileY) {
        return Vars.world.tiles.in(tileX, tileY);
    }

    @Override
    public boolean isGrounded() {
        return accel() < 0.1f;
    }

    @Override
    public boolean isFlying() {
        return accel() > 0.1f;
    }
}
