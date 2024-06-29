package subvoyage.dialog;

import arc.Core;
import arc.math.Interp;
import arc.scene.actions.Actions;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.gen.Icon;
import mindustry.type.Planet;
import mindustry.ui.dialogs.BaseDialog;

import static arc.scene.actions.Actions.*;

public class BetaCompleteDialog extends BaseDialog {
    public BetaCompleteDialog() {
        super("");
        addCloseListener();
        shouldPause = true;

        buttons.defaults().size(210f, 64f);
        buttons.button("@menu", Icon.left, () -> {
            hide();
            Vars.ui.paused.runExitSave();
        });

        buttons.button("@continue", Icon.ok, this::hide);
    }

    public void show(Planet planet){
        cont.clear();
        cont.add(Core.bundle.format("campaign.sbv_beta_completion")).top();

        float playtime = planet.sectors.sumf(s -> s.hasSave() ? s.save.meta.timePlayed : 0) / 1000f;
        //TODO needs more info?
        cont.add(Core.bundle.format("campaign.playtime", UI.formatTime(playtime))).bottom().row();

        setTranslation(0f, -Core.graphics.getHeight());
        color.a = 0f;

        show(Core.scene, Actions.sequence(parallel(fadeIn(1.1f, Interp.fade), translateBy(0f, Core.graphics.getHeight(), 6f, Interp.pow5Out))));
    }
}
