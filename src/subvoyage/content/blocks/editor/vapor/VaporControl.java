package subvoyage.content.blocks.editor.vapor;

import arc.struct.Seq;
import mindustry.ai.Pathfinder;
import mindustry.game.FogControl;
import mindustry.io.SaveFileReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VaporControl implements SaveFileReader.CustomChunk {
    public static void load() {
        Seq<Pathfinder.PathCost> newCosts = Seq.with();
        Pathfinder.costTypes.forEach(e -> {
            //TODO: replace when vapor check is done
            newCosts.add((team, tile) -> false ? -1 : e.getCost(team,tile));
        });
    }

    @Override
    public void write(DataOutput stream) throws IOException {

    }

    @Override
    public void read(DataInput stream) throws IOException {

    }
}
