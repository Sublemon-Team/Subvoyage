package subvoyage.core.ui.settings;

import arc.Core;
import arc.func.Prov;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.scene.utils.Elem;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.atlas;
import static arc.Core.settings;

public class ButtonPref extends SettingsMenuDialog.SettingsTable.Setting {
    Drawable icon;
    Runnable listener;

    public Prov<CharSequence> title = () -> Core.bundle.get("setting." + name);

    public ButtonPref(String name, Drawable icon, Runnable listener){
        super(name);
        this.icon = icon;
        this.listener = listener;
    }
    public ButtonPref(String name, String icon, Runnable listener){
        this(name, new TextureRegionDrawable(atlas.find(icon)), listener);
    }
    public ButtonPref(String name, Drawable icon, Prov<CharSequence> title, Runnable listener){
        this(name, icon, listener);
        this.title = title;
    }


    @Override
    public void add(SettingsMenuDialog.SettingsTable table){
        Table t = new Table();
        Image i = new Image(icon);

        int width = (int) Math.min(Core.graphics.getWidth() / 1.2f, 460f);

        t.add(i).padRight(3f).width(32).height(32);
        t.label(title).padLeft(5f).width(width*0.5f-20f).wrap();

        t.button(Core.bundle.get("setting." + name + ".btn"), Styles.flatBordert, () -> {
            if (listener != null) listener.run();
        }).height(30f).width(width*0.5f-26f).get();

        table.add(t).left().padTop(6f).get();
        table.row();
    }
}
