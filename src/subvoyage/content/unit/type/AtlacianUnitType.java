package subvoyage.content.unit.type;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.content.UnitTypes;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.world.meta.*;
import subvoyage.content.unit.weapons.HydromechWeapon;
import subvoyage.content.world.items.*;

public class AtlacianUnitType extends UnitType{
    public float bodyScale = 1f;
    public boolean bodyHeat = false;
    public TextureRegion regionHeat;
    public Color heatColor = Pal.turretHeat;
    public AtlacianUnitType(String name){
        super(name);
        outlineColor = Pal.darkOutline;
        envDisabled = Env.space;
        ammoType = new ItemAmmoType(SvItems.corallite);
        researchCostMultiplier = 10f;
    }

    @Override
    public void load() {
        super.load();
        if(bodyHeat) regionHeat = Core.atlas.find(name+"-heat");
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        if(bodyHeat)
            for (Weapon weapon : unit.type.weapons) {
                weapon.heatColor = this.heatColor;
            }
    }

    @Override
    public void drawBody(Unit unit) {
        float scl = Draw.scl;
        Draw.scl(bodyScale);
        super.drawBody(unit);
        if(regionHeat != null && regionHeat.found()) {
            float warmup = 0f;
            for (WeaponMount mount : unit.mounts) {
                if(mount.weapon instanceof HydromechWeapon hw && !hw.hasHeat) continue;
                warmup = Math.max(warmup, mount.heat);
                //mount.heat = mount.warmup;
            }
            Draw.blend(Blending.additive);
            Draw.color(heatColor,warmup);
            Draw.scl(bodyScale);
            Draw.rect(regionHeat, unit.x, unit.y, unit.rotation - 90);
            Draw.blend();
        }
        Draw.scl();
    }

    @Override
    public void drawCell(Unit unit) {
        float scl = Draw.scl;
        Draw.scl(bodyScale);
        super.drawCell(unit);
        Draw.scl();
    }
}
