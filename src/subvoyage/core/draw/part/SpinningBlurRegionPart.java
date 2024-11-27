package subvoyage.core.draw.part;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;
import subvoyage.Subvoyage;

public class SpinningBlurRegionPart extends DrawPart{
    public TextureRegion rotator, outlineR, blurR, top;
    public float layer = -1, outlineLayerOffset = -0.001f;
    public float x, y, xScl = 1f, yScl = 1f, rotationRad = 720f, rotationSpeed;
    public float unitX, unitY, unitRot;
    public boolean mirror = false;

    public @Nullable Color color;
    public String suffix = Subvoyage.ID + "-base-rotator";
    public boolean outline = false;
    public boolean blur = true;
    public boolean draw = true;

    public SpinningBlurRegionPart(String suffix){
        this.suffix = suffix;
    }

    public SpinningBlurRegionPart(){
    }

    @Override
    public void draw(PartParams params){
        if(!draw) return;
        if(rotator.found()){
            float layer = this.layer+ 3;
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

            }

            Draw.reset();
        }
    }

    @Override
    public void load(String name){
        rotator = Core.atlas.find(suffix);
        top = Core.atlas.find(suffix + "-top");

        if(blur) {
            blurR = Core.atlas.find(suffix + "-blur");
        }
    }
}