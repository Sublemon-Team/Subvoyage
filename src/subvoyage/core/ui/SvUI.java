package subvoyage.core.ui;

import arc.scene.ui.layout.WidgetGroup;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.ui.dialogs.PlanetDialog;
import subvoyage.core.ui.advancements.*;
import subvoyage.util.All;

public class SvUI {
    public static AdvancementsDialog advancements;
    public static PlanetQualityDialog planetQuality;

    public static AdvancementToastFragment advancementFrag;

    public static WidgetGroup overGroup;

    public static void load() {
        advancements = new AdvancementsDialog();
        planetQuality = new PlanetQualityDialog();

        Advancement.load();
        AdvancementsHook.load();

        PlanetDialog.debugSelect = All.isDev();

        advancementFrag = new AdvancementToastFragment();
        advancementFrag.build(overGroup);
    }
}
