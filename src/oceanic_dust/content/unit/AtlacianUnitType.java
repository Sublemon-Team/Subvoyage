package oceanic_dust.content.unit;

import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.world.meta.*;
import oceanic_dust.content.world.items.*;

public class AtlacianUnitType extends UnitType{

    public AtlacianUnitType(String name){
        super(name);
        outlineColor = Pal.darkOutline;
        envDisabled = Env.space;
        ammoType = new ItemAmmoType(ODItems.corallite);
        researchCostMultiplier = 10f;
    }
}
