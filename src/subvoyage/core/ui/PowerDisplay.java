package subvoyage.core.ui;

import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.world.meta.StatUnit;

public class PowerDisplay extends Table {
    public final float amount;

    public PowerDisplay(float amount){
        this(amount, true);
    }

    public PowerDisplay(float amount, boolean showPower){
        add(new Image(Icon.power.getRegion())).color(Pal.powerLight).size(24f).scaling(Scaling.fit);
        if(showPower) add(Strings.autoFixed(amount*60f,2) + StatUnit.perSecond.localized()).padLeft(4 + amount > 99 ? 4 : 0);

        this.amount = amount;
    }
}

