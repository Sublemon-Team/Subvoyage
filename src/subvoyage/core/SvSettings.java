package subvoyage.core;

import arc.Core;
import arc.func.Boolc;
import arc.func.Prov;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import mindustry.content.TechTree;
import mindustry.gen.Icon;
import mindustry.type.Planet;
import mindustry.ui.dialogs.SettingsMenuDialog;
import subvoyage.content.SvUnits;
import subvoyage.core.ui.SvUI;
import subvoyage.core.ui.advancements.Advancement;
import subvoyage.core.ui.settings.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static subvoyage.Subvoyage.ID;
import static subvoyage.content.world.SvPlanets.atlacian;

public class SvSettings {
    public static void load() {
        ui.settings.addCategory(bundle.get("setting.subvoyage-title"),"subvoyage-icon",t -> {
            table = t;

            t.pref(new BannerPref(ID+"-modname",256));

            separator("subvoyage-data");

            buttonPref("subvoyage-clear-campaign",Icon.save,() -> {
                ui.showConfirm("@confirm", "@settings.sv-clear-campaign.confirm", () -> {
                    resetSaves(atlacian);
                    ui.showInfoOnHidden("@settings.sv-clear-campaign-close.confirm", () -> {
                        Core.app.exit();
                    });
                });
            });
            buttonPref("subvoyage-clear-tech-tree",Icon.tree,() -> {
                ui.showConfirm("@confirm", "@settings.sv-clear-tech-tree.confirm", () -> resetTree(atlacian.techTree));
            });
            buttonPref("subvoyage-clear-advancements",Icon.modePvp,() -> {
                ui.showConfirm("@confirm", "@settings.sv-clear-advancements.confirm", SvSettings::resetAdvancements);
            });

            separator("subvoyage-graphics");

            var planetQuality = bundle.get("setting.subvoyage-planet-divisions.name");
            var lowQuality = bundle.get("setting.subvoyage-planet-divisions.1");
            var mediumQuality = bundle.get("setting.subvoyage-planet-divisions.2");
            var highQuality = bundle.get("setting.subvoyage-planet-divisions.3");
            buttonPref("subvoyage-planet-divisions",() -> planetQuality + ":\n" + bundle.get("setting.subvoyage-planet-divisions."+i("planet-divisions")),new TextureRegionDrawable(atlas.find(ID+"-atlacian")),
                    () -> {
                        SvUI.planetQuality.showDialog();
                    });
            settings.defaults("subvoyage-planet-divisions", 6);

            switchPref(ID+"-overdrive-projector","subvoyage-drawer-mode",true);

            switchPref(ID+"-laser-projector","subvoyage-laser-shaders",true);
            switchPref(ID+"-power-bubble-node","subvoyage-power-bubble-shaders",true);
            switchPref(ID+"-phosphide-wall-large-full","subvoyage-wall-tiling",true);

            separator("subvoyage-other");

            switchPref(ID+"-energy-dock-ship","subvoyage-autoupdate",true);
            buttonPref("subvoyage-autoupdate-btn",null,UpdateManager::begin);

            separator("subvoyage-fun");

            switchPref(ID+"-leeft-uwu","subvoyage-leeft-uwu",false, SvUnits::loadUwu);

            SvUnits.loadUwu(unitUwu());
        });
    }

    public static boolean drawerMode() {return boolDef("drawer-mode",true);}
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

    static SettingsMenuDialog.SettingsTable table;

    static void separator(String name) {
        table.pref(new SeparatorPref(name));
    }
    static void buttonPref(String name, Drawable drawable, Runnable listener) {
        table.pref(new ButtonPref(name, drawable, listener));
    }
    static void buttonPref(String name, Prov<CharSequence> title, Drawable drawable, Runnable listener) {
        table.pref(new ButtonPref(name, drawable, title, listener));
    }
    static void sliderPref(String ico, String name, int def, int min, int max, SettingsMenuDialog.StringProcessor p) {
        table.pref(new SliderIconSetting(ico,name, def,min,max,1,p));
        settings.defaults(name, def);
    }
    static void switchPref(String ico, String name, boolean def) {
        table.pref(new SwitchPref(ico,name,def,null));
        settings.defaults(name, def);
    }
    static void switchPref(String ico, String name, boolean def, Boolc changed) {
        table.pref(new SwitchPref(ico,name,def,changed));
        settings.defaults(name, def);
    }
    static void checkPref(String ico, String name, boolean def) {
        table.pref(new CheckIconSetting(ico,name,def,null));
        settings.defaults(name, def);
    }
    static void checkPref(String ico, String name, boolean def, Boolc changed) {
        table.pref(new CheckIconSetting(ico,name,def,changed));
        settings.defaults(name, def);
    }
}
