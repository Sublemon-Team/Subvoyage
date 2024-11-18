package subvoyage.type.block.fog;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.entities.Units;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.input.Placement;
import mindustry.logic.*;
import mindustry.type.unit.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class Beacon extends RepairTurret {
    public float discoveryTime = 60f * 10f;
    public TextureRegion glow;
    public Consume heal;
    public Consume discover;

    public Beacon(String name) {
        super(name);
        size = 3;
        outlineColor = Pal.darkOutline;
        envDisabled |= Env.scorching;
        destructible = true;
        swapDiagonalPlacement = true;
        allowDiagonal = true;
    }

    @Override
    public void load() {
        super.load();
        glow = Core.atlas.find(name+"-glow");
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, lightRadius/2 * tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.floor().isLiquid;
    }

    public class BeaconBuild extends RepairPointBuild implements Ranged {
        public float fogProgress;
        public float lastRadius = 0f;
        public float fogEfficiency = 0f;
        public float scl = 1f;

        @Override
        public void update() {
            super.update();
        }

        @Override
        public void updateConsumption() {
            discover.update(this);
        }

        @Override
        public void updateTile(){

            laserColor = Pal.heal;
            fogEfficiency = Mathf.lerp(fogEfficiency,discover.efficiency(this),delta()/40f);

            if(Math.abs(fogRadius() - lastRadius) >= 0.5f){
                Vars.fogControl.forceUpdate(team, this);
                lastRadius = fogRadius();
            }

            fogProgress += (fogEfficiency * delta()) / discoveryTime;
            fogProgress = Mathf.clamp(fogProgress);

            float multiplier = 1f;

            if(target != null && (target.dead() || target.type() instanceof MissileUnitType || target.dst(this) - target.hitSize/2f > repairRadius)){
                target = null;
            }

            if(target == null){
                offset.setZero();
            }

            boolean healed = false;
            float efficiency = heal.efficiency(this);
            if(target != null && efficiency > 0 && target.damaged()){
                float angle = Angles.angle(x, y, target.x + offset.x, target.y + offset.y);
                if(Angles.angleDist(angle, rotation) < 30f){
                    healed = true;
                    target.heal(repairSpeed * strength * delta() * efficiency * multiplier);
                    heal.update(this);
                }
                rotation = Mathf.slerpDelta(rotation, angle, 0.5f * efficiency * timeScale);
            }

            strength = Mathf.lerpDelta(strength, healed ? 1f : 0f, 0.08f * Time.delta);
            if(timer(timerTarget, 20)){
                target = Units.closest(team, x, y, repairRadius, (e) -> !(e.type() instanceof MissileUnitType) && e.damaged());
            }
        }

        @Override
        public float efficiency() {
            return 1f;
        }

        @Override
        public float edelta() {
            return delta();
        }

        @Override
        public float fogRadius(){
            return fogRadius * fogProgress * fogEfficiency;
        }

        @Override
        public boolean canPickup(){
            return false;
        }


        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, fogRadius() * tilesize, Pal.accent);
            Drawf.dashCircle(x, y, repairRadius, Pal.heal);
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);

            float t = Time.time / 60f;

            Draw.z(Layer.turret);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);

            scl = Mathf.lerp(scl,enabled() && target != null ? 1f : 0f,delta()/40f);
            Draw.scl(scl*0.2f+1f);
            Drawf.spinSprite(region, x, y, ((t * 90) % 360));
            Draw.alpha(scl*0.5f);
            Drawf.spinSprite(glow, x, y, ((t * 90) % 360));

            Draw.scl();
            Draw.reset();

            if(heal.efficiency(this) > 0f)
                drawBeam(x, y, rotation, length, id, target, team, strength,
                        pulseStroke, pulseRadius, beamWidth, lastEnd, offset, laserColor, laserTopColor,
                        laser, laserEnd, laserTop, laserTopEnd);
        }

        @Override
        public float progress(){
            return fogProgress;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(fogProgress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            fogProgress = read.f();
        }
    }
}
