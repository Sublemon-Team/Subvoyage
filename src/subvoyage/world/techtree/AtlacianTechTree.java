package subvoyage.world.techtree;

import arc.Core;
import arc.struct.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.*;
import mindustry.type.*;
import subvoyage.content.SvLiquids;

import static mindustry.content.Liquids.*;
import static subvoyage.content.SvLiquids.*;
import static subvoyage.content.SvUnits.*;
import static subvoyage.content.SvPlanets.*;

import static mindustry.Vars.content;

import static mindustry.content.TechTree.*;
import static subvoyage.content.block.SvBlocks.*;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvSectorPresets.*;

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
                node(isolatedDuct,() -> {
                    node(highPressureDuct,() -> {

                    });
                });
                node(ductRouter,() -> {
                    node(ductDistributor);
                    node(ductSorter,() -> {
                        node(ductInvSorter);
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
                node(incinerator,() -> {

                });
            });

            node(buoy, () -> {
                node(tower,with(research(crudeSmelter)),() -> {

                });
                node(beacon,() -> {

                });
            });

            node(submersibleDrill,with(onsector(ridges)), () -> {
               node(tectonicDrill, () -> {

               });
               node(featherDrill,with(research(argonCentrifuge),onsector(encounter)), () -> {

               });
            });

            node(waterDiffuser, () -> {
                node(lowTierPump, () -> {
                    node(centrifugalPump,with(research(energyDistributor)), () -> {

                    });
                });
                node(waterSifter,with(onsector(tarn)),() -> {
                    node(distiller,with(sector(tarn)),() -> {

                    });
                });
                node(fortifiedConduit, () -> {
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
                node(circularCrusher,with(produce(crude)),() -> {

                });
                node(terracottaBlaster, () -> {

                });
                node(crudeSmelter,with(research(tectonicDrill)),() -> {
                    node(crudeCrucible,with(research(poweredEnhancer)),() -> {

                    });
                });
                node(argonCentrifuge, () -> {
                    node(argonCondenser,() -> {

                    });
                    node(propanePyrolyzer,with(research(crude)),() -> {
                        node(heliumCompressor,with(onsector(hedge)),() -> {

                        });
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

            node(spaclaniumHydrolyzer,with(onsector(ridges)),() -> {
                node(energyDock, () -> {
                    node(energyDistributor,() -> {

                    });
                });
                node(windTurbine,with(research(energyDistributor)), () -> {
                    node(hydrocarbonicGenerator,with(research(propanePyrolyzer)),() -> {

                    });
                });
                node(chromiumReactor,with(research(crudeSmelter)),() -> {

                });
                node(regenProjector,with(research(polygen)),() -> {
                    node(overdriveProjector,() -> {

                    });
                });
            });

            node(whirl, () -> {
                node(rupture,with(onsector(ridges)), () -> {
                    node(awe,() -> {
                        node(resonance,with(research(burden),onsector(encounter)), () -> {
                            node(cascade,() -> {

                            });
                            node(inspiration,() -> {

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

            node(helicopterFabricator,with(onsector(tarn)),() -> {
                node(fortifiedPayloadConveyor,() -> {
                    node(fortifiedPayloadRouter,() -> {

                    });
                    node(payloadUnloader,with(onsector(hedge)),() -> {
                        node(payloadLoader,() -> {

                        });
                    });
                    node(payloadLaunchPad,with(onsector(hedge)), () -> {

                    });
                });
                node(lapetus,() -> {

                });
                node(helicopterRefabricator,with(research(lapetus),onsector(encounter)),() -> {
                    node(skath,() -> {
                        node(charon,with(research(laserRefabricator)),() -> {

                        });
                    });
                });
                node(hydromechFabricator,with(onsector(hedge)),() -> {
                    node(hydromechRefabricator,with(research(helicopterRefabricator),research(leeft)),() -> {
                        node(flagshi,() -> {
                            node(vanguard,with(research(laserRefabricator)),() -> {

                            });
                        });
                    });
                    node(leeft,() -> {

                    });
                });
                node(laserRefabricator,with(research(laserProjector)),() -> {
                    node(helicopterAssembler,() -> {
                        node(callees,() -> {
                            node(ganymede,with(research(assemblyModule)),() -> {

                            });
                            node(assemblyModule,() -> {

                            });
                        });
                    });
                    node(hydromechAssembler,with(research(hydromechRefabricator)),() -> {
                        node(squadron,() -> {
                            node(armada,with(research(assemblyModule)),() -> {

                            });
                        });
                    });
                });
            });

            node(laserProjector,with(),() -> {
                node(laserNode,with(),() -> {
                    node(laserAmplificator,with(),() -> {
                        node(laserSplitter,with(),() -> {

                        });
                        node(laserBlaster,with(),() -> {

                        });
                    });
                });
            });

            node(coreShore,() -> {
                node(unloader,() -> {

                });
                node(vault,() -> {
                    node(largeVault,with(research(coreReef)),() -> {

                    });
                });
                node(coreDecoder,with(onsector(encounter)),() -> {

                });
                node(coreReef,with(research(tugRoller),research(quartzScutcher)),() -> {

                });
            });

            node(dive, () -> {
                node(ridges,with(sector(dive),research(fortifiedConduit),research(conduitRouter)),() -> {
                    node(tarn,with(sector(ridges),research(spaclaniumHydrolyzer),research(energyDock),research(energyDistributor)),() -> {
                        node(encounter,with(sector(tarn),research(distiller)),() -> {

                        });
                        node(hedge,with(sector(tarn),research(propanePyrolyzer)),() -> {

                        });
                    });
                });
            });
            node(spaclanium,with(produce(spaclanium)), () -> {
                node(corallite,with(produce(corallite)),() -> {
                    node(iridium,with(produce(iridium),onsector(ridges)),() -> {

                    });
                    node(chromium,with(research(crudeSmelter)),() -> {
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
                    nodeProduce(hardWater,() -> {

                    });
                    node(polygen,with(produce(polygen)),() -> {

                    });
                });
                node(argon,with(produce(argon)),() -> {
                    nodeProduce(propane,() -> {

                    });
                    nodeProduce(helium,() -> {

                    });
                    nodeProduce(SvLiquids.nitrogen,() -> {

                    });
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
