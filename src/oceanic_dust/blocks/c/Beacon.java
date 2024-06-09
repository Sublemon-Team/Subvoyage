package oceanic_dust.blocks.c;

import arc.graphics.g2d.Draw;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.logic.Ranged;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.units.RepairTurret;
import mindustry.world.meta.Env;

public class Beacon extends RepairTurret {

    public Beacon(String name) {
        super(name);
        size = 3;
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

            Draw.rect(region, x, y, (t * 90) % 360);

            if(shouldConsume()) Draw.scl();

            drawBeam(x, y, rotation, length, id, target, team, strength,
                    pulseStroke, pulseRadius, beamWidth, lastEnd, offset, laserColor, laserTopColor,
                    laser, laserEnd, laserTop, laserTopEnd);
        }
    }
}
