package subvoyage.core;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Time;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.core.GameState;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Musics;
import mindustry.gen.Teamc;
import mindustry.world.Tile;
import subvoyage.SvVars;
import subvoyage.content.SvPlanets;
import subvoyage.content.SvTeam;
import subvoyage.content.SvUnits;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.sound.SvMusic;
import subvoyage.type.block.core.SubvoyageCoreBlock;
import subvoyage.type.block.production.WaterSifter;
import subvoyage.type.unit.ability.LegionfieldAbility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import static arc.Core.bundle;
import static arc.Core.settings;
import static mindustry.Vars.*;
import static subvoyage.content.SvPlanets.atlacian;

public class Logic {

    static AtomicBoolean frog = new AtomicBoolean(false);

    public static void clientLoad() {
        checkUpdates();
        checkHell();
        control.input.addLock(SubvoyageCoreBlock.lock);
        bundle.getProperties().put("sector.curcapturefake","[lightgray]"+bundle.get("sector.curcapture")+"[]");
    }

    public static void newGame() {
        var core = player.bestCore();
        if(core == null) return;
        if(!settings.getBool("skipcoreanimation") && !state.rules.pvp){
            SvMusic.theAtlacian.stop();
            if(settings.getInt("musicvol") > 0 && state.rules.planet == atlacian){
                Musics.land.stop();
                SvMusic.theAtlacian.play();
            }
        }
        SubvoyageCoreBlock.cutscene = true;
        SubvoyageCoreBlock.landTime = 160f;
        Time.run(160f,() -> {
            SubvoyageCoreBlock.cutscene = false;
        });
    }

    public static void update() {
        if(state.isGame()) gameUpdate();
        if(state.isMenu()) menuUpdate();
        SvVars.fumesMap.update();
    }

    public static void gameUpdate() {
        if(player.team() == Team.sharded && state.rules.planet == atlacian) {
            player.team(SvTeam.melius);
        } else if(state.rules.planet == atlacian) {
            /*Groups.all.each((b) -> {
                if(b instanceof Teamc t) {
                    if(t.team() == Team.sharded) {
                        t.team(SvTeam.melius);
                    }
                }
            });*/
        }
        if(SvMusic.theAtlacian.isPlaying()) {
            SvMusic.theAtlacian.pause(false);
            control.sound.stop();
        }
        LegionfieldAbility.update();
        if(!state.isPaused() && SubvoyageCoreBlock.cutscene) SubvoyageCoreBlock.landTime-=Time.delta;
    }
    public static void menuUpdate() {
        updateHell();
        if(SvMusic.theAtlacian.isPlaying()) {
            SvMusic.theAtlacian.pause(true);
        }
    }

    public static void reset() {
        SvVars.fumesMap.stop();
    }

    public static void worldLoad() {
        if(SvBlocks.waterSifter instanceof WaterSifter sifter) sifter.worldReset();
        SvVars.fumesMap.recalc();
        SubvoyageCoreBlock.cutscene = false;
        SubvoyageCoreBlock.landTime = 0f;
    }



    public static void updateHell() {
        if(frog.get()) {
            if(state.isMenu()) {
                playHellSector();
            }
        }
    }

    public static void checkHell() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate targetDate = LocalDate.of(2024, 11, 4);
        if (now.toLocalDate().equals(targetDate) && now.getMinute() == 0 && now.getHour() == 0) {
            frog.set(true);
            playHellSector();
        }
    }

    public static void playHellSector() {
        world.loadSector(Planets.tantros.getLastSector());
        state.rules.defaultTeam = Team.neoplastic;
        for (Tile tile : world.tiles) {
            if(tile.block() == Blocks.coreShard) {
                tile.setBlock(SvBlocks.corePuffer, Team.neoplastic);
            }
        }
        player.team(Team.neoplastic);
        player.clearUnit();

        state.set(GameState.State.playing);
        Timer.schedule(Logic::scheduleMove,0f,0.1f);
    }

    public static void scheduleMove() {
        player.unit().impulse(new Vec2().rnd(2500f));
        if(player.unit() != null) {
            player.unit().plans().add(new BuildPlan(player.tileX(),player.tileY(),0,SvBlocks.buoy));
        }
        if(state.getSector().planet != Planets.tantros) {
            playHellSector();
        } else if(state.teams.playerCores().size == 0) {
            Core.app.exit();
        }
        SvUnits.leeft.spawn(Team.crux,world.width()*8f*Mathf.random(),world.height()*8f*Mathf.random());
    }

    public static void checkUpdates() {
        boolean autoUpdate = settings.getBool("sv-autoupdate");
        Log.info("[Subvoyage] Autoupdate: "+(autoUpdate ? "Enabled" : "Disabled"));
        if(autoUpdate) AutoUpdater.begin();
    }
}
