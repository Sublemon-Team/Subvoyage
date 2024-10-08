package subvoyage.type.unit.rover;

import mindustry.world.meta.Env;
import subvoyage.type.unit.type.AtlacianUnitType;

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
}
