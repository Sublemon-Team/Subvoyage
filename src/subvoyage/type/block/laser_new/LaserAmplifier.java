package subvoyage.type.block.laser_new;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import subvoyage.type.block.laser.LaserUtil;

import static mindustry.Vars.tilesize;

public class LaserAmplifier extends Block implements LaserBlock {

    public TextureRegion heatRegion;
    public TextureRegion laserRegion;
    public TextureRegion laserTopRegion;
    public TextureRegion laserStartRegion;

    public IntSeq inputs = IntSeq.range(0,4);
    public IntSeq outputs = IntSeq.range(0,4);

    public short inputRange = 8,outputRange = 8;
    public byte maxSuppliers = 4;

    public float capacity = 60f;

    public LaserAmplifier(String name) {
        super(name);
        destructible = true;
        regionRotated1 = 1;
        update = true;
        quickRotate = true;
        solid = true;
        group = BlockGroup.logic;
        sync = true;

        rotate = true;
        rotateDraw = true;
        replaceable = true;
        allowDiagonal = false;
        drawArrow = false;
    }

    @Override
    public void init() {
        super.init();
        clipSize = Math.max(clipSize, Math.max(inputRange(),outputRange()) * tilesize);
    }
    @Override
    public void load() {
        super.load();
        laserRegion = Core.atlas.find(name+"-laser","subvoyage-power-laser");
        laserTopRegion = Core.atlas.find(name+"-laser-top","subvoyage-power-laser-top");
        laserStartRegion = Core.atlas.find(name+"-laser-start","subvoyage-power-laser-start");
        heatRegion = Core.atlas.find(name+"-heat");
    }

    @Override public short inputRange() {return inputRange;}
    @Override public short outputRange() {return outputRange;}

    @Override public byte maxSuppliers() {return maxSuppliers;}

    @Override public IntSeq inputs() {return inputs;}
    @Override public IntSeq outputs() {return outputs;}

    @Override public TextureRegion laserRegion() {return laserRegion;}
    @Override public TextureRegion laserStartRegion() {return laserStartRegion;}
    @Override public TextureRegion laserTopRegion() {return laserTopRegion;}



    public class LaserNodeBuild extends Building implements LaserBuild {

        private LaserGraph graph;


        @Override
        public void updateTile() {
            super.updateTile();
            updateLaser(this);
        }

        float smthScl = 0f;
        @Override
        public void draw() {
            drawStatus(this);
            super.draw();
            if(graph() == null) return;
            float laser = laser();
            float scl = Mathf.clamp(laser);
            smthScl = Mathf.lerp(smthScl, scl, Time.delta/20f);
            Color color = LaserUtil.getLaserColor(laser);
            for (Building consumer : graph().consumers) {
                Draw.color(color);
                drawLaser(x,y,consumer.x,consumer.y,size,consumer.block.size,smthScl);
            }
        }

        @Override
        public void onRemoved() {
            clearLaser(this);
        }
        @Override
        public void onDestroyed() {clearLaser(this);}

        @Override
        public void created() {
            graph = new LaserGraph();
        }



        @Override
        public float laser() {
            return graph().broken() ? 0f : inputLaser(this) * efficiency;
        }

        @Override
        public float laserRequirement() {
            return 0;
        }

        @Override
        public float maxPower() {
            return capacity;
        }

        @Override
        public boolean consumer() {
            return true;
        }

        @Override
        public boolean supplier() {
            return true;
        }

        @Override
        public LaserGraph graph() {
            return graph;
        }
    }
}
