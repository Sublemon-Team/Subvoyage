package subvoyage.core;

import arc.util.Log;

import static arc.Core.settings;

public class Logic {
    public static void clientLoad() {
        checkUpdates();
    }

    public static void checkUpdates() {
        boolean autoUpdate = settings.getBool("sv-autoupdate");
        Log.info("[Subvoyage] Autoupdate: "+(autoUpdate ? "Enabled" : "Disabled"));
        if(autoUpdate) AutoUpdater.begin();
    }
}
