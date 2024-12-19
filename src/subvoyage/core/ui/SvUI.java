package subvoyage.core.ui;

import arc.scene.ui.layout.WidgetGroup;
import mindustry.Vars;
import mindustry.ui.dialogs.PlanetDialog;
import subvoyage.core.ui.advancements.Advancement;
import subvoyage.core.ui.advancements.AdvancementToastFragment;
import subvoyage.core.ui.advancements.AdvancementsDialog;
import subvoyage.core.ui.advancements.AdvancementsHook;

public class SvUI {
    public static AdvancementsDialog advancements;

    public static AdvancementToastFragment advancementFrag;

    public static WidgetGroup overGroup;

    public static void load() {
        advancements = new AdvancementsDialog();

        Advancement.load();
        AdvancementsHook.register();

        advancementFrag = new AdvancementToastFragment();
        advancementFrag.build(overGroup);

        PlanetDialog.debugSelect = true;
    }
}
