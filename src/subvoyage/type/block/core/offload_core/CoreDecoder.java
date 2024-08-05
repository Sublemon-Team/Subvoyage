package subvoyage.type.block.core.offload_core;

import mindustry.gen.*;
import mindustry.world.blocks.units.UnitFactory;

import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvUnits.cryptal;

public class CoreDecoder extends UnitFactory {


    public CoreDecoder(String name) {
        super(name);
        configurable = false;
        plans.add(new UnitPlan(cryptal, 60f*30f, with()));
        update = true;
        ambientSound = Sounds.electricHum;
    }

    @Override
    public void init() {
        super.init();
    }
}
