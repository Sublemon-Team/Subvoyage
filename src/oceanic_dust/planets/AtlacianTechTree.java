package oceanic_dust.planets;

import arc.struct.*;
import mindustry.content.SerpuloTechTree;
import mindustry.game.*;
import mindustry.type.*;

import static mindustry.Vars.content;
import static mindustry.content.TechTree.*;
import static oceanic_dust.blocks.ODBlocks.*;
import static oceanic_dust.sectors.ODSectorPresets.crystalShores;
import static oceanic_dust.sectors.ODSectorPresets.divingPoint;

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

            node(sulfurator,Seq.with(new Objectives.SectorComplete(crystalShores)),() -> {

            });

            node(lowTierPump,() -> {
                node(waterDiffuser,() -> {
                    node(waterSifter);
                    node(waterMetallizer);
                });
                node(clayConduit);
            });

            node(ceramicBurner,() -> {
                node(argonCentrifuge);
            });

            node(whirl,Seq.with(new Objectives.SectorComplete(divingPoint)),() -> {

            });

            node(divingPoint, () -> {
                node(crystalShores,Seq.with(
                        new Objectives.SectorComplete(divingPoint),
                        new Objectives.Research(whirl),
                        new Objectives.Research(ceramicBurner)
                ),() -> {

                });
            });
        });
    }

}
