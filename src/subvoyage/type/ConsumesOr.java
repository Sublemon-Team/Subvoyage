package subvoyage.type;

import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;
import subvoyage.core.ui.PowerDisplay;

public class ConsumesOr extends Consume {

    public Seq<Consume> consumes;

    public ConsumesOr(Consume... consumes) {
        this.consumes = Seq.with(consumes);
    }

    @Override
    public void apply(Block block) {
        super.apply(block);
        consumes.each(e -> e.apply(block));
    }

    @Override
    public void build(Building build, Table table) {
        table.table(t -> {
            int i = 0;
            for (Consume consume : consumes) {
                if(consume instanceof ConsumePower cp) {
                    Image image = new Image(Icon.power.getRegion());
                    image.setColor(Pal.powerLight);
                    t.add(new ReqImage(image,
                            () -> cp.efficiency(build) > 0.01f)).size(24f, 32f).scaling(Scaling.fit);

                }
                else consume.build(build,t);
                if(i != consumes.size-1) t.label(() -> "/");
                i++;
            }
        }).left();
    }

    @Override
    public void display(Stats stats) {
        int i = 0;
        for (Consume consume : consumes) {
            if(consume instanceof ConsumePower cp) {
                stats.add(Stat.input, table -> {
                    table.add(new PowerDisplay(cp.usage)).padRight(5);
                });
            }
            else consume.display(stats);
            if(i != consumes.size-1) stats.add(Stat.input, table -> table.label(() -> "/"));
            i++;
        }
    }

    @Override
    public void update(Building build) {
        active(build).update(build);
    }

    @Override
    public void trigger(Building build) {
        active(build).trigger(build);
    }

    @Override
    public float efficiency(Building build) {
        return active(build).efficiency(build);
    }

    public Consume active(Building build) {
        return consumes.max(e -> e.efficiency(build));
    }
}
