package subvoyage.entities.part;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;

public class RotatorRegionPart extends DrawPart{
    public TextureRegion rotator, outlineR;
    public float layerOffset = -1, outlineLayerOffset = -0.001f;
    public float x, y, xScl = 1f, yScl = 1f, rotation;
    public float moveRot;
    public @Nullable Color color;
    public String suffix = "-rotator";
    public boolean outline = true;

    public RotatorRegionPart(String suffix){
        this.suffix = suffix;
    }

    public RotatorRegionPart(){
    }

    @Override
    public void draw(PartParams params){
        if(rotator.found()){
            Tmp.v1.set(x, y).rotate(params.rotation);
            float t = Time.time / 60f;
            float rx = params.x + x, ry = params.y + y, rot = (t * moveRot) % rotation;

            Draw.xscl *= xScl;
            Draw.yscl *= yScl;
            Draw.z(layerOffset);
            Drawf.spinSprite(rotator, rx, ry, rot);
            if(outline){
                Draw.z(outlineLayerOffset);
                Draw.color(Pal.darkOutline);
                Draw.rect(outlineR, rx, ry, rot);
                Draw.reset();
                Draw.z(Draw.z());
            }

            Draw.reset();
        }
    }

    @Override
    public void load(String name){
        rotator = Core.atlas.find(name + suffix);
        if(outline){
            outlineR = Core.atlas.find(name + suffix + "-outline");
        }
    }
}