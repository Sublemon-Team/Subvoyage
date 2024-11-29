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
        new Block("anno-loader") { //<- not a real block
            @Override
            public void load() {
                super.load();
                LoadAnnoProcessor.begin(SvCrafting.class);
                LoadAnnoProcessor.begin(SvDefense.class);
                LoadAnnoProcessor.begin(SvDistribution.class);
                LoadAnnoProcessor.begin(SvEnvironment.class);
                LoadAnnoProcessor.begin(SvLaser.class);
                LoadAnnoProcessor.begin(SvPayload.class);
                LoadAnnoProcessor.begin(SvPower.class);
                LoadAnnoProcessor.begin(SvProduction.class);
                LoadAnnoProcessor.begin(SvSpecial.class);
                LoadAnnoProcessor.begin(SvStorage.class);
                LoadAnnoProcessor.begin(SvTurret.class);
            }
        };

        SvPlanets.load();
        SvSectorPresets.load();
        SvLoadouts.load();

        SvWeather.load();
    }
}
