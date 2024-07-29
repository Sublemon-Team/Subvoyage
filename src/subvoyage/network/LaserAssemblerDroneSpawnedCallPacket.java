package subvoyage.network;

import mindustry.gen.AssemblerDroneSpawnedCallPacket;
import mindustry.world.blocks.units.UnitAssembler;
import subvoyage.type.block.laser_blocks.unit.LaserUnitAssembler;

public class LaserAssemblerDroneSpawnedCallPacket extends AssemblerDroneSpawnedCallPacket {
    @Override
    public void handleClient() {
        LaserUnitAssembler.assemblerDroneSpawned(this.tile, this.id);
    }
}
