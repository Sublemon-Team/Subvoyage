package subvoyage.content;

import arc.struct.Seq;
import mindustry.type.*;
import subvoyage.content.block.SvBlocks;

import static subvoyage.content.SvPlanets.atlacian;

public class SvSectorPresets {
    public static SectorPreset dive, ridges, tarn, encounter, hedge;
    public static Seq<SectorPreset> all = Seq.with();

    public static void load() {

        //TODO UNCOMMENT WHEN SECTORS ARE RESTORED
        dive = new SectorPreset("divingPoint",atlacian,13) {{
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


        ridges = new SectorPreset("gustyRidges",atlacian,25) {{
            difficulty = 2;
            captureWave = 20;

            rules = (r) -> {
                r.attackMode = false;
                r.winWave = 20;
            };
        }};

        tarn = new SectorPreset("noxiousTarn",atlacian,31) {{
            difficulty = 3;
            captureWave = 0;
            rules = (r) -> {
                r.attackMode = true;
                r.enemyCoreBuildRadius = 400f;
            };
        }};

        encounter = new SectorPreset("rapidEncounter",atlacian,2) {{
            difficulty = 4;
            captureWave = 0;
            rules = (r) -> {
                r.attackMode = true;
                r.enemyCoreBuildRadius = 450f;
            };
        }};

        hedge = new SectorPreset("hedge",atlacian,79) {{
            difficulty = 4;
            captureWave = 0;
            rules = (r) -> {
                r.attackMode = true;
                r.enemyCoreBuildRadius = 480f;
                r.bannedBlocks.addAll(SvBlocks.helicopterFabricator,SvBlocks.helicopterRefabricator);
            };
        }};


        all.addAll(dive, ridges, tarn, hedge, encounter);
    }
}
