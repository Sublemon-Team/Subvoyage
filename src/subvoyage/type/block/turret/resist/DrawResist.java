package subvoyage.type.block.turret.resist;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import java.util.Iterator;

public class DrawResist extends DrawBlock {
    protected static final Rand rand = new Rand();
    public Seq<DrawPart> parts = new Seq();
    public String basePrefix = "";
    @Nullable
    public Liquid liquidDraw;
    public TextureRegion base;
    public TextureRegion liquid;
    public TextureRegion top;
    public TextureRegion heat;
    public TextureRegion preview;
    public TextureRegion outline;

    public DrawResist(String basePrefix) {
        this.basePrefix = basePrefix;
    }

    public DrawResist() {
    }

    public void getRegionsToOutline(Block block, Seq<TextureRegion> out) {
        Iterator var3 = this.parts.iterator();

        while(var3.hasNext()) {
            DrawPart part = (DrawPart)var3.next();
            part.getOutlines(out);
        }

        if (block.region.found() && (block.outlinedIcon <= 0 || block.outlinedIcon >= block.getGeneratedIcons().length || !block.getGeneratedIcons()[block.outlinedIcon].equals(block.region))) {
            out.add(block.region);
        }

        block.resetGeneratedIcons();
    }

    public void draw(Building build) {
        ResistTurret turret = (ResistTurret) build.block;
        ResistTurret.PowerRingTurretBuild tb = (ResistTurret.PowerRingTurretBuild)build;
        Draw.rect(this.base, build.x, build.y);
        Draw.color();
        Draw.z(49.5F);
        Drawf.shadow(this.preview, build.x + tb.recoilOffset.x - turret.elevation, build.y + tb.recoilOffset.y - turret.elevation, tb.drawrot());
        Draw.z(50.0F);
        this.drawTurret(turret, tb);
        this.drawHeat(turret, tb);
        if (this.parts.size > 0) {
            if (this.outline.found()) {
                Draw.z(49.99F);
                Draw.rect(this.outline, build.x + tb.recoilOffset.x, build.y + tb.recoilOffset.y, tb.drawrot());
                Draw.z(50.0F);
            }

            float progress = tb.progress();
            DrawPart.PartParams params = DrawPart.params.set(build.warmup(), 1.0F - progress, 1.0F - progress, tb.heat, tb.curRecoil, 0, tb.x + tb.recoilOffset.x, tb.y + tb.recoilOffset.y, tb.rotation);
            Iterator var6 = this.parts.iterator();

            while(var6.hasNext()) {
                DrawPart part = (DrawPart)var6.next();
                params.setRecoil(tb.curRecoil);
                part.draw(params);
            }
        }

    }

    public void drawTurret(ResistTurret block, ResistTurret.PowerRingTurretBuild build) {
        if (block.region.found()) {
            Draw.rect(block.region, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot());
        }

        if (this.liquid.found()) {
            Liquid toDraw = this.liquidDraw == null ? build.liquids.current() : this.liquidDraw;
            Drawf.liquid(this.liquid, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.liquids.get(toDraw) / block.liquidCapacity, toDraw.color.write(Tmp.c1).a(1.0F), build.drawrot());
        }

        if (this.top.found()) {
            Draw.rect(this.top, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot());
        }

    }

    public void drawHeat(ResistTurret block, ResistTurret.PowerRingTurretBuild build) {
        if (!(build.heat <= 1.0E-5F) && this.heat.found()) {
            Drawf.additive(this.heat, block.heatColor.write(Tmp.c1).a(build.heat), build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot(), 50.1F);
        }
    }

    public void load(Block block) {
        if (!(block instanceof ResistTurret)) {
            throw new ClassCastException("This drawer can only be used on power ring turrets.");
        } else {
            this.preview = Core.atlas.find(block.name + "-preview", block.region);
            this.outline = Core.atlas.find(block.name + "-outline");
            this.liquid = Core.atlas.find(block.name + "-liquid");
            this.top = Core.atlas.find(block.name + "-top");
            this.heat = Core.atlas.find(block.name + "-heat");
            this.base = Core.atlas.find(block.name + "-base");
            Iterator var2 = this.parts.iterator();

            while(var2.hasNext()) {
                DrawPart part = (DrawPart)var2.next();
                part.turretShading = true;
                part.load(block.name);
            }

            if (!this.base.found() && block.minfo.mod != null) {
                this.base = Core.atlas.find(block.minfo.mod.name + "-" + this.basePrefix + "block-" + block.size);
            }

            if (!this.base.found()) {
                this.base = Core.atlas.find(this.basePrefix + "block-" + block.size);
            }

        }
    }

    public TextureRegion[] icons(Block block) {
        return this.top.found() ? new TextureRegion[]{this.base, this.preview, this.top} : new TextureRegion[]{this.base, this.preview};
    }
}
