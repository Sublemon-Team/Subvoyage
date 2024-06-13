package subvoyage.content.world.planets;

import arc.struct.*;
import mindustry.game.*;
import mindustry.type.*;
import subvoyage.content.liquids.*;

import static mindustry.Vars.content;
import static mindustry.content.TechTree.*;
import static subvoyage.content.blocks.ODBlocks.*;
import static subvoyage.content.world.items.ODItems.*;
import static subvoyage.content.world.sectors.ODSectorPresets.*;

public class AtlacianTechTree {

    public static void load() {
        Seq<Objectives.Objective> sector = Seq.with(new Objectives.OnPlanet(ODPlanets.atlacian));
        ODPlanets.atlacian.techTree = nodeRoot("atlacian",corePuffer,true,() -> {
            ObjectFloatMap<Item> costMultipliers = new ObjectFloatMap<>();
            for(var item : content.items()) costMultipliers.put(item, 0.4f);
            context().researchCostMultipliers = costMultipliers;

            node(duct,() -> {
                node(ductRouter,() -> {
                    node(ductSorter);
                });
                node(ductBridge);
            });

            node(buoy,() -> {
                node(beacon);
            });

            node(sulfurator,Seq.with(/*new Objectives.SectorComplete(crystalShores)*/),() -> {
                node(energyDock);
                node(energyDistributor);
            });

            node(submersibleDrill,Seq.with(/*new Objectives.SectorComplete(crystalShores),*/
                    new Objectives.Research(waterMetallizer),
                    new Objectives.Produce(ODLiquids.polygen)),() -> {
            });

            node(lowTierPump,() -> {
                node(waterDiffuser,() -> {
                    node(waterSifter);
                    node(waterMetallizer);
                });
                node(clayConduit,() -> {
                    node(conduitRouter);
                    node(conduitBridge);
                });
            });

            node(ceramicBurner,() -> {
                node(argonCentrifuge,Seq.with(new Objectives.Research(waterMetallizer)),() -> {

                });
                node(terracottaBlaster,Seq.with(new Objectives.Research(energyDistributor)),() -> {

                });
            });

            node(whirl,Seq.with(/*new Objectives.SectorComplete(divingPoint)*/),() -> {
                node(rupture,Seq.with(/*new Objectives.SectorComplete(crystalShores)*/),() -> {

                });
            });

            /*node(divingPoint, () -> {
                node(crystalShores,Seq.with(
                        new Objectives.SectorComplete(divingPoint),
                        new Objectives.Research(whirl),
                        new Objectives.Research(ceramicBurner)
                ),() -> {
                    node(furtherInstallation, Seq.with(
                            new Objectives.SectorComplete(crystalShores),
                            new Objectives.Research(sulfurator),
                            new Objectives.Research(submersibleDrill),
                            new Objectives.Produce(iridium)
                    ),() -> {

                    });
                });
            });*/
        });
    }

}
