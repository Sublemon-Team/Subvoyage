package subvoyage.draw.block;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawRegion;

public class DrawOutline extends DrawRegion {

    public TextureRegion outline;

    public DrawOutline(String s) {
        super(s);
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{outline,region};
    }

    @Override
    public void getRegionsToOutline(Block block, Seq<TextureRegion> out) {
        super.getRegionsToOutline(block, out);
        if(block.region.found() && !(block.outlinedIcon > 0 && block.outlinedIcon < block.getGeneratedIcons().length && block.getGeneratedIcons()[block.outlinedIcon].equals(block.region))){
            out.add(block.region);
        }
    }
    @Override
    public void draw(Building build) {
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(outline.found()){
            Draw.z(layer-0.01f);
            Draw.rect(outline, build.x, build.y, build.drawrot());
            Draw.z(layer);
        }
        if(spinSprite){
            Drawf.spinSprite(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
        }else{
            Draw.rect(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
        }
        Draw.z(z);
    }

    @Override
    public void load(Block block) {
        super.load(block);
        outline = Core.atlas.find(block.name + "-outline");
    }
}
