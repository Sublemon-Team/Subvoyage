package subvoyage.content.unit.weapons;

import mindustry.gen.Bullet;

public class WeaponStatState {
    public float damage;
    public float lifetime;


    public void apply(Bullet bullet) {
        bullet.damage = damage;
        bullet.lifetime = lifetime;
    }
}
