package subvoyage.content.block;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.TargetPriority;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.WorldLabel;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.Pump;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import subvoyage.content.other.SvAttribute;
import subvoyage.core.draw.block.Draw3DSprite;
import subvoyage.type.block.crafter.AttributeCrafterBoostable;
import subvoyage.type.block.production.CoralliteGrinder;
import subvoyage.type.block.production.ProductionAnchor;
import subvoyage.type.block.production.Sifter;

import static arc.graphics.g2d.Draw.color;
import static mindustry.Vars.tilesize;
import static mindustry.content.Items.sand;
import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvBlocks.atl;

public class SvProduction {
    public static Block
            coralliteGrinder, sifter,                 // water-based harvesting
        featherDrill, crudeDrill,      // drill harvesting
        productionAnchor,   //special harvesting

        centrifugalPump //liquids
        ;


    public static void load() {
        coralliteGrinder = new CoralliteGrinder("corallite-grinder") {{
            requirements(Category.production, atl(), with(corallite, 12));
            size = 2;
            craftTime = 360f;
            itemCapacity = 50;

            researchCost = with(corallite,5);

            buildCostMultiplier = 2f;

            squareSprite = false;

            consumeLiquid(water, 15/60f);
            envDisabled |= Env.scorching;

            outputItems = new ItemStack[] {new ItemStack(corallite,3),new ItemStack(finesand,1)};
            maxLiquidTiles = 3;
        }};

        sifter = new Sifter("water-sifter") {{
            requirements(Category.production, atl(), with(spaclanium,20,corallite,60));
            harvestTime = 80f;
            itemCapacity = 50;
            researchCost = with(spaclanium,100,corallite,60,clay,50);

            buildCostMultiplier = 2f;

            consumeItem(finesand,2);

            size = 2;
            envDisabled |= Env.scorching;
            squareSprite = false;
            drawer = new DrawMulti(
                    new DrawDefault()
            );
        }};

        crudeDrill = new AttributeCrafterBoostable("tectonic-drill") {{
            requirements(Category.production,atl(), with(corallite, 200,  clay, 150, iridium, 100, finesand, 30));
            researchCost = with(corallite,1000,spaclanium,600,iridium,400);
            attribute = SvAttribute.crude;

            minEfficiency = 9f - 0.0001f;
            baseEfficiency = 0f;
            displayEfficiency = false;

            craftTime = 200;
            size = 3;
            itemCapacity = 20;
            outputItem = new ItemStack(crude, 3);
            hasPower = true;
            hasLiquids = false;
            ambientSound = Sounds.drill;
            ambientSoundVolume = 0.15f;

            consumeLiquid(hydrogen,4f/60f).boost();

            boostScale = 1f / 9f;
            /*consumeCoolant()
            coolant = consume(new ConsumeLiquid(nitrogen, 20f / 60f));*/

            craftEffect = new MultiEffect(Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.orangeSpark, 20f));
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawBlurSpin("-rotator", 4),
                    new DrawDefault(),
                    new DrawRegion("-top")
            );

            fogRadius = 4;
            squareSprite = false;
            consumePower(0.2f);
        }

            @Override
            public void drawPlace(int x, int y, int rotation, boolean valid) {
                super.drawPlace(x, y, rotation, valid);
                int efficiency = (int)((baseEfficiency + Math.min(maxBoost, boostScale * sumAttribute(attribute, x, y))) * 100f);
                if(efficiency < 100) {
                    Draw.z(Layer.block+2);
                    //Drawf.dashCircle((x+size/4f)*tilesize,(y+size/4f)*tilesize,oreSearchRadius*tilesize, Pal.redDust);
                    WorldLabel.drawAt(Core.bundle.get("tectonic-drill.place.message"), (x + size / 4f - 0.75f) * tilesize, (y + size / 4f + size - 0.1f - 1f) * tilesize,
                            Layer.block + 3, WorldLabel.flagOutline, 0.8f);

                    color(Color.scarlet);
                    Draw.rect(Icon.cancel.getRegion(), (x+size/4f - 0.75f)*tilesize,(y+size/4f+size-0.1f+0.5f - 1f)*tilesize);
                }
            }
        };

        featherDrill = new BurstDrill("feather-drill") {{
            requirements(Category.production,atl(), with(corallite, 250, spaclanium, 300, clay, 250, iridium,190));
            tier = 2;
            drillTime = 60f * 8f;
            size = 3;
            itemCapacity = 40;
            blockedItem = sand;
            fogRadius = 4;
            squareSprite = false;
            updateEffect = Fx.none;

            drillMultipliers.put(spaclanium,0.1f);
            drillMultipliers.put(corallite,0.1f);
            drillMultipliers.put(iridium,0.1f);
            drillMultipliers.put(sulfur,0.1f);

            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.redLight, 40f));
            researchCost = with(corallite,200,spaclanium,100);

            consumeLiquid(water, 15/60f);
            consumeLiquid(hydrogen,8f/60f).boost();
        }
            @Override
            public void setStats() {
                super.setStats();
                if (findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase) {
                    stats.remove(Stat.booster);
                    stats.add(Stat.booster,
                            StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                                    consBase.amount, liquidBoostIntensity, false,
                                    l -> (consumesLiquid(l) && (findConsumer(f -> f instanceof ConsumeLiquid).booster || ((ConsumeLiquid) findConsumer(f -> f instanceof ConsumeLiquid)).liquid != l)))
                    );
                }
            }
        };

        productionAnchor = new ProductionAnchor("production-anchor") {{
            requirements(Category.effect, BuildVisibility.editorOnly, with());
            health = 1500;
            size = 3;

            priority = TargetPriority.core-0.2f; // this is pretty good resource source so yes

            itemBatches = with(
                    corallite,40,
                    corallite,40,
                    spaclanium,35,
                    spaclanium,35,
                    finesand,20,
                    sulfur,15,
                    spaclanium,35,
                    spaclanium,35,
                    finesand,20,
                    sulfur,15,
                    corallite,40,
                    corallite,40
            );
        }};

        centrifugalPump = new Pump("centrifugal-pump") {{
            requirements(Category.liquid,atl(), with(corallite, 20));

            researchCost = with(corallite,5);

            size = 2;
            squareSprite = false;
            envDisabled |= Env.scorching;

            pumpAmount = 15f/60f/4f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidRegion(),
                    new DrawRegion("-top-shadow"),
                    new DrawRegion("") {{
                        layer = Layer.blockOver;
                    }},
                    new DrawRegion("-top")
            );

            //consumeLiquid(hydrogen,4f/60f).boost(); todo: add this again later
        }};
    }
}
