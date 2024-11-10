package subvoyage.network;

import mindustry.gen.AssemblerUnitSpawnedCallPacket;
import subvoyage.type.block.laser_old_blocks.unit.LaserUnitAssembler;

public class LaserAssemblerUnitSpawnedCallPacket extends AssemblerUnitSpawnedCallPacket {
    @Override
    public void handleClient() {
        LaserUnitAssembler.assemblerUnitSpawned(this.tile);
    }
}
