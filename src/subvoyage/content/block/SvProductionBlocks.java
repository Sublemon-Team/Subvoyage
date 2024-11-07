package subvoyage.content.block;

import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Env;
import subvoyage.draw.block.DrawBurner;
import subvoyage.draw.block.DrawHeatGlow;
import subvoyage.draw.visual.SvFx;

import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.block.SvBlocks.atl;

public class SvProductionBlocks {

    public static Block
        ceramicBurner, argonCentrifuge, circularCrusher, crudeCrucible,
        hydrogenElectrolyzer, propanePyrolyzer, heliumCompressor, phosphidePhotosynthesizer,
        quartzScutcher, tugRoller
            ;

    public static void load() {
        ceramicBurner = new GenericCrafter("ceramic-burner") {{
            requirements(Category.crafting,atl(),with(corallite,150,spaclanium,20));
            researchCost = with(corallite,30,spaclanium,30);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawBurner(),
                    new DrawRegion("-top"),
                    new DrawHeatGlow()
            );
            squareSprite = false;
            size = 3;

            craftEffect = SvFx.burnFlash;
            craftTime = 0.8f*60f;

            consumeLiquid(water, 14/60f);
            consumeItem(finesand,2);

            outputItem = new ItemStack(clay,3);
            itemCapacity = 30;
        }};
    }
}
