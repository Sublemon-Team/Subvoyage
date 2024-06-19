package subvoyage.content.world.blocks.offload_core;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.game.MapObjectives;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import subvoyage.content.unit.SvUnits;

import java.io.InputStream;

import static arc.math.Mathf.pi;
import static mindustry.Vars.*;

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
        public final float enemySpawnTimeF = 1.1f*60*60f;
        public float enemySpawnTime = 2.3f*60*60f;
        public int currentWave = 0;


        //0 - just damage
        //1 - survive waves
        //2 - use special building
        //3 - timer
        //4 - item payload
        public byte completeType = 0x79;

        public float damageDealt = 0f;
        public float timePassed = 0;

        public int damageToDeal = 0;
        public int waveToSurvive =10;
        public int secondsToSurvive = 60;

        public Rand rand = new Rand(0);


        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(damageDealt);
            write.f(timePassed);
            write.i(currentWave);
            write.f(enemySpawnTime);
        }


        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            try {
                /*int a = 0;
                if(read.input instanceof InputStream r){
                    if(r.available() < 4+4+4+4) a = -1;
                } else a = -1;
                if(a == -1) return;*/
                damageDealt = read.f();
                timePassed = read.f();
                currentWave = read.i();
                enemySpawnTime = read.f();
            } catch (Exception e) {

            }
        }

        @Override
        public void created() {
            super.created();
            rand = new Rand((long) ((long)
                    state.rules.planet.id*(state.rules.sector == null ? 1 : state.rules.sector.id)
                    +state.rules.planet.id
                    +(state.rules.sector == null ? 1 : state.rules.sector.id)
                    +x*y
                    +x
                    +y
            ));
            if(completeType == 0x79) {
                completeType = (byte) ((byte) rand.nextInt(60)%5);
                damageToDeal = 10000+rand.nextInt(10000);
                waveToSurvive = 20+rand.nextInt(2*2+1)*5;
                secondsToSurvive = 8*60+rand.nextInt(8*30)*2;
            }
        }


        @Override
        public void draw() {
            super.draw();
            if(!isShieldDisabled) drawShield();
        }


        @Override
        public void drawStatus() {
            super.drawStatus();
        }

        @Override
        public void update() {
            super.update();

            switch (completeType) {
                case 0x0:
                    if(damageDealt >= damageToDeal) disableShield();
                    break;
                case 0x1:
                    if(currentWave >= waveToSurvive) disableShield();
                    break;
                case 0x2:
                case 0x4:
                    break;
                case 0x3:
                    if(timePassed >= secondsToSurvive*60f) disableShield();
                    break;
            }

            //we don't want to player spawn wave without a core, right?
            state.rules.waveSending = false;

            timePassed += Time.delta;
            lastHitTime-=Time.delta/30f;
            enemySpawnProgress+=Time.delta/enemySpawnTime;

            for (Bullet bullet : Groups.bullet) {
                if(bullet.within(x,y,shieldRadius) && bullet.team != team) {
                    Fx.absorb.create(bullet.x,bullet.y,0,team.color,new Object());
                    lastHitTime = 1f;
                    enemySpawnProgress *= 0.999f;
                    damageDealt += bullet.damage;
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

        public void disableShield() {
            isShieldDisabled = false;
            Fx.instBomb.create(x,y,0, Pal.accent,new Object());
            Call.buildDestroyed(this);
        }

        private void beginWave() {
            state.wave = currentWave+1;

            int enemyCount = currentWave == 0 ? 1 : rand.nextInt(Math.max(currentWave/5,1))+1;
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

        protected void drawShield() {
            float alpha = shieldAlpha();
            float radius = shieldRadius-Mathf.clamp(lastHitTime);
            float waveRadius = radius*enemySpawnProgress;
            Fill.light(x, y, Lines.circleVertices(radius), radius,
                    Color.clear.lerp(team.color,0.5f),
                    Tmp.c2.set(team.color).a(0.7f * alpha)
            );

            Drawf.circles(x,y,radius,team.color);
            Lines.stroke(Math.abs(Mathf.sinDeg((Time.time*2)%360))*3,Color.white);
            Lines.circle(x,y,waveRadius);
            Lines.stroke(1f,new Color(team.color).add(0.15f,0.15f,0.15f));
            Lines.circle(x,y,waveRadius);

            float progress = 1f-Mathf.clamp(switch (completeType) {
                case 0x0 -> damageDealt/damageToDeal;
                case 0x1 -> (float) currentWave /waveToSurvive;
                case 0x3 -> timePassed/(secondsToSurvive*60f);
                default -> 0f;
            });
            int sideCount = (completeType)+3;
            int shapeCount = Math.max(completeType-2,0)+1;
            float step = (float) 360 /shapeCount;
            float dst = tilesize*size*progress;
            float t = (Time.time/Math.max(progress,1/100f)/4f) % 360;
            float t2 = (Time.time/4f) % 360;
            for (int i = 0; i < shapeCount; i++) {
                float xi = Mathf.sinDeg(step*i+t)*dst;
                float yi = Mathf.cosDeg(step*i+t)*dst;
                Lines.stroke(4f);
                Draw.color(team.color);
                Lines.poly(x+xi,y+yi,sideCount,tilesize*progress,i % 2 == 0 ? Time.time : Time.time+180);

                Lines.stroke(1f);
                Draw.color();
                Lines.poly(x+xi,y+yi,sideCount,tilesize*progress,i % 2 == 0 ? Time.time : Time.time+180);

                float xi2 = Mathf.sinDeg(step*i+t2)*tilesize*size;
                float yi2 = Mathf.cosDeg(step*i+t2)*tilesize*size;
                Lines.stroke(4f);
                Draw.color(team.color);
                Draw.alpha(0.3f);
                Lines.poly(x+xi2,y+yi2,sideCount,tilesize,i % 2 == 0 ? Time.time : Time.time+180);

                Lines.stroke(1f);
                Draw.color();
                Draw.alpha(0.3f);
                Lines.poly(x+xi2,y+yi2,sideCount,tilesize,i % 2 == 0 ? Time.time : Time.time+180);
                Draw.reset();
            }
            Draw.reset();
        }

        protected float shieldAlpha() {
            return 0.5f;
        }

        @Override
        public void damage(Team source, float damage) {
            if(isShieldDisabled) super.damage(source, damage);
            else damageDealt += damage;
        }

    }
}
