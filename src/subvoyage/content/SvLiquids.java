package subvoyage.content;

import arc.graphics.*;
import mindustry.type.*;
import subvoyage.content.other.*;

public class SvLiquids{
    public static Liquid
            //liquid
    polygen, hardWater,
            //gas
            argon, propane, nitrogen, helium
    ;

    public static void load() {

        polygen = new Liquid("meta-water", SvPal.polygen) {{
            heatCapacity = 0.35f;
            boilPoint = 0.5f;
            gasColor = Color.grays(0.9f);
            flammability = 0f;
            coolant = false;
            moveThroughBlocks = true;
        }};

        hardWater = new Liquid("liquid-hard-water", SvPal.hardWater){{
            flammability = 0f;
            coolant = false;
            moveThroughBlocks = true;
        }};

        argon = new Liquid("argon",SvPal.argon) {{
            gas = true;
            flammability = 1f;
        }};

        propane = new Liquid("propane",SvPal.propane) {{
            gas = true;
            flammability = 0.4f;
            explosiveness = 0.6f;
            blockReactive = true;
            temperature = 0.6f;
            gasColor = color;
        }};

        helium = new Liquid("helium",SvPal.helium) {{
            gas = true;
            flammability = 0.5f;
            explosiveness = 0f;
            temperature = 0.4f;
            gasColor = color;
        }};
        nitrogen = new Liquid("nitrogen",SvPal.nitrogen) {{
            //gas = true;
            coolant = true;
            boilPoint = -1f;
            explosiveness = 0f; //turrets can go kaboom
            temperature = 0.2f;
            gasColor = color;
            heatCapacity = 0.4f;
        }};
    }

}
