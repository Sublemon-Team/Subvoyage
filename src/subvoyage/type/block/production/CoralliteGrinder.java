package subvoyage.type.block.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Image;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.world.Tile;

import mindustry.world.blocks.distribution.Duct;
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
import subvoyage.core.draw.SvRender;

import java.util.Objects;

import static mindustry.Vars.world;

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
        if(tile == null) return false;
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

            if(output() != null)
                for (var output : output())
                    for (int i = 0; i < output.amount; i++)
                        offload(output.item);

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        @Override
        public void dumpOutputs() {
            if(output() != null && timer(timerDump, dumpTime / timeScale)){
                for(ItemStack output : output()){
                    dump(output.item);
                }
            }

            if(outputLiquids != null){
                for(int i = 0; i < outputLiquids.length; i++){
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                    dumpLiquid(outputLiquids[i].liquid, 2f, dir);
                }
            }
        }

        public ItemStack[] output() {
            return useSulfur() ? sulfurVariant : outputItems;
        }

        @Override
        public void draw() {
            if(useSulfur()) sulfurDrawer.draw(this);
            else super.draw();

            Seq<Building> neighbors = new Seq<>();
            int baseX = tile.x;
            int baseY = tile.y;

            for(int dx = 0; dx < size; dx++){
                neighbors.add(world.build(baseX + dx, baseY + size));
                neighbors.add(world.build(baseX + dx, baseY - 1));
            }
            for(int dy = 0; dy < size; dy++){
                neighbors.add(world.build(baseX + size, baseY + dy));
                neighbors.add(world.build(baseX - 1, baseY + dy));
            }

            Draw.z(SvRender.Layer.overlayUI);
            Draw.color(Color.scarlet);
            neighbors.select(Objects::nonNull).select(e -> !(e instanceof Pump.PumpBuild) && e.block.outputsLiquid && e.front() == this)
                .each(e -> {
                    Draw.rect(Icon.cancel.getRegion(), e.x, e.y);
                });
            Draw.color();
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
