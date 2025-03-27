package subvoyage.core;

import arc.Core;
import arc.func.Boolc;
import mindustry.content.TechTree;
import mindustry.gen.Icon;
import mindustry.type.Planet;
import mindustry.ui.dialogs.SettingsMenuDialog;
import subvoyage.content.SvUnits;
import subvoyage.core.ui.advancements.Advancement;
import subvoyage.core.ui.settings.BannerPref;
import subvoyage.core.ui.settings.ButtonPref;
import subvoyage.core.ui.settings.CheckIconSetting;
import subvoyage.core.ui.settings.SliderIconSetting;

import static arc.Core.bundle;
import static arc.Core.settings;
import static mindustry.Vars.*;
import static subvoyage.Subvoyage.ID;
import static subvoyage.content.world.SvPlanets.atlacian;

public class SvSettings {
    public static void load() {
        ui.settings.addCategory(bundle.get("setting.subvoyage-title"),"subvoyage-icon",t -> {
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
            t.pref(new ButtonPref(Core.bundle.get("sv-clear-advancements"),Icon.trash,() -> {
                ui.showConfirm("@confirm", "@settings.sv-clear-advancements.confirm", SvSettings::resetAdvancements);
            }));

            sliderPref(t,ID+"-atlacian","subvoyage-planet-divisions",
                    6,5,8,
                    String::valueOf);
            /*sliderPref(t,ID+"-liquid-hard-water","sv-metal-fuming-opacity",
                    75,0,100,
                    s -> s == 0 ? bundle.get("off") : s+"%");*/

            checkPref(t,ID+"-energy-dock-ship","subvoyage-autoupdate",true);
            checkPref(t,ID+"-leeft-uwu","subvoyage-leeft-uwu",false, SvUnits::loadUwu);
            checkPref(t,ID+"-phosphide-wall-large-full","subvoyage-wall-tiling",true);

            checkPref(t, ID+"-laser-projector","subvoyage-laser-shaders",true);
            checkPref(t, ID+"-power-bubble-node","subvoyage-power-bubble-shaders",true);

            SvUnits.loadUwu(unitUwu());
        });
    }

    public static boolean wallTiling() {
        return boolDef("wall-tiling",true);
    }
    public static boolean autoUpdate() {
        return boolDef("autoupdate",true);
    }
    public static boolean unitUwu() {
        return boolDef("leeft-uwu",false);
    }

    public static boolean bool(String key) {
        return settings.getBool("subvoyage"+"-"+key);
    }
    public static boolean boolDef(String key,boolean def) {
        return settings.getBool("subvoyage"+"-"+key,def);
    }
    public static void bool(String key,boolean bool) {
        settings.put("subvoyage"+"-"+key,bool);
    }
    public static int i(String key) {
        return settings.getInt("subvoyage"+"-"+key);
    }
    public static int iDef(String key, int def) {
        return settings.getInt("subvoyage"+"-"+key,def);
    }
    public static void i(String key, int val) {
        settings.put("subvoyage"+"-"+key,val);
    }

    public static void resetSaves(Planet planet) {
        planet.sectors.each(sector -> {
            if (sector.hasSave()) sector.save.delete();
        });
    }
    public static void resetTree(TechTree.TechNode root) {
        root.reset();
        root.content.clearUnlock();
        root.children.each(SvSettings::resetTree);
    }
    public static void resetAdvancements() {
        Advancement.all.each(Advancement::lock);
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
