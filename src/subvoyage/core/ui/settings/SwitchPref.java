package subvoyage.core.ui.settings;

import arc.Core;
import arc.func.Boolc;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.atlas;
import static arc.Core.settings;
import static mindustry.Vars.ui;

public class SwitchPref extends SettingsMenuDialog.SettingsTable.CheckSetting {
    String icon;
    Boolc changed;
    public SwitchPref(String icon, String name, boolean def, Boolc changed) {
        super(name, def, changed);
        this.icon = icon;
        this.changed = changed;
    }

    @Override
    public void add(SettingsMenuDialog.SettingsTable table) {
        Table t = new Table();
        Image i = new Image(new TextureRegionDrawable(atlas.find(icon)));

        int width = (int) Math.min(Core.graphics.getWidth() / 1.2f, 460f);

        t.add(i).padRight(3f).width(32).height(32);

        t.add(title).padLeft(5f).width(width*0.4f-20f).wrap();

        t.table(t2 -> {
            t2.background(Styles.defaultSlider.background);

            TextButton falseBtn, trueBtn;

            Runnable[] upt = new Runnable[1];

            falseBtn = t2.button(Core.bundle.get("setting." + name + ".1"), Styles.flatTogglet, () -> {
                settings.put(name, false);
                if (upt[0] != null) upt[0].run();
            }).height(30f).width(width*0.3f-20f).get();

            trueBtn = t2.button(Core.bundle.get("setting." + name + ".2"), Styles.flatTogglet, () -> {
                settings.put(name, true);
                if (upt[0] != null) upt[0].run();
            }).height(30f).width(width*0.3f-20f).get();

            upt[0] = () -> {
                boolean value = settings.getBool(name);
                falseBtn.setChecked(!value);
                trueBtn.setChecked(value);

                if(changed != null) changed.get(value);
            };

            upt[0].run();
        }).pad(0f,8f,0f,8f).growX();

        addDesc(table.add(t).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).left().padTop(2f).get());

        table.row();

        /*CheckBox box = new CheckBox(title);

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
        table.row();*/
    }
}
