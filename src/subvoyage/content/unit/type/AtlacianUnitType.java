package subvoyage.content.unit.type;

import arc.graphics.g2d.Draw;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.world.meta.*;
import subvoyage.content.world.items.*;

public class AtlacianUnitType extends UnitType{
    public float bodyScale = 1f;
    public AtlacianUnitType(String name){
        super(name);
        outlineColor = Pal.darkOutline;
        envDisabled = Env.space;
        ammoType = new ItemAmmoType(SvItems.corallite);
        researchCostMultiplier = 10f;
    }

    @Override
    public void drawBody(Unit unit) {
        float scl = Draw.scl;
        Draw.scl(bodyScale);
        super.drawBody(unit);
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
