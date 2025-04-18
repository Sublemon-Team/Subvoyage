package subvoyage.core;

import arc.Core;
import arc.files.Fi;
import arc.func.Floatc;
import arc.util.*;
import arc.util.io.Streams;
import arc.util.serialization.Jval;
import subvoyage.Subvoyage;
import subvoyage.util.Var;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static arc.Core.bundle;
import static mindustry.Vars.*;
import static mindustry.Vars.mods;

public class UpdateManager {
    public static String FICO_REPO = "Sublemon-Team/FontIconsLib";

    public static void checkFico() {
        if(mods.getMod("font-icon-lib") != null) return; //it's installed
        Core.settings.put("mod-" + "font-icon-lib" + "-enabled",true);
        ui.loadfrag.show(Core.bundle.get("@subvoyage-installing-fico"));
        Var<Float> loadingProgress = new Var<>(0f);
        ui.loadfrag.setProgress(() -> loadingProgress.val);

        Http.get(ghApi + "/repos/" + FICO_REPO + "/releases/latest", res -> {
            var json = Jval.read(res.getResultAsString());
            var assets = json.get("assets").asArray();

            var dexedAsset = assets.find(j -> j.getString("name").startsWith("dexed") && j.getString("name").endsWith(".jar"));
            var asset = dexedAsset == null ? assets.find(j -> j.getString("name").endsWith(".jar")) : dexedAsset;

            if(asset != null){
                var url = asset.getString("browser_download_url");

                Http.get(url, result -> {
                    try{
                        Fi file = tmpDirectory.child(FICO_REPO.replace("/", "") + ".zip");
                        long len = result.getContentLength();
                        Floatc cons = len <= 0 ? f -> {} : p -> loadingProgress.val = p;

                        try(var stream = file.write(false)){
                            Streams.copyProgress(result.getResultAsStream(), stream, len, 4096, cons);
                        }

                        Core.settings.put("mod-" + "font-icon-lib" + "-enabled",true);

                        var mod = mods.importMod(file);

                        try {
                            Class.forName("fonticolib.IconPropsParser").getMethod("start").invoke(null);
                            Class.forName("fonticolib.IconPropsParser").getMethod("loadTeams").invoke(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mods.list().add(mod);
                        mod.setRepo(FICO_REPO);
                        file.delete();

                        Reflect.set(mods,"requiresReload",false);

                        ui.loadfrag.hide();
                    }catch(Throwable e){
                        modError(e);
                    }
                }, UpdateManager::importFail);
            }else{
                throw new ArcRuntimeException("No JAR file found in releases. Make sure you have a valid jar file in the mod's latest Github Release.");
            }
        }, UpdateManager::importFail);
    }

    public static void begin() {
        Log.info("[Subvoyage] Fetching latest Updates...");
        Http.get(ghApi+"/repos/"+Subvoyage.GITHUB_REPO +"/releases/latest", res -> {
            var json = Jval.read(res.getResultAsString());
            String tagName = json.getString("tag_name");
            Log.info("[Subvoyage] Latest Release Tag: "+tagName);
            boolean upToDate = Subvoyage.versionControl.isUpToDate(Subvoyage.currentTag, tagName);
            Log.info("[Subvoyage] "+(!upToDate ? "New update is available" : "Version is up-to-date"));
            if(!upToDate) {
                String text = bundle.format("settings.sv-update-version.confirm",tagName,Subvoyage.currentTag);
                ui.showConfirm("@update", text, UpdateManager::update);
            }
        },(err) -> {
            ui.showInfoOnHidden("@settings.sv-update-failed.show", () -> {

            });
        });
    }
    static float modImportProgress = 0f;
    public static void update() {
        ui.loadfrag.show();
        ui.loadfrag.setProgress(() -> modImportProgress);

        Http.get(ghApi + "/repos/" + Subvoyage.GITHUB_REPO + "/releases/latest", res -> {
            var json = Jval.read(res.getResultAsString());
            var assets = json.get("assets").asArray();

            var dexedAsset = assets.find(j -> j.getString("name").startsWith("dexed") && j.getString("name").endsWith(".jar"));
            var asset = dexedAsset == null ? assets.find(j -> j.getString("name").endsWith(".jar")) : dexedAsset;

            if(asset != null){
                var url = asset.getString("browser_download_url");

                Http.get(url, result -> handleMod(Subvoyage.GITHUB_REPO, result), UpdateManager::importFail);
            }else{
                throw new ArcRuntimeException("No JAR file found in releases. Make sure you have a valid jar file in the mod's latest Github Release.");
            }
        }, UpdateManager::importFail);
    }
    private static void handleMod(String repo, Http.HttpResponse result){
        try{
            Fi file = tmpDirectory.child(repo.replace("/", "") + ".zip");
            long len = result.getContentLength();
            Floatc cons = len <= 0 ? f -> {} : p -> modImportProgress = p;

            try(var stream = file.write(false)){
                Streams.copyProgress(result.getResultAsStream(), stream, len, 4096, cons);
            }

            var mod = mods.importMod(file);
            mod.setRepo(repo);
            file.delete();
            Core.app.post(() -> {
                try{
                    ui.loadfrag.hide();
                    ui.showInfoOnHidden("@mods.reloadexit", () -> {
                        Log.info("Exiting to reload mods.");
                        Core.app.exit();
                    });
                }catch(Throwable e){
                    ui.showException(e);
                }
            });
        }catch(Throwable e){
            modError(e);
        }
    }
    private static void importFail(Throwable t){
        Core.app.post(() -> modError(t));
    }
    static void modError(Throwable error){
        ui.loadfrag.hide();

        if(error instanceof NoSuchMethodError || Strings.getCauses(error).contains(t -> t.getMessage() != null && (t.getMessage().contains("trust anchor") || t.getMessage().contains("SSL") || t.getMessage().contains("protocol")))){
            ui.showErrorMessage("@feature.unsupported");
        }else if(error instanceof Http.HttpStatusException st){
            ui.showErrorMessage(Core.bundle.format("connectfail", Strings.capitalize(st.status.toString().toLowerCase())));
        }else{
            ui.showException(error);
        }
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
                int orV = i >= originVersion.length ? 0 : originVersion[i];
                int reV = i >= releaseVersion.length ? 0 : releaseVersion[i];
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
