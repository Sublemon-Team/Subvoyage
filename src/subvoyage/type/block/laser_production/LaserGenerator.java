package subvoyage.type.block.laser_production;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import subvoyage.content.other.SvPal;
import subvoyage.type.block.laser.LaserBlock;
import subvoyage.type.block.laser.LaserUtil;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class LaserGenerator extends LaserBlock {
    public TextureRegion laserRegion;
    public TextureRegion laserStartRegion;
    public TextureRegion topRegion1;
    public TextureRegion topRegion2;
    public int range;
    public LaserGenerator(String name) {
        super(name);
        rotate = true;
        rotateDraw = true;
    }

    @Override
    public void init() {
        super.init();
        clipSize = Math.max(clipSize, range * tilesize);
    }


    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[] {region,topRegion1};
    }
    @Override
    public void load() {
        super.load();
        laserRegion = Core.atlas.find(name+"-laser","subvoyage-power-laser");
        laserStartRegion = Core.atlas.find(name+"-laser-start","subvoyage-power-laser-start");
        topRegion1 = Core.atlas.find(name+"-top1");
        topRegion2 = Core.atlas.find(name+"-top2",topRegion1);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Draw.rect(rotation < 2 ? topRegion1 : topRegion2, x*tilesize, y*tilesize, (float)(rotation * 90));
    }

    public class LaserGeneratorBuild extends LaserBlockBuilding {

        public int lastChange = -2;

        @Override public boolean isConsumer() {
            return false;
        }
        @Override public boolean isSupplier() {
            return true;
        }

        @Override
        public void draw() {
            if(lasers == null) return;
            float scl = Math.max(0.1f,efficiency());
            Color color = LaserUtil.getLaserColor(lasers.power());
            for (Building consumer : lasers.graph.consumers) {
                Draw.color(color);
                drawLaser(x,y,consumer.x,consumer.y,size,consumer.block.size,scl);
            }
            Draw.color();
            Draw.rect(this.block.region, this.x, this.y, 0);
            this.drawTeamTop();
            Draw.rect(rotation < 2 ? topRegion1 : topRegion2, x, y, (float)(rotation * 90));
            if(heatRegion.found()) {
                Draw.color(SvPal.laserRed);
                Draw.blend(Blending.additive);
                Draw.alpha(lasers.smoothEfficiency);
                Draw.rect(heatRegion, x, y, (float)(rotation * 90));
                Draw.alpha(1f);
                Draw.blend(Blending.normal);
                Draw.color();
            }
        }
    }
}
