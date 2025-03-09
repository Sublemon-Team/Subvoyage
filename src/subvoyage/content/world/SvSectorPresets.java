package subvoyage.content.world;

import arc.struct.Seq;
import mindustry.type.*;
import subvoyage.type.world.SvSectorPreset;

import static subvoyage.content.world.SvPlanets.atlacian;

public class SvSectorPresets {
    public static SectorPreset submerging;
    public static Seq<SectorPreset> all = Seq.with();

    public static void load() {
        /*submerging = new SvSectorPreset("dive",atlacian,221,(state) -> {

        }) {{
            alwaysUnlocked = true;

            overrideLaunchDefaults = true;

            addStartingItems = false;
            difficulty = 1;

            rules = (r) -> {
                r.loadout = Seq.with();
            };
        }};*/
        all.addAll();
    }
}
