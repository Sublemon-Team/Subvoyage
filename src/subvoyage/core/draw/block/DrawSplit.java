package subvoyage.core.draw.block;

import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import subvoyage.core.SvSettings;

public class DrawSplit extends DrawBlock {

    public DrawBlock drawerLow;
    public DrawBlock drawer;

    public boolean splitAssets = false;

    public DrawSplit(DrawBlock low, DrawBlock def) {
        drawerLow = low;
        drawer = def;
    }
    public DrawSplit(DrawBlock def) {
        drawerLow = new DrawDefault();
        drawer = def;
    }
    public DrawSplit(DrawBlock... other) {
        drawerLow = new DrawDefault();
        drawer = new DrawMulti(other);
    }

    public static DrawSplit withLow(DrawBlock... other) {
        return new DrawSplit(new DrawMulti(other),new DrawDefault());
    }
    public static DrawSplit withLow() {
        return new DrawSplit(new DrawDefault());
    }
    public DrawSplit with(DrawBlock... other) {
        this.drawer = new DrawMulti(other);
        return this;
    }
    public DrawSplit splitAssets() {
        splitAssets = true;
        return this;
    }

    @Override
    public void draw(Building build) {
        current().draw(build);
    }

    @Override
    public void drawLight(Building build) {
        current().drawLight(build);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        current().drawPlan(block,plan,list);
    }

    @Override
    public void getRegionsToOutline(Block block, Seq<TextureRegion> out) {
        drawerLow.getRegionsToOutline(block,out);
        drawer.getRegionsToOutline(block,out);
    }

    @Override
    public void load(Block block) {
        drawerLow.load(block);
        drawer.load(block);
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return drawer.icons(block); // regular drawer would probably not make impact on this
    }

    public DrawBlock current() {
        return SvSettings.drawerMode() ? drawer : drawerLow;
    }
}
