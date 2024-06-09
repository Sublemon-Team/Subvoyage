package oceanic_dust.entities.part;

import mindustry.gen.Building;
import mindustry.world.draw.DrawTurret;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DrawTurretCallbacked extends DrawTurret {

    public BiConsumer<Building,DrawTurretCallbacked> onDraw;

    @Override
    public void draw(Building build) {
        onDraw.accept(build,this);
        super.draw(build);
    }
}
