package subvoyage.core.draw.part;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;
import subvoyage.Subvoyage;

public class SpinningBlurRegionPart extends DrawPart{
    public TextureRegion rotator, outlineR, blurR, lightR, top;
    public float layer = -1, outlineLayerOffset = -0.001f;
    public float x, y, xScl = 1f, yScl = 1f, rotationRad = 720f, rotationSpeed;
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
            float layer = params.warmup > 0.1f ? this.layer + 3 : Layer.legUnit + 3;
            Vec2 vec = Tmp.v2.set(x,y).rotate(params.rotation - 90);
            float t = Time.time / 60f;
            float accel = Mathf.clamp(params.warmup,0f,1f);
            float rx = params.x, ry = params.y, rot = (t * rotationSpeed * 4f) + (params.rotation-90);

            rot = Mathf.lerp(params.rotation-90f,rot,accel);

            Draw.xscl *= xScl;
            Draw.yscl *= yScl;
            Draw.z(layer + 1);

            Draw.xscl /= 1.5f;
            Draw.yscl /= 1.5f;
            Draw.rect(top, vec.x + rx, vec.y + ry, vec.angle());
            Draw.xscl *= 1.5f;
            Draw.yscl *= 1.5f;

            Draw.alpha(1f-accel);
            Draw.z(layer-5f);
            Draw.rect(outlineR, vec.x + rx, vec.y + ry, rot);
            Draw.z(layer);
            Draw.rect(rotator, vec.x + rx, vec.y + ry, rot);
            if(blur){
                Draw.z(layer - 1);
                Draw.alpha(accel);
                Draw.rect(blurR, vec.x + rx, vec.y + ry, rot);
                Draw.alpha(accel*.8f);
                Draw.rect(lightR, vec.x + rx, vec.y + ry, 0);
                Draw.z(Draw.z());
                Draw.alpha(1);
            }

            Vec2 mirrorVec = Tmp.v2.set(-x,y).rotate(params.rotation - 90);
            if(mirror) {
                Draw.z(layer + 1);

                Draw.xscl /= 1.5f;
                Draw.yscl /= 1.5f;
                Draw.rect(top, mirrorVec.x + rx, mirrorVec.y + ry, mirrorVec.angle());
                Draw.xscl *= 1.5f;
                Draw.yscl *= 1.5f;

                Draw.alpha(1f-accel);
                Draw.z(layer-5f);
                Draw.rect(outlineR, mirrorVec.x + rx, mirrorVec.y + ry, -rot + (params.rotation-90) * 2);
                Draw.z(layer);
                Draw.rect(rotator, mirrorVec.x + rx, mirrorVec.y + ry, -rot + (params.rotation-90) * 2);
                if(blur  && accel > 0.3f){
                    Draw.z(layer - 1);
                    Draw.alpha(accel);
                    Draw.rect(blurR, mirrorVec.x + rx, mirrorVec.y + ry, -rot + (params.rotation-90) * 2);
                    Draw.alpha(accel*.8f);
                    Draw.rect(lightR, vec.x + rx, vec.y + ry, 0);
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
            lightR = Core.atlas.find(suffix + "-blur-light");
        }

        outlineR = Core.atlas.find(suffix + "-outline");
    }

    @Override
    public void getOutlines(Seq<TextureRegion> out) {
        out.addAll(rotator, top);
    }
}