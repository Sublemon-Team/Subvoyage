package subvoyage.type.block.laser.blocks;

import arc.math.Mathf;
import arc.struct.IntSeq;
import mindustry.world.blocks.production.GenericCrafter;
import subvoyage.type.block.laser.LaserBlock;
import subvoyage.type.block.laser.LaserBuild;
import subvoyage.type.block.laser.LaserGraph;

public class LaserCrafter extends GenericCrafter implements LaserBlock {
    public IntSeq inputs = IntSeq.with(0,1,2,3);
    public IntSeq outputs = IntSeq.with();

    public short inputRange = 0,outputRange = 8;
    public byte maxSuppliers = 4;

    public float capacity = 60f;

    public float laserRequirement = 0f;
    public float laserMaxEfficiency = 1f;
    public float laserOverpowerScale = 1f;
    public float laserOutput = 0f;

    public boolean drawInputs = false;
    public boolean drawOutputs = true;

    public LaserCrafter(String name) {
        super(name);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        if(valid) drawLinks(this,x,y,rotation,drawInputs,drawOutputs);
    }

    @Override public short inputRange() {return inputRange;}
    @Override public short outputRange() {return outputRange;}

    @Override public byte maxSuppliers() {return maxSuppliers;}

    @Override public IntSeq inputs() {return inputs;}
    @Override public IntSeq outputs() {return outputs;}


    public class LaserCrafterBuild extends GenericCrafterBuild implements LaserBuild {
        LaserGraph graph;

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
        public float efficiencyScale() {
            float over = Math.max(laser() - laserRequirement(), 0.0F);
            return Math.min(Mathf.clamp(laser() / laserRequirement()) + over / laserRequirement() * laserOverpowerScale, laserMaxEfficiency);
        }

        @Override
        public float warmupTarget() {
            return Mathf.clamp(laser() / laserRequirement());
        }

        @Override
        public float laser() {
            return graph().broken() ? 0f : inputLaser(this)+laserOutput*efficiency;
        }
        @Override
        public float rawLaser() {
            return inputLaser(this)+ laserOutput * efficiency;
        }

        @Override
        public float laserRequirement() {
            return laserRequirement;
        }

        @Override
        public float maxPower() {
            return capacity;
        }

        @Override
        public boolean consumer() {
            return false;
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
