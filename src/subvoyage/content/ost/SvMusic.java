package subvoyage.content.ost;

import arc.Core;
import arc.Events;
import arc.audio.Music;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.gen.Musics;
import subvoyage.content.world.SvPlanets;
import subvoyage.content.block.SvStorage;

public class SvMusic {
    public static Music land;
    public static Music theAtlacian = new Music();
    public static Music ambient1 = new Music();
    public static Music ambient2 = new Music();
    public static Music ambient3 = new Music();
    public static Music atlLand = new Music();

    private static final ObjectMap<String, Seq<Music>> musicSets = new ObjectMap<>();


    public static void load() {
        land = Musics.land;
        Core.assets.load("music/atlacian.ogg", Music.class).loaded = (a) -> {
            theAtlacian = a;
        };
        Core.assets.load("music/atl_land.ogg", Music.class).loaded = (a) -> {
            atlLand = a;
        };
        Core.assets.load("music/atl-ambient1.ogg", Music.class).loaded = (a) -> {
            ambient1 = a;
        };
        Core.assets.load("music/atl-ambient2.ogg", Music.class).loaded = (a) -> {
            ambient2 = a;
        };
        Core.assets.load("music/atl-ambient3.ogg", Music.class).loaded = (a) -> {
            ambient3 = a;
        };

        musicSets.put("vanillaAmbient",new Seq<>(Vars.control.sound.ambientMusic));
        musicSets.put("atlacianAmbient", Seq.with(ambient1,ambient2,ambient3,theAtlacian));
        musicSets.put("atlacianDark", Seq.with(ambient1,ambient2,ambient3,theAtlacian));
        musicSets.put("atlacianBoss", Seq.with());

        Events.on(EventType.WorldLoadEvent.class, e -> {
            updateLandMusic();
            updatePlanetMusic();
        });
    }

    /** Updates landing music based on core block type. */
    private static void updateLandMusic() {
        Vars.state.rules.defaultTeam.cores().each(core ->
                Musics.land = (core.block == SvStorage.corePuffer)
                        ? atlLand
                        : land);
    }
    private static void updatePlanetMusic() {
        if (Vars.state.rules.planet != Planets.sun) {
            String prefix = Vars.state.rules.planet == SvPlanets.atlacian ? "atlacian" : "vanilla";
            setMusicSet(prefix + "Ambient", Vars.control.sound.ambientMusic);
            setMusicSet(prefix + "Dark", Vars.control.sound.darkMusic);
            setMusicSet(prefix + "Boss", Vars.control.sound.bossMusic);
        } else {
            // For 'any' environment (Planets.sun), mix mod and vanilla music
            mixMusic();
        }
    }
    private static void setMusicSet(String setName, Seq<Music> target) {
        Seq<Music> set = musicSets.get(setName);
        if (set != null) {
            target.set(set);
        }
    }
    private static void mixMusic() {
        mixMusicSets("vanillaAmbient", "atlacianAmbient", Vars.control.sound.ambientMusic);
        mixMusicSets("vanillaDark", "atlacianDark", Vars.control.sound.darkMusic);
        mixMusicSets("vanillaBoss", "atlacianBoss", Vars.control.sound.bossMusic);
    }
    private static void mixMusicSets(String vanillaSetName, String modSetName, Seq<Music> target) {
        Seq<Music> vanillaSet = musicSets.get(vanillaSetName);
        Seq<Music> modSet = musicSets.get(modSetName);
        if (vanillaSet != null && modSet != null) {
            target.clear();
            target.addAll(vanillaSet);
            target.addAll(modSet);
        }
    }
}
