package subvoyage.content.unit.better;

import mindustry.gen.Unit;
import subvoyage.content.unit.AtlacianUnitType;

public class HelicopterUnitTypeNew extends AtlacianUnitType {

    public float accelerationTime = 60f;
    public float accelerationInertia = 0.1f;

    public HelicopterUnitTypeNew(String name) {
        super(name);
        flying = true;
        lowAltitude = true;

        omniMovement = false;
        rotateMoveFirst = true;
    }

    @Override
    public void draw(Unit unit) {
        if(!(unit instanceof HelicopterUnitEntityNew helicopter)) return;

        super.draw(unit);
    }
}
