package subvoyage.core.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
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
import static mindustry.type.Weather.rand;
import static subvoyage.content.world.SvPlanets.atlacian;

public class SvRender {
    public static FrameBuffer buffer;
    public static @Nullable Bloom bloom;

    public static Runnable atlacianRenderer;

    public static class Layer extends mindustry.graphics.Layer {
        public static final float
                laser = 72.2f,
                powerBubbles = 76.7f,

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
        Color rainColor = Color.valueOf("4589EF").mul(1.1f);
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
            Draw.blend(Blending.additive);
            SvRender.drawParticlesOffset(atlas.find("circle"), SvPal.hydrogen.cpy().mul(1.3f),
                    20f,
                    32f,
                    0.0001f,
                    10000f,
                    1f,
                    0.3f,
                    windx/2f + SvDraw._3D.xOffset(camera.position.x,0.001f),
                    windy/2f + SvDraw._3D.yOffset(camera.position.y,0.001f),
                    0.05f,
                    0.08f,
                    20f,
                    30f,
                    1f,
                    Time.time * 0.02f,
                    true
            );
            SvRender.drawParticlesOffset(atlas.find("circle"), SvPal.hydrogen.cpy().mul(1.7f),
                    40f,
                    62f,
                    0.0008f,
                    30000f,
                    1f,
                    0.3f,
                    windx/4f,
                    windy/4f,
                    0.1f,
                    0.2f,
                    50f,
                    70f,
                    1f,
                    Time.time * 0.02f,
                    true
            );
            SvRender.drawParticlesOffset(atlas.find("circle"), SvPal.hydrogen.cpy().mul(1.7f),
                    70f,
                    92f,
                    0.0016f,
                    1000000f,
                    0.5f,
                    0.3f,
                    windx/6f,
                    windy/6f,
                    0.3f,
                    0.5f,
                    50f,
                    70f,
                    1f,
                    Time.time * 0.02f,
                    true
            );
            Draw.blend();
        };
    }

    public static void drawParticlesOffset(TextureRegion region, Color color, float sizeMin, float sizeMax, float height, float density, float intensity, float opacity, float windx, float windy, float minAlpha, float maxAlpha, float sinSclMin, float sinSclMax, float sinMagMin, float sinMagMax, boolean randomParticleRotation) {
        rand.setSeed(0L);
        Tmp.r1.setCentered(Core.camera.position.x, Core.camera.position.y, (float)Core.graphics.getWidth() / Vars.renderer.minScale(), (float)Core.graphics.getHeight() / Vars.renderer.minScale());
        Tmp.r1.grow(sizeMax * 1.5F);
        Core.camera.bounds(Tmp.r2);
        int total = (int)(Tmp.r1.area() / density * intensity);
        Draw.color(color, opacity);

        for(int i = 0; i < total; ++i) {
            float scl = rand.random(0.5F, 1.0F);
            float scl2 = rand.random(0.5F, 1.0F);
            float size = rand.random(sizeMin, sizeMax);
            float x = rand.random(0.0F, (float)Vars.world.unitWidth()) + Time.time * windx * scl2;
            float y = rand.random(0.0F, (float)Vars.world.unitHeight()) + Time.time * windy * scl;
            float alpha = rand.random(minAlpha, maxAlpha);
            float rotation = randomParticleRotation ? rand.random(0.0F, 360.0F) : 0.0F;
            x += Mathf.sin(y, rand.random(sinSclMin, sinSclMax), rand.random(sinMagMin, sinMagMax));
            x += SvDraw._3D.xOffset(x, height * 30f);
            y += SvDraw._3D.yOffset(y, height * 30f);

            x -= Tmp.r1.x;
            y -= Tmp.r1.y;
            x = Mathf.mod(x, Tmp.r1.width);
            y = Mathf.mod(y, Tmp.r1.height);
            x += Tmp.r1.x;
            y += Tmp.r1.y;
            if (Tmp.r3.setCentered(x, y, size).overlaps(Tmp.r2)) {
                Draw.alpha(alpha * opacity);
                Draw.rect(region, x, y, size, size, rotation);
            }
        }

        Draw.reset();
    }
}
