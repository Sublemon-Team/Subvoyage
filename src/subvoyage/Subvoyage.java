package subvoyage;

import arc.*;
import arc.graphics.Color;
import arc.graphics.Colors;
import arc.util.*;
import mindustry.Vars;
import mindustry.async.AsyncProcess;
import mindustry.async.PhysicsProcess;
import mindustry.core.Version;
import mindustry.entities.EntityGroup;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.Unit;
import mindustry.mod.*;
import subvoyage.content.world.SvTechTree;
import subvoyage.content.SvContent;
import subvoyage.content.SvUnits;
import subvoyage.content.other.SvTeam;
import subvoyage.core.UpdateManager;
import subvoyage.core.anno.LoadAnnoProcessor;
import subvoyage.core.draw.shader.SvShaders;
import subvoyage.core.draw.SvRender;
import subvoyage.core.logic.SvCall;
import subvoyage.core.logic.SvLogic;
import subvoyage.core.ui.advancements.Advancement;
import subvoyage.core.SvSettings;
import subvoyage.type.unit.entity.HelicopterUnitEntity;

import static arc.Core.*;
import static mindustry.Vars.mods;

public class Subvoyage extends Mod {
    public static String ID = "subvoyage";

    public static UpdateManager.VersionControl versionControl = new UpdateManager.VersionControl();
    public static String currentTag;
    public static String GITHUB_REPO = "Sublemon-Team/Subvoyage";

    public Subvoyage(){
        Events.run(Trigger.update, SvLogic::update);
        Events.on(ClientLoadEvent.class, e -> SvLogic.clientLoad());
        Events.on(WorldLoadEvent.class, e -> SvLogic.worldLoad());
        Events.on(EventType.ResetEvent.class, e -> SvLogic.reset());
        Events.run(Trigger.newGame, SvLogic::newGame);
        Events.run(EventType.Trigger.draw, SvRender::draw);

        Events.on(UnitCreateEvent.class,e -> {
            if(e.unit.team == SvTeam.melius) {
                if(e.unit.type == SvUnits.lapetus) Advancement.unit_helio.unlock();
                if(e.unit.type == SvUnits.leeft) Advancement.unit_hydro.unlock();
                if(e.unit.type == SvUnits.stunt) Advancement.unit_rover.unlock();
            }
        });

        Events.on(EventType.FileTreeInitEvent.class, e -> app.post(SvShaders::init));
        Events.on(EventType.DisposeEvent.class, e -> SvShaders.dispose());
        Events.on(ContentInitEvent.class, e -> {
            LoadAnnoProcessor.begin(ID);
        });
    }

    @Override
    public void init() {
        super.init();
        SvSettings.load();
        Colors.put("sv-accent",Color.valueOf("7ede8a"));
        //we'll just break in and do our giggles
        /*Vars.asyncCore.processes.add(new AsyncProcess() {
            @Override
            public void begin() {
                AsyncProcess.super.begin();
                PhysicsProcess physics = (PhysicsProcess) Vars.asyncCore.processes.find(e -> e instanceof PhysicsProcess phys);
                EntityGroup<Unit> group = Reflect.get(physics,"group");
                group.each(e -> {
                    if(e instanceof HelicopterUnitEntity h) h.physref.body.layer = 2;
                });
            }
        });*/

        try {
            if(Version.isAtLeast("147")) {
                Reflect.invoke(Vars.logicVars, "put", new Object[] {"@melius", SvTeam.melius},
                        String.class, Object.class);
            }
            else {
                Reflect.invoke(Vars.logicVars, "put", new Object[] {"@melius", SvTeam.melius},
                        String.class, Object.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //FontIconGenerator.loadIcons();
    }

    @Override
    public void loadContent(){
        Log.info("Poof-poof, Subvoyage loads up!");

        SvCall.registerPackets();
        SvContent.load();

        SvRender.initEnv();

        SvTechTree.load();
    }

}
