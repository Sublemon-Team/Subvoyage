package subvoyage.content.block;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.struct.IntSeq;
import mindustry.content.Fx;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.Env;
import subvoyage.core.draw.SvPal;
import subvoyage.core.draw.block.DrawBurner;
import subvoyage.core.draw.block.DrawColorWeave;
import subvoyage.core.draw.block.DrawHeatGlow;
import subvoyage.core.draw.block.DrawMixer;
import subvoyage.core.draw.SvFx;
import subvoyage.type.block.laser.blocks.*;
import subvoyage.type.block.crafter.crude.CrudeSmelter;
import subvoyage.type.block.crafter.CircularCrusher;

import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvBlocks.atl;

public class SvCrafting {

    public static Block
        ceramicBurner, argonCentrifuge, circularCrusher,
        hydrogenElectrolyzer, propanePyrolyzer, crudeCrucible, heliumCompressor,
        phosphidePhotosynthesizer, nitrideBlaster,
        quartzScutcher, tugRoller
            ;

    public static void load() {
        ceramicBurner = new GenericCrafter("ceramic-burner") {{
            requirements(Category.crafting, atl(),with(corallite,150,spaclanium,120));
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
        argonCentrifuge = new GenericCrafter("argon-centrifuge") {{
            requirements(Category.crafting, atl(),with(corallite,200,spaclanium,50,iridium,200,clay,80));
            researchCost = with(corallite,350,spaclanium,400,iridium,150,clay,40);

            hasLiquids = true;

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawLiquidRegion(argon),
                    new DrawArcSmelt(){{
                        flameColor = SvPal.argonFlame;
                        midColor = SvPal.argonMidSmelt;
                        flameRad = 1.0F;
                        circleSpace = 1.0F;
                        flameRadiusScl = 3.0F;
                        flameRadiusMag = 0.3F;
                        circleStroke = 1.25F;
                        particles = 16;
                        particleLife = 30.0F;
                        particleRad = 5.2F;
                        particleStroke = 0.8F;
                        particleLen = 2.25F;
                    }}
            );

            squareSprite = false;
            size = 3;

            craftEffect = Fx.none;
            craftTime = 0.56f*60f;

            consumeItem(corallite,1);
            consumeItem(sulfur,1);
            consumeLiquid(water,24f/60f);

            outputLiquid = new LiquidStack(argon, 48/60f);
        }};
        circularCrusher = new CircularCrusher("circular-crusher") {{
            requirements(Category.crafting, atl(), with(corallite,120,spaclanium,60,iridium,20));
            size = 3;
            researchCost = with(corallite,600,spaclanium,120,iridium,400);

            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.crawlDust;
            craftTime = 40f;
            envDisabled |= Env.scorching;

            consumeItem(crude,1);
            consumePower(0.6f);

            outputItem = new ItemStack(finesand,3);
            hasItems = true;
            hasPower = true;
        }};
        hydrogenElectrolyzer = new GenericCrafter("hydrogen-electrolyzer") {{
            requirements(Category.crafting, atl(), with(corallite,240,spaclanium,180,iridium,90,clay,120));
            craftTime = 50f;

            researchCost = with(corallite,720,spaclanium,720,iridium,340,clay,340);

            itemCapacity = 30;
            size = 3;

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
            envDisabled |= Env.scorching;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawMixer(),
                    new DrawRegion(),
                    new DrawRegion("-top")
            );

            outputLiquid = new LiquidStack(hydrogen,18/60f);

            consumeLiquid(water,24/60f);
            consumePower(0.8f);
        }};
        propanePyrolyzer = new GenericCrafter("propane-pyrolizer") {{
            requirements(Category.crafting, atl(),with(iridium,300,corallite,300,clay,150));
            researchCost = with(iridium,300,corallite,700,clay,400);

            itemCapacity = 20;
            size = 3;
            craftEffect = Fx.fireSmoke;
            craftTime = 0.8f*60f;
            envDisabled |= Env.scorching;

            consumeLiquid(hydrogen,18/60f);
            consumeItem(crude,1);
            consumePower(0.45f);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawHeatGlow()
            );

            outputLiquid = new LiquidStack(propane,48/60f);
            hasLiquids = true;
        }};
        crudeCrucible = new CrudeSmelter("crude-crucible"){{
            requirements(Category.crafting, atl(), with(corallite, 340, iridium, 120, clay, 250));
            researchCost = with(corallite, 680, iridium, 700, clay, 650);

            squareSprite = false;

            itemCapacity = 30;
            size = 3;
            craftEffect = Fx.smokePuff;
            recipes = recipes(corallite, 4, 80, spaclanium, 3, 60, iridium, 5, 90, chrome, 2, 120);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawFlame(){{
                        lightRadius = 70f;
                        flameRadius = 6f;
                    }}
            );
            consumeItem(crude, 2);
            consumeLiquid(hydrogen, 12f/60f);
            consumeLiquid(propane, 32f/60f);
            consumePower(1.2f);

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};
        heliumCompressor = new GenericCrafter("helium-compressor") {{
            requirements(Category.crafting, atl(),with(chrome,120,iridium,120,corallite,300,clay,100));
            researchCost = with(chrome,500,iridium,600,corallite,1200,clay,800);

            itemCapacity = 20;
            size = 3;
            craftEffect = SvFx.smokeCloud;
            craftTime = 1.5f*60f;
            envDisabled |= Env.scorching;

            consumeLiquid(water,16/60f);
            consumeLiquid(hydrogen,20/60f);
            consumePower(0.6f);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawLiquidRegion(helium)
            );

            outputLiquid = new LiquidStack(helium,52/60f);
            hasLiquids = true;
        }};
        phosphidePhotosynthesizer = new LaserCrafter("phosphide-photosynthesizer") {{
            requirements(Category.crafting, atl(),with(chrome,240,iridium,250,corallite,150,clay,150)); //TODO: reqs

            researchCost = with(chrome,600,iridium,400,corallite,600,clay,400);

            itemCapacity = 25;
            size = 3;
            craftEffect = SvFx.photosynthFlash;
            craftTime = 2f*60f;
            envDisabled |= Env.scorching;

            consumeItem(iridium,1);
            consumeItem(spaclanium,2);
            consumePower(0.8f);

            laserRequirement = 15f;
            laserOverpowerScale = 0.8f;
            laserMaxEfficiency = 2.50f;

            maxSuppliers = 1;
            inputRange = 8;
            drawInputs = false;

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawPistons(),
                    new DrawRegion("-top")
            );

            outputItem = new ItemStack(phosphide,3);
        }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[] {fullIcon};
            }
        };
        nitrideBlaster = new LaserCrafter("nitride-blaster") {{
            requirements(Category.crafting, atl(),with(chrome,240,iridium,250,corallite,150,clay,150)); //TODO: reqs

            researchCost = with(chrome,600,iridium,400,corallite,600,clay,400);

            itemCapacity = 25;
            size = 3;
            craftEffect = SvFx.blast;
            craftTime = 2f*60f;
            envDisabled |= Env.scorching;

            consumeItem(sulfur,1);
            consumeLiquid(propane,32/60f);
            consumePower(0.8f);

            laserRequirement = 15f;
            laserOverpowerScale = 0.8f;
            laserMaxEfficiency = 2.50f;

            maxSuppliers = 1;
            inputRange = 8;
            drawInputs = false;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawArcSmelt() {{
                        drawCenter = true;
                        flameColor = SvPal.teslaCharge;
                        midColor = SvPal.teslaCharge.cpy().lerp(Color.white,0.5f);
                        particleStroke = circleStroke = 0.3f;
                    }},
                    new DrawRegion("") {{
                        layer = Layer.blockOver;
                    }}
            );

            outputItem = new ItemStack(nitride,2);
        }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[] {fullIcon};
            }
        };
        quartzScutcher = new LaserCrafter("quartz-scutcher") {{
            requirements(Category.crafting,atl(),with(phosphide,350,iridium,600,spaclanium,300,chrome,150));

            researchCost = with(phosphide,2350,iridium,2400,spaclanium,2400,chrome,1500);

            itemCapacity = 30;
            size = 3;
            craftEffect = SvFx.scutchFlash;
            craftTime = 100f;
            envDisabled |= Env.scorching;

            consumeItem(spaclanium,3);
            consumeLiquid(hydrogen,38/60f);
            consumeLiquid(argon,38/60f);
            consumePower(400/60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawColorWeave(SvPal.quartzWeave), new DrawDefault());
            outputItem = new ItemStack(quartzFiber,2);

            laserRequirement = 120f;
            laserOverpowerScale = 0.8f;
            laserMaxEfficiency = 2f;
            inputs = IntSeq.range(0,3);

            capacity = 300;

            maxSuppliers = 1;
            inputRange = 8;
            drawInputs = false;

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
        }};
    }
}
