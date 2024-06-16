package subvoyage.content.unit.systematical;

import mindustry.entities.part.DrawPart;

public abstract class UnitDrawPart extends DrawPart {

    public abstract void draw(BaseUnit unit, PartParams params);

    @Override
    public void load(String name) {

    }

    @Override
    public void draw(PartParams params) {

    }
}
