package subvoyage.type.block.production;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Image;
import arc.util.Eachable;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.world.Tile;

import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import subvoyage.content.SvItems;
import subvoyage.content.block.SvEnvironment;
import subvoyage.core.anno.LoadAnnoProcessor.LoadAnno;

public class CoralliteGrinder extends GenericCrafter {
    public int maxLiquidTiles = 3;
    public ItemStack[] sulfurVariant = new ItemStack[0];
    public DrawBlock sulfurDrawer;
    public CoralliteGrinder(String name) {
        super(name);
    }

    public @LoadAnno("@-sulfur") TextureRegion sulfurTx;

    @Override
    public void load() {
        super.load();
        sulfurDrawer.load(this);
    }

    @Override
    public void setStats() {
        super.setStats();

        this.stats.remove(Stat.output);

        if(outputItems != null){
            stats.add(Stat.output, table -> {
                table.margin(8f);
                table.row();
                table.table(t -> {
                    t.add(new Image(Core.atlas.find("subvoyage-hard-water-edge"))).padRight(5f);
                    t.label(() -> Core.bundle.get("rules.anyenv") + " [gray]>[]").padRight(20f);
                    StatValues.items(craftTime, outputItems).display(t);
                }).margin(8f).left();
                table.row();
                table.table(t -> {
                    t.add(new Image(Core.atlas.find("subvoyage-sulfur-sand1"))).padRight(5f);
                    t.label(() -> SvEnvironment.sulfurSand.localizedName + " [gray]>[]").padRight(20f);
                    StatValues.items(craftTime, sulfurVariant).display(t);
                }).margin(8f).left();
            });
        }
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        if(useSulfur(plan.tile())) sulfurDrawer.drawPlan(this,plan,list);
        else super.drawPlanRegion(plan,list);
    }
    public boolean useSulfur(Tile tile) {
        return tile.getLinkedTilesAs(this,tempTiles).sum(other -> other.floor().itemDrop == SvItems.sulfur ? 1 : 0) > 1;
    }
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.getLinkedTilesAs(this, tempTiles).sum((other) -> other.floor().isLiquid ? 1 : 0) <= maxLiquidTiles;
    }

    public class CoralliteRefinerBuild extends GenericCrafterBuild {
        @Override
        public void craft() {
            consume();

            var items = useSulfur() ? sulfurVariant : outputItems;

            if(items != null){
                for(var output : items){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        @Override
        public void draw() {
            if(useSulfur()) sulfurDrawer.draw(this);
            else super.draw();
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if(source instanceof Pump.PumpBuild) return super.acceptLiquid(source,liquid);
            return false;
        }
        public boolean useSulfur() {
            return tile.getLinkedTilesAs(block,tempTiles).sum(other -> other.floor().itemDrop == SvItems.sulfur ? 1 : 0) > 1;
        }
        @Override
        public boolean acceptItem(Building source, Item item) {
            return false;
        }
    }
}
