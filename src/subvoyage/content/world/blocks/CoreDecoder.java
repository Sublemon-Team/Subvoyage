package subvoyage.content.world.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class CoreDecoder extends Block {
    public int frequency = 120;
    public float radius = 10;
    public CoreDecoder(String name) {
        super(name);
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
            timePassed+=Time.delta;
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
            Fx.overdriveWave.create(x,y,0, Pal.accent, new Object());
        }
    }
}
