package subvoyage.type.unit.type;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.Scaled;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.ai.ControlPathfinder;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.abilities.Ability;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.environment.Floor;
import subvoyage.type.unit.ai.HelicopterAI;
import subvoyage.type.unit.entity.HelicopterUnitEntity;
import subvoyage.type.unit.weapon.HydromechWeapon;

import java.util.function.Consumer;

import static mindustry.Vars.player;
import static mindustry.Vars.world;

public class HelicopterUnitType extends AtlacianUnitType {
    public Consumer<HelicopterUnitEntity> onUpdate = (e) -> {};

    public HelicopterUnitType(String name) {
        super(name);
        flying = true;
        lowAltitude = true;
        rotateMoveFirst = false;
        drag = 0.15f;
        strafePenalty = 1f;
        pathCost = (team,tile) -> 100;

        aiController = HelicopterAI::new;
    }

    @Override
    public void init() {
        super.init();
        payloadCapacity = hitSize * hitSize * 0.85f;
    }

    @Override
    public void draw(Unit unit) {
        if(unit.inFogTo(Vars.player.team())) return;

        float scale = unit instanceof HelicopterUnitEntity h ? h.accel()*0.1f + 0.9f : 1f;
        boolean isPayload = !unit.isAdded();

        Mechc mech = unit instanceof Mechc ? (Mechc)unit : null;
        float z = isPayload ? Draw.z() : lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit;

        if(!isPayload && (unit.isFlying() || shadowElevation > 0)){
            Draw.z(Math.min(Layer.darkness, z - 1f));
            drawShadow(unit);
        }

        Draw.z(z - 0.02f);

        if(unit instanceof Tankc){
            drawTank((Unit & Tankc)unit);
        }

        if(unit instanceof Legsc && !isPayload){
            drawLegs((Unit & Legsc)unit);
        }

        Draw.z(Math.min(z - 0.01f, Layer.bullet - 1f));

        if(unit instanceof Payloadc){
            drawPayload((Unit & Payloadc)unit);
        }

        drawSoftShadow(unit);

        Draw.z(z);

        if(unit instanceof Crawlc c){
            drawCrawl(c);
        }

        

        if(drawBody) drawOutline(unit);
        
        drawWeaponOutlines(unit);
        
        if(engineLayer > 0) Draw.z(engineLayer);
        
        if(trailLength > 0 && !naval && (unit.isFlying() || !useEngineElevation)){
            drawTrail(unit);
        }
        
        if(engines.size > 0) drawEngines(unit);
        Draw.z(z);
        
        if(drawBody) drawBody(unit);
        
        if(drawCell) drawCell(unit);
        
        drawWeapons(unit);
        
        if(drawItems) drawItems(unit);
        
        drawLight(unit);
        
        if(unit.shieldAlpha > 0 && drawShields){
            drawShield(unit);
        }

        //TODO how/where do I draw under?
        if(parts.size > 0){
            for(int i = 0; i < parts.size; i++){
                var part = parts.get(i);

                WeaponMount first = unit.mounts.length > part.weaponIndex ? unit.mounts[part.weaponIndex] : null;
                if(first != null){
                    DrawPart.params.set(unit.vel.len() / unit.speed(), first.reload / weapons.first().reload, first.smoothReload, first.heat, first.recoil, first.charge, unit.x, unit.y, unit.rotation);
                }else{
                    DrawPart.params.set(unit.vel.len() / unit.speed(), 0f, 0f, 0f, 0f, 0f, unit.x, unit.y, unit.rotation);
                }

                if(unit instanceof Scaled s){
                    DrawPart.params.life = s.fin();
                }

                part.draw(DrawPart.params);
            }
        }

        if(!isPayload){
            for(Ability a : unit.abilities){
                Draw.reset();
                a.draw(unit);
            }
        }

        Draw.reset();
    }

    @Override
    public void drawBody(Unit unit) {
        applyColor(unit);
        Draw.rect(region, unit.x, unit.y, unit.rotation - 90);
        Draw.reset();
        if(bodyHeat && regionHeat != null && regionHeat.found()) {
            float warmup = 0f;
            for (WeaponMount mount : unit.mounts) {
                if(mount.weapon instanceof HydromechWeapon hw && !hw.hasHeat) continue;
                if(mount.weapon instanceof HydromechWeapon hw) {
                    warmup = Math.max(warmup,hw.warmupToHeat ? mount.warmup : mount.heat);
                    continue;
                }
                warmup = Math.max(warmup, mount.heat);
                //mount.heat = mount.warmup;
            }
            Draw.blend(Blending.additive);
            Draw.color(heatColor,warmup);
            Draw.scl(bodyScale);
            Draw.rect(regionHeat, unit.x, unit.y, unit.rotation - 90);
            Draw.blend(Blending.normal);
        }
        Draw.scl();
    }

    @Override
    public void drawCell(Unit unit) {
        applyColor(unit);

        Draw.color(cellColor(unit));
        Draw.rect(cellRegion, unit.x, unit.y, unit.rotation - 90);
        Draw.reset();
    }

    @Override
    public void drawShadow(Unit unit) {
        float e = Mathf.clamp(unit.elevation, shadowElevation, 1f) * shadowElevationScl * (1f - unit.drownTime);
        e *= unit instanceof HelicopterUnitEntity h ? h.accel() : 1f;
        float x = unit.x + shadowTX * e, y = unit.y + shadowTY * e;
        Floor floor = world.floorWorld(x, y);

        float dest = floor.canShadow ? 1f : 0f;
        //yes, this updates state in draw()... which isn't a problem, because I don't want it to be obvious anyway
        unit.shadowAlpha = unit.shadowAlpha < 0 ? dest : Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f);
        Draw.color(Pal.shadow, Pal.shadow.a * unit.shadowAlpha);

        Draw.rect(shadowRegion, unit.x + shadowTX * e, unit.y + shadowTY * e, unit.rotation - 90);
        Draw.color();
    }

    @Override
    public Unit spawn(Position pos) {
        return super.spawn(pos);
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
    }


    @Override
    public Unit create(Team team) {
        Unit unit = constructor.get();
        unit.team = team;
        unit.setType(this);
        unit.ammo = ammoCapacity; //fill up on ammo upon creation
        unit.elevation = flying ? 1f : 0;
        unit.heal();
        if(unit instanceof HelicopterUnitEntity e) {
            e.localAcceleration = 0;
            e.isAccelerating = false;
        }
        if(unit instanceof TimedKillc u){
            u.lifetime(lifetime);
        }
        return unit;
    }

    @Override
    public Unit spawn(Position pos, Team team) {
        return super.spawn(pos, team);
    }
}
