package subvoyage.ui.setting;

import arc.Core;
import arc.func.Boolc;
import arc.scene.Element;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.CheckBox;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.atlas;
import static arc.Core.settings;

public class CheckIconSetting extends SettingsMenuDialog.SettingsTable.CheckSetting {
    boolean def;
    Boolc changed;
    String ico;

    public CheckIconSetting(String ico, String name, boolean def, Boolc changed) {
        super(name, def, changed);
        this.def = def;
        this.changed = changed;
        this.ico = ico;
    }

    @Override
    public void add(SettingsMenuDialog.SettingsTable table) {
        CheckBox box = new CheckBox(title);

        Table both = new Table();


        Image i = new Image(new TextureRegionDrawable(atlas.find(ico)));

        box.update(() -> box.setChecked(settings.getBool(name)));

        box.changed(() -> {
            settings.put(name, box.isChecked());
            if(changed != null){
                changed.get(box.isChecked());
            }
        });

        box.left();
        both.add(i).padRight(3f).left().width(32).height(32);
        both.left();
        Element a = both.add(box).left().padTop(3f).get();
        addDesc(table.add(both).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).left().padTop(4f).get());
        table.row();
    }
}
