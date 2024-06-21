package subvoyage.content.world.blocks.energy;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import arc.util.Time;
import arc.util.noise.Simplex;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.meta.Attribute;
import subvoyage.content.world.blocks.offload_core.CoreDecoder;

import static mindustry.Vars.state;

public class WindTurbine extends SolarGenerator {
    public WindTurbine(String name) {
        super(name);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("efficiency", (WindTurbineBuild e) ->
                new Bar(() -> Core.bundle.format("bar.efficiency", Strings.fixed(e.productionEfficiency*100, 1)), () -> Pal.powerBar, () -> e.productionEfficiency));
    }

    public class WindTurbineBuild extends SolarGenerator.SolarGeneratorBuild {
        @Override
        public void updateTile(){
            if(state.rules.sector == null) return;
            float value = Mathf.clamp(Simplex.noise3d(state.rules.sector.id,2,0.6,1/100f,x,Time.time/60/2,y)*2);
            productionEfficiency = enabled ? value : 0;
        }
    }
}
