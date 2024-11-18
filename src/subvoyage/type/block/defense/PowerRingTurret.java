package subvoyage.type.block.defense;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.struct.OrderedMap;
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
import mindustry.world.draw.DrawBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import java.util.ArrayList;
import java.util.List;

import static mindustry.Vars.*;

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
    public DrawBlock drawer;

    public float recoilTime;
    public float cooldownTime;
    public float recoilPow;
    public float recoil;

    public Color heatColor;
    public float elevation;

    public PowerRingTurret(String name) {
        super(name);
        elevation = -1f;
        heatColor = Color.red;
        recoilTime = 30f;
        cooldownTime = 20f;
        recoil = 5f;
        recoilPow = 1;

    }

    @Override
    public void load() {
        super.load();
        ringRegion = Core.atlas.find(name+"ring","subvoyage-tesla-ring");
        ringSparkRegion = Core.atlas.find(name+"ring-spark","subvoyage-tesla-ring-star");
        this.drawer.load(this);
    }

    @Override
    public void setStats() {
        super.setStats();
        this.stats.add(Stat.ammo, StatValues.ammo(OrderedMap.of(
                this,bulletType
        )));
    }

    public TextureRegion[] icons() {
        return this.drawer.finalIcons(this);
    }

    public void getRegionsToOutline(Seq<TextureRegion> out) {
        this.drawer.getRegionsToOutline(this, out);
    }

    @Override
    public void init() {
        super.init();
        clipSize = size*2 + range*4;
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
                if(other != null && other.build != null && other.build.team != team) continue;
                if(other != null && other.block() instanceof PowerRingTurret turbine && (turbine == this || turbine.intersectsSpacing(other.build.tile, tile))) return false;
            }
        }

        return true;
    }

    public class PowerRingTurretBuild extends BaseTurretBuild {
        public Vec2 recoilOffset = new Vec2();
        List<PowerRing> rings = new ArrayList<>();
        public float curRecoil;
        public float heat;
        public float smoothWarmup = 0;

        @Override
        public boolean wasVisible() {
            return true;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            wasVisible = true;
            int estDefRingCount = (int) Mathf.lerp(0,minRingCount,efficiency());
            int estimatedRingCount = (int) Mathf.lerp(estDefRingCount,estDefRingCount+boostRingCount,boost()*efficiency());
            smoothWarmup = Mathf.lerp(smoothWarmup,(float) estimatedRingCount/(minRingCount+boostRingCount),Time.delta/40f);
            float rr =ringRadius*1.5f;
            if(rings.size() > estimatedRingCount) rings = rings.subList(0,estimatedRingCount);
            if(rings.size() < estimatedRingCount) rings.add(new PowerRing() {{
                x = PowerRingTurretBuild.this.x+(rand.nextFloat()*rr*2f-rr);
                y = PowerRingTurretBuild.this.y+(rand.nextFloat()*rr*2f-rr);
            }});
            float ringAccuracy = ((PowerRingTurret) block).ringAccuracy*(1f+boost());
            this.curRecoil = Mathf.approachDelta(this.curRecoil, 0.0F, 1.0F / recoilTime);
            this.heat = Mathf.approachDelta(heat, 0.0F, 1.0F / cooldownTime);
            float targetRingX = 0f;
            float targetRingY = 0f;
            boolean targetRingHas = false;
            for (PowerRing ring : rings) {
                if(!Mathf.within(ring.x,ring.y,x,y,range+rr)) {
                    ring.x = x; ring.y = y;
                    ring.targetX = 0; ring.targetY = 0;
                    ring.hasTarget = false;
                    continue;
                }
                for (PowerRing otherRing : rings) {
                    if(otherRing == ring) continue;
                    if(Mathf.within(ring.x,ring.y,otherRing.x,otherRing.y,ringRadius*1.25f)) {
                        float angle = Angles.angle(ring.x,ring.y,otherRing.x,otherRing.y);
                        otherRing.angle = angle;
                        break;
                    }
                }
                if(ring.hasTarget) {
                    targetRingX += ring.x;
                    targetRingY += ring.y;
                    targetRingHas = true;
                    if(!Mathf.within(ring.targetX,ring.targetY,x,y,range*2f)) {
                        ring.hasTarget = false;
                        continue;
                    }
                    ring.lifetime += Time.delta;
                    Tmp.v1.setZero();
                    Tmp.v1.trns(ring.angle,ringMovementSpeed*delta());
                    ring.x += Tmp.v1.x;
                    ring.y += Tmp.v1.y;
                    if(Mathf.within(ring.x,ring.y,ring.targetX,ring.targetY,rr)) {
                        ring.charge += delta()/ringChargeTime;
                    } else {
                        float targetAngle = Mathf.round(Mathf.angleExact(ring.targetX-ring.x,ring.targetY-ring.y));
                        ring.angle = Angles.moveToward(ring.angle, targetAngle, rotateSpeed *ringAccuracy * delta() * potentialEfficiency);
                        ring.charge -= delta()/ringChargeTime/2f;
                    }
                    ring.charge = Mathf.clamp(ring.charge);
                    if(ring.charge >= 1f) {
                        ring.charge %= 1f;
                        consume();
                        ring.shoot(this);
                        curRecoil = 2f;
                        heat = 1;
                    }
                }
            }

            if(findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase) {
                consBase.update(this);
            }
            this.recoilOffset.trns(this.rotation, -Mathf.pow(this.curRecoil, recoilPow) * recoil);
            if(timer(timerTarget, targetInterval)){
                findTargets();
            }
            if(targetRingHas) {
                targetRingX /= rings.size();
                targetRingY /= rings.size();
                float targetAngle = Angles.angle(x,y,targetRingX,targetRingY);
                rotation = Angles.moveToward(rotation,targetAngle,10f/Time.delta);
            }
        }

        public void findTargets() {
            float range = range();
            Seq<Posc> existingTargets = Seq.with();
            for (PowerRing ring : rings) {
                Posc target = Units.bestTarget(team, x, y, range,
                        e -> !e.dead() && !existingTargets.contains(e) && Mathf.within(e.x,e.y,x,y,range*2f),
                        b -> !existingTargets.contains(b) && Mathf.within(b.x,b.y,x,y,range*2f), UnitSorts.closest);
                if(target == null) target = Units.bestTarget(team, x, y, range,
                        e -> !e.dead() && Mathf.within(e.x,e.y,x,y,range*2f),
                        b -> Mathf.within(b.x,b.y,x,y,range*2f), UnitSorts.closest);
                if(target == null) continue;
                existingTargets.add(target);
                ring.targetX = target.x();
                ring.targetY = target.y();
                ring.hasTarget = true;
            }
            existingTargets.clear();
        }
        public final Rand rand = new Rand();
        @Override
        public void draw() {
            drawer.draw(this);
            Draw.z(Layer.effect);
            for (PowerRing ring : rings) {
                float pulse = Mathf.sin(1f,1f)*0.05f;
                Lines.stroke(5f+ring.charge*3f, Color.white);
                float v = Mathf.clamp(ring.lifetime/10f);
                Draw.alpha((0.75f+ring.charge*0.25f)*v);
                //Lines.circle(ring.x, ring.y, rad);
                Draw.scl(((Mathf.sinDeg(Time.time*6)+1)/8f+0.88f-ring.charge*0.3f+pulse)*v);
                Draw.rect(ringSparkRegion,ring.x,ring.y,state.isPaused() ? 0f : rand.nextFloat()*360f);
                Draw.scl((1f-ring.charge*0.5f+pulse)*v);
                Draw.rect(ringRegion,ring.x,ring.y, state.isPaused() ? 0f : rand.nextFloat()*360f);
            }
            Draw.reset();
        }

        @Override
        public float warmup() {
            int estDefRingCount = (int) Mathf.lerp(0,minRingCount,efficiency());
            int estimatedRingCount = (int) Mathf.lerp(estDefRingCount,estDefRingCount+boostRingCount,boost()*efficiency());
            return smoothWarmup;
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
