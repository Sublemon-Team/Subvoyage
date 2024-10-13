package subvoyage.type.shoot;

import arc.math.Mathf;
import mindustry.entities.pattern.ShootPattern;
import mindustry.entities.pattern.ShootSpread;

import static mindustry.Vars.tilesize;

public class ShootUpsurge extends ShootPattern {
    @Override
    public void shoot(int totalShots, BulletHandler handler, Runnable barrelIncrementer) {
        for(int i = 0; i < shots; i++){
            int finalI = i;
            handler.shoot(0, 0, 0, firstShotDelay + shotDelay * i,
                    b -> b.moveRelative(finalI * 0.75f, 0f));
        }
    }
}
