package oceanic_dust.blocks.content.world.sectors;

import arc.struct.Seq;
import mindustry.type.*;

import static oceanic_dust.blocks.content.world.planets.ODPlanets.atlacian;

public class ODSectorPresets {
    public static SectorPreset divingPoint, crystalShores;

    public static void load() {
        divingPoint = new SectorPreset("divingpoint",atlacian,7) {{
            alwaysUnlocked = true;

            overrideLaunchDefaults = true;
            captureWave = 0;


            addStartingItems = false;
            difficulty = 0;

            rules = (r) -> {
                r.attackMode = false;
                r.canGameOver = false;
                r.loadout = new Seq<>();
            };

            startWaveTimeMultiplier = 0f;
        }};
        crystalShores = new SectorPreset("crystalShores",atlacian,15) {{
            overrideLaunchDefaults = false;
            rules = (r) -> {
                r.attackMode = false;
            };
        }};
    }
}
