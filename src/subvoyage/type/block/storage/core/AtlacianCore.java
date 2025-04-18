package subvoyage.type.block.storage.core;

import arc.Core;
import arc.audio.Music;
import arc.func.Boolp;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Scl;
import arc.struct.*;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.LaunchAnimator;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.storage.*;
import subvoyage.content.block.SvLaser;
import subvoyage.content.ost.SvMusic;
import subvoyage.core.anno.LoadAnnoProcessor;
import subvoyage.core.draw.SvFx;
import subvoyage.core.draw.SvPal;
import subvoyage.core.draw.SvDraw;
import subvoyage.core.draw.SvRender;
import subvoyage.core.draw.shader.SvShaders;

import static mindustry.Vars.*;

public class AtlacianCore extends CoreBlock {
    public Seq<Item> bannedItems = new Seq<>();

    public @LoadAnnoProcessor.LoadAnno("@-border") TextureRegion border;

    public AtlacianCore(String name) {
        super(name);
    }

    public class AtlacianCoreBuild extends CoreBuild implements LaunchAnimator {
        @Override
        public void handleStack(Item item, int amount, Teamc source) {
            if(!bannedItems.contains(item) && item != Items.copper) super.handleStack(item, amount, source);
            else Fx.coreBurn.at(x, y);
        }

        @Override
        public void draw() {
            if(renderer.getLandTime() <= 0f) super.draw();
        }

        @Override
        public void update() {
            super.update();
            items.remove(Items.copper,1000);
        }

        @Override
        public void beginLaunch(boolean launching) {
            if(launching){
                Fx.coreLaunchConstruct.at(x, y, size);
            }
            if(!headless){
                // Add fade-in and fade-out foreground when landing or launching.
                if(renderer.isLaunching()){
                    float margin = 30f;

                    Image image = new Image();
                    image.color.a = 0f;
                    image.touchable = Touchable.disabled;
                    image.setFillParent(true);
                    image.actions(Actions.delay((launchDuration() - margin) / 60f), Actions.fadeIn(margin / 60f, Interp.pow2In), Actions.delay(6f / 60f), Actions.remove());
                    image.update(() -> {
                        image.toFront();
                        ui.loadfrag.toFront();
                        if(state.isMenu()){
                            image.remove();
                        }
                    });
                    Core.scene.add(image);
                }else{
                    Image image = new Image();
                    image.color.a = 1f;
                    image.touchable = Touchable.disabled;
                    image.setFillParent(true);
                    image.actions(Actions.fadeOut(35f / 60f), Actions.remove());
                    image.update(() -> {
                        image.toFront();
                        ui.loadfrag.toFront();
                        if(state.isMenu()){
                            image.remove();
                        }
                    });
                    Core.scene.add(image);

                    Time.run(launchDuration(), () -> {
                        launchEffect.at(this);
                        Effect.shake(5f, 5f, this);

                        if(state.isCampaign() && Vars.showSectorLandInfo && (state.rules.sector.preset == null || state.rules.sector.preset.showSectorLandInfo)){
                            ui.announce("[accent]" + state.rules.sector.name() + "\n" +
                                    (state.rules.sector.info.resources.any() ? "[lightgray]" + Core.bundle.get("sectors.resources") + "[white] " +
                                            state.rules.sector.info.resources.toString(" ", UnlockableContent::emoji) : ""), 5);
                        }
                    });
                }
            }
        }

        @Override
        public void drawLaunchGlobalZ() {
            float fin = renderer.getLandTimeIn();
            float fout = 1f - fin;

            drawLanding(x, y);

            Draw.color();
            Draw.mixcol(Color.white, Interp.pow5In.apply(fout));

            //accent tint indicating that the core was just constructed
            if(renderer.isLaunching()){
                float f = Mathf.clamp(1f - fout * 12f);
                if(f > 0.001f){
                    Draw.mixcol(Pal.accent, f);
                }
            }
        }

        @Override
        public void drawLaunch() {

        }

        @Override
        public void drawLanding(float x, float y) {
            float fin = Interp.pow3.apply(renderer.getLandTimeIn()+0.2f);
            float fin2 = Interp.smooth.apply(Interp.slope.apply(renderer.getLandTimeIn()));
            float s = region.height/4f*fin;

            Draw.z(SvRender.Layer.hardWater);

            float pulse = Mathf.sin(10f,2f);
            Draw.color(SvPal.hardWater.cpy().value(0.8f));
            Fill.circle(x,y,fin2*tilesize*size+pulse+4f);
            Draw.color(SvPal.hardWater.cpy().value(0.9f));
            Fill.circle(x,y,fin2*tilesize*size+pulse+2f);
            Draw.color(SvPal.hardWater);
            Fill.circle(x,y,fin2*tilesize*size+pulse);

            Draw.z(Layer.floor+1);
            Draw.color();

            Draw.alpha(Mathf.clamp(fin/0.4f));
            Draw.mixcol(Color.blue.cpy().mul(0.2f),1f-Mathf.clamp(fin/0.6f));

            Draw.z(Layer.block+0.5f);
            if(fin > 0.2f)
                Draw.rect(border,x,y,(s+2)*fin2,(s+2)*fin2);
            Draw.rect(region,x,y,s,s);
            if (teamRegions[team.id] == teamRegion)
                Draw.color(team.color);
            Draw.rect(teamRegions[team.id], x, y, s, s);

            if(!renderer.isLaunching())
                Draw.draw(Layer.blockOver, () -> {
                    Draw.scl(fin);

                    Shaders.blockbuild.region = unitType.fullIcon;
                    Shaders.blockbuild.progress = renderer.getLandTimeIn();
                    Shaders.blockbuild.time = Time.time;

                    Draw.color(Pal.accent);
                    Draw.shader(Shaders.blockbuild);
                    Draw.rect(unitType.fullIcon, x, y, rotation);
                    Draw.shader();

                    Draw.reset();
                    Draw.scl(1f);
                });
        }

        @Override
        public void updateLaunch() {
            Effect.shake(0.3f, 5f, this);

            float fin = Interp.pow3.apply(renderer.getLandTimeIn()+0.2f);

            float in = renderer.getLandTimeIn() * launchDuration();
            float tsize = Mathf.sample(thrusterSizes, (in + 35f) / launchDuration());
            landParticleTimer += tsize * Time.delta;
            if(landParticleTimer >= 1f){
                tile.getLinkedTiles(t -> {
                    if(Mathf.chance(0.4f)){
                        float tx = t.worldx();
                        float ty = t.worldy();
                        float dtx = tx-x;
                        float dty = ty-y;

                        if(renderer.getLandTimeIn() < 0.6f && fin > 0.4f) SvFx.steam.at(x+dtx*fin*1.1f,y+dty*fin*1.1f);
                    }
                });

                landParticleTimer = 0f;
            }
        }

        @Override
        public float zoomLaunch() {
            Core.camera.position.set(this);
            float fin = Interp.pow3.apply(renderer.getLandTimeIn()+0.2f);
            return Scl.scl(4f+2f*(1-fin));
        }

        @Override
        public Music launchMusic() {
            return SvMusic.land;
        }

        @Override
        public Music landMusic() {
            return SvMusic.land;
        }

        @Override
        public void handleItem(Building source, Item item) {
            if(!bannedItems.contains(item) && item != Items.copper) super.handleItem(source, item);
            else incinerateEffect(this, source);
        }
    }

}
