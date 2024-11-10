package subvoyage.type.block.laser_new;

import mindustry.gen.Building;
import subvoyage.utility.Var;

public interface LaserBuild {
    float laser();
    float laserRequirement();

    float maxPower();

    boolean consumer();
    boolean supplier();

    LaserGraph graph();

    default void updateLaser(Building building) {
        if(graph() != null) graph().update(building);
    }
    default void clearLaser(Building building) {
        if(graph() != null) graph().clearGraph(building);
    }

    default float inputLaser(Building building) {
        try {
            Var<Float> laser = new Var<>(0f);
            graph().getSuppliers().each((b) -> {
                if (b instanceof LaserBuild lb) {
                    laser.val += lb.laser();
                }
            });
            return laser.val;
        } catch (StackOverflowError e) {
            return 0f;
        }
    };
}
