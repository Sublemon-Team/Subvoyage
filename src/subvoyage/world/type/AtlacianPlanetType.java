package subvoyage.world.type;

import arc.math.Mathf;
import mindustry.type.Planet;
import mindustry.type.Sector;

public class AtlacianPlanetType extends Planet {
    public AtlacianPlanetType(String name, Planet parent, float radius, int i) {
        super(name, parent, radius,i);
    }

    @Override
    public void updateBaseCoverage() {
        for(Sector sector : sectors){
            //TODO: make this accurate
            float sum = 1f;
            for(Sector other : sector.near()){
                if(other.generateEnemyBase || other.info.attack){
                    sum += 0.9f;
                }
            }

            if(sector.hasEnemyBase() || sector.info.attack){
                sum += 0.88f;
            }

            sector.threat = sector.preset == null ? Math.min(sum / 5f, 1.2f) : Mathf.clamp(sector.preset.difficulty / 10f);
        }
    }
}
