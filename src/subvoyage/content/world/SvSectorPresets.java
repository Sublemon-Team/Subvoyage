package subvoyage.content.world;

import arc.struct.Seq;
import mindustry.game.MapObjectives;
import mindustry.type.*;
import subvoyage.type.world.SvSectorPreset;

import static subvoyage.content.world.SvPlanets.atlacian;

public class SvSectorPresets {
    public static SectorPreset dive;
    public static Seq<SectorPreset> all = Seq.with();

    public static void load() {
        dive = new SvSectorPreset("dive",atlacian,24,(state) -> {

        }) {{
            alwaysUnlocked = true;

            overrideLaunchDefaults = true;

            addStartingItems = false;
            difficulty = 1;

            rules = (r) -> {
                r.loadout = Seq.with();
            };
            atlacian.startSector = 24;
        }};
        all.addAll(dive);
        /*dive = new SectorPreset("dive",atlacian,13) {{
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

            atlacian.startSector = 13;
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
                r.bannedBlocks.addAll(SvPayload.helicopterFabricator,SvPayload.helicopterRefabricator);
            };
        }};*/


        //all.addAll(dive, ridges, tarn, hedge, encounter);
    }
}
