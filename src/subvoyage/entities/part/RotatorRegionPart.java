package subvoyage.entities.part;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;

// TODO: Fix rotations when moving, and maybe do a custom unit type to depend the rotations on unit speed?
public class RotatorRegionPart extends DrawPart{
    public TextureRegion rotator, outlineR;
    public float layerOffset = -1, outlineLayerOffset = -0.001f;
    public float x, y, xScl = 1f, yScl = 1f, rotation;
    public float growX, growY, moveRot;
    public @Nullable Color color;
    public String suffix = "-rotator";
    public PartProgress growProgress = PartProgress.warmup;
    public boolean outline = true;

    public RotatorRegionPart(String suffix){
        this.suffix = suffix;
    }

    public RotatorRegionPart(){
    }

    @Override
    public void draw(PartParams params){
        Tmp.v1.set(x, y);
        float t = Time.time / 60f;
        float sclProg = growProgress.getClamp(params);
        float gx = growX * sclProg, gy = growY * sclProg;
        float rx = params.x + x, ry = params.y + y, rot = (t * moveRot) % rotation;



        Draw.xscl *= xScl + gx;
        Draw.yscl *= yScl + gy;
        Draw.z(Draw.z() + layerOffset);
        Drawf.spinSprite(rotator, rx, ry, rot);
        if(outline){
            Draw.z(Draw.z() + outlineLayerOffset);
            Draw.color(Pal.darkOutline);
            Draw.rect(outlineR, rx, ry, rot);
            Draw.reset();
            Draw.z(Draw.z());
        }

        Draw.reset();
    }

    @Override
    public void load(String name){
        rotator = Core.atlas.find(name + suffix);
        if(outline){
            outlineR = Core.atlas.find(name + suffix + "-outline");
        }
    }
}