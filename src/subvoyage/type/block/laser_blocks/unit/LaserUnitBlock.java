package subvoyage.type.block.laser_blocks.unit;

import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.UnitBlockSpawnCallPacket;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.meta.BlockGroup;
import subvoyage.network.LaserUnitBlockSpawnCallPacket;

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
                if (Vars.net.server() || !Vars.net.active()) {
                    if(tile == null || !(tile.build instanceof LaserUnitBuild build)) return;
                    build.spawned();
                }

                if (Vars.net.server()) {
                    LaserUnitBlockSpawnCallPacket packet = new LaserUnitBlockSpawnCallPacket();
                    packet.tile = tile;
                    Vars.net.send(packet, true);
                }
            }
        }
    }
}
