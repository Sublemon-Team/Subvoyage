package subvoyage.type.block.laser_old;

import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.ArrayList;
import java.util.List;

import static mindustry.Vars.world;

public class LaserGraph {
    public Seq<Building> consumers = new Seq<>(false, 4, Building.class);
    public Seq<Building> suppliers = new Seq<>(false, 4, Building.class);
    public Seq<Building> all = new Seq<>(false,4,Building.class);
    public int lastChange = -2;
    public int lastRotation = -2;


    public static List<LaserLink> getLinks(int x, int y, int rotation, Block block) {
        List<LaserLink> links = new ArrayList<>();
        int sides = 4;
        LaserBlock selfBlock = (LaserBlock) block;
        Tile tile = world.tile(x,y);
        if(tile == null) return links;
        int offset = selfBlock.size/2;
        int outputRange = selfBlock.outputRange;
        int inputRange = selfBlock.inputRange;

        List<Integer> selfInputs = new ArrayList<>();
        List<Integer> selfOutputs = new ArrayList<>();
        for (int input : selfBlock.inputs) selfInputs.add((input+rotation)%4);
        for (int output : selfBlock.outputs) selfOutputs.add((output+rotation)%4);


        for (int i = 0; i < sides; i++) {
            Point2 dir = Geometry.d4[i];
            int invI = (i+2)%4;
            //first find our consumers
            for(int j = 1 + offset; j <= outputRange + offset; j++){
                var other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);
                if(other != null && other.isInsulated()){
                    break;
                }
                if(other != null && dir.y != 0 && other.tileX() != tile.x+j*dir.x) break;
                if(other != null && dir.x != 0 && other.tileY() != tile.y+j*dir.y) break;
                if(other != null && other.block instanceof LaserBlock lb){
                    int[] otherInputs = lb.inputs;
                    List<Integer> consInputs = new ArrayList<>();
                    for (int input : otherInputs) consInputs.add((input+other.rotation)%4);

                    if(!selfOutputs.contains(i)) break; //skip, it's not an output side
                    if(!consInputs.contains(invI)) break; //skip, consumer doesn't have an input side on our side
                    if(j > lb.inputRange+offset) break; //skip, we can't reach this block
                    LaserBlock.LaserBlockBuilding otherBuild = (LaserBlock.LaserBlockBuilding) other;
                    int finalI = i;
                    int finalJ = j;
                    links.add(new LaserLink() {{
                        object = other;
                        isSupplier = false;
                        isConsumer = otherBuild.isConsumer();
                        side = finalI;
                        len = finalJ;
                    }});
                    break;
                }
            }
            //then find our suppliers
            for(int j = 1 + offset; j <= inputRange + offset; j++){
                var other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);
                if(other != null && other.isInsulated()){
                    break;
                }
                if(other != null && dir.y != 0 && other.tileX() != tile.x+j*dir.x) break;
                if(other != null && dir.x != 0 && other.tileY() != tile.y+j*dir.y) break;
                if(other != null && other.block instanceof LaserBlock lb){
                    int[] otherOutputs = lb.outputs;
                    List<Integer> consOutputs = new ArrayList<>();
                    for (int output : otherOutputs) consOutputs.add((output+other.rotation)%4);

                    if(!selfInputs.contains(i)) break; //skip, it's not an input side
                    if(!consOutputs.contains(invI)) break; //skip, supplier doesn't have output on our side
                    if(j > lb.outputRange+offset) break; //skip, it can't reach us
                    LaserBlock.LaserBlockBuilding otherBuild = (LaserBlock.LaserBlockBuilding) other;
                    int finalI = i;
                    int finalJ = j;
                    links.add(new LaserLink() {{
                        object = other;
                        isSupplier = otherBuild.isSupplier();
                        side = finalI;
                        len = finalJ;
                    }});
                    break;
                }
            }
        }
        return links;
    }


    public List<LaserLink> getLinks(Building building) {
        return getLinks(building.tileX(),building.tileY(),building.rotation,building.block);
    }

    public void reloadLinks(Building building) {
        clearGraph(building);
        List<LaserLink> newLinks = getLinks(building);
        for (LaserLink link : newLinks) {
            Building obj = link.object;
            LaserGraph objGraph = ((LaserBlock.LaserBlockBuilding) obj).lasers.graph;
            if(link.isConsumer) {
                addConsumer(obj); removeSupplier(obj);
                objGraph.addSupplier(building); objGraph.removeConsumer(building);
            }
            if(link.isSupplier) {
                addSupplier(obj); removeConsumer(obj);
                objGraph.addConsumer(building); objGraph.removeSupplier(building);
            }
        }
    }

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
        if(suppliers.size > ((LaserBlock) build.block).maxSuppliers) {
            for (Building supplier : suppliers) {
                Fx.coreLaunchConstruct.create(supplier.x,supplier.y,0, Pal.accent,new Object());
                Fx.unitEnvKill.create(supplier.x,supplier.y,0,Pal.accent,new Object());
            }
            Fx.coreLaunchConstruct.create(build.x,build.y,0,Pal.accent,new Object());
            Fx.unitEnvKill.create(build.x,build.y,0,Pal.accent,new Object());
            Sounds.plasmadrop.play(1f,2f,0f);
            removeSuppliers(build);
        }

        if(lastChange != world.tileChanges || lastRotation != build.rotation){
            lastChange = world.tileChanges;
            lastRotation = build.rotation;
            reloadLinks(build);
        }

        ArrayList<Building> toRemove = new ArrayList<>();
        consumers.each(cons -> {
            if(Vars.world.tile(cons.tileX(),cons.tileY()).block() == Blocks.air) toRemove.add(cons);
        });
        suppliers.each(cons -> {
            if(Vars.world.tile(cons.tileX(),cons.tileY()).block() == Blocks.air) toRemove.add(cons);
        });
        all.each(cons -> {
            if(Vars.world.tile(cons.tileX(),cons.tileY()).block() == Blocks.air) toRemove.add(cons);
        });
        for (Building building : toRemove) {
            all.remove(building);
            consumers.remove(building);
            suppliers.remove(building);
            Vars.world.tile(building.tileX(),building.tileY()).build = null;
        }
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
