package subvoyage.content.world.items;

import arc.graphics.*;
import arc.struct.*;
import mindustry.type.*;

public class SvItems{
    public static final Seq<Item> atlacianItems = new Seq<>();

    public static Item spaclanium, corallite,fineSand, sulfur,
            iridium,chromium,
            crude, clay,
            tugSheet,quartzFiber;

    public static void load() {

        spaclanium = new Item("spaclanium", Color.valueOf("D8C4FF")) {{
            cost = 0.3f;
            radioactivity = 0.3f;
            charge = 0.1f;
            flammability = 0.2f;
        }};
        clay = new Item("clay", Color.valueOf("B28768")) {{
            cost = 0.5f;
        }};
        fineSand = new Item("finesand", Color.valueOf("F7CBA4")) {{
            cost = 0.3f;
        }};
        corallite = new Item("corallite", Color.valueOf("81AA72")) {{
            cost = 0.4f;
        }};
        sulfur = new Item("sulfur",Color.valueOf("C4A981")) {{
            cost = 0.6f;
            hardness = 1;
            explosiveness = 0.8f;
        }};
        iridium = new Item("iridium", Color.valueOf("8b9098")) {{
            cost = 0.8f;
            charge = 0.4f;
            hardness = 1;
        }};
        crude = new Item("crude", Color.valueOf("393f42")) {{
            cost = 0.2f;
            lowPriority = true;
            buildable = false;
        }};
        chromium = new Item("chromium", Color.valueOf("8e898b")){{
            cost = 0.9f;
            charge = 0.3f;
            hardness = 1;
        }};
        tugSheet = new Item("tug-sheet", Color.valueOf("72817d")){{
            cost = 1.2f;
            charge = 0.1f;
            hardness = 2;
        }};
        quartzFiber = new Item("quartz-fiber", Color.valueOf("f4cec2")){{
            cost = 1.2f;
            charge = 0.5f;
            radioactivity = 0.3f;
            hardness = 3;
        }};

        atlacianItems.addAll(spaclanium, fineSand, clay, corallite, iridium, crude);
    }
}
