package subvoyage.entities.shoot;

import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.pattern.ShootPattern;

public class ShootLeeft extends ShootPattern{
    /** barrels [in x, y, rotation] format. */
    public float[] barrels = {0f, 0f, 0f};
    /** offset of barrel to start on */
    public int barrelOffset = 0;
    public float scl = 4f, mag = 1.5f, offset = Mathf.PI * 1.25f;

    @Override
    public void flip(){
        barrels = barrels.clone();
        for(int i = 0; i < barrels.length; i += 3){
            barrels[i] *= -1;
            barrels[i + 2] *= -1;
        }
    }

    @Override
    public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer){
        int index = ((totalShots + barrelOffset) % (barrels.length / 3)) * 3;
        for(int i = 0; i < shots; i++){
            float signs = shots / ((float)barrels.length / 2) * 2;
            int finalI = i;
            handler.shoot(barrels[index], barrels[index + 1], barrels[index + 2], firstShotDelay + shotDelay,
            b -> b.moveRelative(0, Mathf.pow((b.fin())*1.5f, 2)*Mathf.sin(b.time + offset, scl*2f, signs)*(finalI % 2 == 0 ? -1 : 1)));
        }
        if(barrelIncrementer != null) barrelIncrementer.run();
    }
}
