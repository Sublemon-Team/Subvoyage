package subvoyage.content.liquids;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.type.*;

public class ODLiquids {
    public static Liquid
            //liquid
            polygen,
            //gas
            argon
    ;

    public static void load() {
        polygen = new Liquid("meta-water", Color.valueOf("8997FF")) {{
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
