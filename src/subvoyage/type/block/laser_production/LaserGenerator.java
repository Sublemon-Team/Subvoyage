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
                    if(i == rotation && build.isConsumer()) {
                        //consumer
                        int dx = dir.x, dy = dir.y;
                        Drawf.square(other.x,other.y,other.block.size/2f*tilesize,0,Pal.heal);
                        Drawf.dashLine(Pal.techBlue,
                                x * tilesize + dx*size/2f*tilesize,
                                y * tilesize + dy*size/2f*tilesize,
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
                    x * tilesize + dx*size/2f*tilesize,
                    y * tilesize + dy*size/2f*tilesize,
                    x * tilesize - dx*size/2f*tilesize + dir.x*range*tilesize,
                    y * tilesize - dy*size/2f*tilesize + dir.y*range*tilesize);
        Drawf.arrow(
                x * tilesize  + dx*size/2f*tilesize,
                y * tilesize + dy*size/2f*tilesize,
                x * tilesize + dx*size*tilesize,
                y * tilesize + dy*size*tilesize,
                size/4f*tilesize,
                size/4f*tilesize,
                Pal.techBlue
        );
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
            float scl = Math.max(0.2f,efficiency())*0.5f;
            Color color = LaserUtil.getLaserColor(lasers.power());
            for (Building consumer : lasers.graph.consumers) {
                Draw.color(color);
                drawLaser(x,y,consumer.x,consumer.y,size,consumer.block.size,scl);
            }
            Draw.color();
            Draw.rect(this.block.region, this.x, this.y, 0);
            this.drawTeamTop();
            Draw.rect(rotation < 2 ? topRegion1 : topRegion2, x, y, (float)(rotation * 90));
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(lastChange != world.tileChanges){
                lastChange = world.tileChanges;
            }
        }
    }
}
