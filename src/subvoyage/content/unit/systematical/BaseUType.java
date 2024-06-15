package subvoyage.content.unit.systematical;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.Scaled;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

import static mindustry.Vars.player;

public class BaseUType extends UnitType {
    public BaseUType(String name) {
        super(name);
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        //TODO how/where do I draw under?
        if(parts.size > 0){
            for(int i = 0; i < parts.size; i++){
                var part = parts.get(i);

                WeaponMount first = unit.mounts.length > part.weaponIndex ? unit.mounts[part.weaponIndex] : null;
                if(first != null){
                    DrawPart.params.set(first.warmup, first.reload / weapons.first().reload, first.smoothReload, first.heat, first.recoil, first.charge, unit.x, unit.y, unit.rotation);
                }else{
                    DrawPart.params.set(0f, 0f, 0f, 0f, 0f, 0f, unit.x, unit.y, unit.rotation);
                }

                if(unit instanceof Scaled s){
                    DrawPart.params.life = s.fin();
                }

                if(part instanceof UnitDrawPart drawPart && unit instanceof BaseUnit base)
                    drawPart.draw(base,DrawPart.params);
            }
        }
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
    }
}
