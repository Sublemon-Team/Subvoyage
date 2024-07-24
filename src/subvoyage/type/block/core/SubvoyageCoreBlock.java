package subvoyage.type.block.core;

import arc.struct.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.storage.*;
import subvoyage.content.sound.SvMusic;

import static arc.Core.settings;

public class SubvoyageCoreBlock extends CoreBlock {
    public Seq<Item> bannedItems = new Seq<>();
    public SubvoyageCoreBlock(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();
        lightRadius = 30f + 20f * (size+1);
        fogRadius = Math.max(fogRadius, (int)(lightRadius / 8f * 3f) + 13);
        emitLight = true;
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
