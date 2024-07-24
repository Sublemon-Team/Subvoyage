package subvoyage;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.mod.*;
import subvoyage.content.*;
import subvoyage.content.block.*;
import subvoyage.content.other.*;
import subvoyage.content.sound.*;
import subvoyage.draw.visual.*;
import subvoyage.type.block.environment.vapor.*;
import subvoyage.type.block.production.*;
import subvoyage.ui.dialog.*;
import subvoyage.world.techtree.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class SubvoyageMod extends Mod {
    public static String ID = "subvoyage";

    public BetaCompleteDialog betaCompleteDialog;

    public static VaporControl vaporControl;

    public SubvoyageMod(){
        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            betaCompleteDialog = new BetaCompleteDialog();
            /*for (TechTree.TechNode node : TechTree.all) {
                UnlockableContent content = node.content;
                if (content.locked()) {
                    //Log.info("[UnlockTechTreeMod] Unlocking content " + (content.name).replace("content", ""));
                    content.unlock();
                }
            }*/

        });
        Events.on(UnlockEvent.class,e -> {

        });
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
            if(e.sector.preset == SvSectorPresets.noxiousTarn) {
                betaCompleteDialog.show(SvPlanets.atlacian);
            };
        });
        Events.on(WorldLoadEvent.class, e -> {
            if(SvBlocks.waterSifter instanceof WaterSifter) ((WaterSifter) SvBlocks.waterSifter).worldReset();
        });
        Events.on(MusicRegisterEvent.class, e -> {
            //control.sound.ambientMusic.add(SvMusic.theAtlacian);
        });
        Events.run(Trigger.update,() -> {
            if(state.isGame()) {
                if(SvMusic.theAtlacian.isPlaying()) {
                    SvMusic.theAtlacian.pause(false);
                    control.sound.stop();
                }
                if (!state.isPaused()) {
                    vaporControl.update();
                }
            } else {
                if(SvMusic.theAtlacian.isPlaying()) {
                    SvMusic.theAtlacian.pause(true);
                }
            }
        });
        Events.run(Trigger.draw, () -> {
            if(state.isGame()) {
                Bits bits = vaporControl.getVapor();
                if(bits != null) {
                    Draw.z(VaporControl.layer);
                    Draw.color(Pal.neoplasm1);
                    for (int i = 0; i < bits.length(); i++) {
                        boolean isVapor = bits.get(i);
                        int x = i % world.width();
                        int y = Math.floorDiv(i,world.height());
                        if(isVapor) {
                            Draw.alpha(0.95f);
                            Fill.circle(x*tilesize+Mathf.absin(Time.time+i,10f,1f),
                                    y*tilesize+Mathf.absin(Time.time+i+90f,10f,1f),tilesize);
                            Draw.alpha(0.5f);
                            Fill.circle(x*tilesize+Mathf.absin(Time.time+i,9f,1f),
                                    y*tilesize+Mathf.absin(Time.time+i+90f,8f,1f),tilesize*1.5f);
                            Draw.alpha(0.1f);
                            Fill.circle(x*tilesize+Mathf.absin(Time.time+i,7f,1f),
                                    y*tilesize+Mathf.absin(Time.time+i+90f,6f,1f),tilesize*2.5f);
                        }
                    }
                    Draw.reset();
                }
            }
        });

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
    }

    @Override
    public void loadContent(){
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
            t.checkPref("sv-leeft-uwu",false,(v) -> {
                SvUnits.leeft.region = atlas.find(SvUnits.leeft.name+(v ? "-uwu" :""));
                SvUnits.leeft.drawCell = !v;
                SvUnits.leeft.weapons.first().layerOffset = v ? -1 : 0;
            });
            t.sliderPref("sv-offload-shield-sides", 6, 3, 10, s -> s == 10 ? bundle.get("circle") : s+"");
        });
        SvUnits.leeft.region = atlas.find(SvUnits.leeft.name+(settings.getBool("sv-leeft-uwu") ? "-uwu" :""));
        SvUnits.leeft.weapons.first().layerOffset = settings.getBool("sv-leeft-uwu") ? -1 : 0;
        SvUnits.leeft.drawCell = !settings.getBool("sv-leeft-uwu");
    }

}
