package subvoyage.core.ui.advancements;

import arc.Core;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.Structs;
import subvoyage.core.SvSettings;
import subvoyage.core.ui.SvUI;

public class Advancement {
    public static Seq<Advancement> all = Seq.with();

    public static Advancement
            welcome,beta,
            sector_dive,
            unit_helio, unit_hydro, unit_rover,
            overstress,
            uwu
            ;

    public String id;
    public String title;
    public String description;
    public String icon;

    private Advancement() {}

    public static void load() {
        add(
                "welcome","sublemon_frog", // Launching Subvoyage
                "beta","sodilate-boulder1", // Launching Subvoyage

                "sector_dive","ceramic-burner", // Capturing Dive

                "unit_helio","lapetus-full", // Unit - Lapetus
                "unit_hydro","leeft-full", // Unit - Leeft
                "unit_rover","stunt-full", //Unit - Stunt

                "overstress","water-diffuser", //Overstressing Diffuser

                "uwu","leeft-uwu" //UwU Mode
        );
    }

    public static Advancement get(String id) {
        return all.find(a -> a.id.equals(id));
    };
    public static boolean unlocked(Advancement adv) {
        return SvSettings.bool(adv.id+"-adv-unlocked");
    };
    public static void unlock(Advancement adv) {
        if(!unlocked(adv)) toast(adv);
        SvSettings.bool(adv.id+"-adv-unlocked",true);
    };
    public static void lock(Advancement adv) {
        SvSettings.bool(adv.id+"-adv-unlocked",false);
    };
    public static void toast(Advancement adv) {
        Log.info("Advancement toast: "+adv.title);
        SvUI.advancementFrag.queue.addUnique(adv);
    }

    public static void add(String... all) {
        if(all.length % 2 != 0) return;
        for (int i = 0; i < all.length; i+=2) {
            String t = all[i];
            String ic = all[i+1];
            Advancement.all.add(from(t,ic));
        }
    }

    public static Advancement from(String title_, String icon_) {
        Advancement adv = new Advancement() {{
            this.id = title_;
            this.title = Core.bundle.get("sv_advancement."+title_+".name");
            this.description = Core.bundle.get("sv_advancement."+title_+".description");
            this.icon = "subvoyage-"+icon_;
        }};
        if(Structs.contains(Advancement.class.getFields(),(t) -> t.getName().equals(title_))) Reflect.set(Advancement.class,title_,adv);
        return adv;
    }

    public void unlock() {
        Advancement.unlock(this);
    }

}
