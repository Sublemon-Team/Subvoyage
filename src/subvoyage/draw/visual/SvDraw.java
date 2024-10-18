package subvoyage.draw.visual;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.graphics.Shaders;

import static mindustry.Vars.renderer;

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

    public static void applyBloom(Runnable draw) {
        applyBloom(1f,draw);
    }

    public static void applyBloom(float intensity, Runnable draw) {
        int w = Core.graphics.getWidth();
        int h = Core.graphics.getHeight();
        Draw.flush();
        renderer.bloom.resize(w,h);
        renderer.bloom.capture();;
        renderer.bloom.setBloomIntensity(intensity);
        draw.run();
        renderer.bloom.render();
    }
    public static void applyBloomBasic(Runnable draw) {
        float z = Draw.z();
        Draw.z(Layer.effect);
        draw.run();
        Draw.z(z);
    }
}
