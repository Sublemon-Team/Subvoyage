package subvoyage.type.shoot;

import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.pattern.ShootHelix;

public class ShootZigZag extends ShootHelix {


    @Override
    public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer){
        for(int i = 0; i < shots; i++){
            for(int sign : Mathf.signs){
                handler.shoot(0, 0, 0, firstShotDelay + shotDelay * i,
                        b -> b.moveRelative(0f, nonsin(b.time + offset, scl, mag * sign)));
            }
        }
    }
    public static float nonsin(float radians, float scl, float mag){
        return nonsin((float) Math.toDegrees(radians / scl)) * mag / 90f;
    }
    public static float nonsin(float x) {
        float xMod = x % 360;
        if (xMod < 0) xMod += 360;
        if (xMod < 90) return xMod;
        if (xMod < 180) return 90 - (xMod - 90);
        if (xMod < 270) return -(xMod - 180);
        return -(90 - (xMod - 270));
    }

    public static float necossin(float x) {
        float xMod = x % 360;
        if (xMod < 0) xMod += 360;
        if (xMod < 90) return 90 - xMod;
        if (xMod < 180) return -(xMod - 90);
        if (xMod < 270) return -(90 - (xMod - 180));
        return (xMod - 270);
    }
}
