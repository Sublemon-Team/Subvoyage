package subvoyage.entities.part;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;
import subvoyage.*;

public class RotatorRegionPart extends DrawPart{
    public TextureRegion rotator, outlineR;
    public float layerOffset = -1, outlineLayerOffset = -0.001f;
    public float x, y, xScl = 1f, yScl = 1f, rotation, unitrot;
    public float moveRot;
    public float unitX;
    public float unitY;

    public @Nullable Color color;
    public String suffix = SubvoyageMod.ID + "-base-rotator";
    public boolean outline = false;

    public RotatorRegionPart(String suffix){
        this.suffix = suffix;
    }

    public RotatorRegionPart(){
    }

    @Override
    public void draw(PartParams params){
        if(rotator.found()){
            Vec2 vec = Tmp.v2.set(x,y).rotate(unitrot);
            float t = Time.time / 60f;
            float rx = params.x, ry = params.y, rot = (t * moveRot) % rotation;

            Draw.xscl *= xScl;
            Draw.yscl *= yScl;
            Draw.z(layerOffset);
            Drawf.spinSprite(rotator, vec.x + rx, vec.y + ry, rot);
            if(outline){
                Draw.z(outlineLayerOffset);
                Draw.color(Pal.darkOutline);
                Draw.rect(outlineR, vec.x + rx, vec.y + ry, rot);
                Draw.reset();
                Draw.z(Draw.z());
            }

            Draw.reset();
        }
    }

    @Override
    public void load(String name){
        rotator = Core.atlas.find(suffix);
        if(outline){
            outlineR = Core.atlas.find(name + suffix + "-outline");
        }
    }
}