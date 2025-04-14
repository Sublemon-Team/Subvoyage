package subvoyage.type.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Bullet;
import mindustry.graphics.Trail;
import subvoyage.core.draw.SvDraw;

import static mindustry.Vars.headless;

public class BallisticBulletType extends BasicBulletType {

    public BallisticBulletType(float speed, float damage) {
        super(speed, damage);
    }

    @Override
    public void updateTrail(Bullet b) {
        float camHeight = new Interp.PowOut(2).apply(Interp.slope.apply(b.fin()))*(1f/b.type.speed*(b.lifetime/60f));
        float camSize = 1f + new Interp.PowOut(2).apply(Interp.slope.apply(b.fin()))*(1f/b.type.speed*(b.lifetime/60f));

        float x = SvDraw._3D.xHeight(b.x,camHeight);
        float y = SvDraw._3D.yHeight(b.y,camHeight);
        if(!headless && trailLength > 0){
            if(b.trail == null){
                b.trail = new Trail(trailLength);
            }
            b.trail.length = trailLength;
            b.trail.update(x, y, trailInterp.apply(b.fin()) * camSize * (1f + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0f)));
        }
    }

    @Override
    public void drawParts(Bullet b) {
        if(parts.size > 0){
            float camHeight = new Interp.PowOut(2).apply(Interp.slope.apply(b.fin()))*(1f/b.type.speed*(b.lifetime/60f));
            float camSize = 1f + new Interp.PowOut(2).apply(Interp.slope.apply(b.fin()))*(1f/b.type.speed*(b.lifetime/60f));

            float x = SvDraw._3D.xHeight(b.x,camHeight);
            float y = SvDraw._3D.yHeight(b.y,camHeight);
            DrawPart.params.set(b.fin(), 0f, 0f, 0f, 0f, 0f, x, y, b.rotation());
            DrawPart.params.life = b.fin();

            for(int i = 0; i < parts.size; i++){
                parts.get(i).draw(DrawPart.params);
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        float camHeight = new Interp.PowOut(2).apply(Interp.slope.apply(b.fin()))*(1f/b.type.speed*(b.lifetime/60f));
        float camSize = 1f + new Interp.PowOut(2).apply(Interp.slope.apply(b.fin()))*(1f/b.type.speed*(b.lifetime/60f));

        float x = SvDraw._3D.xHeight(b.x,camHeight);
        float y = SvDraw._3D.yHeight(b.y,camHeight);

        drawTrail(b);
        drawParts(b);
        float shrink = shrinkInterp.apply(b.fout());
        float height = this.height * ((1f - shrinkY) + shrinkY * shrink) * camSize;
        float width = this.width * ((1f - shrinkX) + shrinkX * shrink) * camSize;
        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;

        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

        Draw.mixcol(mix, mix.a);

        if(backRegion.found()){
            Draw.color(backColor);
            Draw.rect(backRegion, x, y, width, height, b.rotation() + offset);
        }

        Draw.color(frontColor);
        Draw.rect(frontRegion, x, y, width, height, b.rotation() + offset);

        Draw.reset();
    }
}
