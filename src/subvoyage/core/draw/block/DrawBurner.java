package subvoyage.core.draw.block;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawBurner extends DrawBlock{
    public TextureRegion middle, magma;

    public void draw(Building build){
        Draw.color(Pal.lightPyraFlame, build.warmup());
        Draw.rect(middle, build.x, build.y, build.totalProgress() * 6);

        Draw.blend(Blending.additive);
        Draw.alpha((0.3F + Mathf.absin(Time.time, 2.0F + 2.0F, 0.3F + 0.05F)) * build.warmup());
        Draw.rect(magma, build.x, build.y, build.totalProgress() * 12);
        Draw.color();
        Draw.blend();
    }

    @Override
    public void load(Block block){
        middle = Core.atlas.find(block.name + "-middle");
        magma = Core.atlas.find(block.name + "-magma");
    }
}
