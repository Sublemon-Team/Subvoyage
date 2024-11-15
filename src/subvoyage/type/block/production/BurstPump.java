package subvoyage.type.block.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Pump;
import mindustry.world.consumers.ConsumeLiquidBase;

import static mindustry.Vars.*;

public class BurstPump extends Pump {

    public float impactTime = 20f;
    public float liquidBoost = 1.5f;

    public Effect impactEffect = Fx.none;

    public BurstPump(String name) {
        super(name);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

        Tile tile = world.tile(x, y);
        if(tile == null) return;

        float amount = 0f;
        Liquid liquidDrop = null;

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canPump(other)){
                if(liquidDrop != null && other.floor().liquidDrop != liquidDrop){
                    liquidDrop = null;
                    break;
                }
                liquidDrop = other.floor().liquidDrop;
                amount += other.floor().liquidMultiplier;
            }
        }

        if(liquidDrop != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.pumpspeed", amount/impactTime * pumpAmount * 60f, 0), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            float ratio = (float)liquidDrop.fullIcon.width / liquidDrop.fullIcon.height;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(liquidDrop.fullIcon, dx, dy - 1, s * ratio, s);
            Draw.reset();
            Draw.rect(liquidDrop.fullIcon, dx, dy, s * ratio, s);
        }
    }

    public class BurstPumpBuild extends PumpBuild {
        float time = 0f;

        public float boost() {
            if(findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase) {
                return Mathf.clamp(consBase.efficiency(this)*liquidBoost,1f,liquidBoost);
            }
            return 1f;
        }

        @Override
        public void updateTile() {
            if(efficiency > 0 && liquidDrop != null){

                //does nothing for most pumps, as those do not require items.
                if((consTimer += delta()) >= consumeTime){
                    consume();
                    consTimer %= 1f;
                }
                float maxPump = Math.min(liquidCapacity - liquids.get(liquidDrop), amount * pumpAmount * edelta());
                warmup = Mathf.approachDelta(warmup, maxPump > 0.001f ? 1f : 0f, warmupSpeed);
                time += warmup * edelta() * boost();
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }
            if(time >= impactTime) {
                time %= impactTime;
                float maxPump = Math.min(liquidCapacity - liquids.get(liquidDrop), amount * pumpAmount * edelta());
                liquids.add(liquidDrop, maxPump);
                impactEffect.create(x,y,rotation, Color.white,new Object());
                warmup = 0f;
            }

            totalProgress += warmup * Time.delta;

            if(liquidDrop != null){
                dumpLiquid(liquidDrop);
            }
        }
    }
}
