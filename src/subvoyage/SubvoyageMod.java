package subvoyage;

import arc.*;
import arc.func.*;
import arc.util.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import subvoyage.content.blocks.*;
import subvoyage.content.liquids.*;
import subvoyage.content.unit.*;
import subvoyage.content.unit.ai.*;
import subvoyage.content.world.*;
import subvoyage.content.world.items.*;
import subvoyage.content.world.planets.*;
import subvoyage.content.world.planets.atlacian.*;
import subvoyage.content.world.sectors.*;

import static mindustry.Vars.*;
import static mindustry.ai.Pathfinder.costNaval;

public class SubvoyageMod extends Mod {
    public static String ID = "subvoyage";

    public static int navalCargoId;

    public SubvoyageMod(){
        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            for (TechTree.TechNode node : TechTree.all) {
                UnlockableContent content = node.content;
                if (content.locked()) {
                    //Log.info("[UnlockTechTreeMod] Unlocking content " + (content.name).replace("content", ""));
                    content.unlock();
                }
            }
        });
        Events.on(WorldLoadEvent.class, e -> {
            Prov<Pathfinder.Flowfield> navalCargo = WaterCargoAI.WaterCargoFlowfield::new;
            Pathfinder.fieldTypes.add(navalCargo);
            navalCargoId = Pathfinder.fieldTypes.indexOf(navalCargo);
            pathfinder.getField(state.rules.waveTeam, costNaval, navalCargoId);
        });
    }

    @Override
    public void loadContent(){
        Log.info("Poof-poof, Subvoyage loads up!");
        SvItems.load();
        SvLiquids.load();

        SvUnits.load();

        SvWorldBlocks.load();
        SvBlocks.load();

        SvLoadouts.load();
        SvPlanets.load();

        SvSectorPresets.load();

        EnvRenderer.init();

        AtlacianTechTree.loadBalanced();
    }

}
