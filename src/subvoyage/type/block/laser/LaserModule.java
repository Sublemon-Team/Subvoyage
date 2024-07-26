package subvoyage.type.block.laser;

import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.util.Time;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.world.modules.BlockModule;
import mindustry.world.modules.PowerModule;

public class LaserModule extends BlockModule {
    public LaserGraph graph = new LaserGraph();
    public IntSeq links = new IntSeq();
    private float laserPower = 0f;
    private float supplierLaserPower = 0f;
    public float smoothEfficiency = 0f;

    public void update(Building build) {
        graph.update(build);
        smoothEfficiency = Mathf.lerp(smoothEfficiency,build.efficiency, Time.delta/20f);
        if(build.block instanceof LaserBlock lb) laserPower = lb.outputLaserPower*smoothEfficiency+supplierLaserPower;
        supplierLaserPower = 0;
    }

    @Override
    public void write(Writes write) {
        //we don't need to write current power because it's not a battery
    }

    public float power() {
        return laserPower;
    }

    public void setPower(float power) {
        supplierLaserPower+=power;
    }
}
