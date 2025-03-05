package subvoyage.core.draw.shader;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.*;
import arc.math.geom.Vec3;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.type.Planet;
import subvoyage.core.SvVars;
import subvoyage.core.draw.SvRender;

import static mindustry.Vars.*;
import static mindustry.graphics.Shaders.getShaderFi;


public class SvShaders{
    public static SurfaceShader hardWater;
    public static SurfaceShader powerBubbles;
    public static SurfaceShader underwaterRegion;
    public static SurfaceShader bloom;

    public static boolean useDefaultBuffer = false;

    public static CacheLayer.ShaderLayer hardWaterLayer;
    public static CacheLayer.ShaderLayer bloomLayer;
    public static PlanetTextureShader planetTextureShader;

    public static Fi file(String name){
        return tree.get("shaders/" + name);
    }

    public static void init(){
        hardWater = new SurfaceShader("hard-water");
        planetTextureShader = new PlanetTextureShader();
        underwaterRegion = new SurfaceShader("underwater-region") {

            @Override
            public String textureName() {
                return "noise";
            }

            @Override
            public void apply() {
                setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
                setUniformf("u_resolution", Core.camera.width, Core.camera.height);
                setUniformf("u_time", Time.time);

                setUniformf("u_ww",world.width()*tilesize);
                setUniformf("u_wh",world.height()*tilesize);

                setUniformf("u_opacity",Core.settings.getInt("sv-metal-fuming-opacity")/100f);

                if(hasUniform("u_noise")){
                    if(noiseTex == null){
                        noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                    }

                    noiseTex.bind(1);
                    if(SvVars.effectBuffer != null) SvVars.effectBuffer.getTexture().bind(0);
                    else renderer.effectBuffer.getTexture().bind(0);

                    setUniformi("u_noise", 1);
                }
                if(hasUniform("u_distortmap")){
                    Texture heatTex = SvVars.atlacianMapControl.toTexture();
                    if(heatTex == null) return;

                    heatTex.bind(2);
                    if(SvVars.effectBuffer != null) SvVars.effectBuffer.getTexture().bind(0);
                    else renderer.effectBuffer.getTexture().bind(0);

                    setUniformi("u_distortmap", 2);
                }
            }
        };
        powerBubbles = new SurfaceShader("powerbubbles") {

            @Override
            public String textureName() {
                return "noise";
            }

            @Override
            public void apply() {
                setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
                setUniformf("u_resolution", Core.camera.width, Core.camera.height);
                setUniformf("u_time", Time.time);

                setUniformf("u_ww",world.width()*tilesize);
                setUniformf("u_wh",world.height()*tilesize);

                //setUniformf("u_opacity",Core.settings.getInt("sv-metal-fuming-opacity")/100f);

                if(hasUniform("u_noise")){
                    if(noiseTex == null){
                        noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                    }

                    noiseTex.bind(1);
                    if(SvRender.buffer != null) SvRender.buffer.getTexture().bind(0);
                    else renderer.effectBuffer.getTexture().bind(0);

                    setUniformi("u_noise", 1);
                }
            }
        };
        bloom = new SurfaceShader("bloom");
        CacheLayer.addLast(
                hardWaterLayer = new CacheLayer.ShaderLayer(hardWater),
                bloomLayer = new CacheLayer.ShaderLayer(bloom)
        );
    }

    public static void dispose(){
        if(!headless){
            hardWater.dispose();
        }
    }

    public static class PlanetTextureShader extends SvLoadShader {
        public Vec3 lightDir = new Vec3(1, 1, 1).nor();
        public Color ambientColor = Color.white.cpy();
        public Vec3 camDir = new Vec3();
        public float alpha = 1f;
        public Planet planet;

        public PlanetTextureShader(){
            super("circle-mesh", "circle-mesh");
        }

        @Override
        public void apply(){
            camDir.set(renderer.planets.cam.direction).rotate(Vec3.Y, planet.getRotation());

            setUniformf("u_alpha", alpha);
            setUniformf("u_lightdir", lightDir);
            setUniformf("u_ambientColor", ambientColor.r, ambientColor.g, ambientColor.b);
            setPlanetInfo("u_sun_info", planet.solarSystem);
            setPlanetInfo("u_planet_info", planet);
            setUniformf("u_camdir", camDir);
            setUniformf("u_campos", renderer.planets.cam.position);
        }

        private void setPlanetInfo(String name, Planet planet){
            Vec3 position = planet.position;
            Shader shader = this;
            shader.setUniformf(name, position.x, position.y, position.z, planet.radius);
        }
    }
    public static class SvLoadShader extends Shader{

        public SvLoadShader(String fragment, String vertex){
            super(
                    file(vertex + ".vert"),
                    file(fragment + ".frag")
            );
        }

        public void set(){
            Draw.shader(this);
        }

        @Override
        public void apply(){
            super.apply();

            setUniformf("u_time_millis", System.currentTimeMillis() / 1000f * 60f);
        }
    }

    public static class SurfaceShader extends Shader{
        Texture noiseTex;

        public SurfaceShader(String frag){
            super(getShaderFi("screenspace.vert"), file(frag + ".frag"));
            loadNoise();
        }

        public SurfaceShader(String vertRaw, String fragRaw){
            super(vertRaw, fragRaw);
            loadNoise();
        }

        public String textureName(){
            return "noise";
        }

        public void loadNoise(){
            Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(TextureFilter.linear);
                t.setWrap(TextureWrap.repeat);
            };
        }

        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

            setUniformf("u_ww",world.width()*tilesize);
            setUniformf("u_wh",world.height()*tilesize);

            if(hasUniform("u_noise")){
                if(noiseTex == null){
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }
    }
}
