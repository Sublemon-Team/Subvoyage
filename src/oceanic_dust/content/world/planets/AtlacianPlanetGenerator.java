package oceanic_dust.content.world.planets;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.Vars.*;
import static oceanic_dust.content.blocks.ODWorldBlocks.*;

public class AtlacianPlanetGenerator extends PlanetGenerator {
    float ocean = -0.4f;
    public static float liqThresh = 0.64f, liqScl = 37f;
    Block[][] arr = {
    {Blocks.carbonStone, legartyteStone, agaryteStone, Blocks.carbonStone, legartyteStone, Blocks.carbonStone, agaryteStone, Blocks.carbonStone, Blocks.carbonStone}
    };

    {
        defaultLoadout = ODLoadouts.basicPuffer;
        baseSeed = 1;
    }

    @Override
    public void generateSector(Sector sector){

    }

    @Override
    public float getHeight(Vec3 position) {
        return getRawHeight(position);
    }

    private float getRawHeight(Vec3 position) {
        float noise = Simplex.noise3d(seed,4,0.7,1f,position.x,position.y,position.z);
        float waveNoise = Ridged.noise3d(seed,position.x/6f+noise*4,position.y,position.z+Math.sin(noise)*6,1/4f);
        float actualNoise = noise * waveNoise;
        return Math.max(actualNoise,ocean);
    }

    @Override
    public Color getColor(Vec3 position) {
        float wiggle = (float) Math.sin(position.len()/10f);
        float noise = Simplex.noise3d(seed*2,2,0.6,1/4f,position.x+wiggle,position.y+wiggle,position.z);
        float noiseG = Simplex.noise3d(seed*2,3,0.8,1f,position.x+wiggle+noise,position.y+wiggle,position.z);
        float invertMask = Simplex.noise3d(seed,3,0.4,1f,position.z,position.y,position.x);

        float r = 100+(noiseG*noise*wiggle*40);
        float g = 120+(noiseG*80);
        float b = 190+(noise*wiggle*wiggle*50);

        if(getRawHeight(position) <= ocean) return Color.valueOf("4265AF");
        if(invertMask > 0.7) return  Color.white;
        if(invertMask > 0.4) return Color.cyan;
        return Color.rgb((int) r, (int) g, (int) b);
    }

    @Override
    public void addWeather(Sector sector, Rules rules){
        //no weather... yet
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);

        if(tile.floor == Blocks.redmat && rand.chance(0.25)){
            tile.block = Blocks.redweed;
        }

        if(tile.floor == Blocks.bluemat && rand.chance(0.35)){
            tile.block = Blocks.purbush;
        }

        if(tile.floor == Blocks.bluemat && rand.chance(0.4)){
            tile.block = Blocks.yellowCoral;
        }
    }

    @Override
    protected void generate(){
        median(2, 0.6);
        cells(4);
        pass((x, y) -> {
            if(floor == Blocks.beryllicStone && noise(x, y, 3, 0.4f, 13f, 1f) > 0.59f){
                block = Blocks.beryllicStoneWall;
            }

            if(floor == Blocks.carbonStone && noise(x, y, 6, 0.2f, 8f, 1f) > 0.6f){
                block = Blocks.carbonWall;
            }

            float max = 0;
            for(Point2 p : Geometry.d8){
                max = Math.max(max, world.getDarkness(x + p.x, y + p.y));
            }

            if(max > 0){
                block = floor.asFloor().wall;
            }

            if(floor != Blocks.carbonStone) return;
            if(nearWall(x, y)) return;
            float noise = noise(x + 300, y - x*1.6f + 100, 3, 0.8f, liqScl, 1f);
            if(noise > liqThresh){
                floor = Blocks.water;
            }
        });

        median(3, 0.6);
        distort(5f, 8f);
        blend(Blocks.water, Blocks.darksandWater, 4);
        blend(legartyteStone, darkLegartyteStone, 6);
        float length = width/2.6f;
        Vec2 trns = Tmp.v1.trns(rand.random(360f), length);
        int spawnX = (int)(trns.x + width/2f), spawnY = (int)(trns.y + height/2f), endX = (int)(-trns.x + width/2f), endY = (int)(-trns.y + height/2f);
        float maxd = Mathf.dst(width/2f, height/2f);

        erase(spawnX, spawnY, 15);
        brush(pathfind(spawnX, spawnY, endX, endY, tile -> (tile.solid() ? 300f : 0f) + maxd - tile.dst(width/2f, height/2f)/10f, Astar.manhattan), 9);
        erase(endX, endY, 15);
        Schematics.placeLaunchLoadout(width / 2, height / 2);
    }

    float rawHeight(Vec3 position){
        return Simplex.noise3d(seed, 8, 0.7f, 1f, position.x, position.y, position.z);
    }

    Block getBlock(Vec3 position){
        float height = rawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(2f);
        float temp = Simplex.noise3d(seed, 8, 0.6, 1f/2f, position.x, position.y + 99f, position.z);
        height *= 1.2f;
        height = Mathf.clamp(height);
        return arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
    }
}
