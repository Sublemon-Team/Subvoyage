package subvoyage.type.block.laser_old_blocks.node;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.world.Tile;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.other.SvPal;
import subvoyage.type.block.laser_old.LaserBlock;
import subvoyage.type.block.laser.LaserUtil;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class LaserSplitter extends LaserBlock {

    public TextureRegion topRegion1;
    public TextureRegion topRegion2;
    public int range;


    public LaserSplitter(String name) {
        super(name);
        rotate = true;
        rotateDraw = true;
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
        region = SvBlocks.laserNode.region;
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
        Draw.rect(rotation < 2 ? topRegion1 : topRegion2, x*tilesize, y*tilesize, (float)(rotation * 90));
    }


    public class LaserNodeBuild extends LaserBlockBuilding {

        public int lastChange = -2;

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
            if(lasers == null) return;
            lasers.supplierLaserMultiplier = 0.5f;
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
        public void draw() {
            if(lasers == null) return;
            float scl = Math.max(0.1f,rawLaserEfficiency());;
            Color color = LaserUtil.getLaserColor(lasers.power());
            for (Building consumer : lasers.graph.consumers) {
                Draw.color(color);
                drawLaser(x,y,consumer.x,consumer.y,size,consumer.block.size,scl);
            }
            Draw.color();
            Draw.rect(this.block.region, this.x, this.y, 0);
            this.drawTeamTop();
            Draw.rect(rotation < 2 ? topRegion1 : topRegion2, this.x, this.y, (float)(this.rotation * 90));
            if(heatRegion.found()) {
                Draw.color(SvPal.laserRed);
                Draw.blend(Blending.additive);
                Draw.alpha(rawLaserEfficiency());
                Draw.rect(heatRegion, x, y, (float)(rotation * 90));
                Draw.alpha(1f);
                Draw.blend(Blending.normal);
                Draw.color();
            }
        }
    }

}
