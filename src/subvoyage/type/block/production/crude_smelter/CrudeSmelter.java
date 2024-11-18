package subvoyage.type.block.production.crude_smelter;

import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.units.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import java.util.*;

import static mindustry.Vars.content;

public class CrudeSmelter extends GenericCrafter {
    public HashMap<Item,CrudeSmelterRecipe> recipes = new HashMap<>();

    public CrudeSmelter(String name) {
        super(name);
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        config(Item.class, (CrudeSmelterBuild tile, Item item) -> tile.produceItem = item);
        configClear((CrudeSmelterBuild tile) -> tile.produceItem = null);
    }

    @Override
    public void setStats() {
        //stats.timePeriod = craftTime;
        super.setStats();
        /*if((hasItems && itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }*/
        for (CrudeSmelterRecipe v : recipes.values()) {
            stats.add(Stat.output,StatValues.items(v.craftTime,v.outputItem));
        }
        /*if(outputItems != null){
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }

        if(outputLiquids != null){
            stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
        }*/
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(plan, plan.config, "center", true);
    }

    public static HashMap<Item,CrudeSmelterRecipe> recipes(Object... T) {
        HashMap<Item,CrudeSmelterRecipe> recipes = new HashMap<>();
        for (int i = 0; i < T.length; i++) {
            Item itemA = (Item) T[i];
            int countA = (int) T[++i];
            int craftTimeA = (int) T[++i];
            recipes.put(itemA,new CrudeSmelterRecipe() {{
                craftTime = craftTimeA;
                outputItem = new ItemStack(itemA,countA);
            }});
        }
        return recipes;
    }


    public class CrudeSmelterBuild extends GenericCrafterBuild {

        public @Nullable Item produceItem;

        @Override
        public void drawConfigure() {
            super.drawConfigure();
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(CrudeSmelter.this,table,Seq.with(recipes.keySet()),() -> produceItem,this::configure,selectionRows,selectionColumns);
        }

        @Override
        public Item config() {
            return produceItem;
        }

        @Override
        public void updateTile() {
            CrudeSmelterRecipe recipe = recipes.getOrDefault(produceItem,recipes.values().iterator().next());
            if(efficiency > 0 && produceItem != null){
                progress += getProgressIncrease(recipe.craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            totalProgress += warmup * Time.delta;

            if(progress >= 1f && produceItem != null){
                craft();
            }

            dumpOutputs();
        }

        @Override
        public void craft() {
            consume();
            CrudeSmelterRecipe recipe = recipes.getOrDefault(produceItem,recipes.values().iterator().next());
            if(recipe.outputItem != null){
                for(int i = 0; i < recipe.outputItem.amount; i++){
                    offload(recipe.outputItem.item);
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        @Override
        public void dumpOutputs() {
            if(produceItem != null && timer(timerDump, dumpTime / timeScale)){
                CrudeSmelterRecipe recipe = recipes.getOrDefault(produceItem,recipes.values().iterator().next());
                dump(recipe.outputItem.item);
            }
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(produceItem == null ? -1 : produceItem.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            produceItem = content.item(read.s());
            if(revision == 1) {
                new DirectionalItemBuffer(20).read(read);
            }
        }
    }
}
