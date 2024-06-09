package oceanic_dust.blocks;

import arc.graphics.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import oceanic_dust.*;
import oceanic_dust.blocks.c.*;
import oceanic_dust.blocks.shoot.*;
import oceanic_dust.entities.*;
import oceanic_dust.entities.part.*;
import oceanic_dust.liquids.*;

import static mindustry.type.ItemStack.with;
import static oceanic_dust.items.ODItems.*;

public class ODBlocks {
    public static Block
            //DEFENSE
            whirl,
            //CRAFTERS
            waterMetallizer, ceramicBurner, argonCentrifuge,
            //LIQUIDS
            waterDiffuser,waterSifter, clayConduit, lowTierPump,
            //ENERGY
            sulfurator,
            //TRANSPORTATION
            duct,
            //EXPLORATION
            buoy,beacon,
            //CORES
            corePuffer;


    public static void load() {
        //defense

        whirl = new ItemTurret("whirl"){{
            requirements(Category.turret, with(corallite, 85, clay, 45, sulfur, 10));
            ammo(
            spaclanium, new BasicBulletType(3f, 36){{
                width = 7f;
                height = 12f;
                lifetime = 60f;
                shootEffect = ODFx.pulverize;
                smokeEffect = Fx.none;
                hitColor = backColor = trailColor = Pal.suppress;
                frontColor = Color.white;
                trailWidth = 3f;
                trailLength = 5;
                hitEffect = despawnEffect = Fx.hitBulletColor;

                homingPower = 0.08f;
                homingRange = 50f;
            }}
            );

            size = 3;
            drawer = new DrawTurretCallbacked(){{
                DrawTurret draw = (DrawTurret)drawer;
                ODRegionPart liquidPart = new ODRegionPart(draw,"-blade"){{
                    heatColor = Color.sky.cpy().a(0.42f);
                    heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                    mirror = true;
                    under = true;
                    moveX = 1.5f;
                    moveRot = -8;
                }};

                parts.add(new RegionPart("-blade-mid"){{
                    heatColor = Color.sky.cpy().a(0.42f);
                    heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                    progress = PartProgress.recoil;
                    moveY = -1.25f;
                }});

                parts.add(liquidPart);
                onDraw = (build,drawer) -> {
                    liquidPart.liquidDraw = build.liquids.current();
                    liquidPart.liquidAlpha = build.liquids.get(liquidPart.liquidDraw) / build.block.liquidCapacity;
                };
            }};


            shootSound = Sounds.blaster;
            reload = 35f;
            shootY = 5f;
            recoil = 1f;
            rotate = false;
            shoot = new ShootWhirl(){{
                barrels = new float[]{
                -6, 2.5f, 0,
                6, 2.5f, 0
                };
                shotDelay = 5f;
            }};

            range = 170f;
            scaledHealth = 200;
            coolant = consumeCoolant(0.2f);
            researchCostMultiplier = 0.05f;

            limitRange(2);
        }};


        //exploration
        buoy = new Buoy("buoy") {{
            requirements(Category.effect,with(spaclanium,20));

            alwaysUnlocked =true;

            lightRadius = 10f;
            fogRadius = Math.max(fogRadius, (int)(lightRadius / 8f * 3f) + 13);

            size = 1;
            envDisabled |= Env.scorching;
            destructible = true;
            destroyBullet = new BasicBulletType(2f,10f) {{
                width = 11f;
                height = 20f;
                lifetime = 25f;
                shootEffect = Fx.shootBig;
                lightning = 2;
                lightningLength = 6;
                lightningColor = Pal.surge;
                //standard bullet damage is far too much for lightning
                lightningDamage = 20;
            }};

            placeEffect = Fx.healWave;

            health = 120;
        }};

        beacon = new Beacon("beacon") {{
            requirements(Category.effect,with(spaclanium,300,clay, 50,sulfur,200));

            lightRadius = 70f;
            fogRadius = Math.max(fogRadius, (int)(lightRadius / 8f * 3f) + 13);

            size = 3;
            envDisabled |= Env.scorching;
            destructible = true;

            super.length = 6f;
            super.repairSpeed = 1f;
            super.repairRadius = 160f;
            super.powerUse = 5f;
            super.beamWidth = 1.5f;
            super.pulseRadius = 10f;
            super.coolantUse = 0.16f;
            super.coolantMultiplier = 2f;
            super.acceptCoolant = true;


            placeEffect = Fx.healWaveDynamic;

            health = 450;
        }};

        //liquids

        lowTierPump = new Pump("lead-pump") {{
            requirements(Category.liquid, with(spaclanium, 8));
            envDisabled |= Env.scorching;

            pumpAmount = 8f / 60f;
            size = 1;
        }};


        clayConduit = new Conduit("clay-conduit") {{
            requirements(Category.liquid, with(clay, 1));
            envDisabled |= Env.scorching;

            health = 45;
        }};

        waterDiffuser = new Separator("water-diffuser") {{
            requirements(Category.liquid, with(spaclanium, 20, corallite, 5));
            craftTime = 60f*2.5f;
            itemCapacity = 50;
            size = 1;

            researchCost = with(
                    spaclanium, 20,
                    corallite , 10
            );

            consumeLiquid(Liquids.water, 1/60f);
            envDisabled |= Env.scorching;
            results = with(
                    spaclanium,3,
                    corallite,3,
                    fineSand,2
            );
        }};

        waterSifter = new Separator("water-sifter") {{
            requirements(Category.liquid, with(spaclanium,50, corallite, 60,clay,30));
            craftTime = 60f*0.3f;
            itemCapacity = 50;
            size = 2;
            consumeLiquid(Liquids.water, 12/60f);
            envDisabled |= Env.scorching;
            results = with(
                    spaclanium,3,
                    corallite, 2,
                    fineSand, 4,
                    sulfur, 2
            );
        }};

        //energy
        sulfurator = new ConsumeGenerator("sulfurator") {{
            requirements(Category.power, with(corallite, 40, clay, 30));
            powerProduction = 1f;
            itemDuration = 120f;

            size = 1;

            envDisabled |= Env.scorching;

            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.03f;

            consumeEffect = Fx.redgeneratespark;
            generateEffect = Fx.pulverizeSmall;

            drawer = new DrawMulti(new DrawDefault(), new DrawWarmupRegion());

            consumeItem(sulfur);
        }};


        //core
        corePuffer = new CoreBlock("core-puffer"){{
            requirements(Category.effect, with(spaclanium,600,corallite,600,clay,300,sulfur,300));

            alwaysUnlocked = true;

            buildVisibility = BuildVisibility.editorOnly;

            isFirstTier = true;
            unitType = ODUnits.marine;
            health = 6000;
            itemCapacity = 5000;
            size = 5;

            requiresCoreZone = false;

            envDisabled |= Env.scorching;



            unitCapModifier = 12;
        }};


        //transport
        duct = new Duct("duct"){{
            requirements(Category.distribution, with(corallite, 1));
            health = 90;
            speed = 4f;
            researchCost = with(corallite, 5);
            envDisabled |= Env.scorching;
        }};



        //crafters
        ceramicBurner = new GenericCrafter("ceramic-burner") {{

            requirements(Category.crafting,with(spaclanium,30,corallite,70,fineSand,30));

            craftEffect = Fx.absorb;
            craftTime = 60f*2;

            hasItems = true;
            hasLiquids = true;

            outputItem = new ItemStack(clay,2);
            itemCapacity = 3;
            size = 2;
            envDisabled |= Env.scorching;
            consumeLiquid(Liquids.water,1);
            consumeItem(fineSand, 1);
        }};

        argonCentrifuge = new GenericCrafter("argon-centrifuge") {{
           requirements(Category.crafting, with(spaclanium,60,corallite,200,sulfur,30));

           itemCapacity = 10;
           size = 2;

           craftEffect = Fx.smokePuff;
           craftTime = 60f;

           envDisabled |= Env.scorching;

           consumeItem(sulfur,1);
           consumeItem(corallite, 2);
           consumePower(0.8f);

           outputLiquid = new LiquidStack(ODLiquids.argon, 8/60f);

           hasLiquids = true;

           drawer = new DrawMulti(new DrawDefault(), new DrawLiquidRegion());
        }};

        waterMetallizer = new GenericCrafter("water-metallizer") {{
            requirements(Category.crafting, with(spaclanium,100,corallite,60));

            craftEffect = Fx.pulverizeMedium;
            outputLiquid = new LiquidStack(ODLiquids.meta_water, 1);
            craftTime = 20f;

            itemCapacity = 30;
            size = 2;

            hasItems = true;
            hasLiquids = true;
            hasPower = true;
            envDisabled |= Env.scorching;

            consumeLiquid(Liquids.water,1);
            consumeItem(corallite,3);
            consumePower(1);

        }};
    }
}
