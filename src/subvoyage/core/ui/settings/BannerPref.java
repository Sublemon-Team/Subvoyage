package subvoyage.core.ui.settings;

import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Cell;
import arc.util.Scaling;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.atlas;

public class BannerPref extends SettingsMenuDialog.SettingsTable.Setting {
    float width;

    public BannerPref(String name, float width){
        super(name);
        this.width = width;
    }

    @Override
    public void add(SettingsMenuDialog.SettingsTable table){
        Image i = new Image(new TextureRegionDrawable(atlas.find(name)), Scaling.fit);
        Cell<Image> ci = table.add(i).padTop(3f);

        if(width > 0){
            ci.width(width);
        }else{
            ci.grow();
        }

        table.row();
    }
}
