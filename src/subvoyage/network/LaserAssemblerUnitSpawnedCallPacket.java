package subvoyage.network;

import mindustry.gen.AssemblerUnitSpawnedCallPacket;
import mindustry.world.blocks.units.UnitAssembler;
import subvoyage.type.block.laser_blocks.unit.LaserUnitAssembler;

public class LaserAssemblerUnitSpawnedCallPacket extends AssemblerUnitSpawnedCallPacket {
    @Override
    public void handleClient() {
        LaserUnitAssembler.assemblerUnitSpawned(this.tile);
    }
}
