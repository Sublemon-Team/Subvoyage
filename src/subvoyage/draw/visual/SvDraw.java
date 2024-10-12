package subvoyage.draw.visual;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Shaders;

public class SvDraw {
    public static FrameBuffer buffer = new FrameBuffer();
    public static void applyShader(Shader shader, Runnable draw) {
        Draw.flush();
        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        buffer.begin(Color.clear);

        draw.run();

        buffer.end();
        buffer.blit(shader);
        Draw.flush();
    }

    public static void applyCache(CacheLayer cache, Runnable draw) {
        cache.begin();
        draw.run();
        cache.end();
    }
}
