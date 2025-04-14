package subvoyage.core.ui;

import arc.scene.ui.layout.WidgetGroup;
import mindustry.ui.dialogs.PlanetDialog;
import subvoyage.core.ui.advancements.*;

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

        advancementFrag = new AdvancementToastFragment();
        advancementFrag.build(overGroup);
    }
}
