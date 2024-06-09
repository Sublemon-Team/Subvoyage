package oceanic_dust;

import arc.*;
import arc.util.*;
import mindustry.content.Planets;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import oceanic_dust.blocks.ODBlocks;
import oceanic_dust.blocks.ODWorldBlocks;
import oceanic_dust.items.ODItems;
import oceanic_dust.liquids.ODLiquids;
import oceanic_dust.planets.AtlacianTechTree;
import oceanic_dust.planets.ODLoadouts;
import oceanic_dust.planets.ODPlanets;
import oceanic_dust.sectors.ODSectorPresets;
import oceanic_dust.units.ODUnits;

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
