package subvoyage.ui;

import subvoyage.ui.advancements.Advancement;
import subvoyage.ui.advancements.AdvancementsDialog;
import subvoyage.ui.advancements.AdvancementsHook;

public class SvUI {
    public static AdvancementsDialog advancements;

    public static void load() {
        advancements = new AdvancementsDialog();
        Advancement.load();
        AdvancementsHook.register();
    }
}
