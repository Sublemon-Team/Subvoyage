package subvoyage.type.block.laser;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import subvoyage.util.Var;

public interface LaserBuild {
    float laser();
    float rawLaser();
    float laserRequirement();

    float maxPower();

    boolean consumer();
    boolean supplier();

    LaserGraph graph();

    default void drawStatus(Building building) {
        if(graph().broken()) {
            Draw.color(Pal.remove, 0.8f+Mathf.cos(Time.time,10f,0.2f));
        }
    }
    default void updateLaser(Building building) {
        if(graph() != null) graph().update(building);
        if(building instanceof LaserBuild lb) {
            lb.graph().powerOut = lb.rawLaser() > maxPower();
        }
    }
    default void clearLaser(Building building) {
        if(graph() != null) graph().clearGraph(building);
    }

    default float inputLaser(Building building) {
        try {
            Var<Float> laser = Var.f();
            graph().getSuppliers().each((b) -> {
                if (b instanceof LaserBuild lb) {
                    laser.val += lb.laser();
                }
            });
            return laser.val;
        } catch (StackOverflowError e) {
            return 0f;
        }
    }
}
