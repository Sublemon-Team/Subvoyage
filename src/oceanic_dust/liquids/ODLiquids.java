package oceanic_dust.liquids;

import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.type.Liquid;

public class ODLiquids {
    public static Liquid
            //liquid
            meta_water,
            //gas
            argon
    ;

    public static void load() {
        meta_water = new Liquid("meta-water", Color.valueOf("8997FF")) {{
            heatCapacity = 1.5f;
            effect = StatusEffects.overdrive;
            boilPoint = 0.5f;
            gasColor = Color.grays(0.9f);
            flammability = 0f;

            moveThroughBlocks = true;
        }};

        argon = new Liquid("argon",Color.valueOf("FF8C99")) {{
            gas = true;
            flammability = 1f;
        }};
    }

}
