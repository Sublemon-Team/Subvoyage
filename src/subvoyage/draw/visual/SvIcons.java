package subvoyage.draw.visual;

import arc.scene.style.TextureRegionDrawable;

import static arc.Core.atlas;
import static mindustry.gen.Icon.icons;

public class SvIcons {
    public static TextureRegionDrawable atlacian;
    public static void load() {
        atlacian = atlas.getDrawable("subvoyage-atlacian");
        icons.put("atlacian", atlacian);
    }
}
