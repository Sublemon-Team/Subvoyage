package subvoyage.world.generator;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import mindustry.maps.generators.PlanetGenerator;

public class AtlacianPlanetGen extends PlanetGenerator {
    @Override
    public float getHeight(Vec3 position) {
        return 0;
    }

    @Override
    public Color getColor(Vec3 position) {
        return Color.black;
    }
}
