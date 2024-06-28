package subvoyage.content.blocks.energy;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.entities.TargetPriority;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.world.Tile;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.util.Arrays;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class EnergyCross extends PowerBlock {
    public int range = 5;

    public TextureRegion laser;
    public TextureRegion laserEnd;

    public Color laserColor1 = Color.white;
    public Color laserColor2 = Color.valueOf("D9F2FF");
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
        addBar("power", PowerNode.makePowerBalance());
        addBar("batteries", PowerNode.makeBatteryBalance());
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

    public class BeamNodeBuild extends Building{
        //current links in cardinal directions
        public Building[] links = new Building[4];
        public Tile[] dests = new Tile[4];
        public int lastChange = -2;

        @Override
        public void updateTile(){
            //TODO this block technically does not need to update every frame, perhaps put it in a special list.
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
                    if(dst > 1 + size/2){
                        var point = Geometry.d4[i];
                        float poff = tilesize/2f;
                        Drawf.laser(laser, laserEnd, x + poff*size*point.x, y + poff*size*point.y, dests[i].worldx() - poff*point.x, dests[i].worldy() - poff*point.y, w);
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
                    if(other != null && !(other.block instanceof EnergyCross) && other.block.hasPower && other.block.connectedPower && other.team == team){
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
                    }
                }
            }
        }
    }
}
