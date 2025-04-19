package subvoyage.core.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.graphics.EnvRenderers;
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
import static subvoyage.content.world.SvPlanets.atlacian;

public class SvRender {
    public static FrameBuffer buffer;
    public static @Nullable Bloom bloom;

    public static Runnable atlacianRenderer;

    public static class Layer extends mindustry.graphics.Layer {
        public static final float
                laser = 72.2f,
                powerBubbles = 86.7f,

                hardWater = 30.3f,

                effectGround = 74.5f
                ;
    }

    public static void draw() {
        if(bloom == null) bloom = new Bloom(true);
        if(buffer == null) buffer = new FrameBuffer();
        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

        Draw.drawRange(Layer.hardWater, 0.1f,
                () -> {
                    buffer.begin(Color.clear);
                },
                () -> {
                    buffer.end();
                    buffer.blit(SvShaders.hardWater);
                });

        if(SvSettings.bool("laser-shaders"))
            Draw.drawRange(Layer.laser, 0.1f,
                    () -> {
                        bloom.resize(graphics.getWidth(), graphics.getHeight());
                        bloom.setBloomIntensity(2f / 4f);
                        bloom.blurPasses = 1;
                        bloom.capture();
                        buffer.begin(Color.clear);
                    },
                    () -> {
                        buffer.end();
                        buffer.blit(SvShaders.laser);
                        bloom.render();
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
        if(settings.getBool("bloom") && bloom != null) {
            renderer.bloom.resize(graphics.getWidth(), graphics.getHeight());
            renderer.bloom.setBloomIntensity(settings.getInt("bloomintensity", 6) / 4f + 1f);
            renderer.bloom.blurPasses = settings.getInt("bloomblur", 1);

            Draw.drawRange(Layer.effectGround, 0.1f,
                    renderer.bloom::capture, renderer.bloom::render);
        }
        if(state.getPlanet() == atlacian) atlacianRenderer.run();
        SvVars.effectBuffer = buffer;
    }

    public static void initEnv(){
        Color waterColor = Color.valueOf("274D89");
        Color rainColor = Color.valueOf("4589EF");
        Core.assets.load("sprites/distortAlpha.png", Texture.class);
        float windSpeed = 0.3f, windAngle = 45f;
        float windx = Mathf.cosDeg(windAngle) * windSpeed, windy = Mathf.sinDeg(windAngle) * windSpeed;

        atlacianRenderer = () -> {
            Texture tex = Core.assets.get("sprites/distortAlpha.png", Texture.class);
            if(tex.getMagFilter() != Texture.TextureFilter.linear){
                tex.setFilter(Texture.TextureFilter.linear);
                tex.setWrap(Texture.TextureWrap.repeat);
            }

            Draw.z(Layer.light + 0.5f);
            Draw.blend(Blending.additive);
            Draw.color(waterColor.cpy().a(0.3f));
            Draw.rect();
            Draw.blend();

            Draw.z(Layer.weather);
            Draw.color(rainColor);
            Weather.drawRain(
                    2.45f,
                    4f,
                    windx * 2,
                    windy * 2,
                    1000f,
                    1f,
                    0.8f,
                    rainColor
            );
        };
    }
}
