package subvoyage.content.world;

import arc.struct.Seq;
import mindustry.content.SectorPresets;
import mindustry.type.*;
import mindustry.ui.Fonts;
import subvoyage.type.world.SvSectorPreset;

import static subvoyage.content.world.SvPlanets.atlacian;

public class SvSectorPresets {
    public static SectorPreset thaw, construction;
    public static Seq<SectorPreset> all = Seq.with();

    public static void load() {
        thaw = new SvSectorPreset("thaw",atlacian,221,state -> {

        }) {{
            alwaysUnlocked = true;

            overrideLaunchDefaults = true;

            addStartingItems = false;
            difficulty = 1;

            rules = (r) -> {
                r.loadout = Seq.with();
            };
        }};

        construction = new SvSectorPreset("construction",atlacian,63,state -> {

        }) {{
            overrideLaunchDefaults = true;

            captureWave = 12;

            addStartingItems = false;
            difficulty = 1;

            rules = (r) -> {
                r.loadout = Seq.with();
            };
        }};

        all.addAll(thaw);
    }
}
