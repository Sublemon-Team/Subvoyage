package subvoyage.core;

import arc.util.Log;
import arc.util.Time;
import mindustry.content.Planets;
import mindustry.core.GameState;
import mindustry.gen.Musics;
import subvoyage.SvVars;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.sound.SvMusic;
import subvoyage.type.block.core.SubvoyageCoreBlock;
import subvoyage.type.block.production.WaterSifter;
import subvoyage.type.unit.ability.LegionfieldAbility;

import java.util.concurrent.atomic.AtomicBoolean;

import static arc.Core.settings;
import static mindustry.Vars.*;
import static subvoyage.content.SvPlanets.atlacian;

public class Logic {

    static AtomicBoolean hell = new AtomicBoolean(false);

    public static void clientLoad() {
        checkUpdates();
        checkHell();
        control.input.addLock(SubvoyageCoreBlock.lock);
    }

    public static void newGame() {
        var core = player.bestCore();
        if(core == null) return;
        if(!settings.getBool("skipcoreanimation") && !state.rules.pvp){
            SvMusic.theAtlacian.stop();
            if(settings.getInt("musicvol") > 0 && state.rules.planet == atlacian){
                Musics.land.stop();
                SvMusic.theAtlacian.play();
            }
        }
        SubvoyageCoreBlock.cutscene = true;
        SubvoyageCoreBlock.landTime = 160f;
        Time.run(160f,() -> {
            SubvoyageCoreBlock.cutscene = false;
        });
    }

    public static void update() {
        if(state.isGame()) gameUpdate();
        if(state.isMenu()) menuUpdate();
        SvVars.underwaterMap.update();
    }

    public static void gameUpdate() {
        if(SvMusic.theAtlacian.isPlaying()) {
            SvMusic.theAtlacian.pause(false);
            control.sound.stop();
        }
        LegionfieldAbility.update();
        if(!state.isPaused() && SubvoyageCoreBlock.cutscene) SubvoyageCoreBlock.landTime-=Time.delta;
    }
    public static void menuUpdate() {
        updateHell();
        if(SvMusic.theAtlacian.isPlaying()) {
            SvMusic.theAtlacian.pause(true);
        }
    }

    public static void reset() {
        SvVars.underwaterMap.stop();
    }

    public static void worldLoad() {
        if(SvBlocks.waterSifter instanceof WaterSifter sifter) sifter.worldReset();
        SvVars.underwaterMap.recalc();
        SubvoyageCoreBlock.cutscene = false;
        SubvoyageCoreBlock.landTime = 0f;
    }



    public static void updateHell() {
        if(hell.get()) {
            if(state.isMenu()) {
                world.loadSector(Planets.tantros.getLastSector());
                state.set(GameState.State.playing);
            }
        }
    }

    public static void checkHell() {
        if (settings.getBool("sv-dont")) {
            hell.set(true);
            world.loadSector(Planets.tantros.getLastSector());
            state.set(GameState.State.playing);
            settings.put("sv-dont", false);
        }
    }

    public static void checkUpdates() {
        boolean autoUpdate = settings.getBool("sv-autoupdate");
        Log.info("[Subvoyage] Autoupdate: "+(autoUpdate ? "Enabled" : "Disabled"));
        if(autoUpdate) AutoUpdater.begin();
    }
}
