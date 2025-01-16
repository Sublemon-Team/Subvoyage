package subvoyage.content;

import mindustry.world.Block;
import subvoyage.content.block.*;
import subvoyage.content.world.SvPlanets;
import subvoyage.content.world.SvSectorPresets;
import subvoyage.core.anno.LoadAnnoProcessor;
import subvoyage.content.other.SvTeam;
import subvoyage.content.world.SvWeather;
import subvoyage.content.other.SvLoadouts;
import subvoyage.content.ost.SvMusic;
import subvoyage.content.ost.SvSounds;

public class SvContent {

    public static void load() {
        SvTeam.load();

        SvMusic.load();
        SvSounds.load();

        SvItems.load();
        SvUnits.load();

        //SvEnvironment.load();
        SvBlocks.loadCat();

        SvPlanets.load();
        SvSectorPresets.load();
        SvLoadouts.load();

        SvWeather.load();
    }
}
