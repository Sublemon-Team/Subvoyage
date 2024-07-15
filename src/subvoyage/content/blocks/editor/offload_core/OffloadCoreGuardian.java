package subvoyage.content.blocks.editor.offload_core;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.type.UnitType;
import mindustry.world.Tile;

import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.*;
import static subvoyage.content.unit.SvUnits.*;

public class OffloadCoreGuardian extends OffloadCoreOld {
    public OffloadCoreGuardian(String name) {
        super(name);
    }


    public class OffloadCoreGuardBuild extends OffloadCoreOldBuild {

        private Unit boss;
        private boolean isBossStarted = false;


        @Override
        public void created() {
            shieldRadius = size*tilesize*2.5f;
            super.created();
        }

        @Override
        public void update() {
            super.update();
            //we don't want to player spawn wave without a core, right?
            state.rules.waveSending = false;


            enemySpawnProgress = 0;

            for (Bullet bullet : Groups.bullet) {
                if(bullet.within(x,y,shieldRadius) && bullet.team != team) {
                    Fx.absorb.create(bullet.x,bullet.y,0,team.color,new Object());
                    lastHitTime = 1f;
                    bullet.remove();
                }
            }
            for (Unit unit : Groups.unit) {
                if(unit.within(x,y,shieldRadius)) {{
                    unit.move(new Vec2(unit.x,unit.y).sub(x,y).nor());
                }}
            }

            if(!isBossStarted) checkCores();
            else {
                if(boss == null || boss.dead() || boss.health() <= 0) disableShield();
            }
        }

        @Override
        protected void drawShield() {
            float alpha = shieldAlpha();
            float radius = shieldRadius- Mathf.clamp(lastHitTime);
            Fill.light(x, y, Lines.circleVertices(radius), radius,
                    Color.clear.lerp(team.color,0.5f),
                    Tmp.c2.set(team.color).a(0.7f * alpha)
            );
            Drawf.circles(x,y,radius,team.color);
        }

        private void checkCores() {
            AtomicInteger coreCount = new AtomicInteger();
            for (Tile e : world.tiles) {
                if(e.build instanceof OffloadCoreOldBuild && e.build != this) coreCount.getAndIncrement();
            }
            if(coreCount.get() <= 0 && !isBossStarted) initiateBoss();
        }

        private void initiateBoss() {
            isBossStarted = true;
            UnitType[][] bossTypes = new UnitType[][] {
                    {charon,callees,ganymede}
            };
            UnitType[] specie = bossTypes[rand.nextInt(bossTypes.length)];
            int tier = 0;
            if(rand.chance(0.5)) tier++;
            if(rand.chance(0.1)) tier++;
            UnitType boss = specie[tier];
            this.boss = boss.spawn(team,x,y);
            this.boss.apply(StatusEffects.boss);
            this.boss.shield(100f*(tier+1));
        }
    }
}
