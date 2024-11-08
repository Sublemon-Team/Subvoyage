package subvoyage.draw.visual;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.graphics.Shaders;
import arc.math.*;
import mindustry.*;

import static arc.Core.*;
import static arc.math.Mathf.*;

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

    private static float[] vertices = new float[24];
    /** {@link Fill#quad} with region and colors. */
    public static void quad(TextureRegion region, float x1, float y1, float c1, float x2, float y2, float c2, float x3, float y3, float c3, float x4, float y4, float c4){
        float u = region.u, v = region.v, u2 = region.u2, v2 = region.v2, mcolor = Draw.getMixColor().toFloatBits();

        vertices[0] = x1;
        vertices[1] = y1;
        vertices[2] = c1;
        vertices[3] = u;
        vertices[4] = v;
        vertices[5] = mcolor;

        vertices[6] = x2;
        vertices[7] = y2;
        vertices[8] = c2;
        vertices[9] = u;
        vertices[10] = v2;
        vertices[11] = mcolor;

        vertices[12] = x3;
        vertices[13] = y3;
        vertices[14] = c3;
        vertices[15] = u2;
        vertices[16] = v2;
        vertices[17] = mcolor;

        vertices[18] = x4;
        vertices[19] = y4;
        vertices[20] = c4;
        vertices[21] = u2;
        vertices[22] = v;
        vertices[23] = mcolor;

        Draw.vert(region.texture, vertices, 0, vertices.length);
    }


    // author: MEEPofFaith
    // link: https://github.com/MEEPofFaith/tantros-but-java/blob/master/src/poly/tantros/graphics/DrawPseudo3D.java
    public static class _3D {
        public static float xHeight(float x, float height) {
            if (height <= 0) return x;
            return x + xOffset(x, height);
        }

        public static float yHeight(float y, float height) {
            if (height <= 0) return y;
            return y + yOffset(y, height);
        }

        public static float xOffset(float x, float height) {
            return (x - camera.position.x) * hMul(height);
        }

        public static float yOffset(float y, float height) {
            return (y - camera.position.y) * hMul(height);
        }

        public static float hScale(float height) {
            return 1f + hMul(height);
        }

        public static float hMul(float height) {
            return height * Vars.renderer.getDisplayScale();
        }

        public static float layerOffset(float x, float y) {
            float max = Math.max(camera.width, camera.height);
            return -dst(x, y, camera.position.x, camera.position.y) / max / 1000f;
        }

        public static float heightFade(float height){
            float scl = hScale(height);
            return 1f - Mathf.curve(scl, 1.5f, 7f);
        }
    }
}
