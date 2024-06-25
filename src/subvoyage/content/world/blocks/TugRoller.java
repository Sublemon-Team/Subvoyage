package subvoyage.content.world.blocks;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;

public class TugRoller extends AttributeCrafter{
    public float sinMag = 4f, sinScl = 10f, sideOffset = 0f, lenOffset = -1f, horiOffset = 0f, angleOffset = 0f;
    public TextureRegion region1, region2, bottom;

    public TugRoller(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        bottom = Core.atlas.find(this.name + "-bottom");
        region1 = Core.atlas.find(this.name + "-piston" + "0", this.name + "-piston");
        region2 = Core.atlas.find(this.name + "-piston" + "1", this.name + "-piston");
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
            float d = Mathf.lerp(0, 4, Mathf.sqrt(Mathf.random()));
            Item item;

            void draw(){
                if(item != null){
                    Draw.rect(item.fullIcon, x + Mathf.cos(r) * d, y + Mathf.sin(r) * d, Vars.itemSize, Vars.itemSize);
                }
            }
        }

        // TODO: Render only two items (Chromium, Sulfur) and after a spark the output
        // TODO: Sync the movements of pistons to a craft progress
        // я усталь не дают сделать ниче гады свет крадут фу (делалось на примере проджект юнити)
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
            }
        }

        @Override
        public void draw(){
            Draw.rect(bottom, x, y);
            for(var particle : particles){
                particle.draw();
            }

            for(int i = 0; i < 2; i++){
                float len = Mathf.absin(this.totalProgress() + sideOffset * i, sinScl, sinMag) + lenOffset;
                float angle = angleOffset + i * 360f / 2;
                TextureRegion reg =
                angle >= 135 && angle < 315 ? region2 : region1;

                if(Mathf.equal(angle, 315)){
                    Draw.yscl = -1f;
                }

                Tmp.v1.trns(angle, len, -horiOffset);
                Draw.rect(reg, x + Tmp.v1.x, y + Tmp.v1.y, angle);

                Draw.yscl = 1f;
            }

            drawer.draw(this);
        }
    }
}