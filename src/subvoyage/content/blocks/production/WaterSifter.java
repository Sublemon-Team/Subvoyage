package subvoyage.content.blocks.production;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.game.MapObjectives;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.WorldLabel;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mindustry.Vars.*;

public class WaterSifter extends Block {

    public int oreSearchRadius = 9;
    //optimization reasons
    HashMap<Integer,Item> populatedOres = new HashMap<>();
    public DrawBlock drawer = new DrawDefault();
    public float craftTime;

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
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }
    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.floor().isLiquid && !hasNearbySelf(tile) && populatedOres.getOrDefault(Point2.pack(tile.x,tile.y),null) != null;
    }
    public boolean hasNearbySelf(Tile tile) {
        return isSelf(tile,0) || isSelf(tile,1) || isSelf(tile,2) || isSelf(tile,3);
    }
    public boolean isSelf(Tile tile, int dir) {
        return switch (dir) {
            case 0 -> tile.nearby(2,0).block() == this;
            case 1 -> tile.nearby(0,2).block() == this;
            case 2 -> tile.nearby(-1,0).block() == this;
            case 3 -> tile.nearby(0,-1).block() == this;
            default -> throw new IllegalStateException("Unexpected value: " + dir);
        };
    };


    public Item getPopulatedOreItem(Tile tile) {
        List<Item> candidates = new ArrayList<>();
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

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        int pos = Point2.pack(x,y);
        Item toDraw;
        if(!populatedOres.containsKey(pos)) populatedOres.put(pos,getPopulatedOreItem(world.tile(x,y)));
        toDraw = populatedOres.get(pos);
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
        }
    }

}
