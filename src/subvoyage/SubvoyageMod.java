package subvoyage;

import arc.*;
import arc.struct.*;
import arc.util.*;
import arc.util.serialization.Jval;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.type.Sector;
import mindustry.ui.dialogs.ModsDialog;
import subvoyage.content.*;
import subvoyage.content.block.*;
import subvoyage.content.other.*;
import subvoyage.content.sound.*;
import subvoyage.draw.visual.*;
import subvoyage.type.block.environment.vapor.*;
import subvoyage.type.block.production.*;
import subvoyage.ui.dialog.*;
import subvoyage.ui.setting.*;
import subvoyage.utility.*;
import subvoyage.world.techtree.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static arc.Core.*;
import static mindustry.Vars.*;

public class SubvoyageMod extends Mod {
    public static String ID = "subvoyage";

    public BetaCompleteDialog betaCompleteDialog;

    public static VaporControl vaporControl;
    public static VersionControl versionControl = new VersionControl();
    public static String currentTag = "v0.5b";
    public static String repo = "Sublemon-Team/Subvoyage";

    public SubvoyageMod(){
        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            betaCompleteDialog = new BetaCompleteDialog();

            boolean autoUpdate = settings.getBool("sv-autoupdate");
            Log.info("[Subvoyage] Autoupdate: "+(autoUpdate ? "Enabled" : "Disabled"));
            if(autoUpdate) {
                Log.info("[Subvoyage] Fetching latest Updates...");
                Http.get(ghApi+"/repos/"+repo+"/releases/latest", res -> {
                    var json = Jval.read(res.getResultAsString());
                    String tagName = json.getString("tag_name");
                    Log.info("[Subvoyage] Latest Release Tag: "+tagName);
                    boolean upToDate = versionControl.isUpToDate(currentTag, tagName);
                    Log.info("[Subvoyage] "+(!upToDate ? "New update is available" : "Version is up-to-date"));
                    if(!upToDate) {
                        String text = bundle.format("settings.sv-update-version.confirm",tagName,currentTag);
                        ui.showConfirm("@update", text, () -> {
                            ui.mods.show();
                            ui.mods.githubImportMod(repo, false, null);
                        });
                    }
                },(err) -> {
                    ui.showInfoOnHidden("@settings.sv-update-failed.show", () -> {

                    });
                });
            }

            /*SvPlanets.atlacian.sectors.each(a -> {
                a.save = control.saves.getSaveSlots().random();
            });*/
            /*for (TechTree.TechNode node : TechTree.all) {
                UnlockableContent content = node.content;
                if (content.locked()) {
                    //Log.info("[UnlockTechTreeMod] Unlocking content " + (content.name).replace("content", ""));
                    content.unlock();
                }
            }*/

        });
//        Events.on(UnlockEvent.class,e -> {
//
//        });

        Events.run(Trigger.newGame,() -> {
            var core = player.bestCore();
            if(core == null) return;
            if(!settings.getBool("skipcoreanimation") && !state.rules.pvp){
                SvMusic.theAtlacian.stop();
                if(settings.getInt("musicvol") > 0 && state.rules.planet == SvPlanets.atlacian){
                    Musics.land.stop();
                    SvMusic.theAtlacian.play();
                }
            }
        });
        Events.on(SectorCaptureEvent.class,e -> {
            AtomicBoolean finishedAll = new AtomicBoolean(true);
            SvSectorPresets.all.each(s -> {
                if(s == e.sector.preset) return;
                Sector sector = SvPlanets.atlacian.sectors.find(a -> a.preset == s);
                boolean isCaptured = sector.isCaptured();
                if(!isCaptured) finishedAll.set(false);
            });
            if(finishedAll.get()) {
                betaCompleteDialog.show(SvPlanets.atlacian);
            };
        });
        Events.on(WorldLoadEvent.class, e -> {
            if(SvBlocks.waterSifter instanceof WaterSifter) ((WaterSifter) SvBlocks.waterSifter).worldReset();
        });
//        Events.on(MusicRegisterEvent.class, e -> {
//            //control.sound.ambientMusic.add(SvMusic.theAtlacian);
//        });
        Events.run(Trigger.update,() -> {
            if(state.isGame()) {
                if(SvMusic.theAtlacian.isPlaying()) {
                    SvMusic.theAtlacian.pause(false);
                    control.sound.stop();
                }
//                if (!state.isPaused()) {
//                    //vaporControl.update();
//                }
            } else {
                if(SvMusic.theAtlacian.isPlaying()) {
                    SvMusic.theAtlacian.pause(true);
                }
            }
        });
//        Events.run(Trigger.draw, () -> {
//
//        });

        Events.on(EventType.FileTreeInitEvent.class, e ->
            app.post(SvShaders::init)
        );

        Events.on(EventType.DisposeEvent.class, e ->
            SvShaders.dispose()
        );
    }

    @Override
    public void init() {
        super.init();
        loadSettings();
        IconLoader.loadIcons();
    }

    @Override
    public void loadContent(){
        SvCall.registerPackets();
        Log.info("Poof-poof, Subvoyage loads up!");
        SvMusic.load();
        SvSounds.load();

        SvItems.load();
        SvLiquids.load();

        SvUnits.load();

        SvWorldBlocks.load();
        SvBlocks.load();

        SvLoadouts.load();
        SvPlanets.load();

        SvSectorPresets.load();

        EnvRenderer.init();

        AtlacianTechTree.loadBalanced();
        vaporControl = new VaporControl();
        VaporControl.load();
    }

    void loadSettings() {
        ui.settings.addCategory(bundle.get("setting.sv-title"),"subvoyage-icon",t -> {
            t.pref(new BannerPref(ID+"-modname",256));
            t.pref(new ButtonPref(Core.bundle.get("sv-clear-campaign"),Icon.trash,() -> {
                ui.showConfirm("@confirm", "@settings.sv-clear-campaign.confirm", () -> {
                    Seq<Saves.SaveSlot> toDelete = Seq.with();
                    control.saves.getSaveSlots().each(s -> {
                        if(s.getSector() == null) return;
                        if(s.getSector().planet == SvPlanets.atlacian) {
                            toDelete.add(s);
                            Log.info("Deleted Atlacian sector: "+s.getSector().id);
                        }
                    });
                    toDelete.each(Saves.SaveSlot::delete);
                    ui.showInfoOnHidden("@settings.sv-clear-campaign-close.confirm", () -> {
                        Core.app.exit();
                    });
                });
            }));
            t.pref(new ButtonPref(Core.bundle.get("sv-clear-tech-tree"),Icon.trash,() -> {
                ui.showConfirm("@confirm", "@settings.sv-clear-tech-tree.confirm", () -> {
                    SvPlanets.atlacian.techTree.reset();
                    for(TechTree.TechNode node : SvPlanets.atlacian.techTree.children){
                        node.reset();
                    }
                    content.each(c -> {
                        if(c instanceof UnlockableContent u && c.minfo != null && c.minfo.mod != null && c.minfo.mod.name.equals(ID)){
                            u.clearUnlock();
                        }
                    });
                    settings.remove("unlocks");
                });
            }));

            t.sliderPref("sv-offload-shield-sides", 6, 3, 10, s -> s == 10 ? bundle.get("circle") : s+"");
            t.checkPref("sv-autoupdate",true);
            t.checkPref("sv-leeft-uwu",false, SvUnits::loadUwu);
        });
        SvUnits.loadUwu(settings.getBool("svTr-leeft-uwu"));
    }

}
