package subvoyage.utility;

import mindustry.Vars;
import mindustry.gen.AssemblerDroneSpawnedCallPacket;
import mindustry.gen.AssemblerUnitSpawnedCallPacket;
import mindustry.gen.UnitBlockSpawnCallPacket;
import mindustry.world.Tile;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitBlock;
import subvoyage.network.LaserAssemblerDroneSpawnedCallPacket;
import subvoyage.network.LaserAssemblerUnitSpawnedCallPacket;
import subvoyage.network.LaserUnitBlockSpawnCallPacket;
import subvoyage.type.block.laser_blocks.unit.LaserUnitAssembler;
import subvoyage.type.block.laser_blocks.unit.LaserUnitBlock;

public class SvCall {
    public static void assemblerDroneSpawned(Tile tile, int id) {
        if (Vars.net.server() || !Vars.net.active()) {
            LaserUnitAssembler.assemblerDroneSpawned(tile, id);
        }

        if (Vars.net.server()) {
            LaserAssemblerDroneSpawnedCallPacket packet = new LaserAssemblerDroneSpawnedCallPacket();
            packet.tile = tile;
            packet.id = id;
            Vars.net.send(packet, true);
        }
    }
    public static void assemblerUnitSpawned(Tile tile) {
        if (Vars.net.server() || !Vars.net.active()) {
            LaserUnitAssembler.assemblerUnitSpawned(tile);
        }

        if (Vars.net.server()) {
            LaserAssemblerUnitSpawnedCallPacket packet = new LaserAssemblerUnitSpawnedCallPacket();
            packet.tile = tile;
            Vars.net.send(packet, true);
        }
    }

    public static void unitBlockSpawn(Tile tile) {
        if (Vars.net.server() || !Vars.net.active()) {
            if(tile == null || !(tile.build instanceof LaserUnitBlock.LaserUnitBuild build)) return;
            build.spawned();
        }

        if (Vars.net.server()) {
            LaserUnitBlockSpawnCallPacket packet = new LaserUnitBlockSpawnCallPacket();
            packet.tile = tile;
            Vars.net.send(packet, true);
        }
    }
}
