package oceanic_dust.entities.part;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class ODRegionPart extends RegionPart{
    public @Nullable Liquid liquidDraw;
    public TextureRegion liquid;
    public float liquidAlpha = 1f;

    public ODRegionPart(String region){
        this.suffix = region;
    }

    @Override
    public void draw(PartParams params){
        super.draw(params);
        float prog = progress.getClamp(params), sclProg = growProgress.getClamp(params);
        float mr = moveRot * prog + rotation,
        gx = growX * sclProg, gy = growY * sclProg;

        int len = mirror && params.sideOverride == -1 ? 2 : 1;
        Draw.xscl *= xScl + gx;
        Draw.yscl *= yScl + gy;
        for(int s = 0; s < len; s++){
            int i = params.sideOverride == -1 ? s : params.sideOverride;
            float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
            float
            rx = params.x + Tmp.v1.x,
            ry = params.y + Tmp.v1.y,
            rot = mr * sign + params.rotation - 90;
            if(liquid.found()){
                Liquid toDraw = liquidDraw;
                Drawf.liquid(liquid, rx, ry, liquidAlpha, toDraw.color.write(Tmp.c1).a(1f), rot);
            }
        }
    }

    @Override
    public void load(String name){
        super.load(name);
        String realName = this.name == null ? name + suffix : this.name;
        liquid = Core.atlas.find(realName + "-liquid");
    }
}
