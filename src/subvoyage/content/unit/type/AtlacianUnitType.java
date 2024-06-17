package subvoyage.content.unit.type;

import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.world.meta.*;
import subvoyage.content.world.items.*;

public class AtlacianUnitType extends UnitType{
    public AtlacianUnitType(String name){
        super(name);
        outlineColor = Pal.darkOutline;
        envDisabled = Env.space;
        ammoType = new ItemAmmoType(SvItems.corallite);
        researchCostMultiplier = 10f;
    }
}
