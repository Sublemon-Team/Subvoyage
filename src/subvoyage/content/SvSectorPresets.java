package subvoyage.content;

import arc.struct.Seq;
import mindustry.content.Planets;
import mindustry.content.SectorPresets;
import mindustry.type.*;

import static subvoyage.content.SvPlanets.atlacian;

public class SvSectorPresets {
    public static SectorPreset divingPoint, gustyRidges, noxiousTarn, rapidEncounter;
    public static Seq<SectorPreset> all = Seq.with();

    public static void load() {

        //TODO UNCOMMENT WHEN SECTORS ARE RESTORED
        divingPoint = new SectorPreset("divingPoint",atlacian,13) {{
            alwaysUnlocked = true;

            overrideLaunchDefaults = true;
            captureWave = 1;

            addStartingItems = false;
            allowLaunchLoadout = false;


            difficulty = 0;

            rules = (r) -> {
                r.attackMode = false;
                r.winWave = 1;
                r.loadout = Seq.with();
                //r.infiniteResources = true;
            };
        }};


        gustyRidges = new SectorPreset("gustyRidges",atlacian,25) {{
            difficulty = 2;
            captureWave = 20;

            rules = (r) -> {
                r.attackMode = false;
                r.winWave = 20;
            };
        }};

        noxiousTarn = new SectorPreset("noxiousTarn",atlacian,31) {{
            difficulty = 3;
            captureWave = 0;
            rules = (r) -> {
                r.attackMode = true;
                r.enemyCoreBuildRadius = 400f;
            };
        }};

        rapidEncounter = new SectorPreset("rapidEncounter",atlacian,2) {{
            difficulty = 4;
            captureWave = 0;
            rules = (r) -> {
                r.attackMode = true;
                r.enemyCoreBuildRadius = 450f;
            };
        }};

        all.addAll(divingPoint,gustyRidges,noxiousTarn,rapidEncounter);
    }
}
