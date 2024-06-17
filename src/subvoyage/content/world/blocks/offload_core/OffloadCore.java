package subvoyage.content.world.blocks.offload_core;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import subvoyage.content.unit.SvUnits;

import static arc.math.Mathf.pi;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class OffloadCore extends CoreBlock {
    public OffloadCore(String name) {
        super(name);
        lightRadius = 0;
        fogRadius = 0;
        emitLight = true;
        placeOverlapRange = 0f;
        teamPassable = true;
    }

    @Override
    public void init() {
        super.init();
        lightRadius = 10f * size;
        fogRadius = Math.max(fogRadius, (int)(lightRadius / 8f * 3f) + 13);
        emitLight = true;
        placeOverlapRange = 0f;
    }

    @Override
    public boolean canReplace(Block other) {
        return false;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return false;
    }


    public class OffloadCoreBuild extends CoreBuild {

        public boolean isShieldDisabled = false;
        public float shieldRadius = size*tilesize*2;

        public float lastHitTime = 0f;
        public float enemySpawnProgress = 0f;
        public final float enemySpawnTimeF = 60*60f;
        public float enemySpawnTime = 60*60f;
        public int currentWave = 0;

        public Rand rand = new Rand();

        @Override
        public void draw() {
            super.draw();
            if(!isShieldDisabled) drawShield();
        }


        @Override
        public void update() {
            super.update();

            //we don't want to player spawn wave without a core, right?
            state.rules.waveSending = false;
            state.wavetime = enemySpawnTime;

            lastHitTime-=Time.delta/30f;
            enemySpawnProgress+=Time.delta/enemySpawnTime;

            for (Bullet bullet : Groups.bullet) {
                if(bullet.within(x,y,shieldRadius) && bullet.team != team) {
                    Fx.absorb.create(bullet.x,bullet.y,0,team.color,new Object());
                    lastHitTime = 1f;
                    enemySpawnProgress *= 0.99f;
                    bullet.remove();
                }
            }
            for (Unit unit : Groups.unit) {
                if(unit.within(x,y,shieldRadius)) {{
                    unit.move(new Vec2(unit.x,unit.y).sub(x,y).nor());
                }}
            }

            if(enemySpawnProgress >= 1) {
                enemySpawnProgress %= 1f;
                Fx.bigShockwave.create(x,y,0,team.color, new Object());
                Fx.blastExplosion.create(x,y,0,team.color,new Object());
                beginWave();
            }
        }

        private void beginWave() {
            state.wave = currentWave+1;

            int enemyCount = currentWave == 0 ? 1 : rand.nextInt(Math.max(currentWave/10,1))+1;
            boolean[] hasBoss = new boolean[] {false};
            for (int i = 0; i < enemyCount; i++) {
                UnitType currentEnemy = getEnemy(currentWave,hasBoss);
                float spawnDegree = rand.nextFloat()*pi*2;
                float spawnRad = shieldRadius*Mathf.sqrt(rand.nextFloat());
                float relX = (float) (Math.cos(spawnDegree)*spawnRad);
                float relY = (float) (Math.sin(spawnDegree)*spawnRad);
                float unitX = x+relX;
                float unitY = y+relY;
                Unit unit = currentEnemy.spawn(team,unitX,unitY);
            }
            enemySpawnTime = enemySpawnTimeF / (float) Math.sqrt(currentWave/10f+1);
            currentWave++;
        }

        private UnitType getEnemy(int currentWave,boolean[] hasBoss) {
            UnitType[][] unitTypes = new UnitType[][] {
                    {SvUnits.lapetus,SvUnits.skath,SvUnits.charon,SvUnits.callees}
            };
            int tier = rand.nextInt((currentWave > 50 && !hasBoss[0]) ? 4:  currentWave > 30 ? 3 : currentWave > 10 ? 2 : 1);
            if(tier == 3) hasBoss[0] = true;
            int unitType = rand.nextInt(unitTypes.length);
            return unitTypes[unitType][tier];
        }

        private void drawShield() {
            float alpha = shieldAlpha();
            float radius = shieldRadius-Mathf.clamp(lastHitTime);
            float waveRadius = radius*enemySpawnProgress;
            Fill.light(x, y, Lines.circleVertices(radius), radius,
                    Color.clear.lerp(team.color,0.5f),
                    Tmp.c2.set(team.color).a(0.7f * alpha)
            );
            Drawf.circles(x,y,radius,team.color);
            Drawf.dashCircle(x, y, waveRadius, new Color(team.color).lerp(Color.red,enemySpawnProgress));
        }

        private float shieldAlpha() {
            return 0.5f;
        }

        @Override
        public void damage(Team source, float damage) {
            if(isShieldDisabled) super.damage(source, damage);
        }

    }
}
