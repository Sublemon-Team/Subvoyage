package subvoyage.type.block.laser_blocks.unit;

import mindustry.gen.Call;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.meta.BlockGroup;

public class LaserUnitBlock extends LaserPayloadBlock {
    public LaserUnitBlock(String name) {
        super(name);
        group = BlockGroup.units;
        outputsPayload = true;
        rotate = true;
        update = true;
        solid = true;
    }
    public class LaserUnitBuild extends LaserPayloadBlockBuild<UnitPayload> {
        public float progress, time, speedScl;

        public void spawned(){
            progress = 0f;
            payload = null;
        }

        @Override
        public void dumpPayload(){
            if(payload.dump()){
                Call.unitBlockSpawn(tile);
            }
        }
    }
}
