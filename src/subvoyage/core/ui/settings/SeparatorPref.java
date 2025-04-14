package subvoyage.core.ui.settings;

import mindustry.graphics.Pal;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class SeparatorPref extends SettingsMenuDialog.SettingsTable.Setting {
    public SeparatorPref(String name) {
        super(name);
    }

    @Override
    public void add(SettingsMenuDialog.SettingsTable all) {
        all.add(title).growX().center().top().padTop(3f).color(Pal.accent);
        all.row();
        all.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
        all.row();
    }
}
