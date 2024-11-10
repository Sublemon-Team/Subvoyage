package subvoyage.content;

import mindustry.world.Block;
import subvoyage.anno.LoadAnnoProcessor;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.block.SvWorldBlocks;
import subvoyage.content.other.SvLoadouts;
import subvoyage.content.sound.SvMusic;
import subvoyage.content.sound.SvSounds;

public class SvContent {

    public static void load() {
        SvMusic.load();
        SvSounds.load();

        SvItems.load();
        SvUnits.load();

        SvWorldBlocks.load();
        SvBlocks.load();
        new Block("anno-loader") { //<- not a real block
            @Override
            public void load() {
                super.load();
                LoadAnnoProcessor.begin(SvBlocks.class);
            }
        };

        SvLoadouts.load();
        SvPlanets.load();

        SvSectorPresets.load();

        SvWeather.load();
    }
}
