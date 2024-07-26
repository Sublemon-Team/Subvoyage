package subvoyage.type.block.laser;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import subvoyage.content.other.SvPal;

import static mindustry.Vars.tilesize;

public class LaserBlock extends Block {

    public TextureRegion laserRegion;
    public TextureRegion laserTopRegion;
    public TextureRegion laserStartRegion;

    private float consumeLaserPower = 0f;
    public float minLaserEfficiency = 0f;

    public float outputLaserPower = 0f;
    public static float maxLaserPower = 300f;

    public LaserBlock(String name) {
        super(name);
        destructible = true;
        update = true;
        solid = true;
        sync = true;
    }

    public void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2, float scl){
        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f - 1.5f, len2 = size2 * tilesize / 2f - 1.5f;

        Draw.blend(Blending.additive);
        Drawf.laser(laserRegion, laserStartRegion, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, scl);
        Draw.color();
        Drawf.laser(laserTopRegion, laserStartRegion, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, scl);
        Draw.blend(Blending.normal);
    }

    @Override
    public void load() {
        super.load();
        laserRegion = Core.atlas.find(name+"-laser","subvoyage-power-laser");
        laserTopRegion = Core.atlas.find(name+"-laser-top","subvoyage-power-laser-top");
        laserStartRegion = Core.atlas.find(name+"-laser-start","subvoyage-power-laser-start");
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("laser_power",(b) -> {
            if(b instanceof LaserBlockBuilding lb) {
                return new Bar(
                        () -> Core.bundle.format("bar.sv_laser_power", Strings.fixed(lb.lasers.power(), 1)),
                        () -> LaserUtil.getLaserColor(lb.lasers.power()),
                        () -> 1f);
            } else return new Bar(
                    () -> Core.bundle.format("bar.sv_laser_power", Strings.fixed(0f, 1)),
                    () -> LaserUtil.getLaserColor(0f),
                    () -> 1f);
        });
    }

    public void consumeLaserPower(float power) {
        consumeLaserPower = power;
    }

    public class LaserBlockBuilding extends Building {
        @Nullable
        public LaserModule lasers;

        @Override
        public void updateTile() {
            super.updateTile();
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

        @Override
        public void onRemoved() {
            lasers.graph.clearGraph(this);
        }
        @Override
        public void onDestroyed() {
            lasers.graph.clearGraph(this);
        }

        @Override
        public void created() {
            lasers = new LaserModule();
        }

        public boolean isConsumer() {
            return true;
        }
        public boolean isSupplier() {
            return false;
        }
        @Override
        public float efficiency() {
            if(lasers == null) return 0f;
            return lasers.smoothEfficiency * laserEfficiency();
        }

        public float laserEfficiency() {
            float raw = rawLaserEfficiency();
            return raw >= minLaserEfficiency ? raw : 0f;
        }
        public float rawLaserEfficiency() {
            return consumeLaserPower <= 0f ? 1f : Mathf.clamp(lasers.power()/consumeLaserPower);
        }
    }
}
