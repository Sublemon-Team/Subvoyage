package subvoyage.content.blocks.energy;

import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.modules.PowerModule;

public class EnergyDockPowerModule extends PowerModule {
    public int transferTime = 60;
    public PowerGraph graph = new EnergyDockPowerGraph();
    public EnergyDockPowerModule() {
        super.graph = this.graph;
        ((EnergyDockPowerGraph) this.graph).transferTime = transferTime;
    }
}
