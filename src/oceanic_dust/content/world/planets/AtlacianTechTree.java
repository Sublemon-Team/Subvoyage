package oceanic_dust.content.world.planets;

import arc.struct.*;
import mindustry.game.*;
import mindustry.type.*;
import oceanic_dust.liquids.ODLiquids;

import static mindustry.Vars.content;
import static mindustry.content.TechTree.*;
import static oceanic_dust.content.blocks.ODBlocks.*;
import static oceanic_dust.content.world.sectors.ODSectorPresets.crystalShores;
import static oceanic_dust.content.world.sectors.ODSectorPresets.divingPoint;

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

            node(submersibleDrill,Seq.with(new Objectives.SectorComplete(crystalShores),
                    new Objectives.Research(waterMetallizer),
                    new Objectives.Produce(ODLiquids.polygen)),() -> {

            });

            node(lowTierPump,() -> {
                node(waterDiffuser,() -> {
                    node(waterSifter);
                    node(waterMetallizer);
                });
                node(clayConduit);
            });

            node(ceramicBurner,() -> {
                node(argonCentrifuge,Seq.with(new Objectives.Research(waterMetallizer)),() -> {

                });
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
