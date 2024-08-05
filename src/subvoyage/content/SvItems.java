package subvoyage.content;

import arc.struct.*;
import mindustry.type.*;
import subvoyage.content.other.SvPal;

public class SvItems{
    public static final Seq<Item> atlacianItems = new Seq<>();

    public static Item spaclanium, corallite,fineSand, sulfur,
            iridium,chromium,
            crude, clay,
            tugSheet,quartzFiber;

    public static void load() {

        spaclanium = new Item("spaclanium", SvPal.spaclanium) {{
            cost = 0.3f;
            radioactivity = 0.3f;
            charge = 0.1f;
            flammability = 0.2f;
        }};
        clay = new Item("clay", SvPal.clay) {{
            cost = 0.5f;
        }};
        fineSand = new Item("finesand", SvPal.sand) {{
            cost = 0.3f;
        }};
        corallite = new Item("corallite", SvPal.corallite) {{
            cost = 0.4f;
        }};
        sulfur = new Item("sulfur",SvPal.sulfur) {{
            cost = 0.6f;
            hardness = 1;
            explosiveness = 0.8f;
        }};
        iridium = new Item("iridium", SvPal.iridium) {{
            cost = 0.8f;
            charge = 0.4f;
            hardness = 1;
        }};
        crude = new Item("crude",SvPal.crude) {{
            cost = 0.2f;
            lowPriority = true;
            buildable = false;
        }};
        chromium = new Item("chromium", SvPal.chromium){{
            cost = 0.9f;
            charge = 0.3f;
            hardness = 3;
        }};
        tugSheet = new Item("tug-sheet", SvPal.tugSheet){{
            cost = 1.2f;
            charge = 0.1f;
            hardness = 2;
        }};
        quartzFiber = new Item("quartz-fiber", SvPal.quartzFiber){{
            cost = 1.2f;
            charge = 0.5f;
            radioactivity = 0.3f;
            hardness = 3;
        }};

        atlacianItems.addAll(spaclanium, fineSand, clay, corallite, iridium, crude,chromium,tugSheet,quartzFiber);
    }
}
