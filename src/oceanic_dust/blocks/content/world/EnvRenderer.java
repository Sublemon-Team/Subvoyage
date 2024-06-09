package oceanic_dust.blocks.content.world;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import mindustry.graphics.*;

public class EnvRenderer extends EnvRenderers {
    public static void init(){
        Color waterColor = Color.valueOf("2953BC");
        Rand rand = new Rand();

        Core.assets.load("sprites/rays.png", Texture.class).loaded = t -> t.setFilter(Texture.TextureFilter.linear);
        Core.assets.load("sprites/distortAlpha.png", Texture.class);
        float windSpeed = 0.3f, windAngle = 45f;
        float windx = Mathf.cosDeg(windAngle) * windSpeed, windy = Mathf.sinDeg(windAngle) * windSpeed;
        /*renderer.addEnvRenderer(Environment.underwatering, () -> {
            Draw.draw(Layer.light + 1, () -> {
                Texture distort = Core.assets.get("sprites/distortAlpha.png", Texture.class);
                if(distort.getMagFilter() != Texture.TextureFilter.linear){
                    distort.setFilter(Texture.TextureFilter.linear);
                    distort.setWrap(Texture.TextureWrap.repeat);
                }

                //Draw.z(state.rules.fog ? Layer.fogOfWar + 1 : Layer.weather - 1);
                Weather.drawNoiseLayers(distort, waterColor, 1500f, 0.45f, 0.4f, 1f, windx, windy, 4, -1.3f, 0.7f, 0.8f, 0.9f);
                Draw.reset();
            });

            Draw.z(Layer.light + 2);
            int rays = 70;
            float timeScale = 2000f;
            rand.setSeed(0);
            Draw.blend(Blending.additive);

            float t = Time.time / timeScale;
            Texture tex = Core.assets.get("sprites/rays.png", Texture.class);
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
                float topDst = (Core.camera.position.y + Core.camera.height/2f) - (y + tex.height/2f + tex.height*1.9f*sizeScale/2f);
                float invDst = topDst/1000f;
                opacity = Math.min(opacity, -invDst);

                if(opacity > 0.01){
                    Draw.alpha(opacity);
                    Draw.rect(Draw.wrap(tex), x, y + tex.height/2f, tex.width*2*sizeScale, tex.height*2*sizeScale, rot);
                    Draw.color();
                }
            }

            //suspended particles
            Draw.draw(Layer.weather, () -> Weather.drawRain(
                    0.012f,
                    0.32f,
                    windx * 2,
                    windy * 2,
                    4.75f,
                    0.0125f,
                    0.8f,
                    waterColor
            ));

            Draw.blend();
        });*/
    }
}
