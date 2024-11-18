package subvoyage;

import arc.*;
import arc.func.Boolc;
import arc.graphics.gl.FrameBuffer;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.SettingsMenuDialog;
import subvoyage.content.*;
import subvoyage.core.CustomRender;
import subvoyage.core.Logic;
import subvoyage.draw.visual.*;
import subvoyage.ui.setting.*;
import subvoyage.utility.*;
import subvoyage.world.techtree.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static arc.Core.*;
import static mindustry.Vars.*;
import static subvoyage.content.SvPlanets.atlacian;

public class Subvoyage extends Mod {
    public static String ID = "subvoyage";

    public static VersionControl versionControl = new VersionControl();
    public static String currentTag = "v0.6b";
    public static String repo = "Sublemon-Team/Subvoyage";

    public Subvoyage(){
        Events.run(Trigger.update,Logic::update);
        Events.on(ClientLoadEvent.class, e -> Logic.clientLoad());
        Events.on(WorldLoadEvent.class, e -> Logic.worldLoad());
        Events.on(EventType.ResetEvent.class, e -> Logic.reset());
        Events.run(Trigger.newGame,Logic::newGame);
        Events.run(EventType.Trigger.draw, CustomRender::draw);

        Events.on(EventType.FileTreeInitEvent.class, e -> app.post(SvShaders::init));
        Events.on(EventType.DisposeEvent.class, e -> SvShaders.dispose());
    }

    @Override
    public void init() {
        super.init();
        SubvoyageSettings.load();
        IconLoader.loadIcons();
    }

    @Override
    public void loadContent(){
        Log.info("Poof-poof, Subvoyage loads up!");

        SvCall.registerPackets();
        SvContent.load();

        EnvRenderer.init();

        AtlacianTechTree.loadBalanced();
    }

}
