package subvoyage.type.unit.type;

import mindustry.gen.Tankc;
import mindustry.gen.Unit;
import mindustry.world.meta.Env;

public class RoverUnitType extends AtlacianUnitType {
    public RoverUnitType(String name) {
        super(name);
        squareShape = true;
        omniMovement = false;
        rotateMoveFirst = true;
        rotateSpeed = 2f;
        envDisabled = Env.none;
        speed = 1.1f;
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);
    }

    @Override
    public <T extends Unit & Tankc> void drawTank(T unit) {
        if(unit.elevation() > 0.5f) return;
        super.drawTank(unit);
    }
}
