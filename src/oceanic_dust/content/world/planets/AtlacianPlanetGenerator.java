package oceanic_dust.content.world.planets;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.type.Weather.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.OreBlock;

import static mindustry.Vars.*;
import static oceanic_dust.content.blocks.ODWorldBlocks.*;

public class AtlacianPlanetGenerator extends PlanetGenerator {
    float ocean = -0.4f;
    public static float liqThresh = 0.2f, liqScl = 60f;
    Block[][] arr = {
    {Blocks.carbonStone, legartyteStone, agaryteStone,Blocks.carbonStone, legartyteStone, Blocks.carbonStone,
            agaryteStone, Blocks.carbonStone, Blocks.carbonStone,Blocks.carbonStone, legartyteStone,
            agaryteStone,Blocks.carbonStone,Blocks.carbonStone,legartyteStone, agaryteStone}
    };

    {
        defaultLoadout = ODLoadouts.basicPuffer;
        baseSeed = 1;
    }

    private Seq<Block> getOres() {
        Seq<Block> ores = Seq.with(oreSpaclanium,oreCorallite);
        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float scl = 1f;
        float addscl = 1.3f;

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.25f*addscl){
            ores.add(oreSulfur);
        }

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 1, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.35f*addscl){
            ores.add(oreIridium);
        }

        /*if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 2, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.7f*addscl){
            ores.add(Blocks.oreThorium);
        }*/
        return ores;
    };

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
        rules.weather.add(new WeatherEntry(Weathers.rain));
        rules.weather.add(new WeatherEntry(Weathers.fog));
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

        if(tile.block == Blocks.sandWall) tile.block = legartyteWall;
    }

    @Override
    protected void generate(){
        median(2, 0.6);
        cells(4);
        pass((x, y) -> {
            if(floor == legartyteStone && noise(x, y, 3, 0.4f, 12f, 0.8f) > 0.35f)
                block = legartyteWall;

            if(floor == Blocks.carbonStone && noise(x, y, 6, 0.5f, 16f, 0.8f) > 0.35f)
                block = Blocks.carbonWall;

            if(floor == Blocks.carbonStone && noise(x, y, 4, 0.5f, 13f, 0.8f) > 0.35f)
                block = noise(x, y, 2, 0.5f, 16f, 0.8f) > 0.2f ? agaryteWall : legartyteWall;

            if(floor == Blocks.carbonWall && noise(x, y, 2, 0.5f, 16f, 0.8f) > 0.4f)
                block = noise(x, y, 2, 0.5f, 16f, 0.8f) > 0.2f ? agaryteWall : legartyteWall;

            if(block == Blocks.sandWall)
                block = legartyteWall;

            float max = 0;
            for(Point2 p : Geometry.d8){
                max = Math.max(max, world.getDarkness(x + p.x, y + p.y));
            }

            if(max > 0){
                block = floor.asFloor().wall;
            }

            if(floor != Blocks.carbonStone) return;
            if(nearWall(x, y)) return;
            float noise = noise(-y + 300, -x + 100, 2, 0.5f, liqScl, 0.3f);
            float secondNoise = noise(y + 300, x - 100, 4, 0.5f, liqScl/2, 0.4f);
            float thirdNoise = noise(x,y,2,0.3f,10f,0.12f);
            float fourthNoise = Simplex.noise3d(seed,4,0.3,25,x,y,sector.id*10);
            if(noise > liqThresh || secondNoise < liqThresh*2){
                floor = Blocks.water;
            }
            if(thirdNoise > liqThresh/2) {
                block = Blocks.water;
                floor = Blocks.water;
            }
            if(fourthNoise > liqThresh/2) {
                floor = Blocks.water;
            }
        });

        pass((x, y) -> {
            if(block.solid) return;

            Vec3 v = sector.rect.project(x, y);

            float rr = Simplex.noise2d(sector.id, (float)2, 0.6f, 1f / 7f, x, y) * 0.1f;
            float value = Ridged.noise3d(2, v.x, v.y, v.z, 1, 1f / 55f) + rr - rawHeight(v) * 0f;
            float rrscl = rr * 44 - 2;
            if(value < 0.17f) return;
            //do not place rivers on ice, they're frozen
            //ignore pre-existing liquids
            floor = Blocks.water;
        });

        //blend(agaryteStone, Blocks.water, 6);
        median(5, 0.4);
        terrain(Blocks.carbonWall,69.86f,0.35f,1.1f);
        blend(legartyteStone, darkLegartyteStone, 6);

        float length = width/2.6f;
        Vec2 trns = Tmp.v1.trns(rand.random(360f), length);
        int spawnX = (int)(trns.x + width/2f), spawnY = (int)(trns.y + height/2f), endX = (int)(-trns.x + width/2f), endY = (int)(-trns.y + height/2f);
        float maxd = Mathf.dst(width/2f, height/2f);

        erase(spawnX, spawnY, 15);
        Seq<Tile> path = pathfind(spawnX, spawnY, endX, endY, tile -> (tile.solid() ? 300f : 0f) + maxd - tile.dst(width/2f, height/2f)/10f, Astar.manhattan);
        brush(path, 13);
        brushWithBlock(path,9, Blocks.water);
        erase(endX, endY, 15);

        blend(Blocks.water, Blocks.darksandWater, 4);

        distort(10f, 16f);

        Seq<Block> ores = getOres();
        FloatSeq frequencies = new FloatSeq();
        float poles = Math.abs(sector.tile.v.y);
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.1f, 0.01f) + i * 0.01f + poles * 0.04f);
        }
        pass((x,y) -> {
            int offsetX = x - 4, offsetY = y + 23;
            if(Mathf.within(x-spawnX,y-spawnY,6) && rand.chance(0.3)) {
                ore = oreSpaclanium;
                return;
            }
            for(int i = ores.size - 1; i >= 0; i--){
                Block entry = ores.get(i);
                float freq = frequencies.get(i);
                if(Math.abs(0.5f - noise(offsetX, offsetY + i*888, 2, 0.7, (10 + i * 2))) > 0.24f + i*0.01 &&
                        Math.abs(0.5f - noise(offsetX, offsetY - i*888, 1, 1, (15 + i * 4))) > 0.37f + freq){
                    ore = entry;
                    break;
                }
            }
        });



        Schematics.placeLaunchLoadout(spawnX, spawnY);
        tiles.get(endX,endY).setOverlay(Blocks.spawn);
    }


    public void brushWithBlock(Seq<Tile> path, int rad, Block block){
        path.each(tile -> eraseWithBlock(tile.x, tile.y, rad,block));
    }

    public void eraseWithBlock(int cx, int cy, int rad, Block block){
        for(int x = -rad; x <= rad; x++){
            for(int y = -rad; y <= rad; y++){
                int wx = cx + x, wy = cy + y;
                if(Structs.inBounds(wx, wy, width, height) && Mathf.within(x, y, rad)){
                    Tile other = tiles.getn(wx, wy);
                    if(block == Blocks.water) other.setFloor(block.asFloor());
                    else other.setBlock(block);
                }
            }
        }
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
        Block block = arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
        if(block == Blocks.sandWall) block = legartyteWall;
        float thirdNoise = noise(position.x,position.y,2,0.3f,10f,0.12f);
        if(thirdNoise > liqThresh) block = Blocks.water;
        return block;
    }
}
