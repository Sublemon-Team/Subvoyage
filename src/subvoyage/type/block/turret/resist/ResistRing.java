package subvoyage.type.block.turret.resist;


import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import subvoyage.core.draw.SvFx;

import static mindustry.Vars.tilesize;

public class ResistRing {

    public float x,y;
    public float charge;
    public float angle;
    public float lifetime;
    public boolean hasTarget;
    public float targetX, targetY;


    public static Effect effect = new MultiEffect(SvFx.aweExplosion, SvFx.aweExplosionDust, new WaveEffect(){{
        lifetime = 10f;
        strokeFrom = 3f;
        strokeTo = 0f;
        sizeTo = 4*tilesize;
    }});


    public void shoot(ResistTurret.PowerRingTurretBuild build) {
       effect.layer(Layer.bullet).create(x,y,0f,Pal.powerLight,new Object());

        float rr = ((ResistTurret) build.block).ringRadius*1.5f;

        build.getBullet().create(build,x,y,angle);

        x = build.x+(build.rand.nextFloat()*rr*2f-rr);
        y = build.y+(build.rand.nextFloat()*rr*2f-rr);
        hasTarget = false;
        lifetime = 0;
        targetX = 0;
        targetY = 0;
    }
}
