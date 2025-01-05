package subvoyage.type.block.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Structs;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import subvoyage.core.ui.advancements.Advancement;

import static mindustry.Vars.world;

public class Diffuser extends Block {
    protected @Nullable ConsumeItems consItems;

    public Item[] results;
    public float craftTime;
    public Diffuser(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        hasLiquids = true;
        sync = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("harvestspeed", (DiffuserBuild e) ->
                new Bar(() -> Core.bundle.format("bar.harvestspeed", Strings.fixed(60f/craftTime * decrease(e.efficiency,e.diffuserK), 2)), () -> Pal.ammo, () -> decrease(e.efficiency,e.diffuserK)));
    }

    public static float decrease(float x, int n) {
        return x/(1+ Mathf.pow(n,2f)/31.62f);
    }

    @Override
    public void setStats(){
        stats.timePeriod = craftTime;
        super.setStats();

        stats.add(Stat.output, StatValues.items(item -> Structs.contains(results, i -> i == item)));
        stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
    }

    @Override
    public void init(){
        super.init();
        consItems = findConsumer(c -> c instanceof ConsumeItems);
    }

    public class DiffuserBuild extends Building {
        public float progress;
        public float totalProgress;
        public float warmup;
        public int seed;
        public int itemIndex;

        public int diffuserK = 1;
        public int tileChanges = -2;

        @Override
        public void created(){
            seed = Mathf.randomSeed(tile.pos(), 0, Integer.MAX_VALUE - 1);
            tileChanges = world.tileChanges;
            Seq<DiffuserBuild> builds = Seq.with();
            for (Tile tile : world.tiles) if(tile.build instanceof DiffuserBuild db && db.added && db.team == team)
                builds.addUnique(db);
            diffuserK = builds.size;
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }

        @Override
        public boolean shouldConsume(){
            int total = items.total();
            //very inefficient way of allowing separators to ignore input buffer storage
            if(consItems != null){
                for(ItemStack stack : consItems.items){
                    total -= items.get(stack.item);
                }
            }
            return total < itemCapacity && enabled;
        }

        @Override
        public void draw(){
            Draw.rect(this.block.region, this.x, this.y, this.drawrot());

            this.drawTeamTop();
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float progress(){
            return progress;
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        @Override
        public void updateTile(){
            if(tileChanges != world.tileChanges) {
                tileChanges = world.tileChanges;
                Seq<DiffuserBuild> builds = Seq.with();
                for (Tile tile : world.tiles) if(tile.build instanceof DiffuserBuild db && db.added && db.team == team)
                    builds.addUnique(db);
                diffuserK = builds.size;
            }
            
            totalProgress += warmup * delta();

            if(efficiency > 0){
                progress += getProgressIncrease(craftTime);
                warmup = Mathf.lerpDelta(warmup, 1f, 0.02f);
            }else{
                warmup = Mathf.lerpDelta(warmup, 0f, 0.02f);
            }

            if(progress >= 1f){
                progress %= 1f;

                consume();

                Item item = results[itemIndex%results.length];
                if(item != null && items.get(item) < itemCapacity){
                    offload(item);
                    itemIndex++;
                }
            }

            if(timer(timerDump, dumpTime)){
                dump();
            }

        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return progress;
            return super.sense(sensor);
        }

        @Override
        public float edelta() {
            return decrease(this.efficiency,diffuserK) * this.delta();
        }

        @Override
        public boolean canDump(Building to, Item item){
            return !consumesItem(item);
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);
            write.i(seed);
            write.i(itemIndex);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            if(revision == 1 || revision == 2) seed = read.i();
            if(revision == 2) itemIndex = read.i();
        }
    }
}
