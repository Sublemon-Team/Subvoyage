package oceanic_dust.sectors;

import mindustry.type.*;

import static oceanic_dust.planets.ODPlanets.atlacian;

public class ODSectorPresets {
    public static SectorPreset divingPoint;

    public static void load() {
        divingPoint = new SectorPreset("divingpoint",atlacian,7) {{
            alwaysUnlocked = true;

            overrideLaunchDefaults = true;
            captureWave = 0;


            difficulty = 0;

            rules = (r) -> {
                r.attackMode = false;
                r.canGameOver = false;
            };

            startWaveTimeMultiplier = 0f;
        }};
    }
}
