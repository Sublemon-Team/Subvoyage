package subvoyage.type.block.laser_new;

import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import subvoyage.type.block.laser.LaserLink;

import java.util.ArrayList;
import java.util.List;

import static mindustry.Vars.world;

public class LaserGraph {
    public Seq<Building> consumers = new Seq<>(false, 4, Building.class);
    public Seq<Building> suppliers = new Seq<>(false, 4, Building.class);
    public Seq<Building> all = new Seq<>(false,4,Building.class);
    public int lastChange = -2;
    public int lastRotation = -2;

    public boolean broken = false;


    public static List<LaserLink> getLinks(int x, int y, int rotation, Block block) {
        List<LaserLink> links = new ArrayList<>();
        int sides = 4;
        LaserBlock selfBlock = (LaserBlock) block;
        Tile tile = world.tile(x,y);
        if(tile == null) return links;
        int offset = block.size/2;
        int outputRange = selfBlock.outputRange();
        int inputRange = selfBlock.inputRange();

        List<Integer> selfInputs = new ArrayList<>();
        List<Integer> selfOutputs = new ArrayList<>();
        selfBlock.inputs().each(input -> selfInputs.add((input+rotation)%4));
        selfBlock.outputs().each(output -> selfOutputs.add((output+rotation)%4));

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
                    IntSeq otherInputs = lb.inputs();
                    List<Integer> consInputs = new ArrayList<>();
                    otherInputs.each(input -> consInputs.add((input+other.rotation)%4));

                    if(!selfOutputs.contains(i)) break; //skip, it's not an output side
                    if(!consInputs.contains(invI)) break; //skip, consumer doesn't have an input side on our side
                    if(j > lb.inputRange()+offset) break; //skip, we can't reach this block
                    LaserBuild otherBuild = (LaserBuild) other;
                    int finalI = i;
                    int finalJ = j;
                    links.add(new LaserLink() {{
                        object = other;
                        isSupplier = false;
                        isConsumer = otherBuild.consumer();
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
                    IntSeq otherOutputs = lb.outputs();
                    List<Integer> consOutputs = new ArrayList<>();
                    otherOutputs.each(output -> consOutputs.add((output+other.rotation)%4));

                    if(!selfInputs.contains(i)) break; //skip, it's not an input side
                    if(!consOutputs.contains(invI)) break; //skip, supplier doesn't have output on our side
                    if(j > lb.outputRange()+offset) break; //skip, it can't reach us
                    LaserBuild otherBuild = (LaserBuild) other;
                    int finalI = i;
                    int finalJ = j;
                    links.add(new LaserLink() {{
                        object = other;
                        isSupplier = otherBuild.supplier();
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
            LaserGraph objGraph = ((LaserBuild) obj).graph();
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
        if(!(building instanceof LaserBuild laserBuild)) return;
        if(laserBuild.supplier()) suppliers.add((Building) laserBuild);
        all.add((Building) laserBuild);
    }
    public void addConsumer(Building building) {
        if(!(building instanceof LaserBuild laserBuild)) return;
        if(laserBuild.consumer()) consumers.add((Building) laserBuild);
        all.add((Building) laserBuild);
    }

    public void add(Building building) {
        if(!(building instanceof LaserBuild laserBuild)) return;
        addSupplier(building); addConsumer(building);
        all.add((Building) laserBuild);
    }

    public boolean hasConsumer(LaserBuild b, LaserBuild consumer) {
        try {
            boolean has = false;
            for (Building cons : b.graph().consumers) {
                if (cons == consumer) {
                    return true;
                }
                if (cons instanceof LaserBuild lb && hasConsumer(lb, consumer)) {
                    return true;
                }
            }
            return has;
        } catch (StackOverflowError e) {
            return true;
        }
    }


    public void update(Building build) {
        if(suppliers.size > ((LaserBlock) build.block).maxSuppliers()) {
            for (Building supplier : suppliers) {
                if(supplier instanceof LaserBuild lb) lb.graph().broken = true;
            }
            broken = true;
            removeSuppliers(build);
        }
        if(hasConsumer((LaserBuild) build, (LaserBuild) build)) {
            broken = true;
            consumers.each(e -> {
                if(e instanceof LaserBuild lb) lb.graph().broken = true;
            });
            removeConsumers(build);
        }
        if(lastRotation != build.rotation) {
            world.tileChanges += 1;
            lastRotation = build.rotation;
        }
        if(lastChange != world.tileChanges){
            lastChange = world.tileChanges;
            reloadLinks(build);
            broken = false;
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
            if(!(building instanceof LaserBuild laserBuild)) return;
            laserBuild.graph().remove(build);
        });
        consumers.each(consumer -> {
            if(!(consumer instanceof LaserBuild laserBuild)) return;
            laserBuild.graph().all.remove(build);
            laserBuild.graph().suppliers.remove(build);
        });
        suppliers.each(supplier -> {
            if(!(supplier instanceof LaserBuild laserBuild)) return;
            laserBuild.graph().all.remove(build);
            laserBuild.graph().consumers.remove(build);
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
            if(!(supplier instanceof LaserBuild laserBuild)) continue;
            laserBuild.graph().remove(build);
            laserBuild.graph().removeConsumer(build);
        }
        suppliers.clear();
    }
    public void removeConsumers(Building build) {
        for (Building consumer : consumers) {
            if(!(consumer instanceof LaserBuild laserBuild)) continue;
            laserBuild.graph().remove(build);
            laserBuild.graph().removeSupplier(build);
        }
        consumers.clear();
    }
}
