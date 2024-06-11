package oceanic_dust.content.world.blocks.energy;

import arc.Core;
import arc.func.Boolf;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.core.UI;
import mindustry.core.World;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.type.*;
import mindustry.type.UnitType.*;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.draw.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.modules.PowerModule;

import static mindustry.Vars.*;
import static mindustry.world.blocks.power.PowerNode.makeBatteryBalance;
import static mindustry.world.blocks.power.PowerNode.makePowerBalance;

public class EnergyDock extends PowerBlock {

    protected static BuildPlan otherReq;
    protected static float maxRange;
    public int maxNodes = 3;
    public float range;
    public boolean autolink = true, drawRange = true;
    public float laserScale = 0.25f;
    public Color laserColor1 = Color.white;
    public Color laserColor2 = Color.valueOf("D9F2FF");
    public TextureRegion laser;
    public TextureRegion laserEnd;
    public DrawBlock drawer = new DrawDefault();
    protected final static ObjectSet<PowerGraph> graphs = new ObjectSet<>();
    public TextureRegion ship;

    public int transferTime = 60;

    @Override
    public void load() {
        super.load();
        drawer.load(this);
        laser = Core.atlas.find(name + "-laser","laser");
        laserEnd = Core.atlas.find(name + "-laser-end","laser-end");
        ship = Core.atlas.find(name + "-ship");
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

                PowerGraph newgraph = new EnergyDockPowerGraph();
                ((EnergyDockPowerGraph) newgraph).transferTime = transferTime;

                //reflow from this point, covering all tiles on this side
                newgraph.reflow(entity);
                if(valid && other.power.graph != newgraph){
                    //create new graph for other end
                    PowerGraph og = new EnergyDockPowerGraph();
                    ((EnergyDockPowerGraph) newgraph).transferTime = transferTime;
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
        addBar("power",entity -> new Bar(() ->
                Core.bundle.format("bar.powerbalance",
                        ((((EnergyDockPowerGraph) entity.power.graph).getPowerBalanceVisual() >= 0 ? "+" : "") + UI.formatAmount((long)(((EnergyDockPowerGraph) entity.power.graph).getPowerBalanceVisual() * 60)))),
                () -> Pal.powerBar,
                () -> Mathf.clamp(entity.power.graph.getLastPowerProduced() / entity.power.graph.getLastPowerNeeded())
        ));
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

    public static void getNodeLinks(Tile tile, Block block, Team team, Cons<Building> others){
        Boolf<Building> valid = other -> other != null && other.tile() != tile && other.block instanceof EnergyDock node &&
                node.autolink &&
                other.power.links.size < node.maxNodes &&
                node.overlaps(other.x, other.y, tile, block, node.range * tilesize) && other.team == team
                && !graphs.contains(other.power.graph) &&
                !EnergyDock.insulated(tile, other.tile) &&
                !Structs.contains(Edges.getEdges(block.size), p -> { //do not link to adjacent buildings
                    var t = world.tile(tile.x + p.x, tile.y + p.y);
                    return t != null && t.build == other;
                });

        tempBuilds.clear();
        graphs.clear();

        //add conducting graphs to prevent double link
        for(var p : Edges.getEdges(block.size)){
            Tile other = tile.nearby(p);
            if(other != null && other.team() == team && other.build != null && other.build.power != null
                    && !(block.consumesPower && other.block().consumesPower && !block.outputsPower && !other.block().outputsPower)){
                graphs.add(other.build.power.graph);
            }
        }

        if(tile.build != null && tile.build.power != null){
            graphs.add(tile.build.power.graph);
        }

        var rangeWorld = maxRange * tilesize;
        var tree = team.data().buildingTree;
        if(tree != null){
            tree.intersect(tile.worldx() - rangeWorld, tile.worldy() - rangeWorld, rangeWorld * 2, rangeWorld * 2, build -> {
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

        tempBuilds.each(valid, t -> {
            graphs.add(t.power.graph);
            others.get(t);
        });
    }

    public boolean linkValid(Building tile, Building link){
        return linkValid(tile, link, true);
    }

    public boolean linkValid(Building tile, Building link, boolean checkMaxNodes){
        if(tile == link || link == null || !link.block.hasPower || !link.block.connectedPower || tile.team != link.team) return false;

        if(overlaps(tile, link, range * tilesize) || (link.block instanceof EnergyDock node && overlaps(link, tile, node.range * tilesize))){
            if(checkMaxNodes && link.block instanceof EnergyDock node){
                return link.power.links.size < node.maxNodes || link.power.links.contains(tile.pos());
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

    protected boolean overlaps(float srcx, float srcy, Tile other, Block otherBlock, float range){
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

    public class EnergyDockBuild extends Building {
        @Override
        public void created(){ // Called when one is placed/loaded in the world
            if(autolink && range > maxRange) maxRange = range;

            power = new EnergyDockPowerModule();
            ((EnergyDockPowerGraph) power.graph).transferTime = transferTime;
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
            super.placed();
        }

        @Override
        public void dropped(){
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
                power.graph = new EnergyDockPowerGraph();
                ((EnergyDockPowerGraph) power.graph).transferTime = transferTime;
            }

            EnergyDockPowerGraph graph = (EnergyDockPowerGraph) power.graph;
            float progress = graph.timePassed/graph.transferTime;
            boolean isInProgress = graph.isInProgress;
            setupColor(progress);
            for(int i = 0; i < power.links.size; i++){
                Building link = world.build(power.links.get(i));
                if(!linkValid(this, link)) continue;
                if(isInProgress) {
                    progress = 0.4f; //vot eto udalish posle testa
                    float consumerX = link.x;
                    float consumerY = link.y;
                    float thisX = x;
                    float thisY = y;
                    float shipX = Mathf.lerp(thisX,consumerX,progress);
                    float shipY = Mathf.lerp(thisY,consumerY,progress);
                    Vec2 shipPos = new Vec2(shipX,shipY);
                    Vec2 thisPos = new Vec2(thisX,thisY);
                    float endAngle = shipPos.sub(thisPos).nor().angle();
                    float starterAngle =  thisPos.sub(shipPos).nor().angle();
                    float angle;
                    if(endAngle < starterAngle) angle = 360-Mathf.lerp(360-starterAngle,360-endAngle,Math.min(1,progress*4));
                    else angle = Mathf.lerp(starterAngle,endAngle,Math.min(1,progress*4));
                    float distanceToPoints = Math.min(
                            Mathf.dst(shipX,shipY,thisX,thisY),
                            Mathf.dst(shipX,shipY,consumerX,consumerY));
                    float alpha = 0.75f;

                    if(distanceToPoints < 12f)
                        alpha *= Mathf.lerp(0, 1, Math.min(distanceToPoints / 24f,1));

                    Draw.alpha(alpha);

                    Draw.z(Layer.power + 2);
                    Draw.rect(ship,shipX,shipY,angle);

                    Draw.z(Layer.power + 1);
                    drawEngine(shipX, shipY,-3, 0, 2, 0.5f, angle,4f, Pal.techBlue, Color.white);

                    Draw.z(Layer.power);
                    Draw.alpha(1);
                }

                if(link.block instanceof EnergyDock && link.id >= id) continue;

                drawLaser(x, y, link.x, link.y, size, link.block.size);
            }

            Draw.reset();
        }

        public void drawEngine(float originX, float originY, float x, float y, float scale, float radius, float rot, float vectorRotMultiplier, Color color, Color engineColorInner){
            Tmp.v2.set(x, y).nor().times(new Vec2(vectorRotMultiplier,vectorRotMultiplier)).rotate(rot);
            float ex = Tmp.v2.x, ey = Tmp.v2.y;
            System.out.println(x+" "+ex);
            Draw.color(color);
            Fill.circle(
            originX+ ex,
            originY+ ey,
            (radius + Mathf.absin(Time.time, 2f, radius / 4f)) * scale
            );

            Draw.color(engineColorInner);
            Fill.circle(
            originX+ ex,
            originY+ ey,
            (radius + Mathf.absin(Time.time, 2f, radius / 4f)) / 2f  * scale
            );
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
