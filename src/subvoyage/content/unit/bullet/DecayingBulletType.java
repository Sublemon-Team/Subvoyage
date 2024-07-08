package subvoyage.content.unit.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class DecayingBulletType extends BasicBulletType {
    public float dmgReduction;
    public DecayingBulletType(float speed, float dmg, float damageReduction) {
        super(speed,dmg);
        this.dmgReduction = damageReduction;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        if(b.time >= Time.delta/60f)
            b.damage -= dmgReduction/b.lifetime*Time.delta;
        if(b.damage <= 0) b.absorb();
    }

    @Override
    public void draw(Bullet b) {
        drawTrail(b);
        drawParts(b);
        float shrink = shrinkInterp.apply(b.fout());
        float height = this.height * ((1f - shrinkY) + shrinkY * shrink);
        float width = this.width * ((1f - shrinkX) + shrinkX * shrink);
        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;

        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

        Draw.mixcol(mix, mix.a);

        if(backRegion.found()){
            Draw.color(b.team.color);
            Draw.rect(backRegion, b.x, b.y, width, height, b.rotation() + offset);
        }

        Draw.color(frontColor);
        Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation() + offset);

        Draw.reset();
    }


    @Override
    public void drawTrail(Bullet b) {
        if(trailLength > 0 && b.trail != null){
            float z = Draw.z();
            Draw.z(z - 0.0001f);
            b.trail.draw(b.team.color, trailWidth*(Mathf.clamp(b.fout()*2)));
            Draw.z(z);
        }
    }

    @Override
    public void updateTrail(Bullet b) {
        super.updateTrail(b);
    }

    @Override
    public void updateTrailEffects(Bullet b) {
        if(trailChance > 0){
            if(Mathf.chanceDelta(trailChance)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, b.team.color);
            }
        }

        if(trailInterval > 0f){
            if(b.timer(0, trailInterval)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, b.team.color);
            }
        }
    }
}
