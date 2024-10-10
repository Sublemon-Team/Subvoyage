package subvoyage;

import arc.*;
import arc.func.Boolc;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.struct.*;
import arc.util.*;
import arc.util.serialization.Jval;
import mindustry.content.*;
import mindustry.core.GameState;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.input.Binding;
import mindustry.mod.*;
import mindustry.type.Sector;
import mindustry.ui.dialogs.ModsDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;
import subvoyage.content.*;
import subvoyage.content.block.*;
import subvoyage.content.other.*;
import subvoyage.content.sound.*;
import subvoyage.draw.visual.*;
import subvoyage.type.block.environment.vapor.*;
import subvoyage.type.block.production.*;
import subvoyage.type.unit.ability.LegionfieldAbility;
import subvoyage.ui.dialog.*;
import subvoyage.ui.setting.*;
import subvoyage.utility.*;
import subvoyage.world.techtree.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static arc.Core.*;
import static mindustry.Vars.*;
import static subvoyage.content.SvPlanets.atlacian;

public class SubvoyageMod extends Mod {
    public static String ID = "subvoyage";

    public BetaCompleteDialog betaCompleteDialog;

    public static VaporControl vaporControl;
    public static VersionControl versionControl = new VersionControl();
    public static String currentTag = "v0.6b";
    public static String repo = "Sublemon-Team/Subvoyage";

    public FrameBuffer buffer;

    public SubvoyageMod(){
        //listen for game load events
        Events.on(ClientLoadEvent.class, e -> {

            betaCompleteDialog = new BetaCompleteDialog();
            if(settings.getBool("sv-dont")) {
                world.loadSector(Planets.tantros.getLastSector());
                state.set(GameState.State.playing);
            }

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
                if(settings.getInt("musicvol") > 0 && state.rules.planet == atlacian){
                    Musics.land.stop();
                    SvMusic.theAtlacian.play();
                }
            }
        });
        Events.on(SectorCaptureEvent.class,e -> {
            if(e.sector.preset == null || e.sector.planet != atlacian) return;
            AtomicBoolean finishedAll = new AtomicBoolean(true);
            SvSectorPresets.all.each(s -> {
                if(s == e.sector.preset) return;
                Sector sector = atlacian.sectors.find(a -> a.preset == s);
                boolean isCaptured = sector.isCaptured();
                if(!isCaptured) finishedAll.set(false);
            });
            if(finishedAll.get()) {
                betaCompleteDialog.show(atlacian);
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
                LegionfieldAbility.update();
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


        Events.run(EventType.Trigger.draw,() -> {
            Draw.draw(Layer.min,() -> {
                if(buffer == null) buffer = new FrameBuffer();
                buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                buffer.begin(Color.clear);
            });
            Draw.draw(Layer.light+1,() -> {
                buffer.end();
                //buffer.blit(Shaders.screenspace);
                buffer.blit(SvShaders.underwaterRegion);
            });
            SvVars.effectBuffer = buffer;
        });

        Events.run(EventType.Trigger.update,() -> SvVars.underwaterMap.update());
        Events.on(EventType.ResetEvent.class, e -> SvVars.underwaterMap.stop());
        Events.on(EventType.WorldLoadEndEvent.class,e -> SvVars.underwaterMap.recalc());
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
                        if(s.getSector().planet == atlacian) {
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
                    atlacian.techTree.reset();
                    for(TechTree.TechNode node : atlacian.techTree.children){
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

            sliderPref(t,ID+"-offload-core-ico","sv-offload-shield-sides",
                    6,3,10,
                    s -> s == 10 ? bundle.get("circle") : s+"");
            sliderPref(t,ID+"-liquid-hard-water","sv-metal-fuming-opacity",
                    75,0,100,
                    s -> s == 0 ? bundle.get("off") : s+"%");

            checkPref(t,ID+"-energy-dock-ship","sv-autoupdate",true);
            checkPref(t,ID+"-leeft-uwu","sv-leeft-uwu",false, SvUnits::loadUwu);
            checkPref(t,ID+"-sublemon_frog","sv-dont",false,(changed) -> {
                if(changed) {
                    ui.showInfoOnHidden("", () -> {
                        Core.app.exit();
                    });
                } else {
                    ui.showInfo(":p");
                }
            });
        });
        SvUnits.loadUwu(settings.getBool("sv-leeft-uwu"));
    }

    void sliderPref(SettingsMenuDialog.SettingsTable t, String ico, String name, int def, int min, int max, SettingsMenuDialog.StringProcessor p) {
        t.pref(new SliderIconSetting(ico,name, def,min,max,1,p));
    }
    void checkPref(SettingsMenuDialog.SettingsTable t, String ico, String name, boolean def) {
        t.pref(new CheckIconSetting(ico,name,def,null));
    }
    void checkPref(SettingsMenuDialog.SettingsTable t, String ico, String name, boolean def, Boolc changed) {
        t.pref(new CheckIconSetting(ico,name,def,changed));
    }

}
