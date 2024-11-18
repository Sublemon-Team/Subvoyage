package subvoyage.ui.setting;

import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Cell;
import arc.util.Align;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.atlas;

public class IconPref extends SettingsMenuDialog.SettingsTable.Setting {
    float width;

    public IconPref(String name, float width){
        super(name);
        this.width = width;
    }

    @Override
    public void add(SettingsMenuDialog.SettingsTable table){
        Image i = new Image(new TextureRegionDrawable(atlas.find(name)));
        Cell<Image> ci = table.add(i);
        ci.padRight(3f);
        ci.align(Align.right);
        ci.marginRight(width);
        if(width > 0){
            ci.margin(0);
            ci.width(width);
            //noinspection SuspiciousNameCombination
            ci.height(width);
        }else{
            ci.grow();
        }
    }
}
