package oceanic_dust.content.world.draw;

import mindustry.gen.*;
import mindustry.world.draw.*;

import java.util.function.*;

public class DrawTurretCallbacked extends DrawTurret {

    public BiConsumer<Building,DrawTurretCallbacked> onDraw;

    @Override
    public void draw(Building build) {
        onDraw.accept(build,this);
        super.draw(build);
    }
}
