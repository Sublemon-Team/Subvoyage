package oceanic_dust.content.world.blocks;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import mindustry.world.meta.*;

public class Beacon extends RepairTurret {

    public Beacon(String name) {
        super(name);
        size = 3;
        outlineColor = Pal.darkOutline;
        envDisabled |= Env.scorching;
        destructible = true;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.floor() == Blocks.water || tile.block() == Blocks.water;
    }

    public class BeaconBuild extends RepairPointBuild implements Ranged {

        @Override
        public float range() {
            return repairRadius;
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);

            float t = Time.time / 60f;

            Draw.z(Layer.turret);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);

            if(shouldConsume()) Draw.scl(1.2f);
            Drawf.spinSprite(region, x, y, (t * 90) % 360);

            if(shouldConsume()) Draw.scl();

            drawBeam(x, y, rotation, length, id, target, team, strength,
                    pulseStroke, pulseRadius, beamWidth, lastEnd, offset, laserColor, laserTopColor,
                    laser, laserEnd, laserTop, laserTopEnd);
        }
    }
}
