package subvoyage.content.unit.weapons;

import arc.graphics.g2d.Draw;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import subvoyage.content.unit.entity.HydromechUnitEntity;

public class HydromechWeapon extends Weapon {
    public WeaponStatState groundStat, waterStat;
    public float sclX,sclY = 1f;

    public HydromechWeapon(String id) {
        super(id);
    }

    @Override
    protected void handleBullet(Unit unit, WeaponMount mount, Bullet bullet) {
        super.handleBullet(unit, mount, bullet);
        WeaponStatState stat = getStatState(unit);
        if(stat != null) stat.apply(bullet);
    }


    @Override
    public void draw(Unit unit, WeaponMount mount) {
        Draw.scl(sclX,sclY);
        super.draw(unit, mount);
        Draw.scl();
    }

    public WeaponStatState getStatState(Unit unit) {
        if(unit instanceof HydromechUnitEntity hm) return hm.liquidedSmooth() > 0.5f ? waterStat : groundStat;
        return groundStat;
    }
}
