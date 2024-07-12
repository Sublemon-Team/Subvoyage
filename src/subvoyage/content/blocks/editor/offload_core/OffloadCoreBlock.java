package subvoyage.content.blocks.editor.offload_core;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.game.Team;
import mindustry.graphics.Layer;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.storage.CoreBlock;
import subvoyage.content.SvPal;

import static mindustry.Vars.*;

public class OffloadCoreBlock extends CoreBlock {
    public int minShieldLayers = 3;
    public int maxShieldLayers = 5;


    public TextureRegion dangerIconRegion;



    public OffloadCoreBlock(String name) {
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


    public class OffloadCoreBuilding extends CoreBuild {

        int shieldLayers = 0;
        float smoothShieldLayers = 0;
        float waveTimer = 0;

        @Override
        public void created() {
            super.created();
            waveTimer = state.rules.initialWaveSpacing <= 0f ? state.rules.waveSpacing * 2f : state.rules.initialWaveSpacing;
            recalculateStats();
        }

        public void spawnWave() {
            waveTimer = state.rules.initialWaveSpacing <= 0f ? state.rules.waveSpacing * 2f : state.rules.initialWaveSpacing;
        }



        public void tryBreakLayer() {
            breakLayer();
        }

        void breakLayer() {
            shieldLayers--;
        };


        @Override
        public void updateTile() {
            super.updateTile();
            if(waveTimer > 0) waveTimer--;
            else spawnWave();
        }

        @Override
        public void draw() {
            super.draw();
            drawShieldLayers();
            drawWaveCountdown();
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
            cont.defaults().size(40f).width(80f);
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
            return damage * (1f-(delta*2));
        };

        public void recalculateStats() {
            shieldLayers = Mathf.clamp(shieldLayers,minShieldLayers,maxShieldLayers);
        }


        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(shieldLayers);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if(revision != version()) return;
            shieldLayers = read.i();
        }

        @Override
        public byte version() {
            return 1;
        }
    }
}
