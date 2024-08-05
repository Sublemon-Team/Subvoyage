package subvoyage.type.block.production;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class AttributeCrafterBoostable extends AttributeCrafter {
    public float liquidBoost = 1.5f;
    public AttributeCrafterBoostable(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        if(findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase){
            stats.remove(Stat.booster);
            stats.add(Stat.booster,
                    StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                            consBase.amount, liquidBoost, false,
                            l -> (consumesLiquid(l) && (findConsumer(f -> f instanceof ConsumeLiquid).booster || ((ConsumeLiquid)findConsumer(f -> f instanceof ConsumeLiquid)).liquid != l)))
            );
        }
    }

    public class AttributeCrafterBoostableBuild extends AttributeCrafterBuild {
        public float boost() {
            if(findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase) {
                return Mathf.clamp(consBase.efficiency(this)*liquidBoost,1f,liquidBoost);
            }
            return 1f;
        }
        @Override
        public void updateTile() {
            if(efficiency > 0){

                progress += getProgressIncrease(craftTime/boost());
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                //continuously output based on efficiency
                if(outputLiquids != null){
                    float inc = getProgressIncrease(1f);
                    for(var output : outputLiquids){
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            //TODO may look bad, revert to edelta() if so
            totalProgress += warmup * Time.delta;

            if(progress >= 1f){
                craft();
            }

            dumpOutputs();
        }
    }
}
