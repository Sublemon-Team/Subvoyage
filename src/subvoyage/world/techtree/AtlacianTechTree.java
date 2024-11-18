package subvoyage.world.techtree;

import arc.Core;
import arc.struct.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.*;
import mindustry.type.*;

import static subvoyage.content.SvItems.hydrogen;
import static subvoyage.content.SvPlanets.*;

import static mindustry.Vars.content;

import static mindustry.content.TechTree.*;
import static subvoyage.content.block.SvBlocks.*;
import static subvoyage.content.SvItems.*;

public class AtlacianTechTree {

    public static void loadBalanced() {

        ObjectFloatMap<Item> costMultipliers = new ObjectFloatMap<>();
        for(var item : content.items()) {
            float cost = 1f;
            if(item == spaclanium) cost = 0.6f;
            if(item == corallite) cost = 0.5f;
            if(item == sulfur) cost = 0.5f;
            if(item == finesand) cost = 1.1f;
            if(item == iridium) cost = 0.65f;
            if(item == chrome) cost = 0.7f;
            if(item == tugSheet) cost = 0.85f;
            if(item == quartzFiber) cost = 1f;
            cost*=0.8f;
            costMultipliers.put(item,cost);
        }


    }

    public static void load() {
        Seq<Objectives.Objective> sector = Seq.with(new Objectives.OnPlanet(atlacian));
        atlacian.techTree = nodeRoot("atlacian",corePuffer,true,() -> {
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

            node(spaclaniumHydrolyzer,Seq.with(/*new Objectives.SectorComplete(crystalShores)*/),() -> {
                node(energyDock);
                node(energyDistributor);
            });
            node(submersibleDrill,Seq.with(/*new Objectives.SectorComplete(crystalShores),*/
                    new Objectives.Research(waterMetallizer),
                    new Objectives.Produce(hydrogen)),() -> {
            });

            node(lowTierPump,() -> {
                node(waterDiffuser,() -> {
                    node(waterSifter);
                    node(waterMetallizer);
                });
                node(fortifiedConduit,() -> {
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


    public static Seq<Objectives.Objective> with(Objectives.Objective... obj) {
        return Seq.with(obj);
    }
    public static Objectives.SectorComplete sector(SectorPreset preset) {
        return new Objectives.SectorComplete(preset);
    }
    public static Objectives.Objective never() {
        return new Inaccesible();
    }
    public static Objectives.OnSector onsector(SectorPreset preset) {
        return new Objectives.OnSector(preset);
    }
    public static Objectives.Produce produce(UnlockableContent content) {
        return new Objectives.Produce(content);
    }
    public static Objectives.Research research(UnlockableContent content) {
        return new Objectives.Research(content);
    }




    public static class Inaccesible implements Objectives.Objective {

        @Override
        public boolean complete() {
            return false;
        }

        @Override
        public String display() {
            return Core.bundle.format("requirement.never");
        }
    }
}
