package oceanic_dust;

import arc.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import oceanic_dust.blocks.*;
import oceanic_dust.blocks.content.world.*;
import oceanic_dust.entities.*;
import oceanic_dust.items.*;
import oceanic_dust.liquids.*;
import oceanic_dust.blocks.content.world.planets.*;
import oceanic_dust.blocks.content.world.sectors.*;

public class OceanicDustMod extends Mod {

    public OceanicDustMod(){
        Log.info("Loaded OceanicDust constructor.");

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {

        });
    }

    @Override
    public void loadContent(){
        Log.info("Loading OceanicDust content.");
        ODItems.load();
        ODLiquids.load();

        ODUnits.load();

        ODWorldBlocks.load();
        ODBlocks.load();

        ODLoadouts.load();
        ODPlanets.load();

        ODSectorPresets.load();

        EnvRenderer.init();

        AtlacianTechTree.load();
    }

}
