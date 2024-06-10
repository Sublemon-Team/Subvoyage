package oceanic_dust.content.world.blocks.energy;

import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.modules.PowerModule;

public class EnergyDockPowerModule extends PowerModule {
    public PowerGraph graph = new EnergyDockPowerGraph();
    public EnergyDockPowerModule() {
        super.graph = this.graph;
    }
}
