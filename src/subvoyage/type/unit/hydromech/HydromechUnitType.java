package subvoyage.type.unit.hydromech;

import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.Scaled;
import arc.math.geom.*;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import subvoyage.type.unit.type.AtlacianUnitType;
import subvoyage.type.unit.hydromech.custom.HydromechState;
import subvoyage.type.unit.hydromech.custom.UnitStatState;
import subvoyage.type.unit.hydromech.weapons.HydromechWeapon;

import java.util.*;
import java.util.function.*;

import static mindustry.Vars.player;

public class HydromechUnitType extends AtlacianUnitType {
    public Consumer<HydromechUnitEntity> onDraw = (e) -> {
    };

    public HashMap<HydromechState, UnitStatState> states = new HashMap<>();
    public <A> void withStates(A... elements) {
        HashMap<HydromechState,UnitStatState> map = new HashMap<>();
        for (int i = 0; i < elements.length; i+=2) {
            HydromechState state = (HydromechState) elements[i];
            UnitStatState stat = (UnitStatState) elements[i+1];
            map.put(state,stat);
        }
        states = map;
    }


    public HydromechUnitType(String name) {
        super(name);
        canDrown = false;
        flying = false;
        hovering = true;

        trailScl = 8;
        trailLength = 20;
        waveTrailX = 4f;
        waveTrailY = -3f;
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
    public void draw(Unit unit) {
        if(unit instanceof HydromechUnitEntity h) onDraw.accept(h);
        if(unit.inFogTo(Vars.player.team())) return;

        boolean isPayload = !unit.isAdded();

        Mechc mech = unit instanceof Mechc ? (Mechc)unit : null;
        float z = isPayload ? Draw.z() : unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);

        if(unit.controller().isBeingControlled(player.unit())){
            drawControl(unit);
        }

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

        if(parts.size > 0){
            for(int i = 0; i < parts.size; i++){
                var part = parts.get(i);
                float warmup = 0f;

                for (WeaponMount mount : unit.mounts) {
                    if(mount.weapon instanceof HydromechWeapon hw && !hw.hasHeat) continue;
                    warmup = Math.max(warmup, mount.heat);
                    //mount.heat = mount.warmup;
                }
                WeaponMount first = unit.mounts.length > part.weaponIndex ? unit.mounts[part.weaponIndex] : null;
                if(first != null){
                    DrawPart.params.set(first.warmup, first.reload / weapons.first().reload, first.smoothReload,warmup, first.recoil, first.charge, unit.x, unit.y, unit.rotation);
                }else{
                    DrawPart.params.set(0f, 0f, 0f, 0f, 0f, 0f, unit.x, unit.y, unit.rotation);
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
    public Unit create(Team team) {
        Unit unit = constructor.get();
        unit.team = team;
        unit.setType(this);
        unit.ammo = ammoCapacity; //fill up on ammo upon creation
        unit.elevation = flying ? 1f : 0;
        unit.heal();
        if(unit instanceof TimedKillc u){
            u.lifetime(lifetime);
        }
        return unit;
    }



    @Override
    public <T extends Unit & Legsc> void drawLegs(T unit) {
        float v = unit instanceof HydromechUnitEntity hm ? hm.liquidedSmooth() : 0f;
        Draw.scl(1-v);
        Draw.alpha(1-v);
        if(v < 0.5f) super.drawLegs(unit);
    }

    @Override
    public void init() {
        super.init();
        naval = true;
    }

    @Override
    public Unit spawn(Position pos, Team team) {
        return super.spawn(pos, team);
    }
}
