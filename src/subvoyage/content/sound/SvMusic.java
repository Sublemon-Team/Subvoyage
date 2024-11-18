package subvoyage.content.sound;

import arc.Core;
import arc.audio.Music;

public class SvMusic {
    public static Music atlacianLand = new Music();
    public static Music land = new Music();


    public static void load() {
        Core.assets.load("music/atlacian.ogg", Music.class).loaded = (a) -> {
            atlacianLand = a;
        };
        Core.assets.load("music/land.ogg", Music.class).loaded = (a) -> {
            land = a;
        };
    }
}
