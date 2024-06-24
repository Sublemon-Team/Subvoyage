package subvoyage;

import arc.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.Musics;
import mindustry.mod.*;
import subvoyage.content.SvMusic;
import subvoyage.content.blocks.*;
import subvoyage.content.liquids.*;
import subvoyage.content.unit.*;
import subvoyage.content.world.*;
import subvoyage.content.world.items.*;
import subvoyage.content.world.planets.*;
import subvoyage.content.world.planets.atlacian.*;
import subvoyage.content.world.sectors.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.Vars.state;

public class SubvoyageMod extends Mod {
    public static String ID = "subvoyage";


    public SubvoyageMod(){
        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            /*for (TechTree.TechNode node : TechTree.all) {
                UnlockableContent content = node.content;
                if (content.locked()) {
                    //Log.info("[UnlockTechTreeMod] Unlocking content " + (content.name).replace("content", ""));
                    content.unlock();
                }
            }*/

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
        Events.on(WorldLoadEvent.class, e -> {

        });
        Events.on(MusicRegisterEvent.class, e -> {
            control.sound.ambientMusic.add(SvMusic.theAtlacian);
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
    }

}
