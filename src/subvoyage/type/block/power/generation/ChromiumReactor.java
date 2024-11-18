package subvoyage.type.block.power.generation;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;
import subvoyage.content.other.SvPal;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.tilesize;
import static subvoyage.content.SvItems.chrome;

public class ChromiumReactor extends NuclearReactor{
    public static final Rand rand = new Rand();
    public Color pal1 = SvPal.chromiumLightish;
    public Color pal2 = SvPal.chromiumMid;

    public ChromiumReactor(String name){
        super(name);
        itemCapacity = 30;
        liquidCapacity = 30;
        hasItems = true;
        hasLiquids = true;
        rebuildable = false;
        flags = EnumSet.of(BlockFlag.reactor, BlockFlag.generator);
        schematicPriority = -5;
        envEnabled = Env.any;

        itemDuration = 320;
        heating = 0.02f;
        coolantPower = 0.25f;
        fuelItem = chrome;

        explosionShake = 6f;
        explosionShakeDuration = 16f;

        explosionRadius = 16;
        explosionDamage = 1250 * 4;
        lightColor = SvPal.toxicExplosion;
        explodeEffect = new Effect(30, 500f, b -> {
            float intensity = 6.8f;
            float baseLifetime = 25f + intensity * 11f;
            b.lifetime = 50f + intensity * 65f;

            color(pal2);
            alpha(0.7f);
            for(int i = 0; i < 4; i++){
                rand.setSeed(b.id * 2L + i);
                float lenScl = rand.random(0.4f, 1f);
                int fi = i;
                b.scaled(b.lifetime * lenScl, e -> {
                    randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2.9f * intensity), 22f * intensity, (x, y, in, out) -> {
                        float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                        float rad = fout * ((2f + intensity) * 2.35f);

                        Fill.circle(e.x + x, e.y + y, rad);
                        Drawf.light(e.x + x, e.y + y, rad * 2.5f, pal1, 0.5f);
                    });
                });
            }

            b.scaled(baseLifetime, e -> {
                Draw.color();
                e.scaled(5 + intensity * 2f, i -> {
                    stroke((3.1f + intensity / 5f) * i.fout());
                    Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
                    Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
                });

                color(Pal.lighterOrange, pal1, e.fin());
                stroke((2f * e.fout()));

                Draw.z(Layer.effect + 0.001f);
                randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8 * intensity), 28f * intensity, (x, y, in, out) -> {
                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                    Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
                });
            });
        });

        explodeSound = Sounds.explosionbig;
    }

    public class ChromiumReactorBuild extends NuclearReactorBuild{
        @Override
        public void updateTile(){
            int fuel = items.get(fuelItem);
            float fullness = (float)fuel / itemCapacity;
            productionEfficiency = fullness;

            if(fuel > 0 && enabled){
                heat += fullness * heating * Math.min(delta(), 4f);

                if(timer(timerFuel, itemDuration / timeScale)){
                    consume();
                }

                if(Mathf.chance(0.5f / 20.0 * delta())){
                    Fx.surgeCruciSmoke.at(x, y);
                }

                if(Mathf.chance(0.25f / 20.0 * delta())){
                    Fx.neoplasiaSmoke.at(x, y);
                }
            }else{
                productionEfficiency = 0f;
            }

            if(heat > 0){
                float maxUsed = Math.min(liquids.currentAmount(), heat / coolantPower);
                heat -= maxUsed * coolantPower;
                liquids.remove(liquids.current(), maxUsed);
            }

            heat = Mathf.clamp(heat);
            if(heat >= 0.999f){
                Events.fire(Trigger.thoriumReactorOverheat);
                kill();
            }
        }

        @Override
        public void draw(){
            super.draw();

            Draw.color(coolColor, hotColor, heat);
            Fill.rect(x, y, size * tilesize, size * tilesize);
            Drawf.liquid(topRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
            if(heat > flashThreshold){
                flash += (1f + ((heat - flashThreshold) / (1f - flashThreshold)) * 5.4f) * Time.delta;
                Draw.color(Color.red, Color.yellow, Mathf.absin(flash, 9f, 1f));
                Draw.alpha(0.3f);
                Draw.rect(lightsRegion, x, y);
            }

            Draw.reset();
        }
    }
}
