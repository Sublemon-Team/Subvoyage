package subvoyage.core;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import mindustry.ai.types.HugAI;
import mindustry.graphics.Layer;
import subvoyage.SvVars;
import subvoyage.draw.visual.SvShaders;
import subvoyage.type.block.core.SubvoyageCoreBlock;

import static arc.Core.camera;
import static mindustry.Vars.player;
import static mindustry.Vars.renderer;

public class CustomRender {
    public static FrameBuffer buffer;

    public static void draw() {
        if(SubvoyageCoreBlock.cutscene) {
            camera.position.set(player.bestCore().x,player.bestCore().y);
        }
        Draw.draw(Layer.min,() -> {
            if(buffer == null) buffer = new FrameBuffer();
            buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            buffer.begin(Color.clear);
        });
        Draw.draw(Layer.weather+1,() -> {
            buffer.end();
            //buffer.blit(Shaders.screenspace);
            Draw.z(Layer.weather);
            buffer.blit(SvShaders.underwaterRegion);
        });
        SvVars.effectBuffer = buffer;
    }
}
