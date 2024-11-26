package subvoyage.core;

import arc.Core;
import arc.util.Http;
import arc.util.Log;
import arc.util.Structs;
import arc.util.Time;
import arc.util.serialization.Jval;
import mindustry.game.Team;
import mindustry.gen.Musics;
import mindustry.type.SectorPreset;
import subvoyage.Subvoyage;
import subvoyage.SubvoyageSettings;
import subvoyage.SvVars;
import subvoyage.content.SvSectorPresets;
import subvoyage.content.SvTeam;
import subvoyage.content.SvUnits;
import subvoyage.content.block.cat.SvProduction;
import subvoyage.content.sound.SvMusic;
import subvoyage.draw.visual.SvIcons;
import subvoyage.type.block.core.SubvoyageCoreBlock;
import subvoyage.type.block.production.WaterSifter;
import subvoyage.type.unit.ability.LegionfieldAbility;
import subvoyage.ui.SvUI;
import subvoyage.ui.advancements.Advancement;
import subvoyage.utility.Var;
import subvoyage.utility.VersionControl;

import java.util.Arrays;

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

        SvIcons.load();
        SvUnits.loadUwu(SubvoyageSettings.unitUwu());

        SvUI.load();
    }

    /*New game start*/
    public static void newGame() {
        var core = player.bestCore();
        if(core == null) return;
        if(!settings.getBool("skipcoreanimation") && !state.rules.pvp && state.rules.planet == atlacian){
            //beginLandMusic();
            beginLandCutscene();
        }
    }

    /*Landing*/
    public static void beginLandMusic() {
        SvMusic.theAtlacian.stop();
        if(settings.getInt("musicvol") > 0){
            Musics.land.stop();
            SvMusic.theAtlacian.play();
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
    }

    public static void gameUpdate() {
        if(player.team() == Team.sharded && state.rules.planet == atlacian)
            player.team(SvTeam.melius);

        LegionfieldAbility.update();
        if(!state.isPaused() && SubvoyageCoreBlock.cutscene) SubvoyageCoreBlock.landTime-=Time.delta;

        if(state.rules.planet == atlacian) Advancement.welcome.unlock();
        if(state.rules.planet == atlacian && beta) Advancement.beta.unlock();

        for (SectorPreset sect : SvSectorPresets.all) {
            if(sect.sector.isCaptured() && Advancement.get("sector_"+sect.name.replace("subvoyage-","")) != null) {
                Advancement.get("sector_"+sect.name.replace("subvoyage-","")).unlock();
            }
        }
    }
    public static void menuUpdate() {

    }

    /*Reset*/
    public static void reset() {
        SvVars.fumesMap.stop();
    }

    /*World Loading*/
    public static void worldLoad() {
        if(SvProduction.sifter instanceof WaterSifter sifter) sifter.worldReset();
        SvVars.fumesMap.recalc();
        SubvoyageCoreBlock.cutscene = false;
        SubvoyageCoreBlock.landTime = 0f;
    }

    /*Updating*/
    public static boolean beta = false;
    public static void checkUpdates() {
        boolean autoUpdate = SubvoyageSettings.autoUpdate();
        Log.info("[Subvoyage] Autoupdate: "+(autoUpdate ? "Enabled" : "Disabled"));
        if(autoUpdate) AutoUpdater.begin();
        Http.get(ghApi+"/repos/"+ Subvoyage.repo+"/releases/latest", res -> {
            var json = Jval.read(res.getResultAsString());
            String tagName = json.getString("tag_name");
            beta = Structs.contains(Subvoyage.versionControl.parseTagAttributes(tagName.replace("b","-beta")),(a) -> a.name().equals("BETA"));
        },(err) -> {});
    }

    static int currentVersion = 1;
    public static void checkChanges() {
        int previousVersion = settings.getInt("subvoyage-chver",0);
        Log.info("[Subvoyage] Previous revision: "+previousVersion);
        Log.info("[Subvoyage] Current revision: "+currentVersion);
        settings.put("subvoyage-chver",currentVersion);

        if(previousVersion >= currentVersion) return;
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
