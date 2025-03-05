package subvoyage.content.block;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import mindustry.content.Liquids;
import mindustry.entities.TargetPriority;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import subvoyage.content.other.SvTeam;
import subvoyage.type.block.storage.core.OffloadCore;
import subvoyage.type.block.fog.Buoy;

import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvUnits.*;
import static subvoyage.content.SvBlocks.atl;

public class SvSpecial {
    public static Block
        buoy,tower, //fog
            mendProjector, //projectors

        offloadCore, coreDecoder
    ;

    public static void load() {
        buoy = new Buoy("buoy") {{
            requirements(Category.effect,atl(BuildVisibility.fogOnly), with(spaclanium,10));
            alwaysUnlocked = true;
            fogRadius = 25;
            envDisabled |= Env.scorching;
            destructible = true;

            buildCostMultiplier = 3f;

            priority = TargetPriority.wall;
            health = 120;

            researchCost = with(spaclanium,8);
        }};

        tower = new Buoy("tower") {{
            requirements(Category.effect,atl(BuildVisibility.fogOnly), with(chrome,30,clay,50,iridium,30));
            fogRadius = 40;

            envDisabled |= Env.scorching;
            destructible = true;
            isWater = false;
            outlineIcon = true;

            discoveryTime *= 1.5f;

            consumeLiquid(helium,3/60f);

            priority = TargetPriority.wall;
            health = 360;

            researchCost = with(chrome,50,clay,50);
        }};

        // projectors
        mendProjector = new RegenProjector("regen-projector"){{
            requirements(Category.effect,atl(), with(spaclanium, 60, clay, 80, iridium, 10));
            researchCost = with(spaclanium,500,clay,280,iridium,100);

            consumePower(1f);
            consumeLiquid(hydrogen,13/60f);
            squareSprite = false;

            size = 2;
            range = 20;
            healPercent = 4f / 60f;

            baseColor = Pal.regen;
            Color col = Color.valueOf("AFBEFF");
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawGlowRegion(){{
                        color = Color.sky;
                    }},
                    new DrawPulseShape(false){{
                        layer = Layer.effect;
                        color = col;
                    }},
                    new DrawShape(){{
                        layer = Layer.effect;
                        radius = 3.5f;
                        useWarmupRadius = true;
                        timeScl = 2f;
                        color = col;
                    }});
        }};

        //offload
        offloadCore = new OffloadCore("offload-core") {{
            requirements(Category.logic, BuildVisibility.editorOnly, with());
            health = 1000;
            size = 3;

            teamPassable = true;

            itemCapacity = 200;

            lowTierUnits = new UnitType[] {lapetus,leeft,stunt};
            midTierUnits = new UnitType[] {skath,flagshi,zeal};
            highTierUnits = new UnitType[] {charon,vanguard,gambit};

            unitType = shift;
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[SvTeam.melius.id]};
            }
        };

        coreDecoder = new UnitFactory("core-decoder") {{
            requirements(Category.effect,atl(),with(corallite,150,clay,120,iridium,150, chrome,90));

            researchCost = with(iridium,400,chrome,120);

            configurable = false;
            plans.add(new UnitPlan(demolish, 60f*30f, with()));
            update = true;
            ambientSound = Sounds.electricHum;

            health = 2560;
            priority = TargetPriority.core;
            fogRadius = 16;
            size = 3;
            consumePower(6f);
            consumeLiquid(helium,0.95f);
            destructible = true;
            envDisabled |= Env.scorching;
        }};
    }
}
