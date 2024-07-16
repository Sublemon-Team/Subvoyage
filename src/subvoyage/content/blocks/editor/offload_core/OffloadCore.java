package subvoyage.content.blocks.editor.offload_core;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Point2;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.util.Structs;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockFlag;
import subvoyage.content.unit.SvBlockFlag;
import subvoyage.content.world.SvFx;

import static mindustry.Vars.*;
import static subvoyage.content.unit.SvUnits.*;

public class OffloadCore extends CoreBlock implements IOffload {
    public int minShieldLayers = 3;
    public int maxShieldLayers = 5;


    public TextureRegion dangerIconRegion;

    public UnitType[] lowTierUnits = new UnitType[0];
    public UnitType[] midTierUnits = new UnitType[0];
    public UnitType[] highTierUnits = new UnitType[0];



    public OffloadCore(String name) {
        super(name);
        configurable = true;
    }

    @Override
    public void load() {
        super.load();
        dangerIconRegion = Core.atlas.find(name+"-danger","danger-icon");
    }

    @Override public boolean canPlaceOn(Tile tile, Team team, int rotation) {return false;}
    @Override public boolean canReplace(Block other) {return false;}


    public class OffloadCoreBuilding extends CoreBuild  implements IOffload {

        int shieldLayers = 0;
        float smoothShieldLayers = 0;
        float waveTimer = 0;
        boolean isFirstWave = true;
        boolean isUpgradeWave = false;
        int unitTier = 0;
        float smoothTier = 0;
        float shieldCooldown = 0f;
        Rand rand = null;
        UnitType nextUnit = null;

        @Override
        public void created() {
            super.created();
            waveTimer = getWaveTime();
            recalculateStats();
        }

        public void spawnWave() {
            waveTimer = getWaveTime();
            if(nextUnit != null) {
                Unit unit = nextUnit.spawn(team,x,y);
                unit.rotation(-90f);
                unit.shield(40f*unitTier);
                nextUnit = null;
            }
            isFirstWave = false;
            isUpgradeWave = false;
            Sounds.wave.play(2f,1f,0f);
        }



        public boolean tryBreakLayer() {
            if(shieldLayers <= 0) return false;
            if(shieldCooldown > 0) return false;
            damage(player.team(),80f);
            Fx.pointHit.create(x,y,0,Pal.darkPyraFlame,new Object());
            breakLayer();
            return true;
        }

        void breakLayer() {
            shieldLayers--;
            upgrade();
            nextUnit = switch (unitTier) {
                case 0 -> Structs.random(rand,lapetus,leeft);
                case 1,2 -> Structs.random(rand,skath,flagshi);
                case 3 -> Structs.random(rand,charon,vanguard);
                default -> Structs.random(rand,charon,vanguard,callees,squadron);
            };
            waveTimer = unitTier*10f*60f;
            isUpgradeWave = true;
            Sounds.laserbig.play(0.3f,3f,0f);
            SvFx.resonanceExplosion.create(x,y,0,Pal.accent,new Object());
            Fx.blastExplosion.create(x,y,0, Pal.accent,new Object());
            shieldCooldown = 60f;
        }

        private void upgrade() {
            unitTier++;
        }


        @Override
        public void updateTile() {
            super.updateTile();
            if(rand == null) rand = new Rand(Point2.pack(tileX(),tileY())* 100L +(state.rules.sector == null ? 1 : state.rules.sector.id));
            if(nextUnit == null) selectNextUnit();
            if(waveTimer > 0 && nextUnit != null) waveTimer--;
            else if(nextUnit != null) spawnWave();
            if(isUpgradeWave && Time.time % 10f <= Time.delta) {
                Fx.shockwave.create(x,y,0,Pal.accent,new Object());
            }
            shieldCooldown -= Time.delta;
        }

        private void selectNextUnit() {
            UnitType[] selectFrom = new UnitType[0];
            if(unitTier >= 0 && unitTier < 4) for (UnitType u : lowTierUnits) selectFrom = Structs.add(selectFrom,u);
            if(unitTier >= 1) for (UnitType u : midTierUnits) selectFrom = Structs.add(selectFrom,u);
            if(unitTier >= 4) for (UnitType u : highTierUnits) selectFrom = Structs.add(selectFrom,u);
            if(selectFrom.length > 0) nextUnit = Structs.random(rand,selectFrom);
        }

        @Override
        public void draw() {
            smoothTier = Mathf.lerp(smoothTier,unitTier,Time.delta/90f);
            super.draw();
            drawShieldLayers();
            if(nextUnit != null) {
                drawConstructingUnit();
                drawWaveCountdown();
            }
        }

        private void drawConstructingUnit() {
            float progress = 1f-waveTimer/getWaveTime();
            Draw.draw(Layer.blockOver, () -> Drawf.construct(this, nextUnit, -90f, progress, 1, Time.time));
        }

        public float getWaveTime() {
            return (isFirstWave
                    ? (state.rules.initialWaveSpacing <= 0f ? state.rules.waveSpacing * 2f : state.rules.initialWaveSpacing)
                    : state.rules.waveSpacing);
        }

        private void drawWaveCountdown() {
            Draw.z(Layer.blockOver);
            int minutes = (int) (waveTimer/60/60);
            int seconds = (int) (waveTimer/60)%60;
            String m,se;
            m = minutes+""; se = seconds < 10 ? "0"+seconds : seconds+"";
            float width = drawPlaceText(String.format("%s:%s", m,se), tileX(), tileY(), false);
            float dx = x + offset - width/2f - 4f, dy = y + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            if(dangerIconRegion.found()) {
                Draw.mixcol(Color.darkGray, 1f);
                Draw.rect(dangerIconRegion, dx, dy - 1, s, s);
                Draw.reset();
                Draw.rect(dangerIconRegion, dx, dy, s, s);
                Draw.reset();
            }
        }

        private void drawShieldLayers() {
            smoothShieldLayers = Mathf.lerp(smoothShieldLayers,shieldLayers, Time.delta/10f);

            Draw.z(Layer.shields);
            Draw.color(team.color);
            Fill.poly(x,y,6,(smoothShieldLayers < 0.8 ? smoothShieldLayers : size+smoothShieldLayers)*tilesize);

            Draw.reset();
        }

        @Override
        public void buildConfiguration(Table table) {
            buildShieldSelection(this,table,minShieldLayers,maxShieldLayers);
        }

        public void buildShieldSelection(OffloadCoreBuilding build, Table table,int minLayers,int maxLayers) {
            Table main = new Table().background(Styles.black6);
            Table cont = new Table().top();
            cont.defaults().size(60f).width(200f);
            Runnable rebuild = () -> {
                cont.clearChildren();


                cont.label(() -> minLayers+" ≤ ["+build.shieldLayers+"] ≤ "+maxLayers);
                cont.row();
                TextButton button = cont.button("-",() -> {

                        }).get();
                button.changed(() -> {
                    if(!button.isChecked()) return;
                    build.shieldLayers--;
                    button.setChecked(false);
                });
                TextButton button2 = cont.button("+",() -> {

                }).get();
                button2.changed(() -> {
                    if(!button2.isChecked()) return;
                    build.shieldLayers++;
                    button2.setChecked(false);
                });
                cont.row();
                TextButton trybreak = cont.button("break layer (try)", () -> {

                }).get();
                trybreak.changed(() -> {
                    if(!trybreak.isChecked()) return;
                    build.tryBreakLayer();
                    trybreak.setChecked(false);
                });
                cont.row();
                TextButton trybreak2 = cont.button("break layer (straight)", () -> {

                }).get();
                trybreak2.changed(() -> {
                    if(!trybreak2.isChecked()) return;
                    build.breakLayer();
                    trybreak2.setChecked(false);
                });
            };
            rebuild.run();
            main.add(cont);
            table.add(main);
        }

        @Override
        public void damage(Team source, float damage) {
            if(shieldLayers >= minShieldLayers) return;
            if(source == null || source == team) return;
            super.damage(source, calculateDamage(damage));
        }




        public float calculateDamage(float damage) {
            float delta = (float) shieldLayers /minShieldLayers;
            return damage * (1f-(delta))/2f;
        };

        public void recalculateStats() {
            shieldLayers = Mathf.clamp(shieldLayers,minShieldLayers,maxShieldLayers);
        }


        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(shieldLayers);
            write.bool(isFirstWave);
            write.f(waveTimer);
            write.i(unitTier);
            if(nextUnit != null) write.str(nextUnit.name);
            else write.str("nilunit");
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if(revision != version()) return;
            shieldLayers = read.i();
            isFirstWave = read.bool();
            waveTimer = Math.min(read.f(),getWaveTime());
            unitTier = read.i();
            String u = read.str();
            if(!u.equals("nilunit")) nextUnit = content.unit(u);
        }

        @Override
        public byte version() {
            return 2;
        }
    }
}
