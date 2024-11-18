package subvoyage;

import arc.Core;
import arc.func.Boolc;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Saves;
import mindustry.gen.Icon;
import mindustry.type.Planet;
import mindustry.ui.dialogs.SettingsMenuDialog;
import subvoyage.content.SvUnits;
import subvoyage.ui.setting.BannerPref;
import subvoyage.ui.setting.ButtonPref;
import subvoyage.ui.setting.CheckIconSetting;
import subvoyage.ui.setting.SliderIconSetting;

import static arc.Core.bundle;
import static arc.Core.settings;
import static mindustry.Vars.*;
import static subvoyage.Subvoyage.ID;
import static subvoyage.content.SvPlanets.atlacian;

public class SubvoyageSettings {
    public static void load() {
        ui.settings.addCategory(bundle.get("setting.sv-title"),"subvoyage-icon",t -> {
            t.pref(new BannerPref(ID+"-modname",256));

            t.pref(new ButtonPref(Core.bundle.get("sv-clear-campaign"), Icon.trash,() -> {
                ui.showConfirm("@confirm", "@settings.sv-clear-campaign.confirm", () -> {
                    resetSaves(atlacian);
                    ui.showInfoOnHidden("@settings.sv-clear-campaign-close.confirm", () -> {
                        Core.app.exit();
                    });
                });
            }));
            t.pref(new ButtonPref(Core.bundle.get("sv-clear-tech-tree"),Icon.trash,() -> {
                ui.showConfirm("@confirm", "@settings.sv-clear-tech-tree.confirm", () -> resetTree(atlacian.techTree));
            }));

            sliderPref(t,ID+"-offload-core-ico","sv-offload-shield-sides",
                    6,3,10,
                    s -> s == 10 ? bundle.get("circle") : s+"");
            sliderPref(t,ID+"-liquid-hard-water","sv-metal-fuming-opacity",
                    75,0,100,
                    s -> s == 0 ? bundle.get("off") : s+"%");

            checkPref(t,ID+"-energy-dock-ship","sv-autoupdate",true);
            checkPref(t,ID+"-leeft-uwu","sv-leeft-uwu",false, SvUnits::loadUwu);
            checkPref(t,ID+"-phosphide-wall-large-full","sv-wall-tiling",true);
        });

        SvUnits.loadUwu(unitUwu());
    }

    public static boolean wallTiling() {
        return bool("wall-tiling");
    }

    public static boolean autoUpdate() {
        return bool("autoupdate");
    }
    public static boolean unitUwu() {
        return bool("leeft-uwu");
    }

    public static boolean bool(String key) {
        return settings.getBool(ID+"-"+key);
    }

    public static void resetSaves(Planet planet) {
        planet.sectors.each(sector -> {
            if (sector.hasSave()) sector.save.delete();
        });
    }
    public static void resetTree(TechTree.TechNode root) {
        root.reset();
        root.content.clearUnlock();
        root.children.each(SubvoyageSettings::resetTree);
    }

    static void sliderPref(SettingsMenuDialog.SettingsTable t, String ico, String name, int def, int min, int max, SettingsMenuDialog.StringProcessor p) {
        t.pref(new SliderIconSetting(ico,name, def,min,max,1,p));
    }
    static void checkPref(SettingsMenuDialog.SettingsTable t, String ico, String name, boolean def) {
        t.pref(new CheckIconSetting(ico,name,def,null));
    }
    static void checkPref(SettingsMenuDialog.SettingsTable t, String ico, String name, boolean def, Boolc changed) {
        t.pref(new CheckIconSetting(ico,name,def,changed));
    }
}
