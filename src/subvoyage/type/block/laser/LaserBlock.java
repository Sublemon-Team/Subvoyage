package subvoyage.type.block.laser;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import subvoyage.content.other.SvStat;

import java.util.ArrayList;
import java.util.List;

import static mindustry.Vars.tilesize;

public class LaserBlock extends Block {

    public TextureRegion heatRegion;
    public TextureRegion laserRegion;
    public TextureRegion laserTopRegion;
    public TextureRegion laserStartRegion;

    private float consumeLaserPower = 0f;
    public boolean consumeLaser = true;
    public float minLaserEfficiency = 0f;

    public float outputLaserPower = 0f;
    public static float maxLaserPower = 300f;

    public int[] inputs = new int[0];
    public int[] outputs = new int[0];
    public int outputRange = 0;
    public int inputRange = 0;
    public int maxSuppliers = 4;

    public boolean drawInputs = true, drawOutputs = true;


    public LaserBlock(String name) {
        super(name);
        destructible = true;
        regionRotated1 = 1;
        update = true;
        quickRotate = true;
        solid = true;
        group = BlockGroup.logic;
        sync = true;
    }
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        List<LaserLink> links = LaserGraph.getLinks(x,y,rotation,this);
        LaserLink[] consLinks = new LaserLink[4];
        LaserLink[] suppLinks = new LaserLink[4];
        for (LaserLink link : links) {
            Building other = link.object;
            if(link.isConsumer) {
                Drawf.square(other.x,other.y,other.block.size/2f*tilesize,0,Pal.techBlue);
                consLinks[link.side] = link;
            }
            if(link.isSupplier) {
                Drawf.square(other.x,other.y,other.block.size/2f*tilesize,0,Pal.heal);
                suppLinks[link.side] = link;
            }
        }
        List<Integer> selfInputs = new ArrayList<>();
        List<Integer> selfOutputs = new ArrayList<>();
        for (int input : inputs) selfInputs.add((input+rotation)%4);
        for (int output : outputs) selfOutputs.add((output+rotation)%4);
        for (int i = 0; i < 4; i++) {
            Point2 dir = Geometry.d4[i];
            int dx = dir.x, dy = dir.y;

            float inputLen = inputRange;
            float outputLen = outputRange;
            LaserLink consLink = consLinks[i];
            LaserLink suppLink = suppLinks[i];

            if(!selfInputs.contains(i) || !drawInputs) inputLen = 0;
            if(!selfOutputs.contains(i) || !drawOutputs) outputLen = 0;
            if(consLink != null)
                outputLen = consLink.len - consLink.object.block.size/2f;
            if(suppLink != null)
                inputLen = suppLink.len - suppLink.object.block.size/2f;

            Drawf.dashLine(Pal.techBlue,
                    x * tilesize + dx*size/2f*tilesize,
                    y * tilesize + dy*size/2f*tilesize,
                    x * tilesize + dx*size/2f*tilesize + outputLen * tilesize * dx,
                    y * tilesize + dy*size/2f*tilesize + outputLen * tilesize * dy);
            Drawf.dashLine(Pal.heal,
                    x * tilesize + dx*size/2f*tilesize,
                    y * tilesize + dy*size/2f*tilesize,
                    x * tilesize + dx*size/2f*tilesize + inputLen * tilesize * dx,
                    y * tilesize + dy*size/2f*tilesize + inputLen * tilesize * dy);

            if(selfOutputs.contains(i) && drawOutputs) {
                Drawf.arrow(
                        x * tilesize  + dx*size/2f*tilesize,
                        y * tilesize + dy*size/2f*tilesize,
                        x * tilesize + dx*size*tilesize,
                        y * tilesize + dy*size*tilesize,
                        size/4f*tilesize,
                        size/4f*tilesize,
                        Pal.techBlue);
            }

            if(selfInputs.contains(i) && drawInputs) {
                Drawf.arrow(
                        x * tilesize  + dx*size*tilesize,
                        y * tilesize + dy*size*tilesize,
                        x * tilesize - dx*size/2f*tilesize,
                        y * tilesize - dy*size/2f*tilesize,
                        size/4f*tilesize,
                        size/4f*tilesize,
                        Pal.heal);
            }
        }
    }

    public void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2, float scl){
        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f - 2f, len2 = size2 * tilesize / 2f - 2f;

        float layer = Draw.z();
        Draw.z(Layer.blockOver);
       // Draw.blend(Blending.additive);
        Drawf.laser(laserRegion, laserStartRegion, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, scl);
        //Draw.blend();
        Draw.color();
        //Draw.blend(Blending.additive);
        Drawf.laser(laserTopRegion, laserStartRegion, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, scl);
        //Draw.blend();
        Draw.z(layer);
    }

    @Override
    public void load() {
        super.load();
        laserRegion = Core.atlas.find(name+"-laser","subvoyage-power-laser");
        laserTopRegion = Core.atlas.find(name+"-laser-top","subvoyage-power-laser-top");
        laserStartRegion = Core.atlas.find(name+"-laser-start","subvoyage-power-laser-start");
        heatRegion = Core.atlas.find(name+"-heat");
    }

    @Override
    public void setStats() {
        super.setStats();
        if(outputLaserPower != 0) stats.add(SvStat.laserOutput,outputLaserPower);
        if(consumeLaserPower > 0 && consumeLaser) stats.add(SvStat.laserUse,consumeLaserPower);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("laser_power",(b) -> {
            if(b instanceof LaserBlockBuilding lb) {
                return new Bar(
                        () -> Core.bundle.format("bar.sv_laser_power", Strings.fixed(lb.lasers.power() <= 0.001 ? 0 : lb.lasers.power()+0.1f, 1)),
                        () -> LaserUtil.getLaserColor(lb.lasers.power()),
                        lb::laserEfficiency);
            } else return new Bar(
                    () -> Core.bundle.format("bar.sv_laser_power", Strings.fixed(0f, 1)),
                    () -> LaserUtil.getLaserColor(0f),
                    () -> 1f);
        });
    }

    public void consumeLaserPower(float power) {
        consumeLaserPower = power;
    }
    public void setLaserInputs(int ...inputs) {this.inputs = inputs;}
    public void setLaserOutputs(int ...outputs) {this.outputs = outputs;}

    public class LaserBlockBuilding extends Building {
        @Nullable
        public LaserModule lasers;

        @Override
        public void updateTile() {
            super.updateTile();
            if(lasers == null) return;
            lasers.update(this);
            if(lasers.power() >= maxLaserPower) {
                Fx.hitMeltdown.create(x,y,0, Pal.accent,new Object());
                damage(10);
            }
            lasers.graph.consumers.each(consumer -> {
                if(consumer instanceof LaserBlockBuilding lb) {
                    lb.lasers.setPower(lasers.power());
                }
            });
        }

        @Override
        public void onRemoved() {
            lasers.graph.clearGraph(this);
        }
        @Override
        public void onDestroyed() {
            lasers.graph.clearGraph(this);
        }

        @Override
        public void created() {
            lasers = new LaserModule();
        }

        public boolean isConsumer() {
            return true;
        }
        public boolean isSupplier() {
            return false;
        }
        @Override
        public float efficiency() {
            if(lasers == null) return 0f;
            return lasers.smoothEfficiency * laserEfficiency();
        }

        public float laserEfficiency() {
            float raw = rawLaserEfficiency();
            return raw >= minLaserEfficiency ? raw : 0f;
        }
        public float rawLaserEfficiency() {
            if(lasers == null) return 0f;
            return consumeLaserPower <= 0f ? 1f : Mathf.clamp(lasers.power()/consumeLaserPower);
        }
    }
}
