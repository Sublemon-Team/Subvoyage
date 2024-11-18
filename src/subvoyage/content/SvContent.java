package subvoyage.content;

import subvoyage.content.block.SvBlocks;
import subvoyage.content.other.SvLoadouts;
import subvoyage.content.sound.SvMusic;
import subvoyage.content.sound.SvSounds;

public class SvContent {

    public static void load() {
        SvTeam.load();

        SvMusic.load();
        SvSounds.load();

        SvItems.load();
        SvUnits.load();

        //SvEnvironment.load();
        SvBlocks.loadCat();
        /*new Block("anno-loader") { //<- not a real block
            @Override
            public void load() {
                super.load();
                LoadAnnoProcessor.begin(SvBlocks.class);
            }
        };*/

        SvLoadouts.load();
        SvPlanets.load();

        SvSectorPresets.load();

        SvWeather.load();
    }
}
