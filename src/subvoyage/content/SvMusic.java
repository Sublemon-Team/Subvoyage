package subvoyage.content;

import arc.Core;
import arc.audio.Music;

public class SvMusic {
    public static Music theAtlacian = new Music();
    public static Music land = new Music();


    public static void load() {
        Core.assets.load("music/atlacian.ogg", Music.class).loaded = (a) -> {
            theAtlacian = a;
        };
        Core.assets.load("music/land.ogg", Music.class).loaded = (a) -> {
            land = a;
        };
    }
}
