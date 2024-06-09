package oceanic_dust.planets;

import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import mindustry.game.Objectives;
import mindustry.type.Item;

import static mindustry.Vars.content;
import static oceanic_dust.blocks.ODBlocks.*;
import static mindustry.content.TechTree.*;
import static oceanic_dust.sectors.ODSectorPresets.*;

public class AtlacianTechTree {

    public static void load() {
        Seq<Objectives.Objective> sector = Seq.with(new Objectives.OnPlanet(ODPlanets.atlacian));
        ODPlanets.atlacian.techTree = nodeRoot("atlacian",corePuffer,true,() -> {
            ObjectFloatMap<Item> costMultipliers = new ObjectFloatMap<>();
            for(var item : content.items()) costMultipliers.put(item, 0.4f);
            context().researchCostMultipliers = costMultipliers;

            node(duct);

            node(buoy,() -> {
                node(beacon);
            });

            node(sulfurator);

            node(lowTierPump,() -> {
                node(clayConduit);
            });

            node(ceramicBurner,() -> {
                node(argonCentrifuge);
            });

            node(waterDiffuser,() -> {
                node(waterSifter);
                node(waterMetallizer);
            });

            node(whirl);

            node(divingPoint, () -> {

            });
        });
    }

}
