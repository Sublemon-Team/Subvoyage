package oceanic_dust.blocks.content.world;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawMixer extends DrawBlock{
    public Color metaColor = Color.valueOf("6a6fc2");
    public Color metaColorLight = Color.valueOf("a3a8e6");
    public int bubbles = 14;
    public int sides = 8;
    public float strokeMin = 0.25F;
    public float spread = 4.0F;
    public float timeScl = 160.0F;
    public float recurrence = 6F;
    public float radius = 3F;
    public TextureRegion middle;

    public void draw(Building build) {
        Drawf.liquid(this.middle, build.x, build.y, build.warmup(), this.metaColor);
        Draw.color(this.metaColor, this.metaColorLight, build.warmup());
        rand.setSeed(build.pos());
        for(int i = 0; i < this.bubbles; ++i) {
            float x = rand.range(this.spread);
            float y = rand.range(this.spread);
            float life = 1.0F - (Time.time / this.timeScl + rand.random(this.recurrence)) % this.recurrence;
            if (life > 0.0F) {
                Lines.stroke(build.warmup() * (life + this.strokeMin));
                Lines.poly(build.x + x, build.y + y, this.sides, (1.0F - life) * this.radius);
            }
        }

        Draw.color();
    }

    public void load(Block block) {
        this.middle = Core.atlas.find(block.name + "-middle");
    }
}
