package subvoyage.content.unit.weapons;

import arc.graphics.g2d.Draw;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import subvoyage.content.unit.entity.HydromechUnitEntity;
import subvoyage.content.unit.type.HydromechState;

public class HydromechWeapon extends Weapon {
    public WeaponStatState groundStat, waterStat;
    public HydromechState activationState = HydromechState.ANY;
    public boolean activationBasedDraw = false;
    public float sclX,sclY = 1f;

    public HydromechWeapon(String id) {
        super(id);
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
        boolean canShoot = true;
        if(activationState != HydromechState.ANY && unit instanceof HydromechUnitEntity hm)
            canShoot = hm.getState() == activationState;
        if(canShoot) super.shoot(unit, mount, shootX, shootY, rotation);
    }

    @Override
    protected void handleBullet(Unit unit, WeaponMount mount, Bullet bullet) {
        super.handleBullet(unit, mount, bullet);
        WeaponStatState stat = getStatState(unit);
        if(stat != null) stat.apply(bullet);
    }


    @Override
    public void draw(Unit unit, WeaponMount mount) {
        if(activationBasedDraw) {
            boolean canShoot = true;
            if(activationState != HydromechState.ANY && unit instanceof HydromechUnitEntity hm)
                canShoot = hm.getState() == activationState;
            if(!canShoot) return;
        }
        Draw.scl(sclX,sclY);
        super.draw(unit, mount);
        Draw.scl();
    }

    public WeaponStatState getStatState(Unit unit) {
        if(unit instanceof HydromechUnitEntity hm) return hm.liquidedSmooth() > 0.5f ? waterStat : groundStat;
        return groundStat;
    }
}
