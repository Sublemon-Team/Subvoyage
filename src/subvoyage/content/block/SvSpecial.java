package subvoyage.content.block;

import arc.graphics.g2d.TextureRegion;
import mindustry.entities.TargetPriority;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import subvoyage.content.other.SvTeam;
import subvoyage.type.block.storage.core.OffloadCore;
import subvoyage.type.block.fog.Buoy;

import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.block.SvUnits.*;
import static subvoyage.content.SvBlocks.atl;

public class SvSpecial {
    public static Block
        buoy,tower, //fog
        regenProjector, //projectors

        offloadCore, coreDecoder
    ;

    public static void load() {
        buoy = new Buoy("buoy") {{
            requirements(Category.effect,atl(BuildVisibility.fogOnly), with(spaclanium,20));
            alwaysUnlocked = true;
            fogRadius = 32;
            envDisabled |= Env.scorching;
            destructible = true;

            priority = TargetPriority.wall;
            health = 120;

            researchCost = with(spaclanium,8);
        }};

        tower = new Buoy("tower") {{
            requirements(Category.effect,atl(BuildVisibility.fogOnly), with(chrome,10,clay,10));
            fogRadius = 40;

            envDisabled |= Env.scorching;
            destructible = true;
            isWater = false;
            outlineIcon = true;

            discoveryTime *= 1.5f;

            consumeLiquid(hydrogen,4/60f);

            priority = TargetPriority.wall;
            health = 360;

            researchCost = with(chrome,50,clay,50);
        }};

        // projectors
        regenProjector = new MendProjector("regen-projector"){{
            requirements(Category.effect,atl(), with(spaclanium, 60, clay, 80, iridium, 10));
            researchCost = with(spaclanium,500,clay,280,iridium,100);

            consumePower(0.3f);
            consumeLiquid(hydrogen,13/60f);
            squareSprite = false;

            size = 2;
            reload = 100f;
            range = 48f*2;
            healPercent = 5f;
            phaseBoost = 4f;
            phaseRangeBoost = 20f;
            health = 400;
        }};

        //offload
        offloadCore = new OffloadCore("offload-core") {{
            requirements(Category.logic, BuildVisibility.editorOnly, with());
            health = 3000;
            size = 3;

            itemCapacity = 1000;

            lowTierUnits = new UnitType[] {lapetus,leeft};
            midTierUnits = new UnitType[] {skath,flagshi};
            highTierUnits = new UnitType[] {charon,callees,vanguard,squadron};

            unitType = shift;
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[SvTeam.melius.id]};
            }
        };

        coreDecoder = new UnitFactory("core-decoder") {{
            requirements(Category.effect,atl(),with(iridium,150, chrome,90));

            researchCost = with(iridium,400,chrome,120);

            configurable = false;
            plans.add(new UnitPlan(cryptal, 60f*30f, with()));
            update = true;
            ambientSound = Sounds.electricHum;

            health = 2560;
            priority = TargetPriority.core;
            fogRadius = 16;
            size = 2;
            consumePower(6f);
            consumeLiquid(propane,0.95f);
            destructible = true;
            envDisabled |= Env.scorching;
        }};
    }
}
