package subvoyage.content.world.planets.atlacian;

import arc.struct.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.*;
import mindustry.type.*;

import static mindustry.content.Liquids.*;
import static subvoyage.content.liquids.SvLiquids.*;
import static subvoyage.content.unit.SvUnits.*;
import static subvoyage.content.world.planets.SvPlanets.*;

import static mindustry.Vars.content;

import static mindustry.content.TechTree.*;
import static subvoyage.content.blocks.SvBlocks.*;
import static subvoyage.content.world.items.SvItems.*;
import static subvoyage.content.world.sectors.SvSectorPresets.*;

public class AtlacianTechTree {

    public static void loadBalanced() {

        ObjectFloatMap<Item> costMultipliers = new ObjectFloatMap<>();
        for(var item : content.items()) {
            float cost = 1f;
            if(item == spaclanium) cost = 0.6f;
            if(item == corallite) cost = 0.5f;
            if(item == sulfur) cost = 0.5f;
            if(item == fineSand) cost = 1.1f;
            if(item == iridium) cost = 0.65f;
            if(item == chromium) cost = 0.7f;
            if(item == tugSheet) cost = 0.85f;
            if(item == quartzFiber) cost = 1f;
            cost*=0.8f;
            costMultipliers.put(item,cost);
        }


        atlacian.techTree = nodeRoot("atlacian",corePuffer,true, () -> {
            context().researchCostMultipliers = costMultipliers;

            node(duct, () -> {
                node(highPressureDuct,() -> {

                });
                node(ductRouter,() -> {
                    node(ductDistributor);
                    node(ductSorter,() -> {
                        node(ductOverflow);
                        node(ductUnderflow);
                    });
                });
                node(ductBridge,() -> {

                });
                node(shipCargoStation,with(research(vault)),() -> {
                    node(shipUnloadPoint,() -> {

                    });
                });
            });

            node(buoy, () -> {
                node(tower,with(research(crudeSmelter)),() -> {

                });
                node(beacon,() -> {

                });
            });

            node(submersibleDrill, () -> {
               node(tectonicDrill, () -> {

               });
               node(featherDrill,with(research(argonCentrifuge)), () -> {

               });
            });

            node(waterDiffuser, () -> {
                node(lowTierPump, () -> {
                    node(centrifugalPump,with(research(energyDistributor)), () -> {

                    });
                });
                node(waterSifter, () -> {

                });
                node(clayConduit, () -> {
                    node(highPressureConduit,with(research(liquidContainer)),() -> {

                    });
                    node(conduitBridge, () -> {

                    });
                    node(conduitRouter, () -> {
                        node(liquidContainer,() -> {
                           node(liquidTank,() -> {

                           }) ;
                        });
                    });
                });
            });

            node(ceramicBurner,() -> {
                node(terracottaBlaster, () -> {

                });
                node(crudeSmelter,with(research(tectonicDrill)),() -> {
                    node(crudeCrucible,with(research(poweredEnhancer)),() -> {

                    });
                });
                node(argonCentrifuge, () -> {
                    node(argonCondenser,() -> {

                    });
                    node(quartzScutcher,with(research(poweredEnhancer)), () -> {
                        node(tugRoller,with(research(argonCondenser)),() -> {

                        }) ;
                    });
                });
                node(waterMetallizer,with(research(energyDistributor)), () -> {
                    node(poweredEnhancer,() -> {

                    });
                });
            });

            node(spaclaniumHydrolyzer,() -> {
                node(energyDock, () -> {
                    node(energyDistributor,() -> {

                    });
                    node(accumulator,() -> {
                        node(largeAccumulator,() -> {

                        });
                    });
                });
                node(windTurbine,with(research(accumulator)), () -> {

                });
                node(chromiumReactor,with(research(crudeSmelter)),() -> {

                });
                node(regenerator,() -> {
                    node(regenProjector,with(research(polygen)),() -> {

                    });
                });
            });

            node(whirl, () -> {
                node(rupture, () -> {
                    node(awe,() -> {
                        node(resonance,with(research(burden)), () -> {
                            node(cascade,() -> {

                            });
                        });
                    });
                    node(burden,() -> {

                    });
                });
                node(finesandWall,() -> {
                    node(finesandWallLarge,() -> {
                        node(clayWall,with(research(ceramicBurner)),() -> {
                            node(clayWallLarge,() -> {
                                node(tugSheetWall,with(research(tugRoller)),() -> {
                                    node(tugSheetWallLarge);
                                });
                            });
                        });
                    });
                });
            });

            node(helicopterFactory,() -> {
                node(lapetus,() ->
                        node(skath,() ->
                                node(charon,() ->
                                        node(callees,() ->
                                                node(ganymede,() -> {

                                                })
                                        )
                                )
                        )
                );
            });

            node(vault,() -> {
                node(unloader,() -> {

                });
                node(largeVault,with(research(coreShore)),() -> {

                });
                node(coreDecoder,() -> {
                    node(coreDecrypter,with(research(coreShore)), () -> {

                    });
                });
                node(coreShore,() -> {
                    node(coreReef,with(research(tugRoller),research(quartzScutcher)),() -> {

                    });
                });
            });

            node(divingPoint, () -> {

            });
            node(spaclanium,with(produce(spaclanium)), () -> {
                node(corallite,with(produce(corallite)),() -> {
                    node(iridium,with(produce(iridium)),() -> {

                    });
                    node(chromium,with(produce(chromium)),() -> {
                        node(quartzFiber,with(produce(quartzFiber)),() -> {

                        });
                        node(tugSheet,with(produce(tugSheet)),() -> {

                        });
                    });
                });
                node(sulfur,with(produce(sulfur)),() -> {

                });
                node(fineSand,with(produce(fineSand)),() -> {
                    node(clay,with(research(water),produce(clay)),() -> {

                    });
                });
                node(water,with(produce(water)),() -> {
                    node(polygen,with(produce(polygen)),() -> {

                    });
                });
                node(argon,with(produce(argon)),() -> {

                });
            });

        });
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
                    new Objectives.Produce(polygen)),() -> {
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


    public static Seq<Objectives.Objective> with(Objectives.Objective... obj) {
        return Seq.with(obj);
    }
    public static Objectives.SectorComplete sector(SectorPreset preset) {
        return new Objectives.SectorComplete(preset);
    }
    public static Objectives.Produce produce(UnlockableContent content) {
        return new Objectives.Produce(content);
    }
    public static Objectives.Research research(UnlockableContent content) {
        return new Objectives.Research(content);
    }

}
