package subvoyage.content;

import arc.Core;
import arc.func.Prov;
import arc.math.geom.Point2;
import mindustry.content.Blocks;
import mindustry.editor.MapObjectivesDialog;
import mindustry.game.MapObjectives;
import mindustry.game.Team;
import mindustry.world.Block;
import subvoyage.content.block.SvBlocks;

import static mindustry.Vars.*;
import static mindustry.editor.MapObjectivesDialog.defaultInterpreter;

public class SvObjectives {
    static {
        Prov<MapObjectives.MapObjective> activeAnchors = ActiveAnchorsObjective::new;

        MapObjectives.registerObjective(activeAnchors);
        MapObjectivesDialog.setInterpreter(activeAnchors.get().getClass(), defaultInterpreter());
    }


    public static class ActiveAnchorsObjective extends MapObjectives.MapObjective {
        public int count = 1;

        public ActiveAnchorsObjective(int count){
            this.count = count;
        }

        public ActiveAnchorsObjective(){}

        @Override
        public boolean update(){
            return state.stats.placedBlockCount.get(SvBlocks.productionAnchor,0) >= count;
        }

        @Override
        public String text(){
            return Core.bundle.format("objective.sv-anchor", count - state.stats.placedBlockCount.get(SvBlocks.productionAnchor, 0), SvBlocks.productionAnchor.emoji());
        }
    }
}
