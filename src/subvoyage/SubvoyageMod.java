package subvoyage;

import arc.*;
import arc.func.Prov;
import arc.util.*;
import mindustry.ai.Pathfinder;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import subvoyage.content.blocks.*;
import subvoyage.content.unit.*;
import subvoyage.content.unit.ai.WaterCargoAI;
import subvoyage.content.world.*;
import subvoyage.content.world.items.*;
import subvoyage.content.liquids.*;
import subvoyage.content.world.planets.*;
import subvoyage.content.world.sectors.*;

import static mindustry.Vars.pathfinder;
import static mindustry.Vars.state;
import static mindustry.ai.Pathfinder.costNaval;

public class SubvoyageMod extends Mod {
    public static String ID = "subvoyage";

    public static int navalCargoId;

    public SubvoyageMod(){

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {

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

        AtlacianTechTree.load();
    }

}
