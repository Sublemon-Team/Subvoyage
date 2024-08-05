package subvoyage.type.block.defense;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Posc;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.consumers.ConsumeLiquidBase;

import java.util.ArrayList;
import java.util.List;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PowerRingTurret extends BaseTurret {
    public final int timerTarget = timers++;
    /** Ticks between attempt at finding a target. */
    public float targetInterval = 20;

    public int spacing = 6;

    public int minRingCount = 1;
    public int boostRingCount = 2;

    public float ringChargeTime = 180f;
    public float ringMovementSpeed = 0.5f;
    public float ringRadius = 24f;
    public float ringAccuracy = 0.8f;

    public TextureRegion ringRegion;
    public TextureRegion ringSparkRegion;

    public BulletType bulletType;

    public PowerRingTurret(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        ringRegion = Core.atlas.find(name+"ring","subvoyage-tesla-ring");
        ringSparkRegion = Core.atlas.find(name+"ring-spark","subvoyage-tesla-ring-star");
    }

    @Override
    public void init() {
        super.init();
        clipSize = size + range*2;
    }

    public static void select(float x, float y, float radius, float size, Color color){
        Lines.stroke(size, color);
        Lines.square(x, y, radius - 14);
        Draw.reset();
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        select(x * tilesize + offset, y * tilesize + offset, tilesize * (size + spacing), size, Pal.placing);
    }

    public boolean intersectsSpacing(int sx, int sy, int ox, int oy, int ext){
        if(spacing < 1) return true;
        int spacingOffset = spacing + ext;
        int sizeOffset = 1 - (size & 1);

        return ox >= sx + sizeOffset - spacingOffset && ox <= sx + spacingOffset &&
                oy >= sy + sizeOffset - spacingOffset && oy <= sy + spacingOffset;
    }

    public boolean intersectsSpacing(Tile self, Tile other){
        return intersectsSpacing(self.x, self.y, other.x, other.y, 0);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        int off = 1 - size % 2;
        for(int x = tile.x - spacing + off; x <= tile.x + spacing; x++){
            for(int y = tile.y - spacing + off; y <= tile.y + spacing; y++){
                Tile other = world.tile(x, y);
                if(other != null && other.block() instanceof PowerRingTurret turbine && (turbine == this || turbine.intersectsSpacing(other.build.tile, tile))) return false;
            }
        }

        return true;
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
                for (PowerRing otherRing : rings) {
                    if(otherRing == ring) continue;
                    if(Mathf.within(ring.x,ring.y,otherRing.x,otherRing.y,ringRadius*1.5f)) {
                        float angle = Angles.angle(ring.x,ring.y,otherRing.x,otherRing.y);
                        otherRing.angle = angle;
                        break;
                    }
                }
                ring.lifetime += Time.delta;
                if(ring.hasTarget) {
                    Tmp.v1.setZero();
                    Tmp.v1.trns(ring.angle,ringMovementSpeed*Time.delta);
                    ring.x += Tmp.v1.x;
                    ring.y += Tmp.v1.y;
                    /*ring.x = Mathf.approachDelta(ring.x,ring.targetX,ringMovementSpeed*8f);
                    ring.targetX += Mathf.sinDeg((Time.time)%360)*(1f-ringAccuracy)*Time.delta * 1f;
                    ring.y = Mathf.approachDelta(ring.y,ring.targetY,ringMovementSpeed*8f);
                    ring.targetY += Mathf.cosDeg((Time.time)%360)*(1f-ringAccuracy)*Time.delta * 1f;*/
                    if(Mathf.within(ring.x,ring.y,ring.targetX,ring.targetY,ringRadius)) {
                        ring.charge += Time.delta/ringChargeTime;
                    } else {
                        float targetAngle = Mathf.round(Mathf.angleExact(ring.targetX-ring.x,ring.targetY-ring.y));
                        ring.angle = Angles.moveToward(ring.angle, targetAngle, rotateSpeed *ringAccuracy * delta() * potentialEfficiency);
                        ring.charge -= Time.delta/ringChargeTime;
                    }
                    ring.charge = Mathf.clamp(ring.charge);
                    if(ring.charge >= 1f) {
                        ring.charge %= 1f;
                        consume();
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
                if(target == null) target = Units.bestTarget(team, ring.x, ring.y, range,
                        e -> !e.dead(),
                        b -> true, UnitSorts.closest);
                if(target == null) continue;
                existingTargets.add(target);
                ring.targetX = target.x();
                ring.targetY = target.y();
                ring.hasTarget = true;
            }
            existingTargets.clear();
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.effect);
            for (PowerRing ring : rings) {
                float pulse = Mathf.sin(1f,1f)*0.05f;
                Lines.stroke(5f+ring.charge*3f, Color.white);
                Draw.alpha(0.6f+ring.charge*0.4f);
                //Lines.circle(ring.x, ring.y, rad);
                Draw.scl((Mathf.sinDeg(Time.time*6)+1)/8f+0.88f-ring.charge*0.3f+pulse);
                Draw.rect(ringSparkRegion,ring.x,ring.y,Time.time*-5%360);
                Draw.scl(1f-ring.charge*0.5f+pulse);
                Draw.rect(ringRegion,ring.x,ring.y,Time.time*10%360);
            }
            Draw.reset();
        }

        public float boost() {
            if(findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase) {
                return Mathf.clamp(consBase.efficiency(this));
            }
            return 1f;
        }

        public BulletType getBullet() {
            return bulletType;
        }
    }
}
