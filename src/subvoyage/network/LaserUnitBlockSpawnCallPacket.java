package subvoyage.network;

import mindustry.gen.UnitBlockSpawnCallPacket;
import subvoyage.type.block.laser_old_blocks.unit.LaserUnitBlock;

public class LaserUnitBlockSpawnCallPacket extends UnitBlockSpawnCallPacket {
    @Override
    public void handleClient() {
        if(tile == null || !(tile.build instanceof LaserUnitBlock.LaserUnitBuild build)) return;
        build.spawned();
    }
}
