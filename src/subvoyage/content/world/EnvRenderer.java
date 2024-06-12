package subvoyage.content.world;

import arc.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class EnvRenderer extends EnvRenderers {
    public static void init(){
        Color waterColor = Color.valueOf("154755");
        Rand rand = new Rand();
        Core.assets.load("sprites/rays.png", Texture.class).loaded = t -> t.setFilter(Texture.TextureFilter.linear);
        Core.assets.load("sprites/distortAlpha.png", Texture.class);
        float windSpeed = 0.3f, windAngle = 45f;
        float windx = Mathf.cosDeg(windAngle) * windSpeed, windy = Mathf.sinDeg(windAngle) * windSpeed;
        renderer.addEnvRenderer(Environment.legarytic, () -> {
            Texture tex = Core.assets.get("sprites/distortAlpha.png", Texture.class);
            if(tex.getMagFilter() != TextureFilter.linear){
                tex.setFilter(TextureFilter.linear);
                tex.setWrap(TextureWrap.repeat);
            }

            Draw.z(state.rules.fog ? Layer.fogOfWar + 1 : Layer.weather - 1);
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
                float topDst = (Core.camera.position.y + Core.camera.height/2f) - (y + ray.height/2f + ray.height*1.9f*sizeScale/2f);
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
                    6.75f,
                    0.0135f,
                    0.8f,
                    waterColor
            ));

            Draw.blend();
        });
    }
}
