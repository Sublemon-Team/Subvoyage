package subvoyage.type.bullet;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
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
    public void drawTrail(Bullet b) {
        if(trailLength > 0 && b.trail != null){
            float z = Draw.z();
            Draw.z(z - 0.0001f);
            b.trail.draw(trailColor, trailWidth*(Mathf.clamp(b.fout()*2)));
            Draw.z(z);
        }
    }
}
