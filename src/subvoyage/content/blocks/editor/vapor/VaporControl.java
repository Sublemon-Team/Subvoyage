package subvoyage.content.blocks.editor.vapor;

import arc.Events;
import arc.struct.Bits;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.noise.Simplex;
import mindustry.ai.Pathfinder;
import mindustry.game.EventType;
import mindustry.game.FogControl;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.graphics.FogRenderer;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.world.Tile;
import subvoyage.content.blocks.SvWorldBlocks;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static mindustry.Vars.*;
import static subvoyage.SubvoyageMod.vaporControl;

public class VaporControl implements SaveFileReader.CustomChunk {

    private static volatile int ww, wh;
    private volatile @Nullable VaporData map;
    public static int layer = 154;


    public VaporControl() {
        Events.on(EventType.ResetEvent.class,e -> stop());
        Events.on(EventType.WorldLoadEvent.class,e -> {
            stop();
            ww = world.width();
            wh = world.height();
        });
        Events.on(EventType.BlockBuildBeginEvent.class,e -> {
            if(isVaporised(e.tile.x,e.tile.y) && !state.isEditor()) {
                e.tile.remove();
                e.unit.removeBuild(e.tile.x,e.tile.y,true);
            }
        });
        Events.on(EventType.TileChangeEvent.class,e -> {
            if(e.tile.overlay() instanceof VaporFloor) {
                setVaporised(e.tile.x,e.tile.y,true);
                e.tile.clearOverlay();
            }
        });
        SaveVersion.addCustomChunk("subvoyage-vapor-data", this);
    }


    public static void load() {
        Seq<Pathfinder.PathCost> newCosts = Seq.with();
        Pathfinder.costTypes.forEach(e -> {
            //TODO: replace when vapor check is done
            newCosts.add((team, tile) ->
                    vaporControl.isVaporised(world.tile(tile).x,world.tile(tile).y) ? -1 : e.getCost(team,tile));
        });
    }

    public boolean isVaporised(int x,int y) {
        if(map == null) return false;
        if(x < 0 || y < 0 || x >= ww || y >= wh) return false;
        return map.read.get(x + y * ww);
    }
    public void setVaporised(int x,int y,boolean is) {
        if(map == null) return;
        if(x < 0 || y < 0 || x >= ww || y >= wh);
        map.read.set(x + y * ww,is);
    }
    public Bits getVapor() {
        return map.read;
    }


    void stop() {
        map = null;
    }

    @Override
    public boolean shouldWrite() {
        //TODO: maybe some rule-similar thingy?
        return SaveFileReader.CustomChunk.super.shouldWrite();
    }

    boolean changed = false;
    public void update() {
        if(map == null) map = new VaporData();


        for (Tile tile : world.tiles) {
            if(tile.overlay() == SvWorldBlocks.vapor) {
                setVaporised(tile.x,tile.y,true);
                changed = true;
                tile.clearOverlay();
            }
        }

        Groups.unit.forEach(u -> {
            if(u.team == Team.sharded && isVaporised(u.tileX(),u.tileY())) u.damage(1f);
        });
    }



    @Override
    public void write(DataOutput stream) throws IOException {
        stream.writeBoolean(map == null);
        if(map == null) return;
        stream.writeShort(world.width());
        stream.writeShort(world.height());
        stream.writeInt(map.read.length());
        for (int i = 0; i < map.read.length(); i++) {
            stream.writeBoolean(map.read.get(i));
        }
    }

    @Override
    public void read(DataInput stream) throws IOException {
        if(stream.readBoolean()) return;
        ww = stream.readShort();
        wh = stream.readShort();
        int len = stream.readInt();
        if(map == null) map = new VaporData();
        for (int i = 0; i < len; i++) {
            map.write.set(i,stream.readBoolean());
        }
        changed = true;
    }


    void updateDynamic() {
        changed = true;
    }

    static class VaporData {
        volatile Bits read,write;
        long lastMs = 0;
        boolean updated = true;

        VaporData() {
            int len = ww* wh;
            read = new Bits(len);
            write = read;
        }
    }
}
