package subvoyage.type.unit.custom;

import mindustry.gen.Bullet;

public class HydromechWeaponStateStats {
    public float damage;
    public float lifetime;


    public void apply(Bullet bullet) {
        if(bullet == null) return;
        bullet.damage = damage;
        bullet.lifetime = lifetime;
    }
}
