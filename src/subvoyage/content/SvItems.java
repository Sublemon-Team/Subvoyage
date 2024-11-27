package subvoyage.content;

import arc.graphics.Color;
import arc.struct.*;
import mindustry.type.*;
import subvoyage.core.draw.SvPal;

public class SvItems{
    public static final Seq<Item> atlacianItems = new Seq<>();

    public static Item corallite,spaclanium, finesand, sulfur,
            clay, iridium, crude,
            chrome, nitride, phosphide,
            tugSheet,quartzFiber;

    public static Liquid argon,hardWater,propane,helium,hydrogen;

    public static void load() {
        corallite = item("corallite",SvPal.corallite,0.5f);
        spaclanium = item("spaclanium",SvPal.spaclanium,0.5f);
        finesand = item("finesand",SvPal.finesand,0.4f);
        sulfur = item("sulfur",SvPal.sulfur,0.4f);

        clay = item("clay",SvPal.clay,0.6f);
        iridium = item("iridium",SvPal.iridium,0.6f);
        crude = item("crude",SvPal.crude);

        chrome = item("chrome",SvPal.chrome,0.8f);
        phosphide = item("phosphide",SvPal.phosphide,0.8f);
        nitride = item("nitride",SvPal.nitride,0.8f);

        quartzFiber = item("quartz-fiber",SvPal.quartzFiber,1f);
        tugSheet = item("tug-sheet",SvPal.tugSheet,1f);
        
        argon = gas("argon",SvPal.argon,0.2f,0.5f);
        hardWater = gas("liquid-hard-water",SvPal.hardWater,0.05f,0.4f);
        propane = gas("propane",SvPal.propane,0.4f,0.6f);
        helium = gas("helium",SvPal.helium,0f,0.35f);
        hydrogen = coolantGas("hydrogen",SvPal.hydrogen,0.55f,0.25f);
    }

    public static Liquid coolantGas(String id, Color col, float cap, float temp) {
        return new Liquid(id,col) {{
            heatCapacity = cap;
            temperature = temp;

            coolant = true;
            boilPoint = -1f;

            gasColor = col;
        }};
    }

    public static Liquid gas(String id, Color col, float flam, float temp) {
        return new Liquid(id,col) {{
            flammability = flam;
            temperature = temp;

            gas = true;
            gasColor = col;
        }};
    }

    public static Item item(String id, Color col) {
        return item(id,col,1f);
    }

    public static Item item(String id, Color col, float costArg) {
        Item item = new Item(id,col) {{
            cost = costArg;
        }};
        atlacianItems.add(item);
        return item;
    }
}
