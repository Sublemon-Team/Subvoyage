package subvoyage.draw.block;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.world.Block;
import mindustry.world.draw.DrawTurret;

public class SvDrawTurret extends DrawTurret {

    public SvDrawTurret(String s) {
        super(s);
    }

    @Override
    public void getRegionsToOutline(Block block, Seq<TextureRegion> out) {
        super.getRegionsToOutline(block, out);
        if(block.region.found() && !(block.outlinedIcon > 0 && block.outlinedIcon < block.getGeneratedIcons().length && block.getGeneratedIcons()[block.outlinedIcon].equals(block.region))){
            out.add(block.region);
        }
    }

    @Override
    public void load(Block block) {
        preview = Core.atlas.find(block.name + "-preview", block.region);
        outline = Core.atlas.find(block.name + "-outline");
        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
        heat = Core.atlas.find(block.name + "-heat");
        base = Core.atlas.find(block.name + "-base");

        for(var part : parts){
            part.turretShading = true;
            part.load(block.name);
        }

        //TODO test this for mods, e.g. exotic
        if(!base.found() && block.minfo.mod != null) base = Core.atlas.find(block.minfo.mod.name + "-" + basePrefix + "block-" + block.size);
        if(!base.found()) base = Core.atlas.find(basePrefix + "block-" + block.size);
    }
}
