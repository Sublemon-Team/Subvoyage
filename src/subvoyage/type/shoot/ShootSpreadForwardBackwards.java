package subvoyage.type.shoot;

import arc.util.Nullable;
import arc.util.Time;
import mindustry.entities.pattern.ShootPattern;

public class ShootSpreadForwardBackwards extends ShootPattern {

    /** spread between bullets, in degrees. */
    public float spread = 5f;

    public ShootSpreadForwardBackwards(int shots, float spread){
        this.shots = shots;
        this.spread = spread;
    }

    public ShootSpreadForwardBackwards(){
    }

    @Override
    public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer){
        for(int i = 0; i < shots; i++){
            float angleOffset = i * spread - (shots - 1) * spread / 2f;
            int sign = i % 2 == 0 ? 1 : -1;
            handler.shoot(0, 0, angleOffset, firstShotDelay + shotDelay * i,(b) -> {
                b.rotation(b.rotation() - Time.delta * sign * 1.5f);
            });
        }
    }
}
