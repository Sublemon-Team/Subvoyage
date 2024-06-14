package subvoyage.content.world;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.entities.*;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;

public class SvFx{
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();

    public static final Effect

    none = new Effect(0, 0f, e -> {
    }),

    pulverize = new Effect(20, e -> {
        color(e.color.a(0.75f));
        stroke(e.fout() * 2f);
        Lines.circle(e.x, e.y, e.finpow() * (e.lifetime / 5));

        randLenVectors(e.id + 1, 4, 18 * e.finpow(), (x, y) ->
        lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f));
    }),

    smokePuff = new Effect(18, e -> {
        color(e.color.a(0.2f));
        randLenVectors(e.id, 6, 30f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 2f);
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout());
        });
    }),

    hitLaserOrange = new Effect(8, e -> {
        color(Color.white, Pal.lightOrange, e.fin());
        stroke(0.5f + e.fout());
        Lines.circle(e.x, e.y, e.fin() * 5f);

        Drawf.light(e.x, e.y, 23f, Pal.lightOrange, e.fout() * 0.7f);
    }),

    missileTrailSmoke = new Effect(60f, 100f, b -> {
        float intensity = 2f;
        color(b.color, 0.5f);
        for(int i = 0; i < 4; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(0.5f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(1.25f * intensity), 3 * intensity, (x, y, in, out) -> {
                float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                float rad = fout * intensity;
                Fill.circle(e.x + x, e.y + y, rad);
                Drawf.light(e.x + x, e.y + y, rad, b.color, 0.25f);
            }));
        }
    }).layer(Layer.bullet - 1f),

    missileTrailShort = new Effect(16, e -> {
        color(e.color);
        Fill.circle(e.x, e.y, e.rotation * e.fout() / 2);
    }).layer(Layer.bullet - 0.001f),

    shootLauncher = new Effect(70f, e -> {
        rand.setSeed(e.id);
        for(int i = 0; i < 6; i++){
            v.trns(e.rotation + rand.range(15), rand.random(e.finpow() * 40f));
            e.scaled(e.lifetime, b -> {
                color(e.color, Pal.lightishGray, b.fin());
                Fill.circle(e.x + v.x, e.y + v.y, b.fout());
            });
        }
    });
}
