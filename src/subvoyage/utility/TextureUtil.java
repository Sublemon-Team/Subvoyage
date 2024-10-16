package subvoyage.utility;

import arc.Core;
import arc.graphics.Texture;

public class TextureUtil {
    public static Texture crop(String name) {
        return new Texture(Core.atlas.getPixmap(name).crop());
    };
}