package subvoyage.draw.block;

import mindustry.gen.*;
import mindustry.world.draw.*;

import java.util.function.*;

public class DrawTurretCallbacked extends DrawTurret {

    public BiConsumer<Building,DrawTurretCallbacked> onDraw;

    public DrawTurretCallbacked(String basePrefix){
        super(basePrefix);
    }

    public DrawTurretCallbacked(){
        super();
    }

    @Override
    public void draw(Building build) {
        onDraw.accept(build,this);
        super.draw(build);
    }
}
