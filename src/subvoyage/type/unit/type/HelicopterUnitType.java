package subvoyage.type.unit.type;

import arc.math.geom.Position;
import mindustry.game.Team;
import mindustry.gen.TimedKillc;
import mindustry.gen.Unit;
import subvoyage.type.unit.entity.HelicopterUnitEntity;

import java.util.function.Consumer;

public class HelicopterUnitType extends AtlacianUnitType {
    public Consumer<HelicopterUnitEntity> onUpdate = (e) -> {};
    public Consumer<HelicopterUnitEntity> onDraw = (e) -> {};

    public HelicopterUnitType(String name) {
        super(name);
        flying = true;
        lowAltitude = true;
        rotateMoveFirst = true;
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
        if(unit instanceof HelicopterUnitEntity h) onDraw.accept(h);
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
