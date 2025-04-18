package subvoyage.type.block.power.node;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.Renderer;
import mindustry.core.UI;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.io.TypeIO;
import mindustry.logic.LExecutor;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;
import subvoyage.core.SvSettings;
import subvoyage.core.draw.SvPal;
import subvoyage.core.draw.SvRender;
import subvoyage.util.SvMath;
import subvoyage.util.Var;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static mindustry.world.blocks.power.PowerNode.makeBatteryBalance;
import static subvoyage.core.ui.advancements.Advancement.big_bubble;
import static subvoyage.type.block.production.Sifter.drawErrorInfo;

public class PowerBubbleNode extends PowerBlock {

    public int range = 50;

    public PowerBubbleNode(String name) {
        super(name);
        configurable = true;
        swapDiagonalPlacement = true;
        allowDiagonal = true;


        config(Integer.class,(entity,value) -> {
            if(!(entity instanceof PowerBubbleNodeBuild pb)) return;
            var build = world.build(value);
            if(!(build instanceof PowerBubbleNodeBuild pb2)) return;

            if(pb.link == value) {
                pb.setLink(-1);
                pb2.setLink(-2);

                pb.hasLink = false;
                pb2.hasLink = false;

                pb.removeLinks();
                pb2.removeLinks();

                pb.worldChanges++;
                pb2.worldChanges++;
                return;
            }

            pb.setLink(value);
            pb2.setLink(pb.pos());

            pb.worldChanges++;
            pb2.worldChanges++;
        });
        config(Seq.class,(entity, value) -> {
            if(!(entity instanceof PowerBubbleNodeBuild pb)) return;
            value.each(v -> {
                if(v instanceof BuildPlan p && !(p.x == entity.tileX() && p.y == entity.tileY())) {
                    pb.setLink(new Point2(p.x,p.y).pack());
                    pb.worldChanges++;
                }
            });
        });

        consumePowerDynamic((t) -> {
            if(t instanceof PowerBubbleNodeBuild pb) return pb.getPowerConsumption();
            return 0f;
        });
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("power",entity -> {
            PowerGraph g = entity.power.graph;
            return new Bar(() ->
                    Core.bundle.format("bar.powerbalance",
                            ((g.getPowerBalance() >= 0 ? "+" : "") + UI.formatAmount((long)(g.getPowerBalance() * 60)))),
                    () -> Pal.powerBar,
                    () -> Mathf.clamp(entity.power.graph.getLastPowerProduced() / entity.power.graph.getLastPowerNeeded())
            );
        });
        addBar("batteries", makeBatteryBalance());
    }

    @Override
    public void init() {
        super.init();
        clipSize = size * tilesize + range * tilesize * 2f;
    }

    private void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2, float scl, float bloomIntensity){
        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f, len2 = size2 * tilesize / 2f;
        //len1 = 0, len2 = 0;
        float layer = Draw.z();

        scl = Math.max(scl+bloomIntensity/2f,0.2f);
        Draw.z(Layer.blockOver);
        Fill.circle(x1 + vx * len1, y1 + vy * len1, 6f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Fill.circle(x2 - vx * len2, y2 - vy * len2, 6f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Lines.stroke(8f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Lines.line(x1 + vx * len1, y1 + vy * len1, x2 - vx * len2, y2 - vy * len2);
        Draw.color();
        Lines.stroke(3f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Fill.circle(x1 + vx * len1, y1 + vy * len1, 2f * scl + Mathf.cos(Time.time + 5, 10f, 0.5f) * (scl - 0.2f));
        Fill.circle(x2 - vx * len2, y2 - vy * len2, 2f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Lines.line(x1 + vx * len1, y1 + vy * len1, x2 - vx * len2, y2 - vy * len2);
        Draw.z(layer);
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation, boolean diagonalOn) {
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
        if(points.size > 2) points.removeRange(2, points.size - 1);
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans) {
        Seq<BuildPlan> cpy = Seq.with();
        plans.each(p -> cpy.add(p.copy()));

        plans.each(p -> p.config = cpy.map(e -> Point2.pack(e.x,e.y)).find(e -> !e.equals(Point2.pack(p.x,p.y))));
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, range * tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }
    protected static boolean overlaps(float srcx, float srcy, Tile other, Block otherBlock, float range){
        return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), Tmp.r1.setCentered(other.worldx() + otherBlock.offset, other.worldy() + otherBlock.offset,
                otherBlock.size * tilesize, otherBlock.size * tilesize));
    }

    protected boolean overlaps(float srcx, float srcy, Tile other, float range){
        return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), other.getHitbox(Tmp.r1));
    }

    protected boolean overlaps(Building src, Building other, float range){
        return overlaps(src.x, src.y, other.tile, range);
    }

    protected boolean overlaps(Tile src, Tile other, float range){
        return overlaps(src.drawx(), src.drawy(), other, range);
    }

    public boolean linkValid(Building tile, Building link){
        return linkValid(tile, link, true);
    }

    public boolean linkValid(Building tile, Building link, boolean checkMaxNodes){
        if(tile == link || link == null || !(link.block instanceof PowerBubbleNode) || !link.block.hasPower || !link.block.connectedPower || tile.team != link.team) return false;

        return overlaps(tile, link, range * tilesize) || (link.block instanceof PowerBubbleNode node && overlaps(link, tile, node.range * tilesize));
    }

    public class PowerBubbleNodeBuild extends Building {

        public boolean hasLink = false;
        public int link = -1;
        public boolean valid = false;

        public Building link() {
            if(hasLink) {
                if(world.build(link) != null && !world.build(link).isAdded()) {
                    return null;
                }
                return world.build(link);
            }
            return null;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(linkValid(this, other)){
                configure(other.pos());
                return false;
            }

            return true;
        }


        int worldChanges = -2;
        float powerUsage = 0f;

        @Override
        public void onRemoved() {
            super.onRemoved();
            removeLinks();
        }
        public boolean recheck = false;
        @Override
        public void updateTile() {
            super.updateTile();
            if(hasLink && link() instanceof PowerBubbleNodeBuild pb) pb.setLink(pos());
            if(recheck && hasLink && link() instanceof PowerBubbleNodeBuild pb) {
                power.graph.addGraph(pb.power.graph);
            }
            if(worldChanges != world.tileChanges) {
                worldChanges = world.tileChanges;
                updateAll();
            }
            consume();
        }
        public void updateAll() {
            if(hasLink && link() instanceof PowerBubbleNodeBuild pb) pb.setLink(pos());
            valid = checkRectangle();
            if(valid) {
                if(link() instanceof PowerBubbleNodeBuild pb) {
                    float w = Math.abs(pb.x - x);
                    float h = Math.abs(pb.y - y);
                    powerUsage = Mathf.clamp(w*h/80f,0f,60f)/60f/2f;
                }
                updateLinks();
                recheck = true;
            } else {
                removeLinks();
            }
        }
        public void removeLinks() {
            Seq<Building> builds = Seq.with();
            power.links.each(i -> {
                Building prev = world.build(i);
                //disconnected
                if(!builds.contains(prev) && !(prev instanceof PowerBubbleMerger.EnergyDockBuild)) {
                    if(prev != null) prev.power.links.removeValue(pos());
                    if(prev != null) power.links.removeValue(prev.pos());

                    PowerGraph newgraph = new PowerGraph();
                    newgraph.reflow(this);

                    if(prev != null && prev.power.graph != newgraph) {
                        PowerGraph og = new PowerGraph();
                        og.reflow(prev);
                    }
                }
            });
        }
        public void updateLinks() {
            Seq<Building> builds = Seq.with();
            if(!(hasLink && link() instanceof PowerBubbleNodeBuild pb)) return;

            SvMath.rectangle(pb.tileX(),pb.tileY(),tileX(),tileY(),(x,y) -> {
                Tile tile = world.tile(x,y);
                if(tile.build == null || !tile.build.block.hasPower) return;
                builds.add(tile.build);
            });
            builds.addUnique(pb);
            IntSeq seq = builds.mapInt(Building::pos);
            power.links.each(i -> {
                Building prev = world.build(i);
                //disconnected
                if(!builds.contains(prev) && !(prev instanceof PowerBubbleMerger.EnergyDockBuild)) {
                    if(prev != null) prev.power.links.removeValue(pos());
                    if(prev != null) power.links.removeValue(prev.pos());

                    PowerGraph newgraph = new PowerGraph();
                    newgraph.reflow(this);

                    if(prev != null && prev.power.graph != newgraph) {
                        PowerGraph og = new PowerGraph();
                        og.reflow(prev);
                    }
                }
            });
            seq.each(i -> {
                Building next = world.build(i);
                //connected
                if(!power.links.contains(i)) {
                    power.links.addUnique(next.pos());
                    next.power.links.addUnique(pos());

                    power.graph.addGraph(next.power.graph);
                }
            });
        }


        boolean tooBig = false;
        boolean tooSmall = false;
        boolean tooMuchEnv = false;
        boolean overlaps = false;

        public boolean checkRectangle() {
            tooBig = tooSmall = tooMuchEnv = overlaps = false;
            if(!(hasLink && link() instanceof PowerBubbleNodeBuild pb)) return false;

            float maxArea = range*range*tilesize;
            float w = Math.abs(pb.x-x);
            float h = Math.abs(pb.y-y);
            if(w <= tilesize || h <= tilesize)
                tooSmall = true;

            float area = w*h;
            if(area > maxArea)
                tooBig = true;

            Var<Integer> envCount = Var.init(0);
            Var<Boolean> rectOverlap = Var.bool();
            SvMath.rectangle(pb.tileX(),pb.tileY(),tileX(),tileY(),(x,y) -> {
                Tile tile = world.tile(x,y);
                if(tile.block() != null && tile.block().isStatic())
                    envCount.val++;
                if(tile.build instanceof PowerBubbleNodeBuild pb2 && (link != pb2.pos() && pb2 != this))
                    rectOverlap.val = true;
            });
            if(envCount.val > 8*8)
                tooMuchEnv = true;
            if(rectOverlap.val)
                overlaps = true;

            pb.tooBig = tooBig;
            pb.tooSmall = tooSmall;
            pb.tooMuchEnv = tooMuchEnv;
            pb.overlaps = overlaps;

            if(tooBig)
                big_bubble.unlock();
            return !(tooMuchEnv || overlaps || tooBig || tooSmall);
        }

        @Override
        public void draw() {
            super.draw();
            var build = link();

            if(build instanceof PowerBubbleNodeBuild pb) {
                if(valid) {
                    Draw.z(Layer.blockUnder);
                    Draw.rect(region,build.x,y,0);
                    Draw.rect(region,x,build.y,0);

                    Draw.z(SvRender.Layer.powerBubbles);
                    Draw.color(SvPal.powerLaser.cpy().saturation(0.2f));
                    if(!SvSettings.bool("power-bubble-shaders")){
                        Draw.alpha(0.2f);
                    }
                    Fill.crect(x,y,build.x-x,build.y-y);
                    Draw.z(Layer.block);
                } else {
                    Draw.z(Layer.blockUnder);
                    Draw.rect(region,build.x,y,0);
                    Draw.rect(region,x,build.y,0);

                    Draw.z(SvRender.Layer.powerBubbles);
                    Draw.color(Pal.remove);
                    if(!SvSettings.bool("power-bubble-shaders")){
                        Draw.alpha(0.2f);
                    }
                    Fill.crect(x,y,build.x-x,build.y-y);
                    Draw.z(Layer.block);
                }
                Draw.reset();
            }
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Seq<String> errors = Seq.with();
            if(tooBig) errors.add("pbubble.error.too-big");
            if(tooSmall) errors.add("pbubble.error.too-small");
            if(tooMuchEnv) errors.add("pbubble.error.env");
            if(overlaps) errors.add("pbubble.error.overlap");

            drawErrorInfo(size,x-3f,y,errors);

            if(hasLink && link() instanceof PowerBubbleNodeBuild pb) {
                Lines.stroke(1.5f,Pal.accent);
                Lines.quad(x,y,pb.x,y,pb.x,pb.y,x,pb.y);
            }
            Draw.reset();
        }

        @Override
        public void drawConfigure() {
            Draw.color(Pal.accent);
            Lines.stroke(1.0F);
            Lines.square(this.x, this.y, (float)(this.block.size * 8) / 2.0F + 1.0F);
            Draw.reset();

            var build = link();
            if(!(build instanceof PowerBubbleNodeBuild pb)) return;
            Lines.stroke(1.0F);
            Drawf.square(pb.x,pb.y,size*tilesize/2f,Pal.place);

            Draw.reset();
        }

        @Override
        public void configured(Unit builder, Object value) {
            Class<?> type = value == null ? Void.TYPE : (value.getClass().isAnonymousClass() ? value.getClass().getSuperclass() : value.getClass());
            if (value instanceof Item) {
                type = Item.class;
            }

            if (value instanceof Block) {
                type = Block.class;
            }

            if (value instanceof Liquid) {
                type = Liquid.class;
            }

            if (value instanceof UnitType) {
                type = UnitType.class;
            }

            if (value instanceof Point2[]) {
                type = Point2[].class;
            }

            if (builder != null && builder.isPlayer()) {
                this.lastAccessed = builder.getPlayer().coloredName();
            }

            if (this.block.configurations.containsKey(type)) {
                this.block.configurations.get(type).get(this, value);
            } else if (value instanceof Building build) {
                Object conf = build.config();
                if (conf != null && !(conf instanceof Building)) {
                    this.configured(builder, conf);
                }
            }

        }

        public void setLink(int value) {
            if(link == value) {
                hasLink = true;
                return;
            }
            if(hasLink && world.build(link) instanceof PowerBubbleNodeBuild pb) {
                pb.link = -1;
                pb.hasLink = false;
                pb.valid = pb.checkRectangle();
                pb.worldChanges++;
            }
            link = value;
            hasLink = world.build(link) instanceof PowerBubbleNodeBuild;
            valid = checkRectangle();
            worldChanges++;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(link);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if(revision >= 1) {
                setLink(read.i());
            }
        }

        @Override
        public void created() {
            super.created();
            updateAll();
        }

        @Override
        public void placed() {
            super.placed();
            updateAll();
        }

        @Override
        public void updateProximity() {
            super.updateProximity();
            updateAll();
        }

        @Override
        public byte version() {
            return 1;
        }

        public float getPowerConsumption() {
            return powerUsage;
        }
    }
}
