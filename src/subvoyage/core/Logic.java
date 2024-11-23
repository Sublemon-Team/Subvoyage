package subvoyage.core;

import arc.Core;
import arc.util.Log;
import arc.util.Time;
import mindustry.game.Team;
import mindustry.gen.Musics;
import subvoyage.SubvoyageSettings;
import subvoyage.SvVars;
import subvoyage.content.SvTeam;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.sound.SvMusic;
import subvoyage.type.block.core.SubvoyageCoreBlock;
import subvoyage.type.block.production.WaterSifter;
import subvoyage.type.unit.ability.LegionfieldAbility;
import subvoyage.utility.Var;

import static arc.Core.bundle;
import static arc.Core.settings;
import static mindustry.Vars.*;
import static subvoyage.content.SvPlanets.atlacian;

public class Logic {

    /*Client Load*/
    public static void clientLoad() {
        checkUpdates();
        checkChanges();
        control.input.addLock(SubvoyageCoreBlock.lock);
        bundle.getProperties().put("sector.curcapturefake","[lightgray]"+bundle.get("sector.curcapture")+"[]");
    }

    /*New game start*/
    public static void newGame() {
        var core = player.bestCore();
        if(core == null) return;
        if(!settings.getBool("skipcoreanimation") && !state.rules.pvp && state.rules.planet == atlacian){
            beginLandMusic();
            beginLandCutscene();
        }
    }

    /*Landing*/
    public static void beginLandMusic() {
        SvMusic.atlacianLand.stop();
        if(settings.getInt("musicvol") > 0){
            Musics.land.stop();
            SvMusic.atlacianLand.play();
        }
    }

    public static void beginLandCutscene() {
        SubvoyageCoreBlock.cutscene = true;
        SubvoyageCoreBlock.landTime = 160f;
        Time.run(160f,() -> SubvoyageCoreBlock.cutscene = false);
    }

    /*Update*/
    public static void update() {
        if(state.isGame()) gameUpdate();
        if(state.isMenu()) menuUpdate();
        SvVars.fumesMap.update();

        SvMusic.atlacianLand.setVolume(settings.getInt("musicvol") / 100f);
    }

    public static void gameUpdate() {
        if(player.team() == Team.sharded && state.rules.planet == atlacian)
            player.team(SvTeam.melius);

        if(SvMusic.atlacianLand.isPlaying()) {
            SvMusic.atlacianLand.pause(false);

            control.sound.stop();
        }
        LegionfieldAbility.update();
        if(!state.isPaused() && SubvoyageCoreBlock.cutscene) SubvoyageCoreBlock.landTime-=Time.delta;
    }
    public static void menuUpdate() {
        if(SvMusic.atlacianLand.isPlaying()) {
            SvMusic.atlacianLand.pause(true);
        }
    }

    /*Reset*/
    public static void reset() {
        SvVars.fumesMap.stop();
    }

    /*World Loading*/
    public static void worldLoad() {
        if(SvBlocks.waterSifter instanceof WaterSifter sifter) sifter.worldReset();
        SvVars.fumesMap.recalc();
        SubvoyageCoreBlock.cutscene = false;
        SubvoyageCoreBlock.landTime = 0f;
    }


    /*Updating*/
    public static void checkUpdates() {
        boolean autoUpdate = SubvoyageSettings.autoUpdate();
        Log.info("[Subvoyage] Autoupdate: "+(autoUpdate ? "Enabled" : "Disabled"));
        if(autoUpdate) AutoUpdater.begin();
    }

    static int currentVersion = 1;
    public static void checkChanges() {
        int previousVersion = settings.getInt("subvoyage-chver",0);
        Log.info("[Subvoyage] Previous ID: "+previousVersion);
        Log.info("[Subvoyage] Current ID: "+currentVersion);
        settings.put("subvoyage-chver",currentVersion);

        if(previousVersion == currentVersion) return;
        Var<Integer> prev = new Var<>(previousVersion);
        ui.showInfoOnHidden("@settings.sv-update-id.confirm", () -> {
            if(prev.val == 0) {
                SubvoyageSettings.resetSaves(atlacian);
                SubvoyageSettings.resetTree(atlacian.techTree);
                prev.val = 1;
            }
            Core.app.exit();
        });
    };
}
