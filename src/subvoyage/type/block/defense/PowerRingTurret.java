package subvoyage.type.block.defense;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Posc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.consumers.ConsumeLiquidBase;

import java.util.ArrayList;
import java.util.List;

public class PowerRingTurret extends BaseTurret {
    public final int timerTarget = timers++;
    /** Ticks between attempt at finding a target. */
    public float targetInterval = 20;

    public int minRingCount = 1;
    public int boostRingCount = 2;

    public float ringDamage = 0f;
    public float ringChargeTime = 180f;
    public float ringMovementSpeed = 6/60f;
    public float ringRadius = 24f;
    public float ringAccuracy = 0.8f;

    public PowerRingTurret(String name) {
        super(name);
    }

    public class PowerRingTurretBuild extends BaseTurretBuild {
        List<PowerRing> rings = new ArrayList<>();
        @Override
        public void updateTile() {
            super.updateTile();
            int estDefRingCount = (int) Mathf.lerp(0,minRingCount,efficiency());
            int estimatedRingCount = (int) Mathf.lerp(estDefRingCount,estDefRingCount+boostRingCount,boost()*efficiency());
            if(rings.size() > estimatedRingCount) rings = rings.subList(0,estimatedRingCount);
            if(rings.size() < estimatedRingCount) rings.add(new PowerRing() {{
                x = PowerRingTurretBuild.this.x;
                y = PowerRingTurretBuild.this.y;
            }});
            for (PowerRing ring : rings) {
                if(ring.hasTarget) {
                    ring.x = Mathf.approachDelta(ring.x,ring.targetX,ringMovementSpeed*8f);
                    ring.targetX += Mathf.sinDeg((Time.time)%360)*(1f-ringAccuracy)*Time.delta * 1f;
                    ring.y = Mathf.approachDelta(ring.y,ring.targetY,ringMovementSpeed*8f);
                    ring.targetY += Mathf.cosDeg((Time.time)%360)*(1f-ringAccuracy)*Time.delta * 1f;
                    if(Mathf.within(ring.x,ring.y,ring.targetX,ring.targetY,ringRadius)) {
                        ring.charge += Time.delta/ringChargeTime;
                    } else ring.charge -= Time.delta/ringChargeTime;
                    ring.charge = Mathf.clamp(ring.charge);
                    if(ring.charge >= 1f) {
                        ring.charge %= 1f;
                        ring.shoot(this);
                    }
                }
            }
            if(timer(timerTarget, targetInterval)){
                findTargets();
            }
        }

        public void findTargets() {
            float range = range();
            Seq<Posc> existingTargets = Seq.with();
            for (PowerRing ring : rings) {
                Posc target = Units.bestTarget(team, ring.x, ring.y, range,
                        e -> !e.dead() && !existingTargets.contains(e),
                        b -> !existingTargets.contains(b), UnitSorts.closest);
                if(target == null) continue;
                existingTargets.add(target);
                ring.targetX = target.x();
                ring.targetY = target.y();
                ring.hasTarget = true;
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.effect);
            for (PowerRing ring : rings) {
                float rad = ringRadius-12f*ring.charge;
                Lines.stroke(5f+ring.charge*3f, Pal.power);
                Lines.circle(ring.x, ring.y, rad);
                Lines.stroke(1f+ring.charge*1.5f, Color.white);
                Lines.circle(ring.x, ring.y, rad);
            }
            Draw.reset();
        }

        public float boost() {
            if(findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase) {
                return Mathf.clamp(consBase.efficiency(this));
            }
            return 1f;
        }
    }
}
