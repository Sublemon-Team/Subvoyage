package subvoyage.content.world.planets;

import mindustry.game.*;

public class SvLoadouts{
    public static Schematic
        basicPuffer,basicShore,basicReef;

    public static void load() {
        basicPuffer = Schematics.readBase64("bXNjaAF4nGNgYWBhZmDJS8xNZeBOzi9K1S0oTUtLLWLgTkktTi7KLCjJzM9jYGBgy0lMSs0pZmCKjmVkEC0uTSrLr0xMT9VF1sLAwAhCQAIA8R4WdQ==");
        basicShore = Schematics.readBase64("bXNjaAF4nE3MzQpAQBRA4UN+iq3X8ESyuMaNKWPkDuXtWerUtzyUlAXFLkFpXDy1t/WT2pJK8DONuVWDJO+MdlZzpz+SjztQbTLpZuTDmNHZNd3xkUX73wYyyL94AShUHMY=");
        basicReef = Schematics.readBase64("bXNjaAF4nEXMQQ6CMBRF0QshNcrQdbAi4+BTntiEUtNfTdi9zsyZHwJhYNgti3MsVVOVHpy8yXJauHh8KltL0RkXeazp1VLZgbDZrM3pb/eOq7/nTzls1fRfoIP+hy/YIRvI");
    }
}
