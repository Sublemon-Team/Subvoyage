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
    public TextureRegion rotator, outlineR, blurR;
    public float layerOffset = -1, outlineLayerOffset = -0.001f;
    public float x, y, xScl = 1f, yScl = 1f, rotation, unitrot;
    public float moveRot;
    public float unitX;
    public float unitY;
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
            Vec2 vec = Tmp.v2.set(x,y).rotate(unitrot - 90);
            float t = Time.time / 60f;
            float rx = params.x, ry = params.y, rot = (t * moveRot) % rotation;

            Draw.xscl *= xScl;
            Draw.yscl *= yScl;
            Draw.z(layerOffset);
            Drawf.spinSprite(rotator, vec.x + rx, vec.y + ry, rot);
            if(blur){
                Draw.z(layerOffset - 1);
                Draw.alpha(0.75f);
                Drawf.spinSprite(blurR, vec.x + rx, vec.y + ry, rot);
                Draw.z(Draw.z());
            }

            if(outline){
                Draw.z(outlineLayerOffset);
                Draw.color(Pal.darkOutline);
                Draw.rect(outlineR, vec.x + rx, vec.y + ry, rot);
                Draw.z(Draw.z());
            }

            Vec2 mirrorVec = Tmp.v2.set(x,y).rotate(unitrot - 90).inv();
            if(mirror) {
                Draw.z(layerOffset);
                Drawf.spinSprite(rotator, mirrorVec.x + rx, mirrorVec.y + ry, -rot);
                if(blur){
                    Draw.z(layerOffset - 1);
                    Draw.alpha(0.75f);
                    Drawf.spinSprite(blurR, mirrorVec.x + rx, mirrorVec.y + ry, -rot);
                    Draw.z(Draw.z());
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
        if(outline){
            outlineR = Core.atlas.find(suffix + "-outline");
        }

        if(blur) {
            blurR = Core.atlas.find(suffix + "-blur");
        }
    }
}