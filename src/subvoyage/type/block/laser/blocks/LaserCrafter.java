package subvoyage.type.block.laser.blocks;

import arc.Core;
import arc.math.Mathf;
import arc.struct.IntSeq;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import subvoyage.type.block.laser.LaserBlock;
import subvoyage.type.block.laser.LaserBuild;
import subvoyage.type.block.laser.LaserGraph;
import subvoyage.type.block.laser.LaserUtil;

public class LaserCrafter extends GenericCrafter implements LaserBlock {
    public IntSeq inputs = IntSeq.with(0,1,2,3);
    public IntSeq outputs = IntSeq.with();

    public short inputRange = 8,outputRange = 0;
    public byte maxSuppliers = 1;

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
    public void setBars() {
        super.setBars();
        addBar("laser", (entity) -> {
            if(entity instanceof LaserBuild lb)
                return new Bar(
                        () -> Core.bundle.format("bar.laserpercent", (int)(lb.rawLaser() + 0.01F), (int)(entity.efficiencyScale() * 100.0F + 0.01F)),
                        () -> LaserUtil.getLaserColor(lb.rawLaser()),
                        () -> lb.laser() / lb.laserRequirement());
            return new Bar();
        });
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
