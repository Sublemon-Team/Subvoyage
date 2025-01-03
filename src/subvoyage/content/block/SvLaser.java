package subvoyage.content.block;

import arc.struct.IntSeq;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import subvoyage.type.block.laser.blocks.LaserGenerator;
import subvoyage.type.block.laser.nodes.LaserAmplifier;
import subvoyage.type.block.laser.nodes.LaserNode;
import subvoyage.type.block.laser.nodes.LaserSplitter;

import static mindustry.type.ItemStack.with;
import static subvoyage.content.SvItems.*;
import static subvoyage.content.SvBlocks.atl;

public class SvLaser {
    public static Block
        laserProjector, luminescentProjector, quartzProjector, laserSource, laserNode, phosphideLaserNode, tugLaserNode, laserAmplificator, laserSplitter;

    public static void load() {
        laserProjector = new LaserGenerator("laser-projector") {{
            requirements(Category.crafting, atl(), with(iridium, 300, chrome, 200, spaclanium, 150));
            laserOutput = 10f;
            outputRange = 5;

            maxSuppliers = 0;

            outputs = IntSeq.with(0);

            size = 3;
            squareSprite = false;
            consumePower(1.3f);
        }};

        luminescentProjector = new LaserGenerator("luminescent-projector") {{
            requirements(Category.crafting, atl(), with(iridium, 300, phosphide, 200, chrome, 50));
            laserOutput = 60f;
            maxSuppliers = 0;
            size = 3;
            squareSprite = false;
            outputRange = 6;
            outputs = IntSeq.with(0);
            itemDuration = 60f*4f;
            consumeItem(phosphide,1);
            consumePower(1.5f);
        }};
        quartzProjector = new LaserGenerator("quartz-projector") {{
            requirements(Category.crafting, atl(), with(quartzFiber, 80, phosphide, 80, chrome, 50));
            laserOutput = 360f;
            maxSuppliers = 0;
            size = 3;
            squareSprite = false;
            outputRange = 10;
            outputs = IntSeq.with(0);
            itemDuration = 60f*5f;
            consumeItem(quartzFiber,4);
            consumePower(5.2f);
        }};


        laserSource = new LaserGenerator("laser-source") {{
            requirements(Category.crafting, atl(BuildVisibility.sandboxOnly), with());
            laserOutput = 1000f;
            maxSuppliers = 0;
            size = 3;
            squareSprite = false;
            outputRange = 32;
            outputs = IntSeq.with(0);
        }};

        laserNode = new LaserNode("laser-node") {{
            requirements(Category.crafting, atl(), with(iridium, 30, chrome, 30));
            size = 3;
            maxSuppliers = 1;
            squareSprite = false;

            inputRange = 8;
            outputRange = 8;

            capacity = 60;

            outputs = IntSeq.with(0);
            inputs = IntSeq.with(1,2,3);
        }};
        phosphideLaserNode = new LaserNode("phosphide-laser-node") {{
            requirements(Category.crafting, atl(), with(phosphide, 30, chrome, 30));
            size = 3;
            maxSuppliers = 1;
            squareSprite = false;

            inputRange = 16;
            outputRange = 16;

            capacity = 300;

            outputs = IntSeq.with(0);
            inputs = IntSeq.with(1,2,3);
        }};
        tugLaserNode = new LaserNode("tug-laser-node") {{
            requirements(Category.crafting, atl(), with(tugSheet, 10, phosphide, 30));
            size = 3;
            maxSuppliers = 1;
            squareSprite = false;

            inputRange = 24;
            outputRange = 24;

            capacity = 1000;

            outputs = IntSeq.with(0);
            inputs = IntSeq.with(1,2,3);
        }};

        laserAmplificator = new LaserAmplifier("laser-amplifier") {{
            requirements(Category.crafting, atl(), with(iridium, 80, chrome, 80, spaclanium, 10));
            size = 3;
            squareSprite = false;

            maxSuppliers = 3;

            inputRange = 16;
            outputRange = 16;
            outputs = IntSeq.with(0);
            inputs = IntSeq.with(1,2,3);
        }};

        laserSplitter = new LaserSplitter("laser-splitter") {{
            requirements(Category.crafting, atl(), with(iridium, 50, chrome, 40, spaclanium, 10));
            size = 3;
            maxSuppliers = 1;

            squareSprite = false;
            inputRange = 8;
            outputRange = 16;

            outputs = IntSeq.with(1,3);
            inputs = IntSeq.with(2);
        }};
    }
}
