package subvoyage.content.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Strings;
import mindustry.content.Liquids;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.WorldLabel;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Drill;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.*;
import subvoyage.content.blocks.crude_smelter.CrudeSmelterRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mindustry.Vars.*;

public class WaterSifter extends Block {

    //optimization reasons
    HashMap<Integer,Item> populatedOres = new HashMap<>();
    public DrawBlock drawer = new DrawDefault();


    public float harvestTime = 60f;
    public float liquidOutput = 10/60f;
    public int oreSearchRadius = 9;

    public WaterSifter(String name) {
        super(name);
        destructible = true;
        update = true;
        solid = true;

        hasItems = true;
        hasLiquids = true;
        hasPower = true;

        sync = true;
    }


    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.mineSpeed,60f/harvestTime, StatUnit.itemsSecond);
        stats.add(Stat.output, StatValues.liquid(Liquids.water,liquidOutput*60f,true));
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("harvestspeed", (WaterSifterBuild e) ->
                new Bar(() -> Core.bundle.format("bar.harvestspeed", Strings.fixed(60f/e.getHarvestTime() * e.efficiency, 2)), () -> Pal.ammo, () -> e.efficiency));
        addBar("itemprogress",(WaterSifterBuild e) -> {
            Item item = getPopulatedOreItemCached(e.tileX(),e.tileY());
            return new Bar(
                    () -> item.localizedName,
                    () -> item.color,
                    () -> e.progress
            );
        });
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }
    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.floor().isLiquid && !hasNearbySelf(tile) && getPopulatedOreItemCached(tile.x,tile.y) != null;
    }
    public boolean hasNearbySelf(Tile tile) {
        return isSelf(tile,0) || isSelf(tile,1) || isSelf(tile,2) || isSelf(tile,3);
    }
    public boolean isSelf(Tile tile, int dir) {
        return switch (dir) {
            case 0 -> tile.nearby(2,0) != null && tile.nearby(2,0).block() == this;
            case 1 -> tile.nearby(0,2) != null && tile.nearby(0,2).block() == this;
            case 2 -> tile.nearby(-1,0) != null && tile.nearby(-1,0).block() == this;
            case 3 -> tile.nearby(0,-1) != null && tile.nearby(0,-1).block() == this;
            default -> throw new IllegalStateException("Unexpected value: " + dir);
        };
    };


    public Item getPopulatedOreItem(Tile tile) {
        List<Item> candidates = new ArrayList<>();
        if(tile == null) return null;
        Geometry.circle(tile.x,tile.y,oreSearchRadius,(x,y) -> {
            Tile cTile = world.tile(x,y);
            if(cTile == null) return;
            boolean sol = cTile.solid();
            if(!sol) return;
            Item drop = cTile.wallDrop();
            if(drop != null) candidates.add(drop);
        });
        if(candidates.isEmpty()) return null;
        Map<Item,Integer> itemCounts = new HashMap<>();
        int frequentCount = 0;
        Item frequentItem = null;
        for (Item candidate : candidates) {
            if(candidate == null) continue;
            itemCounts.put(candidate, itemCounts.getOrDefault(candidate, 0) + 1);
            int v = itemCounts.get(candidate);
            if(frequentItem == candidate) continue;
            if(v >= frequentCount) {
                frequentItem = candidate;
                frequentCount = v;
            }
        }
        return frequentItem;
    };

    public Item getPopulatedOreItemCached(int x, int y) {
        int pos = Point2.pack(x,y);
        Item item;
        if(!populatedOres.containsKey(pos)) populatedOres.put(pos,getPopulatedOreItem(world.tile(x,y)));
        item = populatedOres.get(pos);
        return item;
    };

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Item toDraw = getPopulatedOreItemCached(x,y);
        if(toDraw == null || toDraw.fullIcon.texture == null) {
            Draw.z(Layer.block+2);
            Drawf.dashCircle((x+size/4f)*tilesize,(y+size/4f)*tilesize,oreSearchRadius*tilesize, Pal.redDust);
        } else {
            Draw.z(Layer.block+2);
            Drawf.dashCircle((x+size/4f)*tilesize,(y+size/4f)*tilesize,oreSearchRadius*tilesize, Pal.accent);
            Draw.rect(toDraw.fullIcon,(x+size/4f)*tilesize,(y+size/4f+size-0.1f+0.5f)*tilesize, itemSize*1.25f, itemSize*1.25f);
            WorldLabel.drawAt(toDraw.localizedName,(x+size/4f)*tilesize,(y+size/4f+size-0.1f)*tilesize,
                    Layer.block+3,WorldLabel.flagOutline,0.8f);
            Draw.color();
        }
    }

    public void worldReset() {
        populatedOres.clear();
    }

    public class WaterSifterBuild extends Building {

        public float progress = 0f;

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }


        @Override
        public void updateTile() {
            super.updateTile();
            Item item = getPopulatedOreItemCached(tile.x,tile.y);
            progress += getProgressIncrease(getHarvestTime());
            float liq = liquidOutput * edelta() - item.hardness*0.8f/60f;
            liquids.add(Liquids.water,liq);
            consume();
            if(progress >= 1f) {
                progress %= 1f;
                harvest();
            }
            dumpOutputs();
        }

        public float getHarvestTime() {
            Item item = getPopulatedOreItemCached(tile.x,tile.y);
            return harvestTime+item.hardness*15f;
        }


        public void dumpOutputs() {
            Item item = getPopulatedOreItemCached(tile.x,tile.y);
            if(item != null && timer(timerDump, dumpTime / timeScale)){
                dump(item);
            }
            dumpLiquid(Liquids.water);
        }

        private void harvest() {
            Item item = getPopulatedOreItemCached(tile.x,tile.y);
            offload(item);
        }

        @Override
        public float warmup() {
            return efficiency;
        }
    }

}
