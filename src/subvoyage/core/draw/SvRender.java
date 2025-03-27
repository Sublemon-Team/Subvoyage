package subvoyage.core.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import mindustry.Vars;
import mindustry.graphics.Layer;
import mindustry.graphics.Shaders;
import mindustry.type.Weather;
import subvoyage.core.SvSettings;
import subvoyage.core.SvVars;
import subvoyage.core.draw.shader.SvShaders;
import subvoyage.type.block.storage.core.AtlacianCore;
import subvoyage.type.world.SvEnvironment;
import subvoyage.util.Var;

import static arc.Core.*;
import static arc.Core.settings;
import static mindustry.Vars.*;

public class SvRender {
    public static FrameBuffer buffer;

    public static class Layer extends mindustry.graphics.Layer {
        public static final float
                laser = 72.2f,
                powerBubbles = 86.7f
                ;
    }

    public static void draw() {
        if(AtlacianCore.cutscene) {
            camera.position.set(player.bestCore().x,player.bestCore().y);
        }
        if(buffer == null) buffer = new FrameBuffer();
        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

        if(SvSettings.bool("laser-shaders"))
            Draw.drawRange(Layer.laser, 0.1f,
                    () -> {
                        renderer.bloom.resize(graphics.getWidth(), graphics.getHeight());
                        renderer.bloom.setBloomIntensity(2f / 4f);
                        renderer.bloom.blurPasses = 1;
                        renderer.bloom.capture();
                        buffer.begin(Color.clear);
                    },
                    () -> {
                        buffer.end();
                        buffer.blit(SvShaders.laser);
                        Draw.blend();
                        renderer.bloom.render();
                    });
        if(SvSettings.bool("power-bubble-shaders"))
            Draw.drawRange(Layer.powerBubbles, 0.2f,
                    () -> {
                        buffer.begin(Color.clear);
                    },
                    () -> {
                        buffer.end();
                        Draw.blend(Blending.additive);
                        buffer.blit(SvShaders.powerBubbles);
                        Draw.blend();
                    });
        SvVars.effectBuffer = buffer;
    }

    public static void initEnv(){
        Color waterColor = Color.valueOf("274D89");
        Rand rand = new Rand();
        Core.assets.load("sprites/rays.png", Texture.class).loaded = t -> t.setFilter(Texture.TextureFilter.linear);
        Core.assets.load("sprites/distortAlpha.png", Texture.class);
        float windSpeed = 0.3f, windAngle = 45f;
        float windx = Mathf.cosDeg(windAngle) * windSpeed, windy = Mathf.sinDeg(windAngle) * windSpeed;
        renderer.addEnvRenderer(SvEnvironment.legarytic, () -> {
            Texture tex = Core.assets.get("sprites/distortAlpha.png", Texture.class);
            if(tex.getMagFilter() != Texture.TextureFilter.linear){
                tex.setFilter(Texture.TextureFilter.linear);
                tex.setWrap(Texture.TextureWrap.repeat);
            }

            Draw.z(state.rules.fog ? Layer.fogOfWar + 1 : Layer.weather - 1);

            Draw.z(Layer.light + 0.5f);
            Draw.blend(Blending.additive);
            Draw.color(waterColor.cpy().a(0.3f));
            Draw.rect();
            Draw.blend();

            Weather.drawNoiseLayers(tex, waterColor, 1000f, 0.34f, 0.4f, 1f, 1f, 0f, 3, -1.1f, 0.45f, 0.38f, 0.4f);
            Draw.reset();

            Draw.z(Layer.light + 2);
            int rays = 70;
            float timeScale = 2000f;
            rand.setSeed(0);
            Draw.blend(Blending.additive);

            float t = Time.time / timeScale;
            Texture ray = Core.assets.get("sprites/rays.png", Texture.class);
            for(int i = 0; i < rays; i++){
                float offset = rand.random(0f, 1f);
                float time = t + offset;
                int pos = (int)time;
                float life = time % 1f;
                float opacity = rand.random(0.2f, 0.5f) * Mathf.slope(life) * 0.7f;
                float x = (rand.random(0f, world.unitWidth()) + (pos % 100)*753) % world.unitWidth();
                float y = (rand.random(0f, world.unitHeight()) + (pos % 120)*453) % world.unitHeight();
                float rot = rand.range(7f);
                float sizeScale = 1f + rand.range(0.3f);
                float topDst = (camera.position.y + camera.height/2f) - (y + ray.height/2f + ray.height*1.9f*sizeScale/2f);
                float invDst = topDst/1000f;
                opacity = Math.min(opacity, -invDst);

                if(opacity > 0.01){
                    Draw.alpha(opacity);
                    Draw.rect(Draw.wrap(ray), x, y + ray.height/2f, ray.width*2*sizeScale, ray.height*2*sizeScale, rot);
                    Draw.color();
                }
            }

            //suspended particles
            Draw.draw(Layer.weather, () -> Weather.drawRain(
                    2.45f,
                    4f,
                    windx * 2,
                    windy * 2,
            1000f,
            1f,
                    0.8f,
                    waterColor
            ));

            Draw.blend();
        });
    }
}
