package subvoyage.type.block.power.node;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.Time;
import mindustry.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;
import subvoyage.content.other.*;

import java.util.*;

import static mindustry.Vars.*;
import static mindustry.world.blocks.power.PowerNode.makeBatteryBalance;

public class EnergyCross extends PowerBlock {
    public int range = 5;

    public TextureRegion laser;
    public TextureRegion laserEnd;

    public Color laserColor1 = Color.white;
    public Color laserColor2 = SvPal.powerLaser;
    public float pulseScl = 7, pulseMag = 0.05f;
    public float laserWidth = 0.4f;


    @Override
    public void load() {
        super.load();
        laser = Core.atlas.find(this.name + "-beam","power-beam");
        laserEnd = Core.atlas.find(this.name + "-beam-end","power-beam-end");
    }

    public EnergyCross(String name){
        super(name);
        consumesPower = outputsPower = false;
        drawDisabled = false;
        envEnabled |= Env.space;
        allowDiagonal = false;
        underBullets = true;
        priority = TargetPriority.transport;
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("power",entity -> {
            PowerGraph g = entity.power().graph;
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
    public void setStats(){
        super.setStats();
        stats.add(Stat.powerRange, range, StatUnit.blocks);
    }

    @Override
    public void init(){
        super.init();
        updateClipRadius((range + 1) * tilesize);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        for(int i = 0; i < 4; i++){
            int maxLen = range + size/2;
            Building dest = null;
            var dir = Geometry.d4[i];
            int dx = dir.x, dy = dir.y;
            int offset = size/2;
            for(int j = 1 + offset; j <= range + offset; j++){
                var other = world.build(x + j * dir.x, y + j * dir.y);

                //hit insulated wall
                if(other != null && other.isInsulated()){
                    break;
                }

                if(other != null && other.block.hasPower && other.team == Vars.player.team() && !(other.block instanceof PowerNode)){
                    maxLen = j;
                    dest = other;
                    break;
                }
            }

            Drawf.dashLine(Pal.placing,
                    x * tilesize + dx * (tilesize * size / 2f + 2),
                    y * tilesize + dy * (tilesize * size / 2f + 2),
                    x * tilesize + dx * (maxLen) * tilesize,
                    y * tilesize + dy * (maxLen) * tilesize
            );

            if(dest != null){
                Drawf.square(dest.x, dest.y, dest.block.size * tilesize/2f + 2.5f, 0f);
            }
        }
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation, boolean diagonal){
        if(!diagonal){
            Placement.calculateNodes(points, this, rotation, (point, other) -> Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) <= range);
        }
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

    public class BeamNodeBuild extends Building{
        //current links in cardinal directions
        public Building[] links = new Building[4];
        public Tile[] dests = new Tile[4];
        public int lastChange = -2;

        @Override
        public void updateTile(){
            if(lastChange != world.tileChanges){
                lastChange = world.tileChanges;
                updateDirections();
            }
        }

        @Override
        public BlockStatus status(){
            if(Mathf.equal(power.status, 0f, 0.001f)) return BlockStatus.noInput;
            if(Mathf.equal(power.status, 1f, 0.001f)) return BlockStatus.active;
            return BlockStatus.noOutput;
        }

        @Override
        public void draw(){
            super.draw();
            if(Mathf.zero(Renderer.laserOpacity)) return;

            Draw.z(Layer.power);
            Draw.color(laserColor1, laserColor2, (1f - power.graph.getSatisfaction()) * 0.86f + Mathf.absin(3f, 0.1f));
            Draw.alpha(Renderer.laserOpacity);
            float w = laserWidth + Mathf.absin(pulseScl, pulseMag);

            for(int i = 0; i < 4; i ++){
                if(dests[i] != null && links[i].wasVisible && (!(links[i].block instanceof BeamNode node) ||
                        (links[i].tileX() != tileX() && links[i].tileY() != tileY()) ||
                        (links[i].id > id && range >= node.range) || range > node.range)){

                    int dst = Math.max(Math.abs(dests[i].x - tile.x),  Math.abs(dests[i].y - tile.y));
                    //don't draw lasers for adjacent blocks
                    if(dst > 1 + size/2 && dests[i] != null && dests[i].build != null){
                        Draw.color(SvPal.powerLaser, Renderer.laserOpacity);
                        drawLaser(x,y,dests[i].worldx(),dests[i].worldy(),size,dests[i].build.block.size,0.5f,0f);
                    }
                }
            }

            Draw.reset();
        }

        @Override
        public void pickedUp(){
            Arrays.fill(links, null);
            Arrays.fill(dests, null);
        }

        public void updateDirections(){
            for(int i = 0; i < 4; i ++){
                var prev = links[i];
                var dir = Geometry.d4[i];
                links[i] = null;
                dests[i] = null;
                int offset = size/2;
                //find first block with power in range
                for(int j = 1 + offset; j <= range + offset; j++){
                    var other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);

                    //hit insulated wall
                    if(other != null && other.isInsulated()){
                        break;
                    }

                    //power nodes do NOT play nice with beam nodes, do not touch them as that forcefully modifies their links
                    if(other != null && !(other.block instanceof EnergyCross) && other.block instanceof PowerBubbleNode && other.block.hasPower && other.block.connectedPower && other.team == team){
                        links[i] = other;
                        dests[i] = world.tile(tile.x + j * dir.x, tile.y + j * dir.y);
                        break;
                    }
                }

                var next = links[i];

                if(next != prev){
                    //unlinked, disconnect and reflow
                    if(prev != null){
                        prev.power.links.removeValue(pos());
                        power.links.removeValue(prev.pos());

                        PowerGraph newgraph = new PowerGraph();
                        //reflow from this point, covering all tiles on this side
                        newgraph.reflow(this);

                        if(prev.power.graph != newgraph){
                            //reflow power for other end
                            PowerGraph og = new PowerGraph();
                            og.reflow(prev);
                        }
                    }

                    //linked to a new one, connect graphs
                    if(next != null){
                        power.links.addUnique(next.pos());
                        next.power.links.addUnique(pos());

                        power.graph.addGraph(next.power.graph);
                        next.power.graph.addGraph(power.graph);
                    }
                }
            }
        }
    }
}
