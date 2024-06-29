package subvoyage.content.blocks.editor.decoration;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.world.*;

public class TreeBlock extends Block{
    public float shadowOffset = -3f;
    public float shadowAlpha = 0.6f;

    public int lobesMin = 7, lobesMax = 7;
    public float sclMin = 30f, sclMax = 50f, magMin = 5f, magMax = 15f, timeRange = 40f, spread = 0f;

    static Rand rand = new Rand();

    public TreeBlock(String name){
        super(name);
        solid = true;
        clipSize = 90;
        customShadow = true;
    }

    @Override
    public void init(){
        super.init();
        hasShadow = true;
    }

    @Override
    public void drawBase(Tile tile){
        rand.setSeed(tile.pos());
        float offset = rand.random(180f);
        int lobes = rand.random(lobesMin, lobesMax);
        for(int i = 0; i < lobes; i++){
            float ba = i / (float)lobes * 360f + offset + rand.range(spread), angle = ba + Mathf.sin(Time.time + rand.random(0, timeRange), rand.random(sclMin, sclMax), rand.random(magMin, magMax));
            Draw.z(Layer.power - 1);
            Draw.color(0f, 0f, 0f, shadowAlpha);
            Draw.rect(variants > 0 ? variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))] : customShadowRegion,
            tile.worldx() + shadowOffset, tile.worldy() + shadowOffset, angle);

            Draw.color();

            Draw.z(Layer.power + 1);
            Draw.rect(variants > 0 ? variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))] : region,
            tile.worldx(), tile.worldy(), angle);
        }
    }

    @Override
    public void drawShadow(Tile tile){

    }

    @Override
    public TextureRegion[] icons(){
        return variants == 0 ? super.icons() : new TextureRegion[]{Core.atlas.find(name + "1")};
    }
}
