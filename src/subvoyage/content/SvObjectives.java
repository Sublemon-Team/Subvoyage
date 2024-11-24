package subvoyage.content;

import arc.Core;
import arc.func.Prov;
import mindustry.editor.MapObjectivesDialog;
import mindustry.game.MapObjectives;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.block.cat.SvProduction;

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
            return state.stats.placedBlockCount.get(SvProduction.productionAnchor,0) >= count;
        }

        @Override
        public String text(){
            return Core.bundle.format("objective.sv-anchor", count - state.stats.placedBlockCount.get(SvProduction.productionAnchor, 0), SvProduction.productionAnchor.emoji());
        }
    }
}
