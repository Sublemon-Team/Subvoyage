package subvoyage.type.block.laser_blocks;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import subvoyage.type.block.laser.LaserBlock;

public class LaserBlaster extends LaserBlock {
    public DrawBlock drawer;
    public BulletType bulletType;
    public float shootDuration;
    public float shootDelay;
    public LaserBlaster(String name) {
        super(name);
        rotate = true;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
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
            Point2 dir = Geometry.d4[rotation];
            float dx = dir.x,dy = dir.y;
            dx *= Vars.tilesize;
            dy *= Vars.tilesize;
            bulletType.create(this,team,x+dx,y+dy,rotation*90f,laserEfficiency()*bulletType.damage,1f,1f,laserEfficiency());
        }

        @Override
        public float warmup() {
            return rawLaserEfficiency()*((lastShoot / (shootDelay+shootDuration)));
        }
    }
}
