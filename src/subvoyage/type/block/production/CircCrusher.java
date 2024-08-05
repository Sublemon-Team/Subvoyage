package subvoyage.type.block.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.graphics.Drawf;
import mindustry.type.Item;
import mindustry.world.blocks.production.AttributeCrafter;
import subvoyage.content.SvItems;

public class CircCrusher extends AttributeCrafter{
    public float sinMag = 0f, sinScl = 10f, sideOffset = 0f, lenOffset = 5.8f, horiOffset = 0f, angleOffset = 0f;
    public TextureRegion saw, sawLight, bottom, saws, sawStill;

    public CircCrusher(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        bottom = Core.atlas.find(this.name + "-bottom");
        saw = Core.atlas.find(this.name + "-saw");
        sawLight = Core.atlas.find(this.name + "-saw-light", saw);
        saws = Core.atlas.find(this.name+"-saws");
        sawStill = Core.atlas.find(this.name+"-saw-still");
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[] {bottom, saws,region};
    }

    public class TugRollerBuild extends AttributeCrafterBuild{
        Seq<ItemParticle> particles = new Seq<>();

        @Override
        public void created(){
            super.created();
            particles = new Seq<>();
            for(int i = 0; i <= 25; i++){
                particles.add(new ItemParticle());
            }
        }

        public class ItemParticle{
            float r = Mathf.random(Mathf.PI2);
            float d = Mathf.lerp(0, 6, Mathf.sqrt(Mathf.random()));
            float aD = 0f;
            Item item;

            void draw(){
                if(item != null){
                    Draw.rect(item.fullIcon,
                            x + Mathf.cos(r) * (d) + Mathf.sinDeg(Time.time*4+r*Mathf.radiansToDegrees)*curEf*2,
                            y + Mathf.sin(r) * d + Mathf.cosDeg(Time.time*4.3f+r*Mathf.radiansToDegrees)*curEf*2,
                            Vars.itemSize, Vars.itemSize);
                }
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(!Vars.headless){
                int total = items.total();
                if(total <= particles.size){
                    int[] index = {0};
                    items.each((item, a) -> {
                        for(int i = 0; i < a; i++){
                            particles.get(index[0]).item = item;
                            index[0]++;
                        }
                    });
                    for(int i = 0; i < particles.size; i++){
                        if(i > total){
                            particles.get(i).item = null;
                        }
                    }
                }else{
                    float ratio = particles.size / (float)total;
                    int lratio = Mathf.floor(1f / ratio);
                    float[] index = {0};
                    items.each((item, a) -> {
                        for(int i = 0; i < a; i += lratio){
                            particles.get(Mathf.floor(index[0])).item = item;
                            index[0] += ratio * lratio;
                            index[0] = Math.min(index[0], particles.size - 1);
                        }
                    });
                }
                for(int i = 0; i < particles.size; i++){
                    if(particles.get(i).item != SvItems.fineSand && particles.get(i).item != SvItems.crude)
                        particles.get(i).item = null;
                    particles.get(i).aD = curEf /sinMag-lenOffset/sinMag;
                }
            }
        }

        float curEf = 0f;
        float curLen2 = 0f;

        @Override
        public void draw(){
            Draw.rect(bottom, x, y);
            for(var particle : particles){
                particle.draw();
            }

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
                Drawf.spinSprite(sawLight, x + Tmp.v1.x, y + Tmp.v1.y + Mathf.sinDeg(Time.time*2)*(i*2-1)*efficiency, (Mathf.sinDeg(rotation)*10+10)/2f);
                Draw.yscl = 1f;
                Draw.reset();
            }

            drawer.draw(this);
        }
    }
}