package subvoyage.type.block.laser_old_blocks.unit;

import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.meta.BlockGroup;
import subvoyage.utility.SvCall;

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
                SvCall.unitBlockSpawn(tile);
            }
        }
    }
}
