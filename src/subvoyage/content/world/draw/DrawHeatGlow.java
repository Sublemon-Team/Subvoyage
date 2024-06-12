package subvoyage.content.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawHeatGlow extends DrawBlock{
    public Color color = new Color(1f, 0.22f, 0.22f, 0.8f);
    public float pulse = 0.3f, pulseScl = 10f;
    public float layer = Layer.blockAdditive;

    public TextureRegion heat;
    public String suffix = "-heat";

    public DrawHeatGlow(float layer){
        this.layer = layer;
    }

    public DrawHeatGlow(String suffix){
        this.suffix = suffix;
    }

    public DrawHeatGlow(){
    }

    @Override
    public void draw(Building build){
        Draw.z(Layer.blockAdditive);
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        Draw.blend(Blending.additive);
        Draw.color(color, Mathf.clamp(build.warmup()) * (color.a * (1f - pulse + Mathf.absin(pulseScl, pulse))));
        Draw.rect(heat, build.x, build.y);
        Draw.blend();
        Draw.color();
        Draw.z(z);
    }

    @Override
    public void load(Block block){
        heat = Core.atlas.find(block.name + suffix);
    }
}
