package subvoyage.content.world.blocks.offload_core;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import subvoyage.content.world.SvFx;
import subvoyage.content.world.blocks.offload_core.OffloadCore;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

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
            SvFx.decoderWave.create(x,y,0, Pal.accent, radius*tilesize);

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
