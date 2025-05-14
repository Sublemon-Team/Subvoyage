package subvoyage.type.block.defense;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import subvoyage.content.other.SvEffects;
import subvoyage.core.anno.LoadAnnoProcessor;
import subvoyage.core.draw.SvRender;
import subvoyage.type.unit.entity.HelicopterUnitEntity;
import subvoyage.util.Var;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.Vars.tilesize;

public class UnitJammer extends Block {


    public float range = 13*8f;

    public int timer = timers++;

    public Effect pulseEffect = new Effect(22, e -> {
        color(e.color, 0.7f);
        stroke(e.fout() * 2f);
        Lines.circle(e.x, e.y, 4f + e.finpow() * e.rotation);
    });

    public @LoadAnnoProcessor.LoadAnno("@-plate") TextureRegion plate;

    public UnitJammer(String name) {
        super(name);

        destructible = true;
        update = true;
        solid = true;
    }

    @Override
    public void init() {
        super.init();
        clipSize += range * 2f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Draw.color(Pal.accent);
        Lines.dashCircle(x*tilesize, y*tilesize, range);
        Draw.color();
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{region, Core.atlas.find(name+"-plate")};
    }

    public class UnitJammerBuild extends Building {
        public float pulse = 0f;
        public float smoothEfficiency = 0f;
        @Override
        public void updateTile() {
            super.updateTile();
            pulse = Mathf.lerpDelta(pulse,0f,1/70f);
            if(timer(UnitJammer.this.timer,90f)) {
                pulseEffect.lifetime = range()*2f;
                pulseEffect.at(x,y,range(),team.color);
                pulse = smoothEfficiency;

                Var<Boolean> jammed = new Var<>(false);

                Groups.unit.each(e -> {
                    if(e instanceof HelicopterUnitEntity heli && heli.dst(this) < range()) {
                        heli.apply(SvEffects.jammed, 95f*Mathf.clamp(smoothEfficiency*1.25f));
                        jammed.val = true;
                    }
                });

                if(jammed.val) consume();
            }

            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency,efficiency,1/45f);
        }


        @Override
        public void draw() {
            super.draw();

            Draw.xscl *= 1f + pulse * 0.35f;
            Draw.yscl *= 1f + pulse * 0.35f;
            Draw.rect(plate,x,y,drawrot());
            Draw.xscl /= 1f + pulse * 0.35f;
            Draw.yscl /= 1f + pulse * 0.35f;

            if(smoothEfficiency <= 0.01f) return;

            Draw.color(team.color,0.25f);

            Draw.z(SvRender.Layer.effectGround);
            Fill.circle(x,y,range());

            Draw.color(team.color.cpy().mul(1.25f));

            Draw.z(SvRender.Layer.powerBubbles);
            Lines.stroke(1f);
            Lines.circle(x,y,range());

            Draw.z(SvRender.Layer.block);
        }

        public float range() {
            return range * smoothEfficiency;
        }
    }
}
