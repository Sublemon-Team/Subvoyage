package subvoyage.type.block.production;

import arc.*;
import arc.func.Intc2;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.production.Drill;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import java.util.*;

import static arc.graphics.g2d.Draw.*;
import static mindustry.Vars.*;

public class Sifter extends Block {

    //optimization reasons
    HashMap<Integer,Item> populatedOres = new HashMap<>();
    public DrawBlock drawer = new DrawDefault();


    public float harvestTime = 60f;
    public float liquidOutput = 10/60f;
    public int oreSearchRadius = 5;

    public Sifter(String name) {
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
            () -> item != null ? item.localizedName : Core.bundle.format("bar.noresources"),
            () -> item != null ? item.color : Color.white,
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
    }

    public Item getPopulatedOreItem(Tile tile) {
        List<Item> candidates = new ArrayList<>();
        if(tile == null) return null;
        for(int dx = -oreSearchRadius; dx <= oreSearchRadius; dx++){
            for(int dy = -oreSearchRadius; dy <= oreSearchRadius; dy++){
                ((Intc2) (x, y) -> {
                    Tile cTile = world.tile(x,y);
                    if(cTile == null) return;
                    boolean sol = cTile.solid();
                    if(!sol) return;
                    Item drop = cTile.wallDrop();
                    if(drop != null) candidates.add(drop);
                }).get(tile.x+dx,tile.y+dy);
            }
        }
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
    }

    @Nullable
    public Item getPopulatedOreItemCached(int x, int y) {
        int pos = Point2.pack(x,y);
        Item item;
        if(!populatedOres.containsKey(pos)) populatedOres.put(pos,getPopulatedOreItem(world.tile(x,y)));
        item = populatedOres.get(pos);
        return item;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Item toDraw = getPopulatedOreItemCached(x,y);

        boolean hasNoWater = !world.tile(x,y).floor().isLiquid;
        boolean hasNearby = hasNearbySelf(world.tile(x,y));
        boolean hasNoOre = toDraw == null || toDraw.fullIcon.texture == null;

        boolean error = hasNoWater || hasNearby || hasNoOre;

        Color oreColor = hasNoOre ? Pal.remove : toDraw.color;

        Seq<String> errors = Seq.with();
        if(hasNoWater) errors.add("sifter.error.no-water");
        if(hasNearby) errors.add("sifter.error.nearby");
        if(hasNoOre) errors.add("sifter.error.no-ore");

        z(Layer.block+2);
        Drawf.square((x+size/4f)*tilesize,(y+size/4f)*tilesize,oreSearchRadius*tilesize*2-20,0f,
                error ? Pal.remove : oreColor);

        drawErrorInfo(size,x*tilesize,y*tilesize,errors);

        if(toDraw != null && !error) {
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / harvestTime, 2), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(toDraw.fullIcon, dx, dy - 1, s, s);
            Draw.reset();
            Draw.rect(toDraw.fullIcon, dx, dy, s, s);
        }

        /*
        if(toDraw == null || toDraw.fullIcon.texture == null) {
            Draw.z(Layer.block+2);
            Drawf.dashSquare(Pal.redDust,(x+size/4f)*tilesize,(y+size/4f)*tilesize,oreSearchRadius*tilesize*2);
            WorldLabel.drawAt(Core.bundle.get("water-sifter.place.message"), (x + size / 4f) * tilesize, (y + size / 4f + size - 0.1f) * tilesize,
            Layer.block + 3, WorldLabel.flagOutline, 0.8f);

            color(Color.scarlet);
            Draw.rect(Icon.cancel.getRegion(), (x+size/4f)*tilesize,(y+size/4f+size-0.1f+0.5f)*tilesize);
        } else {
            Draw.z(Layer.block+2);
            Drawf.dashSquare(Pal.accent,(x+size/4f)*tilesize,(y+size/4f)*tilesize,oreSearchRadius*tilesize*2);
            Draw.rect(toDraw.fullIcon,(x+size/4f)*tilesize,(y+size/4f+size-0.1f+0.5f)*tilesize, itemSize*1.25f, itemSize*1.25f);
            WorldLabel.drawAt(toDraw.localizedName,(x+size/4f)*tilesize,(y+size/4f+size-0.1f)*tilesize,
                    Layer.block+3,WorldLabel.flagOutline,0.8f);
            Draw.color();
        }*/
    }

    public static void drawErrorInfo(int size, float x, float y, Seq<String> infos) {
        if(infos.isEmpty()) return;

        //x += 60/2f + 15f;
        //y += 4f;
        x += 4f;
        y += infos.size * 7f / 2f + size * tilesize - 2f;

        z(Layer.overlayUI);
        color(Color.black,0.7f);

        Fill.rect(x,y,70,infos.size * 7f);

        y += infos.size * 3f - 2f;


        int i = 0;
        for (String info : infos) {
            WorldLabel.drawAt("-",x - 70/2f + 3f,y - i * 6,
                    Layer.overlayUI+1,WorldLabel.flagOutline,0.8f);
            WorldLabel.drawAt(Core.bundle.get(info),x,y - i * 6,
                    Layer.overlayUI+1,WorldLabel.flagOutline,0.8f);
            i++;
        }

        z(Layer.overlayUI+2f);
        color(Color.scarlet);
        rect(Icon.cancel.getRegion(), x, y + 3f);
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
            if(item == null) return;

            progress += getProgressIncrease(getHarvestTime());
            float liq = liquidOutput * edelta() - item.hardness*0.8f/60f;
            if(liquids.get(Liquids.water) < liquidCapacity) liquids.add(Liquids.water,liq);
            if(progress >= 1f) {
                progress %= 1f;
                harvest();
                consume();
            }
            dumpOutputs();
        }

        public float getHarvestTime() {
            Item item = getPopulatedOreItemCached(tile.x,tile.y);
            return item != null ? harvestTime + item.hardness * 15f : 0;
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
