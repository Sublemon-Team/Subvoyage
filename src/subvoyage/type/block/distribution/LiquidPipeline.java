package subvoyage.type.block.distribution;

import mindustry.gen.Building;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class LiquidPipeline extends ItemPipeline {
    public LiquidPipeline(String name) {
        super(name);
        hasItems = true;
        hasLiquids = true;
        outputsLiquid = true;

        canOverdrive = false;
        group = BlockGroup.liquids;
        envEnabled = Env.any;
    }

    public class LiquidPipelineBuild extends ItemPipelineBuild {
        @Override
        public void updateTransport(Building other){
            super.updateTransport(other);
            if(warmup >= 0.25f){
                moved |= moveLiquid(other, liquids.current()) > 0.05f;
            }
        }


        @Override
        public void doDump(){
            super.doDump();
            dumpLiquid(liquids.current(), 1f);
        }
    }
}
