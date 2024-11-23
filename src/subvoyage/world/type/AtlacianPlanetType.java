package subvoyage.world.type;

import arc.func.Prov;
import arc.graphics.Blending;
import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.g3d.Camera3D;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.GenericMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;
import mindustry.type.Sector;

public class AtlacianPlanetType extends Planet {
    public Prov<GenericMesh> additional;
    public GenericMesh addMesh;
    public AtlacianPlanetType(String name, Planet parent, float radius, int i) {
        super(name, parent, radius,i);

    }

    @Override
    public void load() {
        super.load();
        addMesh = additional.get();
    }

    @Override
    public void updateBaseCoverage() {
        for(Sector sector : sectors){
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
    public final Mat3D mat = new Mat3D();

    PlanetParams params;

    @Override
    public void draw(PlanetParams params, Mat3D projection, Mat3D transform) {
        this.params = params;
        super.draw(params, projection, transform);
    }

    @Override
    public void drawAtmosphere(Mesh atmosphere, Camera3D cam) {
        //atmosphere does not contribute to depth buffer
        Gl.depthMask(false);

        Blending.additive.apply();

        Shaders.atmosphere.camera = cam;
        Shaders.atmosphere.planet = this;
        Shaders.atmosphere.bind();
        Shaders.atmosphere.apply();

        atmosphere.render(Shaders.atmosphere, Gl.triangles);
        addMesh.render(params,cam.combined,this.getTransform(mat));

        Blending.normal.apply();

        Gl.depthMask(true);
    }
}
