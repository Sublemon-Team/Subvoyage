package subvoyage.type.block.power.node;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.power.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;
import subvoyage.content.other.*;

import static mindustry.Vars.*;
import static mindustry.world.blocks.power.PowerNode.makeBatteryBalance;

public class EnergyDock extends PowerBlock {

    protected static BuildPlan otherReq;
    protected static float maxRange;
    public static int maxNodes = 3;
    public float range;
    public boolean autolink = true, drawRange = true;
    public float laserScale = 0.25f;
    public Color laserColor1 = Color.white;
    public Color laserColor2 = SvPal.energyLaser;
    public TextureRegion laser, laserEnd, shadowRegion, outlineRegion;
    public DrawBlock drawer = new DrawDefault();
    protected final static ObjectSet<PowerGraph> graphs = new ObjectSet<>();
    public TextureRegion ship;
    public TextureRegion shipWave;

    public int transferTime = 60;

    @Override
    public void load() {
        super.load();
        drawer.load(this);
        laser = Core.atlas.find(name + "-laser","laser");
        laserEnd = Core.atlas.find(name + "-laser-end","laser-end");
        ship = Core.atlas.find(name + "-ship");
        shipWave = Core.atlas.find(name + "-ship-wave");
        outlineRegion = Core.atlas.find(name + "-ship-outline");
        shadowRegion = Core.atlas.find(name + "-ship");
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, range * tilesize);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    public EnergyDock(String name) {
        super(name);
        configurable = true;
        consumesPower = false;
        outputsPower = false;
        canOverdrive = true;
        swapDiagonalPlacement = true;
        schematicPriority = -10;
        drawDisabled = true;
        destructible = true;

        config(Integer.class, (entity, value) -> {
            PowerModule power = entity.power;
            Building other = world.build(value);
            boolean contains = power.links.contains(value), valid = other != null && other.power != null;
            if(other == null) return;
            if(!(other.block() instanceof EnergyDock)) return;
            if(contains){
                //unlink
                power.links.removeValue(value);
                if(valid) other.power.links.removeValue(entity.pos());

                EnergyDockPowerGraph newGraph = new EnergyDockPowerGraph();
                newGraph.transferTime = transferTime;

                //reflow from this point, covering all tiles on this side
                newGraph.reflow(entity);
                if(valid && other.power.graph != newGraph){
                    //create new graph for other end
                    PowerGraph og = new EnergyDockPowerGraph();
                    ((EnergyDockPowerGraph) og).transferTime = transferTime;
                    //reflow from other end
                    og.reflow(other);
                }
            }else if(linkValid(entity, other) && valid && power.links.size < maxNodes){

                power.links.addUnique(other.pos());

                if(other.team == entity.team){
                    other.power.links.addUnique(entity.pos());
                }

                power.graph.addGraph(other.power.graph);
            }
        });

        config(Point2[].class,(tile,value) -> {
            IntSeq old = new IntSeq(tile.power.links);

            //clear old
            for(int i = 0; i < old.size; i++){
                configurations.get(Integer.class).get(tile, old.get(i));
            }

            //set new
            for(Point2 p : value){
                configurations.get(Integer.class).get(tile, Point2.pack(p.x + tile.tileX(), p.y + tile.tileY()));
            }
        });
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("power",entity -> {
            if(!(entity.power.graph instanceof EnergyDockPowerGraph g)) {
                return new Bar(() ->
                        Core.bundle.format("bar.powerbalance",
                                ((entity.power.graph.getPowerBalance() >= 0 ? "+" : "") + UI.formatAmount((long) (entity.power.graph.getPowerBalance() * 60)))),
                        () -> Pal.powerBar,
                        () -> Mathf.clamp(entity.power.graph.getLastPowerProduced() / entity.power.graph.getLastPowerNeeded())
                );
            }
            else return new Bar(() ->
                Core.bundle.format("bar.powerbalance",
                        ((g.getPowerBalanceVisual() >= 0 ? "+" : "") + UI.formatAmount((long)(g.getPowerBalanceVisual() * 60)))),
                () -> Pal.powerBar,
                () -> Mathf.clamp(entity.power.graph.getLastPowerProduced() / entity.power.graph.getLastPowerNeeded())
            );
        });
        addBar("batteries", makeBatteryBalance());
        addBar("connections", entity -> new Bar(() ->
                Core.bundle.format("bar.powerlines", entity.power.links.size, maxNodes),
                () -> Pal.items,
                () -> (float)entity.power.links.size / (float)maxNodes
        ));
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.powerRange,range, StatUnit.blocks);
        stats.add(Stat.powerConnections,maxNodes,StatUnit.none);
    }

    public void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2){
        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f - 1.5f, len2 = size2 * tilesize / 2f - 1.5f;

        Drawf.laser(laser, laserEnd, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, laserScale);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Tile tile = world.tile(x,y);
        if(tile == null || !autolink) return;
        Lines.stroke(1f);
        Draw.color(Pal.placing);
        Drawf.circles(x*tilesize+offset,y * tilesize + offset, range * tilesize);
        getNodeLinks(tile,tile.block(),player.team(),other -> {
            Draw.color(laserColor1, Renderer.laserOpacity * 0.5f);
            drawLaser(x * tilesize + offset, y * tilesize + offset, other.x, other.y, size, other.block.size);

            Drawf.square(other.x, other.y, other.block.size * tilesize / 2f + 2f, Pal.place);
        });
    }

    @Override
    public void drawPlanConfigTop(BuildPlan plan, Eachable<BuildPlan> list){
        if(plan.config instanceof Point2[] ps){
            setupColor(1f);
            for(Point2 point : ps){
                int px = plan.x + point.x, py = plan.y + point.y;
                otherReq = null;
                list.each(other -> {
                    if(other.block != null
                            && (px >= other.x - ((other.block.size-1)/2) && py >= other.y - ((other.block.size-1)/2) && px <= other.x + other.block.size/2 && py <= other.y + other.block.size/2)
                            && other != plan && other.block.hasPower){
                        otherReq = other;
                    }
                });

                if(otherReq == null || otherReq.block == null) continue;

                drawLaser(plan.drawx(), plan.drawy(), otherReq.drawx(), otherReq.drawy(), size, otherReq.block.size);
            }
            Draw.color();
        }
    }

    protected void setupColor(float satisfaction){
        Draw.color(laserColor1, laserColor2, (1f - satisfaction) * 0.86f + Mathf.absin(3f, 0.1f));
        Draw.alpha(Renderer.laserOpacity);
    }
    protected static int returnInt;
    public void getNodeLinks(Tile tile, Block block, Team team, Cons<Building> others){
        if(!autolink) return;

        Boolf<Building> valid = other -> other != null && other.tile() != tile && other.block.connectedPower && other.power != null &&
                (other.block instanceof EnergyDock) &&
                overlaps(tile.x * tilesize + offset, tile.y * tilesize + offset, other.tile(), range * tilesize) && other.team == team &&
                !graphs.contains(other.power.graph) &&
                !EnergyDock.insulated(tile, other.tile) &&
                !(other instanceof EnergyDockBuild obuild && obuild.power.links.size >= maxNodes) &&
                !Structs.contains(Edges.getEdges(size), p -> { //do not link to adjacent buildings
                    var t = world.tile(tile.x + p.x, tile.y + p.y);
                    return t != null && t.build == other;
                });

        tempBuilds.clear();
        graphs.clear();

        //add conducting graphs to prevent double link
        for(var p : Edges.getEdges(size)){
            Tile other = tile.nearby(p);
            if(other != null && other.team() == team && other.build != null && other.build.power != null){
                graphs.add(other.build.power.graph);
            }
        }

        if(tile.build != null && tile.build.power != null){
            graphs.add(tile.build.power.graph);
        }

        var worldRange = range * tilesize;
        var tree = team.data().buildingTree;
        if(tree != null){
            tree.intersect(tile.worldx() - worldRange, tile.worldy() - worldRange, worldRange * 2, worldRange * 2, build -> {
                if(valid.get(build) && !tempBuilds.contains(build)){
                    tempBuilds.add(build);
                }
            });
        }

        tempBuilds.sort((a, b) -> {
            int type = -Boolean.compare(a.block instanceof EnergyDock, b.block instanceof EnergyDock);
            if(type != 0) return type;
            return Float.compare(a.dst2(tile), b.dst2(tile));
        });

        returnInt = 0;

        tempBuilds.each(valid, t -> {
            if(returnInt ++ < maxNodes){
                graphs.add(t.power.graph);
                others.get(t);
            }
        });
    }

    public boolean linkValid(Building tile, Building link){
        return linkValid(tile, link, true);
    }

    public boolean linkValid(Building tile, Building link, boolean checkMaxNodes){
        if(tile == link || link == null || !(link.block instanceof EnergyDock) || !link.block.hasPower || !link.block.connectedPower || tile.team != link.team) return false;

        if(overlaps(tile, link, range * tilesize) || (link.block instanceof EnergyDock node && overlaps(link, tile, node.range * tilesize))){
            if(checkMaxNodes && link.block instanceof EnergyDock node){
                return link.power.links.size < maxNodes || link.power.links.contains(tile.pos());
            }
            return true;
        }
        return false;
    }

    public static boolean insulated(Tile tile, Tile other){
        return insulated(tile.x, tile.y, other.x, other.y);
    }

    public static boolean insulated(Building tile, Building other){
        return insulated(tile.tileX(), tile.tileY(), other.tileX(), other.tileY());
    }

    public static boolean insulated(int x, int y, int x2, int y2){
        return World.raycast(x, y, x2, y2, (wx, wy) -> {
            Building tile = world.build(wx, wy);
            return tile != null && tile.isInsulated();
        });
    }

    protected static boolean overlaps(float srcx, float srcy, Tile other, Block otherBlock, float range){
        return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), Tmp.r1.setCentered(other.worldx() + otherBlock.offset, other.worldy() + otherBlock.offset,
                otherBlock.size * tilesize, otherBlock.size * tilesize));
    }

    protected boolean overlaps(float srcx, float srcy, Tile other, float range){
        return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), other.getHitbox(Tmp.r1));
    }

    protected boolean overlaps(Building src, Building other, float range){
        return overlaps(src.x, src.y, other.tile(), range);
    }

    protected boolean overlaps(Tile src, Tile other, float range){
        return overlaps(src.drawx(), src.drawy(), other, range);
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, range * tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }

    public static int nodeCount = 0;

    public class EnergyDockBuild extends Building {
        @Override
        public void created(){ // Called when one is placed/loaded in the world
            if(autolink && range > maxRange) maxRange = range;

            EnergyDockPowerGraph newGraph = new EnergyDockPowerGraph();
            newGraph.transferTime = transferTime;
            newGraph.reflow(this);
            power.graph = newGraph;

            nodeCount = 0;
            world.tiles.eachTile(e -> {
                if(e.block() instanceof EnergyDock) nodeCount++;
            });

            super.created();
        }


        @Override
        public void placed(){
            if(net.client() || power.links.size > 0) return;
            getNodeLinks(tile, tile.block(), team, other -> {
                            if(!power.links.contains(other.pos())){
                                configureAny(other.pos());
                            }
                        });
        }

        @Override
        public void remove() {
            super.remove();
            nodeCount = 0;
            world.tiles.eachTile(e -> {
                if(e.block() instanceof EnergyDock) nodeCount++;
            });
        }

        @Override
        public void dropped(){
            nodeCount = 0;
            world.tiles.eachTile(e -> {
                if(e.block() instanceof EnergyDock) nodeCount++;
            });
            power.links.clear();
            updatePowerGraph();
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(linkValid(this, other)){
                configure(other.pos());
                return false;
            }

            if(this == other){ //double tapped
                if(other.power.links.size == 0){ //find links
                    Seq<Point2> points = new Seq<>();
                    getNodeLinks(tile,tile.block(), team, link -> {
                        if(!insulated(this, link) && points.size < maxNodes){
                            points.add(new Point2(link.tileX() - tile.x, link.tileY() - tile.y));
                        }
                    });
                    configure(points.toArray(Point2.class));
                }else{ //clear links
                    configure(new Point2[0]);
                }
                deselect();
                return false;
            }

            return true;
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            if(!drawRange) return;
            Lines.stroke(1f);
            Draw.color(Pal.accent);
            Drawf.circles(x, y, range * tilesize);
            Draw.reset();
        }

        @Override
        public void drawConfigure(){
            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
            if(drawRange){
                Drawf.circles(x, y, range * tilesize);
                for(int x = (int)(tile.x - range - 2); x <= tile.x + range + 2; x++){
                    for(int y = (int)(tile.y - range - 2); y <= tile.y + range + 2; y++){
                        Building link = world.build(x, y);
                        if(link != this && linkValid(this, link, false)){
                            boolean linked = linked(link);

                            if(linked){
                                Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f, Pal.place);
                            }
                        }
                    }
                }

                Draw.reset();
            }else{
                power.links.each(i -> {
                    var link = world.build(i);
                    if(link != null && linkValid(this, link, false)){
                        Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f, Pal.place);
                    }
                });
            }
        }

        @Override
        public void draw(){
            drawer.draw(this);
            if(Mathf.zero(Renderer.laserOpacity) || isPayload()) return;

            Draw.z(Layer.power);
            setupColor(power.graph.getSatisfaction());
            if(!(power.graph instanceof EnergyDockPowerGraph)) {
                EnergyDockPowerGraph newGraph = new EnergyDockPowerGraph();
                newGraph.transferTime = transferTime;
                newGraph.reflow(this);
                power.graph = newGraph;
                getNodeLinks(tile, tile.block(), team, other -> {
                    if(!power.links.contains(other.pos())){
                        configureAny(other.pos());
                    }
                });

                //Log.warn("["+ new Date() +"] Created new energy dock graph for block at "+x+", "+y);
            }
            if(!(power.graph instanceof EnergyDockPowerGraph)) return;
            EnergyDockPowerGraph graph = (EnergyDockPowerGraph) power.graph;
            float progress = graph.timePassed/graph.transferTime;
            boolean isInProgress = graph.isInProgress;
            for(int i = 0; i < power.links.size; i++){
                Building link = world.build(power.links.get(i));
                if(!linkValid(this, link)) continue;
                if(link.block instanceof EnergyDock && link.id >= id) continue;
                setupColor(power.graph.getSatisfaction());
                drawLaser(x, y, link.x, link.y, size, link.block.size);
            }

            Draw.reset();
        }

        public void drawShadow(float originX, float originY, float rot, float vectorRotMultiplier, float alpha){
            Tmp.v2.set(x, y).nor().times(new Vec2(vectorRotMultiplier,vectorRotMultiplier)).rotate(rot);
            float e = Mathf.clamp(1, -1, 1f) * 1;
            float x = originX - 12 * e, y = originY - 13 * e;
            Floor floor = world.floorWorld(x, y);

            float dest = floor.canShadow ? alpha : 0f;
            Draw.color(Pal.shadow, Pal.shadow.a * dest);

            Draw.rect(shadowRegion, x, y, rot);
            Draw.color();
        }

        public void drawOutline(float originX, float originY, float rot, float vectorRotMultiplier, float alpha){
            Tmp.v2.set(x, y).nor().times(new Vec2(vectorRotMultiplier,vectorRotMultiplier)).rotate(rot);
            Draw.reset();
            if(Core.atlas.isFound(outlineRegion)){
                Draw.color(Pal.darkOutline, alpha);
                Draw.rect(outlineRegion, originX, originY, rot);
                Draw.reset();
            }
        }

        public void drawEngine(float originX, float originY, float x, float y, float scale, float radius, float alpha, float rot, float vectorRotMultiplier, Color color, Color engineColorInner){
            Tmp.v2.set(x, y).nor().times(new Vec2(vectorRotMultiplier,vectorRotMultiplier)).rotate(rot);
            float ex = Tmp.v2.x, ey = Tmp.v2.y;
            Draw.color(color);
            Draw.alpha(alpha);
            Fill.circle(
            originX+ ex,
            originY+ ey,
            (radius + Mathf.absin(Time.time, 2f, radius / 4f)) * scale
            );

            Draw.color(engineColorInner);
            Draw.alpha(alpha);
            Fill.circle(
            originX+ ex,
            originY+ ey,
            (radius + Mathf.absin(Time.time, 2f, radius / 4f)) / 2f  * scale
            );

            Draw.alpha(1);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }

        protected boolean linked(Building other){
            return power.links.contains(other.pos());
        }

        @Override
        public Point2[] config(){
            Point2[] out = new Point2[power.links.size];
            for(int i = 0; i < out.length; i++){
                out[i] = Point2.unpack(power.links.get(i)).sub(tile.x, tile.y);
            }
            return out;
        }
    }

}
