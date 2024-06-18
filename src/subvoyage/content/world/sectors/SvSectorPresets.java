package subvoyage.content.world.sectors;

import arc.struct.Seq;
import mindustry.type.*;

import static subvoyage.content.world.planets.SvPlanets.atlacian;

public class SvSectorPresets {
    public static SectorPreset divingPoint, crystalShores, furtherInstallation;

    public static void load() {

        //TODO UNCOMMENT WHEN SECTORS ARE RESTORED
        divingPoint = new SectorPreset("divingPoint",atlacian,7) {{
            alwaysUnlocked = true;

            overrideLaunchDefaults = true;
            captureWave = 1;

            addStartingItems = false;
            allowLaunchLoadout = false;


            difficulty = 0;

            rules = (r) -> {
                r.attackMode = false;
                r.canGameOver = false;
                r.loadout = Seq.with();
                r.infiniteResources = true;
            };
        }};/*
        crystalShores = new SectorPreset("crystalShores",atlacian,15) {{
            overrideLaunchDefaults = false;
            rules = (r) -> {
                r.attackMode = false;
            };
            captureWave = 15;
            difficulty = 2;
        }};

        furtherInstallation = new SectorPreset("furtherInstallation",atlacian,3) {{
            overrideLaunchDefaults = false;
            rules = (r) -> {
                r.attackMode = false;
            };
            captureWave = 25;
            difficulty = 3;
        }};*/
    }
}
