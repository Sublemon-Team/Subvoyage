package subvoyage.draw.block;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import subvoyage.draw.visual.SvDraw;

// author: MEEPofFaith
// link: https://github.com/MEEPofFaith/tantros-but-java
public class Draw3DStem extends Draw3DSprite {
    public int segments = 24;
    public boolean enableZOffset = true;

    public Draw3DStem(String suffix) {
        super(suffix);
    }

    public Draw3DStem() {
        super();
    }

    {
        layerFrom = layerTo = Layer.light + 0.25f;
    }

    @Override
    public void draw(Building build) {
        float z = Draw.z();
        Draw.z(layer(build));

        float x = x(build), y = y(build), off = off(build);
        Draw.z(Draw.z() + SvDraw._3D.layerOffset(x, y));

        float hWidth = region.height * Draw.scl * region.scl() * 2, hScl = SvDraw._3D.hScale(off);
        float ex = SvDraw._3D.xHeight(x, off), ey = SvDraw._3D.yHeight(y, off);
        float xDiff = ex - build.x, yDiff = ey - build.y, sDiff = hScl - 1f;

        for (int i = 0; i < segments; i++) {
            float p1 = ((float)i / segments) * ((float)i / segments),
                    p2 = ((i + 1f) / segments) * ((i + 1f) / segments);
            float x1 = build.x + xDiff * p1, y1 = build.y + yDiff * p1,
                    x2 = build.x + xDiff * p2, y2 = build.y + yDiff * p2,
                    s1 = 1f + sDiff * p1, s2 = 1f + sDiff * p2;

            float len = Mathf.len(x2 - x1, y2 - y1);
            float diffX = (x2 - x1) / len * hWidth, diffY = (y2 - y1) / len * hWidth;

            float a1 = Tmp.c1.set(1f, 1f, 1f, SvDraw._3D.heightFade(off * p1)).toFloatBits(),
                    a2 = Tmp.c1.set(1f, 1f, 1f, SvDraw._3D.heightFade(off * p2)).toFloatBits();

            if(i == 0) a1 = Tmp.c1.set(1f, 1f, 1f, SvDraw._3D.heightFade(off * p1)*0f).toFloatBits();

            SvDraw.quad(
                    region,
                    x1 - diffY * s1,
                    y1 + diffX * s1,
                    a1,

                    x1 + diffY * s1,
                    y1 - diffX * s1,
                    a1,

                    x2 + diffY * s2,
                    y2 - diffX * s2,
                    a2,

                    x2 - diffY * s2,
                    y2 + diffX * s2,
                    a2
            );
        }

        Draw.z(z);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        // nothing
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{};
    }
}
