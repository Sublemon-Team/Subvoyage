package subvoyage.content.world;

import arc.Core;
import arc.struct.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.world.Block;

import static mindustry.content.Liquids.water;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvUnits.*;
import static subvoyage.content.world.SvPlanets.*;

import static mindustry.Vars.content;

import static mindustry.content.TechTree.*;

import static subvoyage.content.block.SvStorage.*;
import static subvoyage.content.block.SvCrafting.*;
import static subvoyage.content.block.SvProduction.*;
import static subvoyage.content.block.SvPayload.*;
import static subvoyage.content.block.SvDistribution.*;
import static subvoyage.content.block.SvLaser.*;
import static subvoyage.content.block.SvPower.*;
import static subvoyage.content.block.SvSpecial.*;
import static subvoyage.content.block.SvDefense.*;
import static subvoyage.content.block.SvTurret.*;
import static subvoyage.content.world.SvSectorPresets.*;

public class SvTechTree {

    public static void load() {

        ObjectFloatMap<Item> costMultipliers = new ObjectFloatMap<>();
        for(var item : content.items()) {
            float cost = item.cost;
            cost*=0.4f;
            costMultipliers.put(item,cost);
        }

        atlacian.techTree = nodeRoot("atlacian",corePuffer,true,() -> {
            cost(buoy,corallite,5);
            node(buoy,() -> {
                cost(tower,clay,120,iridium,70,chrome,40);
                node(tower,withNever(),() -> {});
            });
            cost(coralliteGrinder,corallite,5);
            node(coralliteGrinder, with(onsector(thaw)), () -> {
                cost(sifter,corallite,10,spaclanium,10);
                node(sifter,() -> {
                });
                cost(crudeDrill,corallite,300,clay,280,iridium,200,finesand,80);
                node(crudeDrill,with(onsector(segment)),() -> {
                    cost(featherDrill,corallite,200,spaclanium,50,clay,250,iridium,250);
                    node(featherDrill,withNever(),() -> {

                    });
                });
            });
            cost(duct,corallite,2);
            node(duct, with(onsector(thaw)), () -> {
                cost(ductRouter,corallite,15);
                node(ductRouter);
                cost(ductBridge,corallite,15);
                node(ductBridge);
                noCost(ductSorter);
                node(ductSorter,() -> {
                    noCost(ductInvSorter);
                    node(ductInvSorter);
                });
                cost(ductOverflow,corallite,15);
                node(ductOverflow,() -> {
                    noCost(ductUnderflow);
                    node(ductUnderflow);
                });
                cost(incinerator,corallite,120,clay,120,iridium,60,spaclanium,60);
                node(incinerator);

                cost(isolatedDuct,corallite,120,iridium,30);
                node(isolatedDuct,() -> {

                });
            });
            cost(ceramicBurner,corallite,50,spaclanium,10,finesand,5);
            node(ceramicBurner, with(onsector(thaw)), () -> {
                cost(argonCentrifuge,corallite,100,spaclanium,80,clay,80,iridium,40);
                node(argonCentrifuge,with(sector(construction)),() -> {
                    cost(heliumCompressor,corallite,250,iridium,200,clay,250);
                    node(heliumCompressor,withNever(),() -> {

                    });
                    cost(circularCrusher,corallite,150,iridium,200,spaclanium,50);
                    node(circularCrusher,with(research(crudeDrill)), () -> {

                    });
                    cost(propanePyrolyzer,corallite,500,iridium,300,clay,250,chrome,100);
                    node(propanePyrolyzer,withNever(),() -> {
                        cost(nitrideBlaster,iridium,500,clay,400,chrome,400,phosphide,120);
                        node(nitrideBlaster,() -> {

                        });

                        cost(crudeCrucible, iridium,870,clay,740,chrome,300,nitride,400);
                        node(crudeCrucible);
                    });
                    cost(phosphidePhotosynthesizer,spaclanium,500,iridium,300,clay,250,chrome,300);
                    node(phosphidePhotosynthesizer,withNever(),() -> {

                    });
                    cost(hydrogenElectrolyzer,iridium,400,clay,400,chrome,300,phosphide,80);
                    node(hydrogenElectrolyzer,withNever(),() -> {

                    });
                });
            });
            cost(centrifugalPump,corallite,5);
            node(centrifugalPump, with(onsector(thaw)),() -> {
                cost(fortifiedConduit,corallite,4);
                node(fortifiedConduit, () -> {
                    cost(isolatedConduit,corallite,120,clay,60,iridium,30);
                    node(isolatedConduit);

                    cost(conduitBridge,corallite,30);
                    node(conduitBridge);

                    cost(conduitRouter,corallite,30);
                    node(conduitRouter);

                    cost(liquidTank,corallite,225,clay,240,iridium,160);
                    node(liquidTank);
                });
            });
            cost(whirl,corallite,100,clay,60,iridium,30);
            node(whirl,with(onsector(construction)),() -> {
                noCost(rupture);
                node(rupture);

                cost(resonance,corallite,250,spaclanium,50,iridium,300,clay,250);
                node(resonance,with(sector(segment)),() -> {
                    cost(cascade,corallite,500,iridium,400,spaclanium,120,clay,250,chrome,200);
                    node(cascade,withNever(),() -> {
                        cost(upsurge,phosphide,600,iridium,800,spaclanium,500,clay,500,chrome,300);
                        node(upsurge,() -> {

                        });
                    });
                    cost(mendProjector,phosphide,600,chrome,200,nitride,150);
                    node(mendProjector,withNever(),() -> {});
                });

                cost(clayWall,clay,80);
                node(clayWall,() -> {
                    noCost(clayWallLarge);
                    node(clayWallLarge);

                    cost(phosphideWallLarge,phosphide,60);
                    node(phosphideWallLarge,() -> {
                        cost(tugSheetWall,tugSheet,80);
                        node(tugSheetWall,() -> {
                            noCost(tugSheetWallLarge);
                            node(tugSheetWallLarge);
                        });
                    });
                });
            });
            cost(powerBubbleNode,corallite,40,iridium,20);
            node(powerBubbleNode,with(onsector(construction)),() -> {
                noCost(powerBubbleMerger);
                node(powerBubbleMerger);

                cost(spaclaniumHydrolyzer,spaclanium,200,clay,90,iridium,60);
                node(spaclaniumHydrolyzer,() -> {
                    cost(hydrocarbonicGenerator, corallite,300,phosphide,280,clay,250,chrome,235,iridium,140);
                    node(hydrocarbonicGenerator,with(research(propane)),() -> {});
                });
            });
            cost(helicopterFabricator,corallite,100,spaclanium,120,clay,120,iridium,90);
            node(helicopterFabricator,with(onsector(segment)),() -> {
                noCost(lapetus);
                node(lapetus);

                cost(helicopterRefabricator,corallite,600,spaclanium,820,clay,700,iridium,500,chrome,250);
                node(helicopterRefabricator,withNever(),() -> {
                    noCost(skath);
                    node(skath);
                });

                cost(hydromechFabricator,corallite,200,spaclanium,160,clay,250,iridium,120);
                node(hydromechFabricator,withNever(),() -> {
                    noCost(leeft);
                    node(leeft);

                    cost(hydromechRefabricator,corallite,700,spaclanium,920,clay,800,iridium,500,phosphide,450);
                    node(hydromechRefabricator,() -> {
                        noCost(flagshi);
                        node(flagshi);
                    });

                    cost(roverFabricator,corallite,500,chrome,350,clay,400,iridium,400);
                    node(roverFabricator,() -> {
                        noCost(stunt);
                        node(stunt);

                        cost(roverRefabricator,corallite,800,spaclanium,1020,clay,800,iridium,620,nitride,550);
                        node(roverRefabricator,() -> {
                            noCost(zeal);
                            node(zeal);
                        });
                    });
                });
                cost(coreDecoder,corallite,200,chrome,80,clay,150,iridium,150);
                node(coreDecoder,withNever(),() -> {});

                cost(fortifiedPayloadConveyor, iridium,50,chrome,50);
                node(fortifiedPayloadConveyor,() -> {
                    cost(container,chrome,80,iridium,80);
                    node(container,() -> {
                        noCost(unloader);
                        node(unloader);
                    });

                    noCost(fortifiedPayloadRouter);
                    node(fortifiedPayloadRouter);

                    cost(payloadLaunchPad,clay,150,iridium,300,chrome,80);
                    node(payloadLaunchPad);

                    cost(payloadLoader,corallite,350,iridium,300,chrome,80);
                    node(payloadLoader,() -> {
                        noCost(payloadUnloader);
                        node(payloadUnloader);
                    });
                });
            });
            cost(laserProjector,iridium,300,chrome,150,spaclanium,300);
            node(laserProjector,() -> {
                noCost(laserNode);
                node(laserNode,() -> {
                    cost(luminescentProjector,iridium,700,phosphide,900,spaclanium,600,chrome,250);
                    node(luminescentProjector,() -> {
                        noCost(phosphideLaserNode);
                        node(phosphideLaserNode);
                    });
                });
                noCost(laserAmplifier);
                node(laserAmplifier,() -> {
                    cost(laserSplitter,iridium,400,chrome,300,spaclanium,250);
                    node(laserSplitter);
                });
            });
            nodeProduce(corallite,() -> {
                nodeProduce(spaclanium, () -> {

                });
                nodeProduce(finesand, () -> {
                    nodeProduce(clay,() -> {

                    });
                    nodeProduce(crude,() -> {

                    });
                });
                nodeProduce(iridium,() -> {
                    nodeProduce(phosphide,() -> {

                    });
                    nodeProduce(chrome,() -> {

                    });
                });
                nodeProduce(sulfur, () -> {
                    nodeProduce(nitride,() -> {

                    });
                });
                nodeProduce(water,() -> {
                    nodeProduce(hardWater,() -> {

                    });
                });
                nodeProduce(argon,() -> {
                    nodeProduce(helium,() -> {

                    });
                    nodeProduce(propane,() -> {

                    });
                    nodeProduce(hydrogen,() -> {

                    });
                });
            });

            node(thaw,() -> {
                node(construction,with(sector(thaw)), () -> {
                    node(segment,with(sector(construction),research(argonCentrifuge)), () -> {

                    });
                });
            });
        });

    }

    public static void noCost(Block content) {
        content.researchCostMultiplier = 0f;
        content.researchCost = new ItemStack[] {};
    }
    public static void noCost(UnitType content) {
        content.researchCostMultiplier = 0f;
    }
    public static void cost(Block content, Object... items) {
        content.researchCost = ItemStack.with(items);
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


    public static Seq<Objectives.Objective> withNever() {return with(never());}



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
