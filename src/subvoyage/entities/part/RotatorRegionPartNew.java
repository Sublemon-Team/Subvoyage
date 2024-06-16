package subvoyage.entities.part;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.graphics.*;
import subvoyage.*;
import subvoyage.content.unit.systematical.*;

public class RotatorRegionPartNew extends UnitDrawPart{
    public TextureRegion rotator, outlineR, blurR, top;
    public float layerOffset = -1, outlineLayerOffset = -0.001f;
    public float x, y, xScl = 1f, yScl = 1f, rotationRad, rotationSpeed;
    public boolean mirror = false;

    public @Nullable Color color;
    public String suffix = SubvoyageMod.ID + "-base-rotator";
    public boolean outline = false;
    public boolean blur = true;

    public RotatorRegionPartNew(String suffix){
        this.suffix = suffix;
    }

    public RotatorRegionPartNew(){
    }

    @Override
    public void draw(BaseUnit unit, PartParams params){
        if(rotator.found()){
            Vec2 vec = Tmp.v2.set(x,y).rotate(unit.rotation() - 90);
            float t = Time.time / 60f;
            float rx = unit.x(), ry = unit.y(), rot = (t * rotationSpeed) % rotationRad;

            Draw.xscl *= xScl;
            Draw.yscl *= yScl;
            Draw.z(layerOffset+1);
            Draw.rect(top, rx+vec.x, ry+vec.y, unit.rotation()-90);
            Draw.z(layerOffset);
            Drawf.spinSprite(rotator, rx+vec.x, ry+vec.y, rot);
            if(blur){
                Draw.z(layerOffset - 1);
                Draw.alpha(0.75f);
                Drawf.spinSprite(blurR, rx, ry, rot);
                Draw.z(Draw.z());
                Draw.alpha(1);
            }

            if(outline){
                Draw.z(outlineLayerOffset);
                Draw.color(Pal.darkOutline);
                Draw.rect(outlineR, rx, ry, rot);
                Draw.z(Draw.z());
            }

            if(mirror) {
                Draw.z(layerOffset);
                Draw.rect(top, rx-vec.x, ry-vec.y, unit.rotation());
                Drawf.spinSprite(rotator, rx-vec.x, ry-vec.y, -rot);
                if(blur){
                    Draw.z(layerOffset - 1);
                    Draw.alpha(0.75f);
                    Drawf.spinSprite(blurR, rx-vec.x, ry-vec.y, -rot);
                    Draw.z(Draw.z());
                    Draw.alpha(1);
                }

                if(outline){
                    Draw.z(outlineLayerOffset);
                    Draw.color(Pal.darkOutline);
                    Draw.rect(outlineR, rx-vec.x, ry-vec.y, -rot);
                    Draw.z(Draw.z());
                }
            }

            Draw.reset();
        }
    }

    @Override
    public void load(String name){
        rotator = Core.atlas.find(suffix);
        top = Core.atlas.find(suffix + "-top");
        if(outline){
            outlineR = Core.atlas.find(suffix + "-outline");
        }

        if(blur) {
            blurR = Core.atlas.find(suffix + "-blur");
        }
    }
}