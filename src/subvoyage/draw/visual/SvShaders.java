package subvoyage.draw.visual;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.gl.*;
import arc.util.*;
import mindustry.graphics.*;
import subvoyage.SvVars;

import static mindustry.Vars.*;
import static mindustry.graphics.Shaders.getShaderFi;


public class SvShaders{
    public static SurfaceShader hardWater;
    public static SurfaceShader underwaterRegion;
    public static SurfaceShader bloom;

    public static boolean useDefaultBuffer = false;

    public static CacheLayer.ShaderLayer hardWaterLayer;
    public static CacheLayer.ShaderLayer bloomLayer;

    public static Fi file(String name){
        return tree.get("shaders/" + name);
    }

    public static void init(){
        hardWater = new SurfaceShader("hard-water");
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
                    Texture heatTex = SvVars.underwaterMap.toTexture();
                    if(heatTex == null) return;

                    heatTex.bind(2);
                    if(SvVars.effectBuffer != null) SvVars.effectBuffer.getTexture().bind(0);
                    else renderer.effectBuffer.getTexture().bind(0);

                    setUniformi("u_distortmap", 2);
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
