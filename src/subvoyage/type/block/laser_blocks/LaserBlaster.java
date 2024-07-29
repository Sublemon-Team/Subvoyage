package subvoyage.type.block.laser_blocks;

import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import subvoyage.type.block.laser.LaserBlock;

public class LaserBlaster extends LaserBlock {
    public DrawTurret drawer;
    public BulletType bulletType;
    public float shootDuration;
    public float shootDelay;
    public LaserBlaster(String name) {
        super(name);
        rotate = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, bulletType)));
    }

    @Override
    protected TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    public class LaserBlasterBuild extends LaserBlockBuilding {
        float lastShoot;

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void drawLight() {
            drawer.draw(this);
        }

        @Override
        public void update() {
            super.update();
            if(lastShoot > 0) lastShoot -= Time.delta*laserEfficiency();
            else shoot();
        }

        public void shoot() {
            lastShoot += shootDelay+shootDuration;
            if(laserEfficiency() <= 0) return;
            bulletType.create(this,team,x,y,rotation*90f,laserEfficiency()*bulletType.damage,1f,1f,laserEfficiency());
        }
    }
}
