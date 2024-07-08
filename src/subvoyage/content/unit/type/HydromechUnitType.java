package subvoyage.content.unit.type;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import mindustry.game.*;
import mindustry.gen.*;
import subvoyage.content.unit.entity.*;

import java.util.*;
import java.util.function.*;

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
        trailLength = 9;
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
        super.draw(unit);
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
