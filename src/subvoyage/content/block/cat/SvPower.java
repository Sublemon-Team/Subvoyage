package subvoyage.content.block.cat;

import mindustry.type.Category;
import mindustry.world.Block;
import subvoyage.type.block.power.node.PowerBubbleNode;

import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.block.SvBlocks.atl;

public class SvPower {
    public static Block
        powerBubbleNode
    ;

    public static void load() {
        powerBubbleNode = new PowerBubbleNode("power-bubble-node") {{
            requirements(Category.power,atl(),with(iridium,20,corallite,4));
            size = 1;
        }};
    }
}
