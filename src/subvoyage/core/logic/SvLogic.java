package subvoyage.core.logic;

import arc.Core;
import arc.util.Http;
import arc.util.Log;
import arc.util.Structs;
import arc.util.Time;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.game.Team;
import mindustry.gen.Musics;
import mindustry.type.SectorPreset;
import subvoyage.Subvoyage;
import subvoyage.content.SvItems;
import subvoyage.core.SvSettings;
import subvoyage.core.SvVars;
import subvoyage.content.world.SvSectorPresets;
import subvoyage.content.other.SvTeam;
import subvoyage.content.SvUnits;
import subvoyage.content.block.SvProduction;
import subvoyage.content.ost.SvMusic;
import subvoyage.core.ui.SvIcons;
import subvoyage.core.UpdateManager;
import subvoyage.type.block.storage.core.AtlacianCore;
import subvoyage.type.block.production.Sifter;
import subvoyage.type.unit.ability.LegionfieldAbility;
import subvoyage.core.ui.SvUI;
import subvoyage.core.ui.advancements.Advancement;
import subvoyage.util.Var;

import static arc.Core.bundle;
import static arc.Core.settings;
import static mindustry.Vars.*;
import static subvoyage.content.world.SvPlanets.atlacian;

public class SvLogic {

    /*Client Load*/
    public static void clientLoad() {
        //checkUpdates();
        checkChanges();

        UpdateManager.checkFico();

        control.input.addLock(AtlacianCore.lock);
        bundle.getProperties().put("sector.curcapturefake","[lightgray]"+bundle.get("sector.curcapture")+"[]");

        SvIcons.load();
        SvUnits.loadUwu(SvSettings.unitUwu());

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
        AtlacianCore.cutscene = true;
        AtlacianCore.landTime = 160f;
        Time.run(160f,() -> AtlacianCore.cutscene = false);
    }

    /*Update*/
    public static void update() {
        if(state.isGame()) gameUpdate();
        if(state.isMenu()) menuUpdate();
        SvVars.atlacianMapControl.update();

        try {
            if(state.rules.waves) {
                state.rules.objectiveFlags.add("wave"+state.wave);
            }
        } catch (Exception e) {

        }
    }

    public static void gameUpdate() {
        if(player.team() == Team.sharded && state.rules.planet == atlacian)
            player.team(SvTeam.melius);

        LegionfieldAbility.update();
        if(!state.isPaused() && AtlacianCore.cutscene) AtlacianCore.landTime-=Time.delta;

        if(state.rules.planet == atlacian) Advancement.welcome.unlock();
        if(state.rules.planet == atlacian && beta) Advancement.beta.unlock();

        for (SectorPreset sect : SvSectorPresets.all) {
            String id = "sector_" + sect.name.replace("subvoyage-", "").replace("-", "_");
            if(sect.sector.isCaptured() && Advancement.get(id) != null) {
                Advancement.get(id).unlock();
            }
        }

        if(state.getSector() != null && state.getSector().preset == SvSectorPresets.segment) {
            if(state.wave > 100) Advancement.the_segment_hundred_wave.unlock();
        }

        if(SvItems.hardWater.unlocked())
            Advancement.hard_water.unlock();
    }
    public static void menuUpdate() {

    }

    /*Reset*/
    public static void reset() {
        SvVars.atlacianMapControl.stop();
    }

    /*World Loading*/
    public static void worldLoad() {
        if(SvProduction.sifter instanceof Sifter sifter) sifter.worldReset();
        SvVars.atlacianMapControl.recalc();
        AtlacianCore.cutscene = false;
        AtlacianCore.landTime = 0f;
    }

    /*Updating*/
    public static boolean beta = false;
    public static void checkUpdates() {
        boolean autoUpdate = SvSettings.autoUpdate();
        Log.info("[Subvoyage] Autoupdate: "+(autoUpdate ? "Enabled" : "Disabled"));
        if(autoUpdate) UpdateManager.begin();
    }

    static int currentVersion = 1;
    public static void checkChanges() {
        Var<Integer> prev = Var.stgInt("update-idx",0);

        Log.info("[Subvoyage] Previous revision: "+prev.val);
        Log.info("[Subvoyage] Current revision: "+currentVersion);

        SvSettings.i("update-idx",currentVersion);

        if(prev.val >= currentVersion) return;

        ui.showInfoOnHidden("@settings.sv-update-id.confirm", () -> {
            if(prev.val == 0) {
                SvSettings.resetSaves(atlacian);
                SvSettings.resetTree(atlacian.techTree);

                Advancement.beta.unlock();

                prev.val = 1;
            }
            Core.app.exit();
        });
    };
}
