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
        corallite = item("corallite",SvPal.corallite,0.5f,0.05f);
        spaclanium = item("spaclanium",SvPal.spaclanium,0.5f,0.05f);
        finesand = item("finesand",SvPal.finesand,0.4f,-0.02f);
        sulfur = item("sulfur",SvPal.sulfur,0.4f,0.1f);

        clay = item("clay",SvPal.clay,0.6f,0.1f);
        iridium = item("iridium",SvPal.iridium,0.6f,0.17f);
        crude = item("crude",SvPal.crude);

        chrome = item("chrome",SvPal.chrome,0.8f,0.21f);
        phosphide = item("phosphide",SvPal.phosphide,0.8f,0.35f);
        nitride = item("nitride",SvPal.nitride,0.8f,0.4f);

        quartzFiber = item("quartz-fiber",SvPal.quartzFiber,1f,0.25f);
        tugSheet = item("tug-sheet",SvPal.tugSheet,1f,0.5f);
        
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
        return item(id,col,1f,0f);
    }

    public static Item item(String id, Color col, float costArg, float health) {
        Item item = new Item(id,col) {{
            cost = costArg;
            healthScaling = health;
        }};
        atlacianItems.add(item);
        return item;
    }
}
