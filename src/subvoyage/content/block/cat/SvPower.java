package subvoyage.content.block.cat;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawGlowRegion;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawWarmupRegion;
import mindustry.world.meta.Env;
import subvoyage.type.block.power.generation.WindTurbine;
import subvoyage.type.block.power.node.EnergyCross;
import subvoyage.type.block.power.node.EnergyDock;
import subvoyage.type.block.power.node.PowerBubbleNode;

import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.block.SvBlocks.atl;

public class SvPower {
    public static Block
        powerBubbleNode, powerBubbleMerger, windTurbine, spaclaniumHydrolyzer, hydrocarbonicGenerator
    ;

    public static void load() {
        powerBubbleNode = new PowerBubbleNode("power-bubble-node") {{
            requirements(Category.power,atl(),with(iridium,20,corallite,4));
            size = 1;
            outputsPower = false;
            consumesPower = true;
            squareSprite = false;
        }};
        powerBubbleMerger = new EnergyDock("power-bubble-merger") {{
            requirements(Category.power,atl(),with(iridium,20,corallite,20));
            size = 1;
            range = 4f;
            consumePowerBuffered(400f);
            consumesPower = outputsPower = true;
            squareSprite = false;
        }};
        windTurbine = new WindTurbine("wind-turbine") {{
            requirements(Category.power,atl(),with(corallite,60,clay,15,iridium,30));

            researchCost = with(corallite,250,clay,80,iridium,40);

            ambientSound = Sounds.wind;
            ambientSoundVolume = 0.05f;

            powerProduction = 0.2f;
            size = 2;
        }};
        spaclaniumHydrolyzer = new ConsumeGenerator("spaclanium-hydrolyzer") {{
            requirements(Category.power,atl(), with(corallite, 20, clay, 30, iridium, 25));

            researchCost = with(corallite,200,clay,150,iridium,100);

            powerProduction = 5.4f;
            envDisabled |= Env.scorching;

            ambientSound = Sounds.extractLoop;
            ambientSoundVolume = 0.03f;

            size = 2;

            consumeEffect = Fx.generatespark;
            generateEffect = Fx.pulverizeSmall;

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawGlowRegion(){{
                        alpha = 0.75f;
                        color = Color.violet;
                    }}
            );
            itemDuration = 1.8f*60f;
            consumeItem(spaclanium,2);
            consumeLiquid(water,64/60f);
        }};
        hydrocarbonicGenerator = new ConsumeGenerator("hydrocarbonic-generator") {{
            requirements(Category.power,atl(),with(corallite,300,clay,100,iridium,200, chrome,10));

            researchCost = with(corallite,700,clay,350,iridium,350, chrome,50);

            size = 2;

            ambientSound = Sounds.glow;
            ambientSoundVolume = 0.05f;

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawWarmupRegion(),
                    new DrawGlowRegion()
            );

            powerProduction = 24f;
            itemDuration = 0.8f*60f;
            envDisabled |= Env.scorching;
            consumeLiquid(propane,48f/60f);
        }};
    }
}
