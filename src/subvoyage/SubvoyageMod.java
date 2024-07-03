package subvoyage;

import arc.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.struct.Bits;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.Groups;
import mindustry.gen.Musics;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.mod.*;
import subvoyage.content.SvMusic;
import subvoyage.content.blocks.*;
import subvoyage.content.blocks.editor.vapor.VaporControl;
import subvoyage.content.blocks.production.WaterSifter;
import subvoyage.content.liquids.*;
import subvoyage.content.unit.*;
import subvoyage.content.world.*;
import subvoyage.content.world.items.*;
import subvoyage.content.world.planets.*;
import subvoyage.content.world.planets.atlacian.*;
import subvoyage.content.world.sectors.*;
import subvoyage.dialog.BetaCompleteDialog;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.Vars.state;

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
            if(e.content == SvSectorPresets.noxiousTarn) e.content.clearUnlock();
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
            if(e.sector.preset == SvSectorPresets.facility) {
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
                if (!state.isPaused()) {
                    vaporControl.update();
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
    }

    @Override
    public void loadContent(){
        Log.info("Poof-poof, Subvoyage loads up!");
        SvMusic.load();

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

}
