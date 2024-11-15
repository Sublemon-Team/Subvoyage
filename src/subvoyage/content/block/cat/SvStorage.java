package subvoyage.content.block.cat;

import arc.graphics.g2d.TextureRegion;
import mindustry.game.Team;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import subvoyage.type.block.core.SubvoyageCoreBlock;

import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvUnits.shift;
import static subvoyage.content.block.SvBlocks.atl;

public class SvStorage {
    public static Block
        corePuffer
            ;

    public static void load() {
        corePuffer = new SubvoyageCoreBlock("core-puffer"){{
            requirements(Category.effect,atl(), with(spaclanium,600,corallite,600,clay,300,sulfur,300));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.editorOnly;
            isFirstTier = true;
            unitType = shift;
            health = 4000;
            itemCapacity = 3000;
            size = 4;

            incinerateNonBuildable = true;
            squareSprite = false;
            requiresCoreZone = false;
            envDisabled |= Env.scorching;
            unitCapModifier = 12;

            placeableLiquid = true;
            bannedItems.addAll(crude);
        }

            @Override
            protected TextureRegion[] icons(){
                return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
            }
        };
    }
}
