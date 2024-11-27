package subvoyage.content.world;

import arc.Core;
import arc.struct.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.*;
import mindustry.type.*;

import static subvoyage.content.world.SvPlanets.*;

import static mindustry.Vars.content;

import static mindustry.content.TechTree.*;

import static subvoyage.content.block.SvStorage.*;

public class SvTechTree {

    public static void load() {

        ObjectFloatMap<Item> costMultipliers = new ObjectFloatMap<>();
        for(var item : content.items()) {
            float cost = item.cost;
            cost*=0.4f;
            costMultipliers.put(item,cost);
        }

        atlacian.techTree = nodeRoot("atlacian",corePuffer,true,() -> {

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
