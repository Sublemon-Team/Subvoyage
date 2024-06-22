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
    public TextureRegion rotator, outlineR, blurR, top;
    public float layer = -1, outlineLayerOffset = -0.001f;
    public float x, y, xScl = 1f, yScl = 1f, rotationRad = 720f, rotationSpeed;
    public float unitX, unitY, unitRot;
    public boolean mirror = false;

    public @Nullable Color color;
    public String suffix = SubvoyageMod.ID + "-base-rotator";
    public boolean outline = false;
    public boolean blur = true;

    public RotatorRegionPart(String suffix){
        this.suffix = suffix;
    }

    public RotatorRegionPart(){
    }

    @Override
    public void draw(PartParams params){
        if(rotator.found()){
            Vec2 vec = Tmp.v2.set(x,y).rotate(unitRot - 90);
            float t = Time.time / 60f;
            float rx = params.x, ry = params.y, rot = (t * rotationSpeed);

            Draw.xscl *= xScl;
            Draw.yscl *= yScl;
            Draw.z(layer + 1);
            Draw.rect(top, vec.x + rx, vec.y + ry, vec.angle());
            Draw.z(layer);
            Drawf.spinSprite(rotator, vec.x + rx, vec.y + ry, rot);
            if(blur){
                Draw.z(layer - 1);
                Draw.alpha(1f);
                Drawf.spinSprite(blurR, vec.x + rx, vec.y + ry, rot);
                Draw.z(Draw.z());
                Draw.alpha(1);
            }

            if(outline){
                Draw.z(outlineLayerOffset);
                Draw.color(Pal.darkOutline);
                Draw.rect(outlineR, vec.x + rx, vec.y + ry, rot);
                Draw.z(Draw.z());
            }

            Vec2 mirrorVec = Tmp.v2.set(x,y).rotate(unitRot - 90).inv();
            if(mirror) {
                Draw.z(layer + 1);
                Draw.rect(top, mirrorVec.x + rx, mirrorVec.y + ry, mirrorVec.angle());
                Draw.z(layer);
                Drawf.spinSprite(rotator, mirrorVec.x + rx, mirrorVec.y + ry, -rot);
                if(blur){
                    Draw.z(layer - 1);
                    Draw.alpha(1f);
                    Drawf.spinSprite(blurR, mirrorVec.x + rx, mirrorVec.y + ry, -rot);
                    Draw.z(Draw.z());
                    Draw.alpha(1);
                }

                if(outline){
                    Draw.z(outlineLayerOffset);
                    Draw.color(Pal.darkOutline);
                    Draw.rect(outlineR, mirrorVec.x + rx, mirrorVec.y + ry, -rot);
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