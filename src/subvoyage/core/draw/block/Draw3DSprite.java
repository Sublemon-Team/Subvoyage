package subvoyage.core.draw.block;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import subvoyage.core.draw.SvDraw;

// author: MEEPofFaith
// link: https://github.com/MEEPofFaith/tantros-but-java
public class Draw3DSprite extends DrawBlock {
    public TextureRegion region;
    public String suffix = "";
    public float camOffset = 3f;
    public float driftScl = 30, driftMag = 2;
    public float animScl = 30, animMag = 2;
    public float surfaceTime = 0.95f;
    public float layerFrom = Layer.light + 0.25f;
    public float layerTo = Layer.light + 2.1f;
    public boolean enableZOffset = true;

    public boolean anim = false;

    public Draw3DSprite(String suffix) {
        this.suffix = suffix;
    }

    public Draw3DSprite() {
        // nothing
    }

    @Override
    public void draw(Building build) {
        float z = Draw.z();
        Draw.z(layer(build));

        float x = x(build), y = y(build), off = off(build);
        Draw.z(Draw.z() + SvDraw._3D.layerOffset(x, y) + (!enableZOffset ? 0f : Mathf.randomSeed((long) (build.x*build.y+build.x+build.y))*0.2f));

        Draw.scl(SvDraw._3D.hScale(off));
        Draw.alpha(SvDraw._3D.heightFade(off));
        Draw.rect(region, SvDraw._3D.xHeight(x, off), SvDraw._3D.yHeight(y, off));
        Draw.scl();
        Draw.z(z);
    }

    public float layer(Building build) {
        return build.warmup() > surfaceTime ? layerTo : layerFrom;
    }

    public float x(Building build) {
        return build.x + Mathf.cos(Time.time, driftScl, driftMag);
    }

    public float y(Building build) {
        return build.y + Mathf.sin(Time.time, driftScl*1.05f, driftMag);
    }

    public float off(Building build) {
        return anim ? Math.abs(Mathf.cos(Time.time, animScl, animMag)) * camOffset : camOffset;
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy());
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{region};
    }

    @Override
    public void load(Block block) {
        region = Core.atlas.find(block.name + suffix);
    }
}
