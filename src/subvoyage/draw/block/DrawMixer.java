package subvoyage.draw.block;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.draw.*;
import subvoyage.content.other.SvPal;

public class DrawMixer extends DrawLiquidRegion{
    public Color metaColor = SvPal.polygen;
    public int bubbles = 14;
    public float strokeMin = 0.25F;
    public float spread = 4.0F;
    public float timeScl = 160.0F;
    public float recurrence = 6F;
    public float radius = 2F;

    public void draw(Building build){
        float alpha = build.liquids.get(Liquids.water) / build.block.liquidCapacity;
        if(alpha > 0){
            Drawf.liquid(liquid, build.x, build.y - 1.25f, alpha / 2, Liquids.water.color, Mathf.sin(60, (float) 0. + rand.range(this.spread)));
            Draw.color(this.metaColor, alpha / 2);
            rand.setSeed(build.pos());
            for(int i = 0; i < this.bubbles; ++i){
                float x = rand.range(this.spread);
                float y = rand.range(this.spread) - 1.25f;
                float life = 1.0F - (Time.time / this.timeScl + rand.random(this.recurrence)) % this.recurrence;
                if(life > 0.0F){
                    Lines.stroke(life + 0.4f * (life + this.strokeMin));
                    Lines.poly(build.x + x, build.y + y, 6, (1f - life) * this.radius);
                }
            }

            Draw.color();
        }
    }
}
