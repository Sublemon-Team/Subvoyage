package subvoyage.type.unit.type;

import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.Scaled;
import arc.math.geom.*;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Leg;
import mindustry.entities.abilities.Ability;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import subvoyage.type.unit.custom.HydromechState;
import subvoyage.type.unit.custom.HydromechStateStats;
import subvoyage.type.unit.weapon.HydromechWeapon;
import subvoyage.type.unit.entity.HydromechUnitEntity;

import java.util.*;
import java.util.function.*;

import static mindustry.Vars.player;

public class HydromechUnitType extends AtlacianUnitType {
    public Consumer<HydromechUnitEntity> onDraw = (e) -> {
    };

    public HashMap<HydromechState, HydromechStateStats> states = new HashMap<>();
    public <A> void withStates(A... elements) {
        HashMap<HydromechState, HydromechStateStats> map = new HashMap<>();
        for (int i = 0; i < elements.length; i+=2) {
            HydromechState state = (HydromechState) elements[i];
            HydromechStateStats stat = (HydromechStateStats) elements[i+1];
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


    private static final Vec2 legOffset = new Vec2();
    @Override
    public <T extends Unit & Legsc> void drawLegs(T unit) {
        float v = unit instanceof HydromechUnitEntity hm ? hm.liquidedSmooth() : 0f;
        float sc = 1-v;
        Draw.scl(1-v);
        Draw.alpha(1-v);

        applyColor(unit);
        Tmp.c3.set(Draw.getMixColor());

        Leg[] legs = unit.legs();

        float ssize = footRegion.width * footRegion.scl() * 1.5f;
        float rotation = unit.baseRotation();
        float invDrown = 1f - unit.drownTime;

        if(footRegion.found()){
            for(Leg leg : legs){
                Drawf.shadow( Mathf.lerp(leg.base.x,unit.x,1f-sc),  Mathf.lerp(leg.base.y,unit.y,1f-sc), ssize, invDrown);
            }
        }

        //legs are drawn front first
        for(int j = legs.length - 1; j >= 0; j--){
            int i = (j % 2 == 0 ? j/2 : legs.length - 1 - j/2);
            Leg leg = legs[i];
            boolean flip = i >= legs.length/2f;
            int flips = Mathf.sign(flip);

            Vec2 position = unit.legOffset(legOffset, i).add(unit);

            Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension * sc);

            if(footRegion.found() && leg.moving && shadowElevation > 0){
                float scl = shadowElevation * invDrown;
                float elev = Mathf.slope(1f - leg.stage) * scl * sc;
                Draw.color(Pal.shadow);
                Draw.rect(footRegion,  Mathf.lerp(leg.base.x + shadowTX * elev,unit.x,1f-sc),  Mathf.lerp(leg.base.y + shadowTY * elev,unit.y,1f-sc), position.angleTo(leg.base));
                Draw.color();
            }

            Draw.mixcol(Tmp.c3, Tmp.c3.a);

            if(footRegion.found()){
                Draw.rect(footRegion, Mathf.lerp(leg.base.x,unit.x,1f-sc), Mathf.lerp(leg.base.y,unit.y,1f-sc), position.angleTo(leg.base));
            }

            Lines.stroke(legRegion.height * legRegion.scl() * flips);
            Lines.line(legRegion,
                    Mathf.lerp(position.x,unit.x,1f-sc),
                    Mathf.lerp(position.y,unit.y,1f-sc),
                    Mathf.lerp(leg.joint.x,unit.x,1f-sc),
                    Mathf.lerp(leg.joint.y,unit.y,1f-sc), false);

            Lines.stroke(legBaseRegion.height * legRegion.scl() * flips);
            Lines.line(legBaseRegion,
                    Mathf.lerp(leg.joint.x + Tmp.v1.x,unit.x,1f-sc),
                    Mathf.lerp(leg.joint.y + Tmp.v1.y,unit.y,1f-sc),
                    Mathf.lerp(leg.base.x,unit.x,1f-sc),
                    Mathf.lerp(leg.base.y,unit.y,1f-sc), false);

            if(jointRegion.found()){
                Draw.rect(jointRegion, leg.joint.x, leg.joint.y);
            }
        }

        //base joints are drawn after everything else
        if(baseJointRegion.found()){
            for(int j = legs.length - 1; j >= 0; j--){
                //TODO does the index / draw order really matter?
                Vec2 position = unit.legOffset(legOffset, (j % 2 == 0 ? j/2 : legs.length - 1 - j/2)).add(unit);
                Draw.rect(baseJointRegion, position.x, position.y, rotation);
            }
        }

        if(baseRegion.found()){
            Draw.rect(baseRegion, unit.x, unit.y, rotation - 90);
        }

        Draw.reset();
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
