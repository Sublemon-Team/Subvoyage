package oceanic_dust.content.world.items;

import arc.graphics.*;
import arc.struct.*;
import mindustry.type.*;

public class ODItems {
    public static final Seq<Item> atlacianItems = new Seq<>();

    public static Item clay,phosphorus, fineSand, spaclanium, corallite, sulfur, iridium;

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

        atlacianItems.addAll(spaclanium, fineSand, clay, corallite, iridium);

    }
}
