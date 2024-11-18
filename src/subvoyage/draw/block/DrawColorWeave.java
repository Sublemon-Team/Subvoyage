package subvoyage.draw.block;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import subvoyage.content.other.SvPal;

import java.util.HashMap;

public class DrawColorWeave extends DrawBlock{
    public TextureRegion weave;
    public Color color;
    public HashMap<Building,Float> degrees = new HashMap<>();

    public DrawColorWeave(Color color){
        this.color = color;
    }

    public void draw(Building build){
        Draw.rect(this.weave, build.x+Mathf.sinDeg(build.totalProgress()*8), build.y, build.totalProgress());
        Draw.color(color);
        Draw.alpha(build.warmup());
        Lines.stroke(2f);
        Lines.lineAngleCenter(build.x + Mathf.sin(build.totalProgress(), 6.0F, 2.6666667F * (float)build.block.size), build.y, 90.0F, (float)(build.block.size * 8) / 2.0F);
        Draw.reset();
    }

    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{this.weave};
    }

    public void load(Block block){
        this.weave = Core.atlas.find(block.name + "-weave");
    }
}
