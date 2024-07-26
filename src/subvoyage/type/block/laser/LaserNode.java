package subvoyage.type.block.laser;

import arc.Core;
import arc.flabel.effects.GradientEffect;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.ui.dialogs.EffectsDialog;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Duct;
import subvoyage.type.block.laser_production.LaserGenerator;
import subvoyage.type.block.power.node.EnergyCross;

import static mindustry.Vars.*;

public class LaserNode extends LaserBlock {

    public TextureRegion topRegion1;
    public TextureRegion topRegion2;
    public int range;


    public LaserNode(String name) {
        super(name);
        rotate = true;
        rotateDraw = true;
        size = 2;
        replaceable = true;
        allowDiagonal = false;
        drawArrow = false;
    }

    @Override
    public void init() {
        super.init();
        clipSize = Math.max(clipSize, range * tilesize);
    }

    @Override
    public void load() {
        super.load();
        topRegion1 = Core.atlas.find(name+"-top1");
        topRegion2 = Core.atlas.find(name+"-top2",topRegion1);
    }


    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
    }
    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, range * tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[] {region,topRegion1};
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Draw.rect(rotation < 2 ? topRegion1 : topRegion2, x*tilesize+ size/4f*tilesize, y*tilesize+ size/4f*tilesize, (float)(rotation * 90));
        int offset = size/2;
        boolean foundConsumer = false;
        for(int i = 0; i < 4; i ++){
            Point2 dir = Geometry.d4[i];
            for(int j = 1 + offset; j <= range + offset; j++){
                var other = world.build(x + j * dir.x, y + j * dir.y);
                if(other != null && other.isInsulated()){
                    break;
                }
                if(other != null && other.block instanceof LaserBlock lb){
                    if((other.rotation+2)%4 == rotation && ((i+2)%4 == other.rotation || i == rotation)) {
                        break;
                    }
                    LaserBlockBuilding build = ((LaserBlockBuilding) other);
                    if((i+2)%4 == other.rotation && build.isSupplier()) {
                        //supplier
                        int dx = dir.x, dy = dir.y;
                        if(!(other.block instanceof LaserGenerator g && j-offset > g.range)) {
                            Drawf.square(x * tilesize + size / 4f * tilesize, y * tilesize + size / 4f * tilesize, size / 2f * tilesize, 0, Pal.accent);
                            Drawf.dashLine(Pal.heal,
                                    x * tilesize + size / 4f * tilesize + dx * size / 2f * tilesize,
                                    y * tilesize + size / 4f * tilesize + dy * size / 2f * tilesize,
                                    other.x - dx * other.block.size / 2f * tilesize,
                                    other.y - dy * other.block.size / 2f * tilesize);
                            Drawf.square(other.x, other.y, other.block.size / 2f * tilesize, 0, Pal.heal);
                        }
                    }
                    if(i == rotation && build.isConsumer()) {
                        //consumer
                        int dx = dir.x, dy = dir.y;
                        Drawf.square(other.x,other.y,other.block.size/2f*tilesize,0,Pal.heal);
                        Drawf.dashLine(Pal.techBlue,
                                x * tilesize + size/4f*tilesize + dx*size/2f*tilesize,
                                y * tilesize + size/4f*tilesize + dy*size/2f*tilesize,
                                other.x - dx*other.block.size/2f*tilesize,
                                other.y - dy*other.block.size/2f*tilesize);
                        Drawf.square(other.x,other.y,other.block.size/2f*tilesize,0,Pal.techBlue);
                        foundConsumer = true;
                    }
                    break;
                }
            }
        }
        Point2 dir = Geometry.d4[rotation];
        int dx = dir.x, dy = dir.y;
        if(!foundConsumer)
            Drawf.dashLine(Pal.techBlue,
                    x * tilesize + size/4f*tilesize + dx*size/2f*tilesize,
                    y * tilesize + size/4f*tilesize + dy*size/2f*tilesize,
                    x * tilesize + size/4f*tilesize - dx*size/2f*tilesize + dir.x*range*tilesize,
                    y * tilesize + size/4f*tilesize - dy*size/2f*tilesize + dir.y*range*tilesize);
        Drawf.arrow(
                x * tilesize + size/4f*tilesize + dx*size/2f*tilesize,
                y * tilesize + size/4f*tilesize + dy*size/2f*tilesize,
                x * tilesize + size/4f*tilesize + dx*size*tilesize,
                y * tilesize + size/4f*tilesize + dy*size*tilesize,
                size/4f*tilesize,
                size/4f*tilesize,
                Pal.techBlue
                );
    }


    public class LaserNodeBuild extends LaserBlockBuilding {

        public int lastChange = -2;

        @Override
        public void created() {
            super.created();
            reloadLinks();
        }

        public void reloadLinks() {
            int offset = size/2;
            lasers.graph.clearGraph(this);
            for(int i = 0; i < 4; i ++){
                Point2 dir = Geometry.d4[i];
                for(int j = 1 + offset; j <= range + offset; j++){
                    var other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);
                    if(other != null && other.isInsulated()){
                        break;
                    }
                    if(other != null && other.block instanceof LaserBlock lb){
                        if((other.rotation+2)%4 == rotation && ((i+2)%4 == other.rotation || i == rotation)) {
                            Fx.coreLaunchConstruct.create(other.x,other.y,0,Pal.accent,new Object());
                            Fx.unitEnvKill.create(other.x,other.y,0,Pal.accent,new Object());
                            Fx.coreLaunchConstruct.create(x,y,0,Pal.accent,new Object());
                            Fx.unitEnvKill.create(x,y,0,Pal.accent,new Object());
                            Sounds.plasmadrop.play(1f,2.5f,0f);
                            break;
                        }
                        if((i+2)%4 == other.rotation) {
                            LaserBlockBuilding lbuild = (LaserBlockBuilding) other;
                            lbuild.lasers.graph.addConsumer(this);
                            lbuild.lasers.graph.suppliers.remove(this);
                            lasers.graph.addSupplier(lbuild);
                            lasers.graph.consumers.remove(lbuild);
                        }
                        if(i == rotation) {
                            LaserBlockBuilding lbuild = (LaserBlockBuilding) other;
                            lbuild.lasers.graph.addSupplier(this);
                            lbuild.lasers.graph.consumers.remove(this);
                            lasers.graph.addConsumer(lbuild);
                            lasers.graph.suppliers.remove(lbuild);
                        }
                        break;
                    }
                }
            }
        }

        @Override
        public void placed() {
            super.placed();
            reloadLinks();
        }

        @Override
        public boolean isSupplier() {
            return true;
        }

        @Override
        public boolean isConsumer() {
            return true;
        }


        @Override
        public void updateTile() {
            super.updateTile();
            if(lastChange != world.tileChanges){
                lastChange = world.tileChanges;
                reloadLinks();
            }
            if(lasers.graph.suppliers.size > 1 /*&& false*/) {
                for (Building supplier : lasers.graph.suppliers) {
                    Fx.coreLaunchConstruct.create(supplier.x,supplier.y,0,Pal.accent,new Object());
                    Fx.unitEnvKill.create(supplier.x,supplier.y,0,Pal.accent,new Object());
                }
                Fx.coreLaunchConstruct.create(x,y,0,Pal.accent,new Object());
                Fx.unitEnvKill.create(x,y,0,Pal.accent,new Object());
                Sounds.plasmadrop.play(1f,2f,0f);
                lasers.graph.removeSuppliers(this);
            }
        }

        @Override
        public float efficiency() {
            return 1f;
        }

        @Override
        public void draw() {
            float scl = Math.max(0.2f,rawLaserEfficiency())*0.5f;;
            Color color = LaserUtil.getLaserColor(lasers.power());
            for (Building consumer : lasers.graph.consumers) {
                Draw.color(color);
                drawLaser(x,y,consumer.x,consumer.y,size,consumer.block.size,scl);
            }
            Draw.color();
            Draw.rect(this.block.region, this.x, this.y, 0);
            this.drawTeamTop();
            Draw.rect(rotation < 2 ? topRegion1 : topRegion2, this.x, this.y, (float)(this.rotation * 90));
        }
    }

}
