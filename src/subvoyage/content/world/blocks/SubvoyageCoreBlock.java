package subvoyage.content.world.blocks;

import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.blocks.storage.CoreBlock;
import subvoyage.content.SvMusic;

import static arc.Core.settings;

public class SubvoyageCoreBlock extends CoreBlock {
    public Seq<Item> bannedItems = new Seq<>();
    public SubvoyageCoreBlock(String name) {
        super(name);
    }


    public class  SubvoyageCoreBuild extends CoreBuild {
        @Override
        public void handleStack(Item item, int amount, Teamc source) {
            if(!bannedItems.contains(item) && item != Items.copper) super.handleStack(item, amount, source);
            else Fx.coreBurn.at(x, y);
        }

        @Override
        public void update() {
            super.update();
            items.remove(Items.copper,1000);
            SvMusic.theAtlacian.setVolume(settings.getInt("musicvol") / 150f);
        }

        @Override
        public void handleItem(Building source, Item item) {
            if(!bannedItems.contains(item) && item != Items.copper) super.handleItem(source, item);
            else incinerateEffect(this, source);
        }
    }

}
