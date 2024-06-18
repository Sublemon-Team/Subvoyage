package subvoyage.content.world.blocks;

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
import subvoyage.content.world.blocks.offload_core.OffloadCore;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class CoreDecoder extends Block {
    public int frequency = 120;
    public float radius = 10;
    public float hackChance = 0.01f;
    public int minAttempts = 40;
    public CoreDecoder(String name) {
        super(name);
        update = true;
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
            Draw.color(new Color(Color.white).lerp(Pal.accent,timePassed/ (float) frequency));
            Draw.rect(this.block.region, this.x, this.y, this.drawrot());
            drawTeamTop();
            Draw.reset();

            Drawf.dashCircle(x,y,radius*tilesize,Pal.accent);
            Draw.reset();
        }

        private void pulse() {
            Fx.shieldWave.create(x,y,0, Pal.accent, new Object());
            Fx.bigShockwave.create(x,y,0, Pal.accent, new Object());

            for(int xo = (int) -radius; xo < radius; xo++)
                for(int yo = (int) -radius; yo < radius; yo++) {
                    int x= tileX()+xo;
                    int y= tileY()+yo;
                    float dst = Mathf.dst(x,y,tileX(),tileY());
                    if(dst > radius) continue;
                    Tile tile = world.tile(x,y);
                    if(tile.build instanceof OffloadCore.OffloadCoreBuild c) {
                        if(c.completeType != 0x2) return;
                        boolean hack = rand.chance(hackChance*efficiency);
                        boolean protect = rand.chance(hackChance*2*efficiency);
                        //it can't be that fast, like imagine breaking every core from the first attempt,right?
                        if(attempts < minAttempts) {
                            hack = false;
                            protect = true;
                        }
                        if(hack && !protect) c.disableShield();
                        else Fx.absorb.create(c.x,c.y,0,Pal.accent,new Object());
                        attempts++;
                    }
                }
        }
    }
}
