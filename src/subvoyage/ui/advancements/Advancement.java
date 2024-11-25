package subvoyage.ui.advancements;

import arc.Core;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Reflect;
import subvoyage.SubvoyageSettings;

public class Advancement {
    public static Seq<Advancement> all = Seq.with();

    public static Advancement
            welcome
            ;

    public String id;
    public String title;
    public String description;
    public String icon;

    private Advancement() {}

    public static void load() {
        add(
            "welcome","sublemon-frog" // Launching Subvoyage
        );
    }

    public static Advancement get(String id) {
        return all.find(a -> a.id.equals(id));
    };
    public static boolean unlocked(Advancement adv) {
        return SubvoyageSettings.bool(adv.id+"-adv-unlocked");
    };
    public static void unlock(Advancement adv) {
        if(!unlocked(adv)) toast(adv);
        SubvoyageSettings.bool(adv.id+"-adv-unlocked",true);
    };
    public static void lock(Advancement adv) {
        SubvoyageSettings.bool(adv.id+"-adv-unlocked",false);
    };
    public static void toast(Advancement adv) {
        Log.info("Advancement toast: "+adv.title);
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
            this.icon = icon_;
        }};
        Reflect.set(Advancement.class,title_,adv);
        return adv;
    }

}
