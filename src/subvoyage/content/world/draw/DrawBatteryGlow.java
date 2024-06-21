package subvoyage.content.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawBatteryGlow extends DrawBlock{
    public Color color = new Color(0.45f, 0.22f, 0.65f, 0.25f);
    public float pulse = 0.45f, scl = 2.5f;
    public float layer = Layer.blockAdditive;

    public TextureRegion glow;
    public String suffix = "-glow";

    public DrawBatteryGlow(float layer){
        this.layer = layer;
    }

    public DrawBatteryGlow(String suffix){
        this.suffix = suffix;
    }

    public DrawBatteryGlow(String suffix, Color color, float pulse, float scl){
        this.suffix = suffix;
        this.color = color;
        this.pulse = pulse;
        this.scl = scl;
    }

    public DrawBatteryGlow(){
    }

    @Override
    public void draw(Building build){
        Draw.z(Layer.blockAdditive);
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        Draw.blend(Blending.additive);
        Draw.color(color, Mathf.clamp(build.power().status) * (color.a * (build.power().status - pulse + Mathf.absin(scl, pulse))));
        Draw.rect(glow, build.x, build.y);
        Draw.blend();
        Draw.color();
        Draw.z(z);
    }

    @Override
    public void load(Block block){
        glow = Core.atlas.find(block.name + suffix);
    }
}