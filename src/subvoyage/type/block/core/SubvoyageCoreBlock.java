package subvoyage.type.block.core;

import arc.Core;
import arc.func.Boolp;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.gl.FrameBuffer;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Circle;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.struct.*;
import arc.util.Reflect;
import arc.util.Time;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.Tile;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.world.blocks.storage.*;
import subvoyage.content.other.SvPal;
import subvoyage.content.sound.SvMusic;
import subvoyage.draw.visual.SvDraw;
import subvoyage.draw.visual.SvFx;
import subvoyage.draw.visual.SvShaders;

import static arc.Core.*;
import static arc.graphics.g2d.Lines.line;
import static mindustry.Vars.*;
import static subvoyage.draw.visual.SvFx.rand;

public class SubvoyageCoreBlock extends CoreBlock {
    public Seq<Item> bannedItems = new Seq<>();
    public SubvoyageCoreBlock(String name) {
        super(name);
    }
    public static boolean cutscene = false;
    public static final Boolp lock = () -> cutscene;
    public static float landTime = 0f;

    @Override
    public void init() {
        super.init();
        lightRadius = 30f + 20f * (size+1);
        fogRadius = Math.max(fogRadius, (int)(lightRadius / 8f * 3f) + 13);
        emitLight = true;
    }

    @Override
    public void drawLanding(CoreBuild build, float x, float y) {
        Reflect.set(renderer,"camerascale", Scl.scl(4f));
        Reflect.set(renderer,"landTime", 0f);

        player.set(x,y);
        camera.position.set(x,y);
    }

    private static Vec2 vector = new Vec2();
    public static void dashCircleWavy(float x, float y, float radius) {
        float scaleFactor = 0.6F;
        int sides = 10 + (int)(radius * scaleFactor);
        if (sides % 2 == 1) {
            ++sides;
        }

        vector.set(0.0F, 0.0F);

        for(int i = 0; i < sides; i += 2) {
            vector.set(radius, 0.0F).rotate(360.0F / (float)sides * (float)i + 90.0F + (radius*(i+1)*Time.time)%10);
            float x1 = vector.x;
            float y1 = vector.y;
            vector.set(radius, 0.0F).rotate(360.0F / (float)sides * (float)(i + 1) + 90.0F + (radius*(i+1)*Time.time)%10);
            line(x1 + x, y1 + y, vector.x + x, vector.y + y);
        }

    }

    @Override
    public void drawShadow(Tile tile) {
        if(tile.build instanceof SubvoyageCoreBuild b) {
            float fin = Interp.pow3.apply(1-landTime/coreLandDuration);
            float s = region.height/4f*fin;
            Draw.color(0f, 0f, 0f, BlockRenderer.shadowColor.a);
            Draw.rect(
                    variants == 0 ? customShadowRegion :
                            variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))],
                    tile.drawx(), tile.drawy(),s,s, tile.build == null ? 0f : tile.build.drawrot());
            Draw.color();
            return;
        }
        Draw.color(0f, 0f, 0f, BlockRenderer.shadowColor.a);
        Draw.rect(
                variants == 0 ? customShadowRegion :
                        variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))],
                tile.drawx(), tile.drawy(), tile.build == null ? 0f : tile.build.drawrot());
        Draw.color();
    }

    public class  SubvoyageCoreBuild extends CoreBuild {
        @Override
        public void handleStack(Item item, int amount, Teamc source) {
            if(!bannedItems.contains(item) && item != Items.copper) super.handleStack(item, amount, source);
            else Fx.coreBurn.at(x, y);
        }

        @Override
        public void update() {
            super.update();
            items.remove(Items.copper,1000);
            SvMusic.theAtlacian.setVolume(settings.getInt("musicvol") / 150f);
        }

        @Override
        public void draw() {
            if(!cutscene) {
                thrusterTime = 0f;
                super.draw();
            }
            else drawLanding();
        }
        public void drawLanding() {
            float fin = Interp.pow3.apply(1-landTime/coreLandDuration+0.2f);
            float fin2 = Interp.smooth.apply(Interp.slope.apply(1-landTime/coreLandDuration));
            float s = region.height/4f*fin;
            Draw.z(Layer.floor+1);
            SvDraw.applyCache(SvShaders.hardWaterLayer,() -> {
                Draw.z(Layer.floor+1);
                float pulse = Mathf.sin(10f,2f);
                Draw.color(SvPal.hardWater.cpy().value(0.8f));
                Fill.circle(x,y,fin2*tilesize*size+pulse+4f);
                Draw.color(SvPal.hardWater.cpy().value(0.9f));
                Fill.circle(x,y,fin2*tilesize*size+pulse+2f);
                Draw.color(SvPal.hardWater);
                Fill.circle(x,y,fin2*tilesize*size+pulse);
            });
            Draw.color();
            if(fin >= 0.4f) {
                Draw.color();
                Draw.z(Layer.block);
                Draw.rect(region,x,y,s,s);
                if (this.block.teamRegion.found()) {
                    renderer.bloom.capture();
                    renderer.bloom.setBloomIntensity(fin2*2f);
                    if (this.block.teamRegions[this.team.id] == this.block.teamRegion) {
                        Draw.color(this.team.color);
                    }
                    Draw.rect(this.block.teamRegions[this.team.id], this.x, this.y,s,s);
                    Draw.color();
                    renderer.bloom.render();
                }
            }
            camera.position.add(rand.random(-fin2, fin2),rand.random(-fin2,fin2));

        }


        @Override
        public void handleItem(Building source, Item item) {
            if(!bannedItems.contains(item) && item != Items.copper) super.handleItem(source, item);
            else incinerateEffect(this, source);
        }
    }

}
