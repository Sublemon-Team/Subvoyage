package subvoyage.content.other;

import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import subvoyage.core.draw.SvPal;

import static arc.graphics.g2d.Draw.color;

public class SvEffects {
    public static StatusEffect buff, overpower, jammed;

    public static void load() {
        buff = new StatusEffect("buff") {{
            color = SvPal.argon;
            reloadMultiplier = 0.5f;
            effectChance = 0.07f;
            effect = Fx.overclocked;
        }};
        overpower = new StatusEffect("overpower") {{
            color = SvPal.argon;
            reloadMultiplier = 0.25f;
            effectChance = 0.15f;
            effect = Fx.overclocked;
        }};

        jammed = new StatusEffect("jammed") {{
            color = SvPal.clay;
            reloadMultiplier = 0.5f;

            damage = 0.1f;

            dragMultiplier = 1.5f;

            damageMultiplier = 0.5f;
            healthMultiplier = 0.5f;

            applyExtend = true;
            parentizeApplyEffect = true;
            parentizeEffect = true;

            effectChance = 0.025f;

            applyEffect = new Effect(40f, e -> {
                color(e.color);

                Unit u = (Unit) e.data;
                Lines.stroke(0.25f+e.foutpow()*1.75f);
                Lines.circle(u.x,u.y,e.fin()*u.hitSize);
            });

            effect = new Effect(40f, e -> {
                color(e.color);

                Unit u = (Unit) e.data;
                Lines.stroke(0.25f+e.foutpow()*1.75f);
                Lines.circle(u.x,u.y,e.fin()*u.hitSize);
            });
        }};
    }
}
