package subvoyage.ui;

import arc.scene.ui.layout.WidgetGroup;
import mindustry.Vars;
import subvoyage.ui.advancements.Advancement;
import subvoyage.ui.advancements.AdvancementToastFragment;
import subvoyage.ui.advancements.AdvancementsDialog;
import subvoyage.ui.advancements.AdvancementsHook;

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
    }
}
