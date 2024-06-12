package subvoyage;

import arc.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import subvoyage.content.blocks.*;
import subvoyage.content.unit.*;
import subvoyage.content.world.*;
import subvoyage.content.world.items.*;
import subvoyage.content.liquids.*;
import subvoyage.content.world.planets.*;
import subvoyage.content.world.sectors.*;

public class SubvoyageMod extends Mod {

    public SubvoyageMod(){

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {

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
