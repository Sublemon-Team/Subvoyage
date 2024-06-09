package oceanic_dust.items;

import arc.graphics.Color;
import arc.struct.Seq;
import static mindustry.content.Items.*;
import mindustry.type.Item;

public class ODItems {
    public static final Seq<Item> atlacianItems = new Seq<>();

    public static Item clay, fineSand, phosphorus, corallite, sulfur;

    public static void load() {

        phosphorus = new Item("phosphorus", Color.valueOf("D8C4FF")) {{
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

        atlacianItems.addAll(phosphorus, fineSand,clay,corallite);

    }
}
