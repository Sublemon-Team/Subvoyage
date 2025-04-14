package subvoyage.type.world;

import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class SvSectorPreset extends SectorPreset {
    public static ObjectMap<Sector, Cons<GameState>> scripts = new ObjectMap<>();

    static {
        Events.run(EventType.Trigger.update, () -> {
            if (Vars.state.getSector() != null) scripts.get(Vars.state.getSector(), (a) -> {}).get(Vars.state);
        });
    }

    public SvSectorPreset(String name, Planet planet, int sector, Cons<GameState> run) {
        super(name, planet, sector);
    }
    public SvSectorPreset(String name, Planet planet, int sector) {
        super(name, planet, sector);
    }

    /**
     * returns true if a flag is present.
     */
    public static boolean getFlag(String flag, boolean remove) {
        if (Vars.state.rules.objectiveFlags.isEmpty()) return false;
        if (remove) return Vars.state.rules.objectiveFlags.remove(flag);
        return Vars.state.rules.objectiveFlags.contains(flag);
    }

    public static void setLimit() {
        Vars.state.rules.limitMapArea = false;
    }
    public static void setLimit(int x, int y, int x2, int y2) {
        Vars.state.rules.limitMapArea = true;
        Vars.state.rules.limitX = x;
        Vars.state.rules.limitY = y;
        Vars.state.rules.limitWidth = x2 - x;
        Vars.state.rules.limitHeight = y2 - y;
    }
    public static void buildWithEffect(int x, int y, Block block) {
        buildWithEffect(x,y,block,Vars.player.team(),0);
    }
    public static void buildWithEffect(int x, int y, Block block, Team team) {
        buildWithEffect(x,y,block,team,0);
    }
    public static void buildWithEffect(int x, int y, Block block, int rotation) {
        buildWithEffect(x,y,block,Vars.player.team(),rotation);
    }
    public static void buildWithEffect(int x, int y, Block block, Team team, int rotation) {
        block.placeEffect.at(x*tilesize,y*tilesize,block.size);
        Tile tile = world.tile(x, y);
        if(!block.isFloor() || block == Blocks.air)
            if(tile.block() != block || tile.team() != team)
                tile.setNet(block, team, Mathf.clamp(rotation, 0, 3));
    }
}
