package subvoyage.type.block.crafter;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.production.AttributeCrafter;
import subvoyage.content.SvItems;
import subvoyage.core.draw.SvPal;
import subvoyage.core.ui.advancements.Advancement;

public class CircularCrusher extends AttributeCrafter{
    public float sinMag = 0f, sinScl = 10f, sideOffset = 0f, lenOffset = 5f, horiOffset = 0f, angleOffset = 0f;
    public TextureRegion sand, saw, sawLight, sawLightStill, bottom, saws, sawStill;

    public CircularCrusher(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        bottom = Core.atlas.find(this.name + "-bottom");
        sand = Core.atlas.find(this.name + "-sand");
        saw = Core.atlas.find(this.name + "-saw");
        sawLight = Core.atlas.find(this.name + "-saw-light", saw);
        sawLightStill = Core.atlas.find(this.name + "-saw-light-still", sawLight);
        saws = Core.atlas.find(this.name+"-saws");
        sawStill = Core.atlas.find(this.name+"-saw-still");
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[] {bottom, saws,region};
    }

    public class TugRollerBuild extends AttributeCrafterBuild{

        float curEf = 0f;
        float curLen2 = 0f;

        float sandPercentage = 0f;
        float crudePercentage = 0f;

        float visualSand = 0f;
        float visualCrude = 0f;

        @Override
        public void draw(){

            int crudeCount = items.get(SvItems.crude);
            int sandCount = items.get(SvItems.finesand);
            crudePercentage = (float) crudeCount / itemCapacity;
            sandPercentage = (float) sandCount / itemCapacity;

            crudePercentage /= Math.max(1,crudePercentage+sandPercentage);
            sandPercentage /= Math.max(1,crudePercentage+sandPercentage);

            visualCrude = Mathf.lerp(visualCrude,crudePercentage,Time.delta/10f);
            visualSand = Mathf.lerp(visualSand,sandPercentage,Time.delta/10f);

            Draw.rect(bottom, x, y);

            Draw.color(SvPal.finesand.cpy().lerp(SvPal.crude,visualCrude/(visualCrude+visualSand)),visualCrude+visualSand);
            Draw.rect(sand, x, y);
            Draw.color();

            for(int i = 0; i < 2; i++){
                float len = Mathf.absin(progress*90 + sideOffset * i, sinScl, 1) + lenOffset;
                float thisEf = curEf = Mathf.lerp(curEf,efficiency,Time.delta/10f);
                float angle = angleOffset + i * 360f / 2;
                //angle >= 135 && angle < 315 ? sawLight : saw;

                if(Mathf.equal(angle, 315)){
                    Draw.yscl = -1f;
                }

                Tmp.v1.trns(angle, lenOffset, -horiOffset);
                float rotation = ((Time.time*10*thisEf)%360);
                Draw.scl(1.03f-0.2f*(1f-thisEf));
                Drawf.spinSprite(efficiency < 0.3f ? sawStill : saw, x + Tmp.v1.x, y + Tmp.v1.y + Mathf.sinDeg(Time.time*2)*(i*2-1)*efficiency, rotation);
                Drawf.spinSprite(efficiency < 0.3f ? sawLightStill : sawLight, x + Tmp.v1.x, y + Tmp.v1.y + Mathf.sinDeg(Time.time*2)*(i*2-1)*efficiency, (Mathf.sinDeg(rotation)*10)/2f);
                Draw.yscl = 1f;
                Draw.reset();
            }

            drawer.draw(this);
        }
    }
}