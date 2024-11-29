package subvoyage.content.other;

import arc.Events;
import mindustry.Vars;
import mindustry.game.*;
import mindustry.world.blocks.storage.CoreBlock;
import subvoyage.content.world.SvPlanets;
import subvoyage.content.block.SvStorage;

public class SvLoadouts {
    public static Schematic
            corePuffer;

    public static void load() {
        corePuffer = Schematics.readBase64("bXNjaAF4nGNgYWBhZmDJS8xNZWBMZuBOSS1OLsosKMnMz2NgYGDLSUxKzSlmYIqOZWQQLS5NKsuvTExP1U3OL0rVLShNS0stAqpiBCEgAQDM5RJw");
        Events.run(EventType.Trigger.update, () -> {
            if (!Vars.schematics.getLoadouts((CoreBlock) SvStorage.corePuffer).contains(corePuffer)) {
                Vars.schematics.getLoadouts((CoreBlock) SvStorage.corePuffer).clear();
                Vars.schematics.getLoadouts((CoreBlock) SvStorage.corePuffer).add(corePuffer);
            }
            SvPlanets.atlacian.defaultCore = SvStorage.corePuffer;
            SvPlanets.atlacian.generator.defaultLoadout = corePuffer;
        });
    }
}
