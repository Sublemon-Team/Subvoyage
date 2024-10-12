package subvoyage.core;

import arc.util.Http;
import arc.util.Log;
import arc.util.serialization.Jval;
import subvoyage.Subvoyage;

import static arc.Core.bundle;
import static mindustry.Vars.ghApi;
import static mindustry.Vars.ui;

public class AutoUpdater {
    public static void begin() {
        Log.info("[Subvoyage] Fetching latest Updates...");
        Http.get(ghApi+"/repos/"+ Subvoyage.repo+"/releases/latest", res -> {
            var json = Jval.read(res.getResultAsString());
            String tagName = json.getString("tag_name");
            Log.info("[Subvoyage] Latest Release Tag: "+tagName);
            boolean upToDate = Subvoyage.versionControl.isUpToDate(Subvoyage.currentTag, tagName);
            Log.info("[Subvoyage] "+(!upToDate ? "New update is available" : "Version is up-to-date"));
            if(!upToDate) {
                String text = bundle.format("settings.sv-update-version.confirm",tagName,Subvoyage.currentTag);
                ui.showConfirm("@update", text, () -> {
                    ui.mods.show();
                    ui.mods.githubImportMod(Subvoyage.repo, false, null);
                });
            }
        },(err) -> {
            ui.showInfoOnHidden("@settings.sv-update-failed.show", () -> {

            });
        });
    }
}
