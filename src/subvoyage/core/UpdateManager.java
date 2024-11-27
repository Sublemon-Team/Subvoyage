package subvoyage.core;

import arc.util.Http;
import arc.util.Log;
import arc.util.Structs;
import arc.util.serialization.Jval;
import subvoyage.Subvoyage;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static arc.Core.bundle;
import static mindustry.Vars.ghApi;
import static mindustry.Vars.ui;

public class UpdateManager {
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

    public static class VersionControl {

        public enum Attribute {
            FIX,
            BETA
        }
        public boolean areVersionsUpToDate(String origin, String release) {
            Integer[] originVersion = parseTagVersion(origin);
            Integer[] releaseVersion = parseTagVersion(release);
            int len = Math.max(originVersion.length,releaseVersion.length);
            boolean okay = true;
            for (int i = 0; i < len; i++) {
                Integer orV = originVersion[i];
                Integer reV = releaseVersion[i];
                if(orV == null) orV = 0;
                if(reV == null) reV = 0;
                if(reV > orV) {okay = false; break;}
            }
            return okay;
        }
        public boolean areAttributeUpToDate(String origin, String release) {
            List<Attribute> originAttr = Arrays.asList(parseTagAttributes(origin));
            List<Attribute> releaseAttr = Arrays.asList(parseTagAttributes(release));
            if(releaseAttr.contains(Attribute.FIX) && !originAttr.contains(Attribute.FIX)) return false;
            if(!releaseAttr.contains(Attribute.BETA) && originAttr.contains(Attribute.BETA)) return false;
            return true;
        }
        public Integer[] parseTagVersion(String tag) {
            return Arrays.stream(tag.split("-")[0].split("\\.")).map(Integer::parseInt).toArray(Integer[]::new);
        }
        public Attribute[] parseTagAttributes(String tag) {
            String[] rawAttributes = Structs.remove(tag.split("-"),0);
            Arrays.stream(rawAttributes).map(e -> Objects.equals(e, "b") ? "beta" : (Objects.equals(e, "f") ? "fix" : e));
            return Arrays.stream(rawAttributes).map(e -> Attribute.valueOf(e.toUpperCase())).toArray(Attribute[]::new);
        }

        public boolean isUpToDate(String originTag, String releaseTag) {
            originTag = originTag.replace("b","-beta"); originTag = originTag.replace("f","-fix");
            releaseTag = releaseTag.replace("b","-beta"); releaseTag = releaseTag.replace("f","-fix");
            if(originTag.startsWith("v")) originTag = originTag.substring(1);
            if(releaseTag.startsWith("v")) releaseTag = releaseTag.substring(1);
            boolean versionUptd = areVersionsUpToDate(originTag,releaseTag);
            if(!versionUptd) return false;
            boolean attributeUptd = areAttributeUpToDate(originTag,releaseTag);
            if(!attributeUptd) return false;
            return true;
        }
    }
}
