package subvoyage.core.draw.mesh;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import subvoyage.core.draw.shader.SvShaders;

public class AuroraMesh extends PlanetMesh{
    public final Mesh mesh;
    public TextureRegion region;
    public Texture texture;
    public Color color = Color.white.cpy();
    float speed;
    public AuroraMesh(TextureRegion region, Planet planet, int sides, float radiusIn, float off, float speed, Vec3 axis){
        this.planet = planet;
        this.region = region;

        MeshUtils.begin(sides * 6/*points amount*/ * (3/*pos*/ + 3/*normal*/ + 2/*texCords*/) * 2/*top and bottom normal*/);

        Tmp.v33.setZero();
        this.speed = speed;
        class MeshPoint{
            final Vec3 position;
            final Vec2 textureCords;

            public MeshPoint(Vec3 position, Vec2 textureCords){
                this.position = position;
                this.textureCords = textureCords;
            }
        }

        MeshPoint[] meshPoints = {
                new MeshPoint(new Vec3(), new Vec2(0,0)),
                new MeshPoint(new Vec3(), new Vec2(1,0)),
                new MeshPoint(new Vec3(), new Vec2(1,1)),
                new MeshPoint(new Vec3(), new Vec2(0,1)),
        };

        int[] order = {0, 1, 2, 2, 3, 0};
        Vec3 plane = new Vec3()
                .set(1, 0, 0)
                .rotate(Vec3.X, 90)
                .rotate(Vec3.X, axis.angle(Vec3.X) + 1)
                .rotate(Vec3.Y, axis.angle(Vec3.Y) + 1)
                .rotate(Vec3.Z, axis.angle(Vec3.Z) + 1)
                .crs(axis)
                .add(0,0.3f,0);

        Vec3 inv = axis.cpy().unaryMinus();

        for(int i = 0; i < sides; i++){
            float len = 0.12f;
            float lenMult = 1.2f;
            float offsetY = (1f+Mathf.sin((float) i/sides*Mathf.pi,1f,0.05f)+Mathf.random(0.0f,0.01f))+off;
            float offXZ = 1f+Mathf.sin((float) i/sides*Mathf.pi*5f * 20f,Mathf.randomSeed(sides,5,10),0.15f);
            meshPoints[0].position
                    .set(plane)
                    .rotate(axis, i * 1f / sides * 360)
                    .setLength2(len)
                    .scl(offXZ,offsetY,offXZ)
                    .scl(radiusIn);

            meshPoints[1].position
                    .set(plane)
                    .rotate(axis, i * 1f / sides * 360)
                    .setLength2(len*lenMult)
                    .scl(radiusIn)
                    //.scl(1/lenMult,0f,1/lenMult)
            ;

            meshPoints[2].position
                    .set(plane)
                    .rotate(axis, (i + 1f) / sides * 360)
                    .setLength2(len*lenMult)
                    .scl(radiusIn)
                    //.scl(1/lenMult,0f,1/lenMult)
            ;

            meshPoints[3].position
                    .set(plane)
                    .rotate(axis, (i + 1f) / sides * 360)
                    .setLength2(len)
                    .scl(offXZ,offsetY,offXZ)
                    .scl(radiusIn);

            for(int j : order){
                MeshPoint point = meshPoints[j];
                MeshUtils.vert(point.position, axis, point.textureCords);
            }
            for(int j = order.length - 1; j >= 0; j--){
                MeshPoint point = meshPoints[order[j]];
                MeshUtils.vert(point.position, inv, point.textureCords);
            }
        }

        mesh = MeshUtils.end();
    }
    static Mat3D mat = new Mat3D();
    public float relRot(){
        return Time.globalTime * speed / 40f;
    }

    @Override
    public void render(PlanetParams params, Mat3D projection, Mat3D transform){
        //don't waste performance rendering 0-alpha
        //if(params.planet == planet && Mathf.zero(1f - params.uiAlpha, 0.01f)) return;

        preRender(params);
        if(texture == null){
            texture = new Texture(Core.atlas.getPixmap(region).crop());
        }
        texture.setWrap(Texture.TextureWrap.repeat);

        Blending.additive.apply();

        Shader shader = shader();
        shader.bind();
        shader.setUniformMatrix4("u_proj", projection.val);
        shader.setUniformMatrix4("u_trans", mat.setToTranslation(planet.position).rotate(Vec3.Y, planet.getRotation() + relRot()).val);
        shader.setUniformf("u_color", color.hue(Time.globalTime/40f % 1f));
        setPlanetInfo("u_sun_info", planet.solarSystem);
        setPlanetInfo("u_planet_info", planet);
        texture.bind(0);
        shader.setUniformi("u_texture", 0);
        shader.apply();
        Gl.disable(Gl.cullFace);
        mesh.render(shader, Gl.triangles);
        Gl.enable(Gl.cullFace);

        Blending.normal.apply();
    }

    @Override
    public void preRender(PlanetParams params){
        SvShaders.planetTextureShader.planet = planet;
        SvShaders.planetTextureShader.lightDir
                .set(planet.solarSystem.position)
                .sub(planet.position)
                .rotate(Vec3.Y, planet.getRotation())
                .nor();
        SvShaders.planetTextureShader.ambientColor
                .set(planet.solarSystem.lightColor);
        //TODO: better disappearing
        //SvShaders.planetTextureShader.alpha = params.planet == planet ? 1f - params.uiAlpha : 1f;
    }

    private void setPlanetInfo(String name, Planet planet){
        Vec3 position = planet.position;
        Shader shader = shader();
        shader.setUniformf(name, position.x, position.y, position.z, planet.radius);
    }

    private static Shader shader(){
        return SvShaders.planetTextureShader;
    }
}
