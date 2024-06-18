package subvoyage.content.world.blocks.offload_core;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import subvoyage.content.world.*;

import static mindustry.Vars.*;

public class CoreDecoder extends Block {
    public int frequency = 120;
    public float radius = 10;
    public float hackChance = 0.01f;
    public int minAttempts = 40;

    public TextureRegion heat;

    public CoreDecoder(String name) {
        super(name);
        update = true;
    }

    @Override
    public void load() {
        super.load();
        heat = Core.atlas.find(name+"-heat");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x*tilesize,y*tilesize,radius*tilesize,Pal.accent);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("progress", (CoreDecoderBuild e) ->
        new Bar(() -> Core.bundle.format("bar.loadprogress", Strings.fixed(e.timePassed / frequency, 2)), () -> Pal.ammo, () -> e.timePassed / frequency));
    }

    public class CoreDecoderBuild extends Building {
        public float timePassed = 0;
        public int attempts = 0;
        public Rand rand = new Rand();
        @Override
        public void updateTile() {
            super.updateTile();
            if(timePassed >= frequency) {
                timePassed %= frequency;
                pulse();
            }
            timePassed+=Time.delta*efficiency;
        }

        @Override
        public void draw() {
            Draw.rect(this.block.region, this.x, this.y, this.drawrot());
            Draw.reset();

            Draw.z(Layer.blockAdditive);
            Draw.blend(Blending.additive);
            Draw.color(Pal.redLight);
            Draw.alpha(timePassed/frequency);
            Draw.rect(heat,this.x,this.y,this.drawrot());
            Draw.blend();

            Draw.reset();

            drawTeamTop();
            Draw.reset();

        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Drawf.dashCircle(x,y,radius*tilesize,Pal.accent);
            Draw.reset();
        }

        private void pulse() {
            new MultiEffect(Fx.drillSteam, SvFx.decoderWave).create(x, y, 0, Pal.accent, radius * tilesize);
            if(attempts > minAttempts)
                for(int xo = (int) -radius; xo < radius; xo++)
                    for(int yo = (int) -radius; yo < radius; yo++) {
                        int x= tileX()+xo;
                        int y= tileY()+yo;
                        float dst = Mathf.dst(x,y,tileX(),tileY());
                        if(dst > radius) continue;
                        Tile tile = world.tile(x,y);
                        if(tile.build instanceof OffloadCore.OffloadCoreBuild c) {
                            if(c.completeType != 2) {
                                continue;
                            }
                            SvFx.point.create(this.x,this.y,0,Pal.redLight,new Object());
                            SvFx.point.create(c.x,c.y,0,Pal.redLight,new Object());
                            SvFx.beam.create(c.x,c.y,0,Pal.redLight,new float[] {this.x,this.y,c.x,c.y});
                            boolean hack = rand.chance(hackChance*efficiency);
                            boolean protect = rand.chance(hackChance*2*efficiency);
                            //it can't be that fast, like imagine breaking every core from the first attempt,right?
                            if(hack && !protect) c.disableShield();
                            else{
                                Fx.absorb.create(c.x,c.y,0,Pal.accent,new Object());
                            }
                        }
                    }
            attempts++;
        }
    }
}
