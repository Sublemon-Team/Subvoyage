package oceanic_dust.entities.part;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.draw.DrawTurret;

public class ODRegionPart extends RegionPart{
    public @Nullable Liquid liquidDraw;
    public TextureRegion liquid;
    public DrawTurret drawer;
    public float liquidAlpha = 1f;

    public ODRegionPart(DrawTurret drawer, String region){
        this.suffix = region;
        this.drawer = drawer;
    }

    @Override
    public void draw(PartParams params){
        super.draw(params);
        float prog = progress.getClamp(params), sclProg = growProgress.getClamp(params);
        float mx = moveX * prog, my = moveY * prog, mr = moveRot * prog + rotation, gx = growX * sclProg, gy = growY * sclProg;

        int len = mirror && params.sideOverride == -1 ? 2 : 1;
        Draw.xscl *= xScl + gx;
        Draw.yscl *= yScl + gy;
        for(int s = 0; s < len; s++){
            int i = params.sideOverride == -1 ? s : params.sideOverride;
            float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
            Tmp.v1.set((x + mx) * sign, y + my).rotateRadExact((params.rotation - 90) * Mathf.degRad);

            float rx = params.x + Tmp.v1.x, ry = params.y + Tmp.v1.y,
            rot = mr * sign + params.rotation - 90;

            Draw.xscl *= sign;
            if(liquid.found()){
                Liquid toDraw = liquidDraw;
                if(toDraw == null) return;
                Drawf.liquid(liquid, rx, ry, liquidAlpha, toDraw.color.write(Tmp.c1).a(1f), rot);
            }

            Draw.xscl *= sign;
        }
    }

    @Override
    public void load(String name){
        super.load(name);
        String realName = this.name == null ? name + suffix : this.name;
        liquid = Core.atlas.find(realName + "-liquid");
    }
}
