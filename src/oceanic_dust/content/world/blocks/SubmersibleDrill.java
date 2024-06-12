package oceanic_dust.content.world.blocks;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;

public class SubmersibleDrill extends Drill{
    public float shake = 1f;
    public Interp speedCurve = Interp.pow2In;
    public TextureRegion topRegion;
    public TextureRegion glowRegion;
    public Color glowColor = new Color(0.65f, 0.35f, 0.25f);
    public Sound drillSound = Sounds.drillImpact;
    public float drillSoundVolume = 0.6f, drillSoundPitchRand = 0.1f;

    public SubmersibleDrill(String name){
        super(name);
        hardnessDrillMultiplier = 0f;
        liquidBoostIntensity = 1f;
        drillEffectRnd = 0f;
        drillEffect = Fx.shockwave;
        ambientSoundVolume = 0.18f;
        ambientSound = Sounds.drillCharge;
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(this.name + "-top");
        glowRegion = Core.atlas.find(this.name + "-glow");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    @Override
    public float getDrillTime(Item item){
        return drillTime / drillMultipliers.get(item, 1f);
    }

    public class SubmersibleDrillBuild extends DrillBuild{
        //used so the lights don't fade out immediately
        public float smoothProgress = 0f;
        @Override
        public void updateTile(){
            if(dominantItem == null){
                return;
            }

            if(timer(timerDump, dumpTime)){
                dump(items.has(dominantItem) ? dominantItem : null);
            }

            float drillTime = getDrillTime(dominantItem);

            smoothProgress = Mathf.lerpDelta(smoothProgress, progress / (drillTime - 20f), 0.1f);

            if(items.total() <= itemCapacity - dominantItems && dominantItems > 0 && efficiency > 0){
                warmup = Mathf.approachDelta(warmup, progress / drillTime, 0.01f);

                float speed = efficiency;
                timeDrilled += speedCurve.apply(progress / drillTime) * speed;
                lastDrillSpeed = 1f / drillTime * speed * dominantItems;
                progress += delta() * speed;
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, 0.01f);
                lastDrillSpeed = 0f;
                return;
            }

            if(dominantItems > 0 && progress >= drillTime && items.total() < itemCapacity){
                for(int i = 0; i < dominantItems; i++){
                    offload(dominantItem);
                }

                progress %= drillTime;
                if(wasVisible){
                    Effect.shake(shake, shake, this);
                    drillSound.at(x, y, 1f + Mathf.range(drillSoundPitchRand), drillSoundVolume);
                    drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
                }
            }
        }

        @Override
        public float ambientVolume(){
            return super.ambientVolume() * Mathf.pow(progress(), 4f);
        }

        @Override
        public boolean shouldConsume(){
            return items.total() <= itemCapacity - dominantItems && enabled;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            drawDefaultCracks();
            Drawf.spinSprite(topRegion, x, y, timeDrilled * rotateSpeed);
            if(dominantItem != null && drawMineItem){
                Draw.color(dominantItem.color);
                Drawf.spinSprite(itemRegion, x, y, timeDrilled * rotateSpeed);
                Draw.color();
            }

            if(glowRegion.found()){
                Draw.z(Layer.blockAdditive);
                float z = Draw.z();
                Draw.blend(Blending.additive);
                Draw.color(glowColor, smoothProgress - 0.25f);
                Draw.rect(glowRegion, x, y);
                Draw.blend();
                Draw.color();
                Draw.z(z);
            }
        }
    }
}
