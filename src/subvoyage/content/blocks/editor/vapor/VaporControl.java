package subvoyage.content.blocks.editor.vapor;

import arc.struct.Seq;
import mindustry.ai.Pathfinder;

public class VaporControl {
    public static void load() {
        Seq<Pathfinder.PathCost> newCosts = Seq.with();
        Pathfinder.costTypes.forEach(e -> {
            //TODO: replace when vapor check is done
            newCosts.add((team, tile) -> false ? -1 : e.getCost(team,tile));
        });
    }
}
