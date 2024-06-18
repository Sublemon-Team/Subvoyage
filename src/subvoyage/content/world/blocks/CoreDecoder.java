package subvoyage.content.world.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.tilesize;

public class CoreDecoder extends Block {
    public int frequency = 120;
    public float radius = 10;
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
        }
    }
}
