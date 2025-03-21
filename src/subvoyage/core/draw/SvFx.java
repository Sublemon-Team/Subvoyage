package subvoyage.core.draw;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.graphics.*;
import subvoyage.content.block.SvPayload;
import subvoyage.type.block.payload.PayloadLaunchPad;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static subvoyage.type.unit.ability.LegionfieldAbility.lightInner;

public class SvFx{
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();
    public static final Vec2 vec = new Vec2(), vec2 = new Vec2();

    public static TextureRegion laser;
    public static TextureRegion laserEnd;
    public static TextureRegion laserTop;
    public static TextureRegion laserTopEnd;

    public static void loadLaser() {
        laser = Core.atlas.find("laser-white");
        laserEnd = Core.atlas.find("laser-white-end");
        laserTop = Core.atlas.find("laser-top");
        laserTopEnd = Core.atlas.find("laser-top-end");
    }


    public static final Effect

    none = new Effect(0, 0f, e -> {
    }),

    particle = new Effect(10f,e -> {
        stroke(e.fin(),e.color.cpy().value(1.1f));
        z(Layer.bullet+1f);
        Lines.circle(e.x,e.y,e.fout()*5.5f);
        Fill.circle(e.x,e.y,e.fout()*3.5f);
    }),

    heliWave = new Effect(30f, e -> {
        z(Layer.bullet);
        color(e.color);
        float size = (float) e.data;
        float rad = size*12f * e.fin();

        lightInner(e.x,e.y,(int) (rad*8),rad*e.fin(),rad,
                0f, e.color.cpy().a(0f), e.color.cpy());
        lightInner(e.x,e.y,(int) (rad*8),rad+rad*e.fout() * 0.2f,rad,
                0f, e.color.cpy().a(0f), e.color.cpy());

        rand.setSeed(e.id);
        for(int i = 0; i < 16; i++){
            float angle = rand.random(360f);
            float lenRand = rand.random(0.5f, 2f);
            Tmp.v1.trns(angle, rad);
            for(int s : Mathf.signs){
                Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * size * 2f, e.fout() * size * lenRand + 6f, angle + 90f + s * 90f);
            }
        }
    }),

    shootShockwave = new Effect(60f, e -> {
        Draw.color(e.color);

        float
                fin = Interp.circleOut.apply(e.fout()),
                fin2 = new Interp.ExpOut(10, 10).apply(e.fin()),
                fout = new Interp.ExpOut(10, 10).apply(e.fout());

        float progress = e.fin();

        float cover = 280f * fin2 - 40f * Mathf.slope(Interp.circleOut.apply(e.fin()));

        vec.trns(e.rotation, 8f - 18f * fin).add(e.x, e.y);

        EFill.donutEllipse(
                vec.x, vec.y,
                8f * progress * fout, 28f * fout,
                4f * progress * fout, 24f * fout,
                cover/360f,
                -cover/2f, e.rotation
        );
    }).followParent(true).rotWithParent(true),

    burnFlash = new Effect(30f,e -> {
        color(Pal.powerLight);
        Lines.stroke(e.fout()*3f,Pal.powerLight);
        Lines.circle(e.x,e.y,e.finpowdown()*16f);
        Fill.circle(e.x,e.y,e.foutpow()*16f);
    }),

    photosynthFlash = new Effect(30f, e -> {
        stroke(e.fout(Interp.pow2Out), SvPal.photoFlash);
        Lines.square(e.x,e.y,e.fin(Interp.pow2Out)*3*tilesize/2f,45f*e.finpowdown());
        Lines.square(e.x,e.y,e.fin(Interp.pow2Out)*3*tilesize/2f,-45f*e.finpowdown());
    }),
    blast = new Effect(30f, e -> {
        z(Layer.block+.1f);
        color(SvPal.teslaCharge);
        stroke(e.fout() * 4f);
        float circleRad = e.finpow() * 1f*8f;
        Lines.circle(e.x, e.y, circleRad);
        color(Color.white);
        Fill.circle(e.x,e.y,circleRad*e.foutpow()*0.75f);

        rand.setSeed(e.id);
        color(SvPal.teslaCharge.cpy().lerp(Color.white,e.fin()));
        for(int i = 0; i < 16; i++){
            float angle = rand.random(360f);
            float lenRand = rand.random(-0.35f, 0.35f);
            Tmp.v1.trns(angle, circleRad);

            for(int s : Mathf.signs){
                Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 6f, e.fout() * 10f * lenRand + 4f, angle + 90f + s * 90f);
            }
        }
    }),
    upsurgeTrail = new Effect(8f, e -> {
        color(SvPal.phosphide);

        for(int i : Mathf.signs){
            Drawf.tri(e.x, e.y, 6f * e.fout(), 24f, e.rotation + 90 + 90f * i);
        }

        Drawf.light(e.x, e.y, 60f * e.fout(), SvPal.phosphide, 0.5f);
    }),
    skathTrail = new Effect(8f, e -> {
        color(SvPal.heatGlow);

        for(int i : Mathf.signs){
            Drawf.tri(e.x, e.y, 6f * e.fout(), -12f, e.rotation + 90 + 90f * i);
        }

        Drawf.light(e.x, e.y, 60f * e.fout(), SvPal.phosphide, 0.5f);
    }),
    coreBubble = new Effect(20, e -> {
        z(Layer.blockUnder);
        color(Tmp.c1.set(e.color).shiftValue(0.1f));
        stroke(e.fout() + 0.2f);
        randLenVectors(e.id, 2, e.rotation * 0.9f, (x, y) -> {
            Lines.circle(e.x + x, e.y + y, 1f + e.fin() * 3f);
        });
    }),

    scutchFlash = new Effect(30f, e -> {
        stroke(e.fout(Interp.pow2Out), SvPal.quartzFiber);
        Lines.square(e.x,e.y,e.fin(Interp.pow2Out)*3*tilesize/2f,45f*e.finpowdown());
        Lines.poly(e.x,e.y,8,e.fin()*3*tilesize,0);
        Lines.square(e.x,e.y,e.fin(Interp.pow2Out)*3*tilesize/2f,-45f*e.finpowdown());
    }),

    tugPress = new Effect(30f, e -> {
        stroke(e.fout(Interp.pow2Out), SvPal.tugSheetGlow);
        Lines.square(e.x,e.y,e.fin(Interp.pow2Out)*3*tilesize,0);
        Lines.line(e.x+tilesize*2f*e.finpowdown(),e.y-tilesize*2f,e.x+tilesize*2f*e.finpowdown(),e.y+tilesize*2f);
        Lines.line(e.x-tilesize*2f*e.finpowdown(),e.y-tilesize*2f,e.x-tilesize*2f*e.finpowdown(),e.y+tilesize*2f);
    }),

    rocketLandDust = new Effect(100f, e -> {
        color(e.color, e.fout(0.1f)*e.color.a);
        rand.setSeed(e.id);
        Tmp.v1.trns(e.rotation, e.finpow() * 90f * rand.random(0.2f, 1f));
        Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 8f * rand.random(0.6f, 1f) * e.fout(0.2f));
    }).layer(Layer.groundUnit + 1f),


    payloadLaunchPadRocketLaunch = new Effect(240f,e -> {
        PayloadLaunchPad launchPad = (PayloadLaunchPad) SvPayload.payloadLaunchPad;
        Draw.alpha(e.fout());
        Draw.scl(1f+ e.finpowdown()*2f,1f+ e.finpowdown()*2f);
        float x = SvDraw._3D.xHeight(e.x,e.finpowdown()*2f);
        float y = SvDraw._3D.yHeight(e.y,e.finpowdown()*2f);
        Draw.rect(launchPad.rocketRegion,x,y,e.fin()*360f);
        if(e.fout() > 0.75f && !state.isPaused() && rand.chance(0.5f)) rocketLandDust.create(x,y,Mathf.random(360f),Pal.stoneGray.cpy().a(0.2f),new Object());
        if(e.fout() > 0.97f && !state.isPaused() && rand.chance(0.125f)) Fx.launchPod.create(x,y,0,Pal.accent,new Object());
    }),
    payloadLaunchPadRocketLand = new Effect(240f,e -> {
        PayloadLaunchPad launchPad = (PayloadLaunchPad) SvPayload.payloadLaunchPad;
        Draw.alpha(e.finpowdown());
        Draw.scl(1f+ e.foutpow()*2f,1f+ e.foutpow()*2f);
        float x = SvDraw._3D.xHeight(e.x,e.foutpow());
        float y = SvDraw._3D.yHeight(e.y,e.foutpow());
        Draw.rect(launchPad.rocketRegion,x,y,e.fin()*360f);
        if(e.fin() > 0.75f && !state.isPaused() && rand.chance(0.9f)) rocketLandDust.create(x,y,Mathf.random(360f),Pal.stoneGray.cpy().a(0.2f),new Object());
        if(e.fin() > 0.97f && !state.isPaused() && rand.chance(0.125f)) Fx.launchPod.create(x,y,0,Pal.accent,new Object());
    }),

    smokeCloud = new Effect(25, e -> {
        randLenVectors(e.id, e.fin(), 15, 15f, (x, y, fin, fout) -> {
            color(Color.gray);
            alpha((0.5f - Math.abs(fin - 0.5f)) * 2f);
            Fill.circle(e.x + x, e.y + y, 0.5f + fout * 4f);
        });
    }),

    aweExplosion = new Effect(60f, 160f, e -> {
        color(SvPal.teslaCharge);
        stroke(e.fout() * 4f);
        float circleRad = e.finpow() * 6*8f;
        Lines.circle(e.x, e.y, circleRad);

        rand.setSeed(e.id);
        for(int i = 0; i < 16; i++){
            float angle = rand.random(360f);
            float lenRand = rand.random(0.5f, 1f);
            Tmp.v1.trns(angle, circleRad);

            for(int s : Mathf.signs){
                Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 6f, e.fout() * 10f * lenRand + 6f, angle + 90f + s * 90f);
            }
        }
    }),

    resonanceExplosion = new Effect(60f, 160f, e -> {
        color(Color.white);
        stroke(e.fout() * 2f);
        float circleRad = e.finpow() * 10*8f;
        Lines.circle(e.x, e.y, circleRad);

        rand.setSeed(e.id);
        for(int i = 0; i < 16; i++){
            float angle = rand.random(360f);
            float lenRand = rand.random(0.5f, 1f);
            Tmp.v1.trns(angle, circleRad);

            for(int s : Mathf.signs){
                Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 6f, e.fout() * 10f * lenRand + 6f, angle + 90f + s * 90f);
            }
        }
    }),

    spectrumExplosion = new Effect(60f, 160f, e -> {
        color(e.color);
        stroke(e.fout() * 3f);
        float circleRad = 6f + e.finpow() * (float) e.data;
        Lines.circle(e.x, e.y, circleRad);

        lightInner(e.x,e.y,(int) (circleRad*8), circleRad * e.fin(), circleRad, 0f,
                e.color.cpy().a(0f),e.color.cpy());

        rand.setSeed(e.id);
        for(int i = 0; i < 16; i++){
            float angle = rand.random(360f);
            float lenRand = rand.random(0.5f, 2f);
            Tmp.v1.trns(angle, circleRad);
            for(int s : Mathf.signs){
                Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 12f, e.fout() * 10f * lenRand + 6f, angle + 90f + s * 90f);
            }
        }
    }),
    aweExplosionDust = new Effect(60f, 160f, e -> {
        color(SvPal.teslaCharge);
        alpha(0.3f);
        stroke(e.fout() * 5f);
        float circleRad = e.finpow() * 6*8f;
        Lines.circle(e.x, e.y, circleRad);

        rand.setSeed(e.id);
        for(int i = 0; i < 16; i++){
            float angle = rand.random(360f);
            float lenRand = rand.random(0.5f, 1f);
            Tmp.v1.trns(angle, circleRad);

            for(int s : Mathf.signs){
                alpha(0.3f-rand.random(0.25f));
                Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 24f * lenRand);
            }
        }
    }),
    resonanceExplosionDust = new Effect(60f, 160f, e -> {
        color(Color.white);
        alpha(0.3f);
        stroke(e.fout() * 2f);
        float circleRad = e.finpow() * 10*8f;
        Lines.circle(e.x, e.y, circleRad);

        rand.setSeed(e.id);
        for(int i = 0; i < 16; i++){
            float angle = rand.random(360f);
            float lenRand = rand.random(0.5f, 1f);
            Tmp.v1.trns(angle, circleRad);

            for(int s : Mathf.signs){
                alpha(0.3f-rand.random(0.25f));
                Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 24f * lenRand);
            }
        }
    }),

    pulverize = new Effect(20, e -> {
        color(e.color.a(0.75f));
        stroke(e.fout() * 2f);
        Lines.circle(e.x, e.y, e.finpow() * (e.lifetime / 5));

        randLenVectors(e.id + 1, 4, 18 * e.finpow(), (x, y) ->
        lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f));
    }),

    point = new Effect(60, e -> {
        color(e.color.a(e.fout()));
        Lines.circle(e.x,e.y,e.finpow()*4);
    }),

    decoderWave = new Effect(20f, 80f, e -> {
        color(Color.white, Color.lightGray, e.fin());
        stroke(e.fout() * 4f);

        Lines.circle(e.x, e.y, e.fin() * (float) e.data);
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

    missileTrailSmokeMedium = new Effect(120f, 200f, b -> {
        float intensity = 2f;
        color(b.color, 0.7f);
        for(int i = 0; i < 4; i++){
            rand.setSeed(b.id * 2L + i);
            float lenScl = rand.random(0.5f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(1.25f * intensity), 6.5f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                    float rad = fout * ((2f + intensity) * 1.25f);

                    Fill.circle(e.x + x, e.y + y, rad);
                    Drawf.light(e.x + x, e.y + y, rad * 1.85f, b.color, 0.5f);
                });
            });
        }
    }).layer(Layer.bullet - 1f),


    missileTrailSmokeSmall = new Effect(60f, 100f, b -> {
        float intensity = 2f;
        color(b.color, 0.5f);
        for(int i = 0; i < 4; i++){
            rand.setSeed(b.id* 2L + i);
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
    }),

    beam = new Effect(60f, e -> {
        float[] pos = e.data();
        float x1 = pos[0];
        float y1 = pos[1];
        float x2 = pos[2];
        float y2 = pos[3];
        Draw.color(e.color);
        drawLaser(x1,y1,x2,y2,2,2);
        Draw.reset();
    });

    public static Func<Object,Effect>
            hitLaserColor = c -> new Effect(8, e -> {
                color(Color.white, (Color) c, e.fin());
                stroke(0.5f + e.fout());
                Lines.circle(e.x, e.y, e.fin() * 5f);

                Drawf.light(e.x, e.y, 23f, (Color) c, e.fout() * 0.7f);
            }),
            colorRadExplosion = cr -> new Effect(60f, 160f, e -> {
                Color c = (Color) ((Object[]) cr)[0];
                float r = (float) ((Object[]) cr)[1];
                color(c);
                stroke(e.fout() * 2f);
                float circleRad = e.finpow() * r;
                Lines.circle(e.x, e.y, circleRad);

                rand.setSeed(e.id);
                for(int i = 0; i < 16; i++){
                    float angle = rand.random(360f);
                    float lenRand = rand.random(0.5f, 1f);
                    Tmp.v1.trns(angle, circleRad);

                    for(int s : Mathf.signs){
                        Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 6f, e.fout() * 10f * lenRand + 6f, angle + 90f + s * 90f);
                    }
                }
            });


    public static void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2){
        if(laser == null) loadLaser();

        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f - 1.5f, len2 = size2 * tilesize / 2f - 1.5f;

        Drawf.laser(laser, laserEnd, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, 0.25f);
    }
}
