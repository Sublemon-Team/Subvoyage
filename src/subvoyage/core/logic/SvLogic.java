package subvoyage.core.logic;

import arc.util.Log;
import mindustry.game.Team;
import mindustry.type.SectorPreset;
import subvoyage.Subvoyage;
import subvoyage.content.SvItems;
import subvoyage.core.SvSettings;
import subvoyage.core.SvVars;
import subvoyage.content.world.SvSectorPresets;
import subvoyage.content.other.SvTeam;
import subvoyage.content.SvUnits;
import subvoyage.content.block.SvProduction;
import subvoyage.core.ui.SvIcons;
import subvoyage.core.UpdateManager;
import subvoyage.type.block.production.Sifter;
import subvoyage.type.unit.ability.LegionfieldAbility;
import subvoyage.core.ui.SvUI;
import subvoyage.core.ui.advancements.Advancement;
import subvoyage.util.All;

import static mindustry.Vars.*;
import static subvoyage.content.world.SvPlanets.atlacian;
import static subvoyage.content.world.SvSectorPresets.*;
import static subvoyage.core.UpdateManager.checkFico;
import static subvoyage.core.ui.advancements.Advancement.unlock;

public class SvLogic {

    /*Client Load*/
    public static void clientLoad() {
        Subvoyage.currentTag = mods.getMod(Subvoyage.ID).meta.version;
        SvUI.load();

        checkUpdates();
        checkFico();

        SvIcons.load();
        SvUnits.loadUwu(SvSettings.unitUwu());
    }

    /*Update*/
    public static void update() {
        if(state.isGame()) gameUpdate();
        if(state.isMenu()) menuUpdate();

        All.unsafe(state.rules.waves,() -> state.rules.objectiveFlags.add("wave" + state.wave));
    }

    public static void gameUpdate() {
        if(player.team() == Team.sharded && state.rules.planet == atlacian)
            player.team(SvTeam.melius);

        LegionfieldAbility.update();

        All.unsafe(SvLogic::updateAdvancements);
    }
    public static void menuUpdate() {

    }

    public static void updateAdvancements() {
        if(state.getSector() == null) return;
        unlock(state.rules.planet == atlacian, Advancement.welcome);

        unlock(segment,state.wave > 60, Advancement.the_segment_hundred_wave);
        unlock(SvItems.hardWater.unlocked(), Advancement.hard_water);

        unlock(state.getSector().isCaptured(),
                sectorName(state.getSector().preset));

        unlock(state.getSector().isBeingPlayed() &&
                        state.rules.objectives.all.contains(e -> !e.isCompleted()),
                sectorName(state.getSector().preset,"f"));
    }
    private static String sectorName(SectorPreset preset) {
        return sectorName(preset,"");
    }
    private static String sectorName(SectorPreset preset, String postfix) {
        return "sector" + postfix + "_" + preset.name.replace("subvoyage-", "").replace("-", "_");
    }

    /*Reset*/
    public static void reset() {

    }

    /*World Loading*/
    public static void worldLoad() {
        if(SvProduction.sifter instanceof Sifter sifter) sifter.worldReset();
    }

    /*Updating*/
    public static void checkUpdates() {
        boolean autoUpdate = SvSettings.autoUpdate();
        Log.info("[Subvoyage] Autoupdate: "+(autoUpdate ? "Enabled" : "Disabled"));
        if(autoUpdate) UpdateManager.begin();
    }
}
