package subvoyage.content.world.sectors;

import arc.struct.Seq;
import mindustry.type.*;

import static subvoyage.content.world.planets.SvPlanets.atlacian;

public class SvSectorPresets {
    public static SectorPreset divingPoint, hillFacility;

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

        hillFacility = new SectorPreset("hillFacility",atlacian,25) {{
            difficulty = 2;
            captureWave = 1;
            rules = (r) -> {
                r.attackMode = false;
                r.canGameOver = false;
                r.loadout = Seq.with();
                r.winWave = 20;
            };
        }};
    }
}
