package subvoyage.type.block.laser;

import arc.struct.Seq;
import mindustry.gen.Building;

public class LaserGraph {
    public Seq<Building> consumers = new Seq<>(false, 16, Building.class);
    public Seq<Building> suppliers = new Seq<>(false, 16, Building.class);
    public Seq<Building> all = new Seq<>(false,16,Building.class);

    public void addSupplier(Building building) {
        if(!(building instanceof LaserBlock.LaserBlockBuilding laserBuild)) return;
        if(laserBuild.isSupplier()) suppliers.add(laserBuild);
        all.add(laserBuild);
    }
    public void addConsumer(Building building) {
        if(!(building instanceof LaserBlock.LaserBlockBuilding laserBuild)) return;
        if(laserBuild.isConsumer()) consumers.add(laserBuild);
        all.add(laserBuild);
    }

    public void add(Building building) {
        if(!(building instanceof LaserBlock.LaserBlockBuilding laserBuild)) return;
        addSupplier(building); addConsumer(building);
        all.add(laserBuild);
    }

    public void update(Building build) {

    }

    public Seq<Building> getSuppliers() {
        return suppliers;
    }
    public Seq<Building> getConsumers() {
        return consumers;
    }

    public void clearGraph(Building build) {
        all.each(building -> {
            if(!(building instanceof LaserBlock.LaserBlockBuilding laserBuild)) return;
            laserBuild.lasers.graph.remove(build);
        });
        consumers.each(consumer -> {
            if(!(consumer instanceof LaserBlock.LaserBlockBuilding laserBuild)) return;
            laserBuild.lasers.graph.all.remove(build);
            laserBuild.lasers.graph.suppliers.remove(build);
        });
        suppliers.each(supplier -> {
            if(!(supplier instanceof LaserBlock.LaserBlockBuilding laserBuild)) return;
            laserBuild.lasers.graph.all.remove(build);
            laserBuild.lasers.graph.consumers.remove(build);
        });
        suppliers.clear();
        consumers.clear();
    }

    private void remove(Building build) {
        all.remove(build);
        suppliers.remove(build);
        consumers.remove(build);
    }

    private void removeSupplier(Building graphBuilding) {
        suppliers.remove(graphBuilding);
    }
    private void removeConsumer(Building graphBuilding) {
        consumers.remove(graphBuilding);
    }

    public void removeSuppliers(Building build) {
        for (Building supplier : suppliers) {
            if(!(supplier instanceof LaserBlock.LaserBlockBuilding laserBuild)) continue;
            laserBuild.lasers.graph.remove(build);
            laserBuild.lasers.graph.removeConsumer(build);
        }
        suppliers.clear();
    }
}
