package subvoyage.type.shoot;

import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.pattern.ShootPattern;

public class ShootStunt extends ShootPattern {
    public float scl = 2f, mag = 1.5f, offset = Mathf.PI * 1.25f;

    @Override
    public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer){
        for(int i = 0; i < shots; i++){
            int sign = i % 2 == 0 ? 1 : -1;
            handler.shoot(0, 0, 0, firstShotDelay + shotDelay * i,
                    b -> {
                b.moveRelative(0f, Mathf.sin(b.time + offset, scl, mag * sign));
                if(b.time/b.type.lifetime >= 0.65) b.moveRelative(-b.type.speed*2,0);
            });
        }
    }
}
