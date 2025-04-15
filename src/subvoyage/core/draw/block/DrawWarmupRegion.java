package subvoyage.core.draw.block;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

import java.util.HashMap;

public class DrawWarmupRegion extends DrawBlock{
    public TextureRegion region;
    public String suffix = "";
    public boolean spinSprite = false;
    public boolean drawPlan = true;
    public boolean buildingRotate = false;
    public float x, y, rotation;
    public float layer = -1;
    public HashMap<Building,Float> smoothEfficiencies = new HashMap<>();

    public DrawWarmupRegion(String suffix){
        this.suffix = suffix;
    }

    public DrawWarmupRegion(String suffix, boolean spinSprite){
        this.suffix = suffix;
        this.spinSprite = spinSprite;
    }

    public DrawWarmupRegion(){
    }

    @Override
    public void draw(Building build){
        float z = Draw.z();
        float smoothEfficiency = smoothEfficiencies.getOrDefault(build,0f);
        smoothEfficiency = Mathf.lerp(smoothEfficiency,build.efficiency,Time.delta/40f);
        smoothEfficiencies.put(build,smoothEfficiency);
        if(layer > 0) Draw.z(layer);
        if(spinSprite){
            Drawf.spinSprite(region, build.x + x, build.y + y, build.totalProgress() * smoothEfficiency + rotation + (buildingRotate ? build.rotdeg() : 0));
        }else{
            Draw.rect(region, build.x + x, build.y + y, build.totalProgress() * smoothEfficiency + rotation + (buildingRotate ? build.rotdeg() : 0));
        }
        Draw.z(z);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(!drawPlan) return;
        Draw.rect(region, plan.drawx(), plan.drawy(), (buildingRotate ? plan.rotation * 90f : 0));
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{region};
    }

    @Override
    public void load(Block block){
        region = Core.atlas.find(block.name + suffix);
    }
}
