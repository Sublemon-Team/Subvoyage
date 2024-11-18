package subvoyage.type.block.laser.blocks;

import arc.math.Mathf;
import arc.struct.IntSeq;
import mindustry.world.blocks.units.Reconstructor;
import subvoyage.type.block.laser.LaserBlock;
import subvoyage.type.block.laser.LaserBuild;
import subvoyage.type.block.laser.LaserGraph;

public class LaserReconstructor extends Reconstructor implements LaserBlock {
    public float laserRequirement = 0f;

    public IntSeq inputs = IntSeq.with(0,1,2,3);
    public IntSeq outputs = IntSeq.with();

    public short inputRange = 8,outputRange = 0;
    public byte maxSuppliers = 1;

    public float capacity = 300f;

    public float laserMaxEfficiency = 1f;
    public float laserOverpowerScale = 1f;

    public boolean drawInputs = false;
    public boolean drawOutputs = true;

    public LaserReconstructor(String name) {
        super(name);
    }


    @Override public short inputRange() {return inputRange;}
    @Override public short outputRange() {return outputRange;}

    @Override public byte maxSuppliers() {return maxSuppliers;}

    @Override public IntSeq inputs() {return inputs;}
    @Override public IntSeq outputs() {return outputs;}

    public class LaserReconstructorBuild extends ReconstructorBuild implements LaserBuild {
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
        public float laser() {
            return graph().broken() ? 0f : inputLaser(this);
        }
        @Override
        public float rawLaser() {
            return inputLaser(this);
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
            return true;
        }

        @Override
        public boolean supplier() {
            return false;
        }

        @Override
        public LaserGraph graph() {
            return graph;
        }

        @Override
        public void draw() {
            drawStatus(this);
            super.draw();
        }
        @Override
        public void updateTile() {
            super.updateTile();
            updateLaser(this);
        }
    }

}
