package subvoyage.content.unit;

import arc.math.geom.Position;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class HelicopterUnitType extends UnitType {
    public float acceleration = 0.1f;
    public HelicopterUnitType(String name) {
        super(name);
        flying = true;
    }

    @Override
    public void update(Unit unit) {
        accel = acceleration/8f;
        super.update(unit);
    }

    @Override
    public Unit spawn(Position pos, Team team) {
        return super.spawn(pos, team);
    }
}
