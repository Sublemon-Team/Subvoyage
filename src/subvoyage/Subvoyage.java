package subvoyage;

import arc.*;
import arc.func.Boolc;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
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
import mindustry.mod.*;
import mindustry.ui.dialogs.SettingsMenuDialog;
import subvoyage.content.*;
import subvoyage.content.block.*;
import subvoyage.content.other.*;
import subvoyage.content.sound.*;
import subvoyage.core.CustomRender;
import subvoyage.core.Logic;
import subvoyage.draw.visual.*;
import subvoyage.type.block.environment.vapor.*;
import subvoyage.type.block.production.*;
import subvoyage.type.unit.ability.LegionfieldAbility;
import subvoyage.ui.setting.*;
import subvoyage.utility.*;
import subvoyage.world.techtree.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static arc.Core.*;
import static mindustry.Vars.*;
import static subvoyage.content.SvPlanets.atlacian;

public class Subvoyage extends Mod {
    public static String ID = "subvoyage";

    public static VaporControl vaporControl;
    public static VersionControl versionControl = new VersionControl();
    public static String currentTag = "v0.6b";
    public static String repo = "Sublemon-Team/Subvoyage";

    public FrameBuffer buffer;

    public Subvoyage(){
        //listen for game load events
        AtomicBoolean hell = new AtomicBoolean(false);

        Events.run(Trigger.update,Logic::update);
        Events.on(ClientLoadEvent.class, e -> Logic.clientLoad());
        Events.on(WorldLoadEvent.class, e -> Logic.worldLoad());
        Events.on(EventType.ResetEvent.class, e -> Logic.reset());
        Events.run(Trigger.newGame,Logic::newGame);
        Events.run(EventType.Trigger.draw, CustomRender::draw);

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
