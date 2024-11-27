package subvoyage.core.ui.settings;

import arc.Core;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.atlas;
import static arc.Core.settings;

public class SliderIconSetting extends SettingsMenuDialog.SettingsTable.SliderSetting {
    int def, min, max, step;
    String icon;
    SettingsMenuDialog.StringProcessor sp;
    public SliderIconSetting(String icon, String name, int def, int min, int max, int step, SettingsMenuDialog.StringProcessor s) {
        super(name, def, min, max, step, s);
        this.def = def;
        this.min = min;
        this.max = max;
        this.step = step;
        this.sp = s;
        this.icon = icon;
    }

    @Override
    public void add(SettingsMenuDialog.SettingsTable table) {
        Slider slider = new Slider(min, max, step, false);

        slider.setValue(settings.getInt(name));

        Label value = new Label("", Styles.outlineLabel);
        Table content = new Table();
        Image i = new Image(new TextureRegionDrawable(atlas.find(icon)));
        Cell<Image> ci = content.add(i);
        ci.padRight(3f);
        ci.align(Align.left);
        ci.margin(0);
        ci.width(32); ci.height(32);

        content.add(title, Styles.outlineLabel).left().growX().wrap();
        content.add(value).padLeft(10f).right();
        content.margin(3f, 33f, 3f, 33f);
        content.touchable = Touchable.disabled;

        slider.changed(() -> {
            settings.put(name, (int)slider.getValue());
            value.setText(sp.get((int)slider.getValue()));
        });

        slider.change();

        addDesc(table.stack(slider, content).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).left().padTop(4f).get());
        table.row();
    }
}
