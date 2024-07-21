package subvoyage.content.blocks.editor.offload_core;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.units.UnitFactory;
import subvoyage.content.world.*;

import static mindustry.Vars.*;
import static mindustry.type.ItemStack.with;
import static subvoyage.content.unit.SvUnits.cryptal;

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
