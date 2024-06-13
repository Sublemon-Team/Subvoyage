package subvoyage.content.unit;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.Position;
import arc.util.*;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.UnitType;

public class HelicopterUnitType extends UnitType {
    public TextureRegion rotator, outlineR;
    public float layerOffset = 1, outlineLayerOffset = -0.001f;
    public float x, y, xScl = 1f, yScl = 1f, rotation;
    public float moveRot;
    public @Nullable Color color;
    public String suffix = "-rotator";
    public boolean outline = true;

    public float acceleration = 0.1f;
    public HelicopterUnitType(String name) {
        super(name);
        flying = true;
        omniMovement = false;
        rotateMoveFirst = true;
    }

    @Override
    public void update(Unit unit) {
        accel += acceleration/2f;
        super.update(unit);
    }

    public void draw(Unit unit){
        super.draw(unit);
        Tmp.v1.set(x, y);
        float t = (Time.time * unit.speed()) / 2;
        float rx = unit.x + x, ry = unit.y + y, rot = (t * moveRot) % rotation;

        Draw.xscl *= xScl;
        Draw.yscl *= yScl;
        Draw.z(Draw.z() + layerOffset);
        Drawf.spinSprite(rotator, rx, ry, rot);
        if(outline){
            Draw.z(Draw.z() + outlineLayerOffset);
            Draw.color(Pal.darkOutline);
            Draw.rect(outlineR, rx, ry, rot);
            Draw.reset();
            Draw.z(Draw.z());
        }
    }

    @Override
    public Unit spawn(Position pos, Team team) {
        return super.spawn(pos, team);
    }

    public void load(){
        rotator = Core.atlas.find(name + suffix);
        if(outline){
            outlineR = Core.atlas.find(name + suffix + "-outline");
        }

        super.load();
    }
}
