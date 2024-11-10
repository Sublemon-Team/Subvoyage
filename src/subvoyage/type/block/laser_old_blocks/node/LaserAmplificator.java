package subvoyage.type.block.laser_old_blocks.node;

import mindustry.content.Fx;
import mindustry.graphics.Pal;

public class LaserAmplificator extends LaserNode {

    public LaserAmplificator(String name) {
        super(name);
    }

    public class LaserNodeBuild extends LaserNode.LaserNodeBuild {
        @Override
        public void updateTile() {
            if(lasers == null) return;
            lasers.update(this);
            if(lasers.power() >= maxLaserPower) {
                Fx.hitMeltdown.create(x,y,0, Pal.accent,new Object());
                damage(10);
            }
            lasers.graph.consumers.each(consumer -> {
                if(consumer instanceof LaserBlockBuilding lb) {
                    lb.lasers.setPower(lasers.power());
                }
            });
        }
    }

}
