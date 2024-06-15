package subvoyage.content.unit.better;

import mindustry.graphics.Pal;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.meta.Env;
import subvoyage.content.unit.systematical.BaseUType;
import subvoyage.content.world.items.SvItems;

public class AtlacianUnitType extends BaseUType {
    public AtlacianUnitType(String name) {
        super(name);
        outlineColor = Pal.darkOutline;
        envDisabled = Env.space;
        ammoType = new ItemAmmoType(SvItems.corallite);
        researchCostMultiplier = 10f;
    }
}
