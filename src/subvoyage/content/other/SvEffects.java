package subvoyage.content.other;

import mindustry.content.Fx;
import mindustry.type.StatusEffect;
import subvoyage.core.draw.SvPal;

public class SvEffects {
    public static StatusEffect buff, overpower;

    public static void load() {
        buff = new StatusEffect("buff") {{
            color = SvPal.argon;
            reloadMultiplier = 0.5f;
            effectChance = 0.07f;
            effect = Fx.overclocked;
        }};
        overpower = new StatusEffect("buff") {{
            color = SvPal.argon;
            reloadMultiplier = 0.25f;
            effectChance = 0.15f;
            effect = Fx.overclocked;
        }};
    }
}
