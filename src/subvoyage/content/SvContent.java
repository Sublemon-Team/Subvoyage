package subvoyage.content;

import mindustry.world.Block;
import subvoyage.anno.LoadAnnoProcessor;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.block.cat.*;
import subvoyage.content.other.SvSchematics;
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
        SvSchematics.load();

        SvWeather.load();
    }
}
