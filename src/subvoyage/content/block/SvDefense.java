package subvoyage.content.block;

import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ShieldWall;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Env;
import subvoyage.core.draw.SvPal;
import subvoyage.type.block.defense.PowerWall;

import static mindustry.type.ItemStack.mult;
import static mindustry.type.ItemStack.with;
import static subvoyage.core.ContentStates.*;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvBlocks.atl;

public class SvDefense {
    public static Block
            clayWall,clayWallLarge,
            phosphideWall,phosphideWallLarge,
            tugSheetWall, tugSheetWallLarge
            ;

    public static void load() {
        clayWall = new Wall("clay-wall"){{
            requirements(Category.defense,atl(), with(clay, 6));

            researchCost = with(clay,15);

            health = (int) CLAY_WALL_HP;
            envDisabled |= Env.scorching;
        }};

        clayWallLarge = new Wall("clay-wall-large"){{
            requirements(Category.defense,atl(), mult(clayWall.requirements, 4));

            researchCost = with(clay,100);

            health = (int) CLAY_WALL_LARGE_HP;
            size = 2;
            envDisabled |= Env.scorching;
        }};

        phosphideWallLarge = new PowerWall("phosphide-wall-large"){{
            requirements(Category.defense,atl(), with(phosphide,24));

            researchCost = with(phosphide,100);

            health = (int) PHOSPHIDE_WALL_LARGE_HP;
            size = 2;
            envDisabled |= Env.scorching;

            hitGenerationTime = 1.5f;
            hitPower_pt = 80/10f;

            armor = 4f;

            hasPower = true;
            outputsPower = true;
            consumesPower = false;
            conductivePower = true;
        }};

        tugSheetWall = new ShieldWall("tug-sheet-wall") {{
            requirements(Category.defense,atl(), with(tugSheet, 6));
            consumePower(3f / 60f);

            researchCost = with(tugSheet,150);

            glowColor = SvPal.tugSheetGlow.a(0.5f);
            glowMag = 0.8f;
            glowScl = 12f;

            hasPower = true;
            outputsPower = false;
            consumesPower = true;
            conductivePower = true;
            chanceDeflect = 10f;
            armor = 7f;
            health = (int) TUGSHEET_WALL_HP;
            envDisabled |= Env.scorching;
        }};

        tugSheetWallLarge = new ShieldWall("tug-sheet-wall-large") {{
            requirements(Category.defense,atl(), mult(tugSheetWall.requirements, 4));
            researchCost = with(tugSheet,600);

            consumePower(3*4f / 60f);

            glowColor = SvPal.tugSheetGlow.a(0.5f);
            glowMag = 0.8f;
            glowScl = 12f;

            hasPower = true;
            outputsPower = false;
            consumesPower = true;
            conductivePower = true;
            chanceDeflect = 20f;
            armor = 8f;
            health = (int) TUGSHEET_WALL_LARGE_HP;
            envDisabled |= Env.scorching;
            size = 2;
        }};
    }
}
