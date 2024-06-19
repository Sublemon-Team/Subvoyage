package subvoyage.content.world.items;

import arc.graphics.*;
import arc.struct.*;
import mindustry.type.*;

public class SvItems{
    public static final Seq<Item> atlacianItems = new Seq<>();

    public static Item spaclanium, corallite,fineSand, sulfur,
            iridium,chromium,
            stone, clay,
            tugSheet,quartzFiber;


    public static void load() {

        spaclanium = new Item("spaclanium", Color.valueOf("D8C4FF")) {{
            cost = 0.3f;
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
        }};
        iridium = new Item("iridium", Color.valueOf("8b9098")) {{
            cost = 0.8f;
            charge = 0.4f;
        }};
        stone = new Item("stone", Color.valueOf("393f42")) {{
            cost = 0.2f;
            lowPriority = true;
            buildable = false;
        }};
        chromium = new Item("chromium",Color.valueOf("FFFFFF")) {{
            cost = 0.9f;
            charge = 0.3f;
        }};
        tugSheet = new Item("tug-sheet",Color.valueOf("FFFFFF")) {{
            cost = 1.2f;
            charge = 0.1f;
        }};
        quartzFiber = new Item("quartz-fiber",Color.valueOf("FFFFFF")) {{
            cost = 1.2f;
            charge = 0.5f;
        }};

        atlacianItems.addAll(spaclanium, fineSand, clay, corallite, iridium, stone);

    }
}
