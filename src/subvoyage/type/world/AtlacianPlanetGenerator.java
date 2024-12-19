package subvoyage.type.world;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.game.Schematics;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.blocks.environment.SteamVent;
import mindustry.world.blocks.environment.TallBlock;
import subvoyage.content.SvBlocks;
import subvoyage.content.other.SvLoadouts;
import subvoyage.core.draw.SvPal;

import static subvoyage.content.block.SvEnvironment.*;

public class AtlacianPlanetGenerator extends PlanetGenerator {

    //height
    public static float
            heightOct = 6,
            heightPers = 0.6f,
            heightScl = 1.3f,

            heightPow = 1.2f,
            heightVal = 1.5f
                    ;

    //block
    public static float
            airThresh = 0.5f, airScl = 1.2f,

            oceanLevel = 0.65f
            ;

    //liquid
    public static float liqThresh = 0.64f, liqScl = 87f;

    {
        baseSeed = 10;
        defaultLoadout = SvLoadouts.corePuffer;
    }

    Block[] terrain = {Blocks.water,Blocks.water,Blocks.water,Blocks.water,Blocks.water,darkLegartyteStone,darkLegartyteStone,darkLegartyteStone,legartyteStone,legartyteStone,legartyteStone,legartyteStone,agaryteStone,agaryteStone,archalyteStone,sodilateStone};
    Block[] visualTerrain = {Blocks.water,Blocks.water,Blocks.water,Blocks.water,Blocks.water,legartyteStone,legartyteStone,legartyteStone,agaryteStone,archalyteStone,sodilateStone};



    @Override
    public void generateSector(Sector sector) {

    }

    @Override
    public float getHeight(Vec3 position) {
        if(Mathf.pow(rawHeight(position),heightPow) * heightVal <= oceanLevel) return oceanLevel;
        return Math.min(Mathf.pow(rawHeight(position),heightPow) * heightVal + 0.1f,1.2f);
    }

    @Override
    public Color getColor(Vec3 position) {
        Block block = getVisualBlock(position);
        if(block == legartyteStone) return SvPal.legartyte.value(0.2f).saturation(0.5f);
        if(block == agaryteStone) return SvPal.agaryte.cpy().value(0.2f);
        if(block == archalyteStone) return SvPal.corallite.cpy().value(0.3f);
        if(block == sodilateStone) Tmp.c1.set(block.mapColor).a(1f - block.albedo).value(0.5f);
        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public float getSizeScl() {
        return 2000 * 1.07f * 6f / 5f;
    }

    float rawHeight(Vec3 position){
        return Simplex.noise3d(seed, heightOct, heightPers, 1f/heightScl, 10f + position.x, 10f + position.y, 10f + position.z);
    }
    float rawWeight(Vec3 position) {
        return position.dst(0, 0, 1)*2.2f - Simplex.noise3d(seed, 6, 0.35, 1.6f, 12f + position.x, 18f + position.y, 15f + position.z) * 2.9f;
    }

    Block getBlock(Vec3 position){
        float wgh = rawWeight(position);
        Tmp.v32.set(position);
        float height = rawHeight(position);
        Tmp.v31.set(position);

        height *= 1.5f;
        height = Mathf.clamp(height);

        Block result = terrain[Mathf.clamp((int)(height * terrain.length), 0, terrain.length - 1)];

        if(height <= oceanLevel-0.15f) {
            return Blocks.deepwater;
        } else if (height <= oceanLevel-0.05f) {
            return Blocks.water;
        } else if(height <= oceanLevel) {
            return Blocks.darksandWater;
        }

        /*if(height <= oceanLevel || wgh < 0.3 + Math.abs(Ridged.noise3d(seed + (int) waterSeed, position.x + 4f, position.y + 8f, position.z + 1f, (int) waterOct, waterScl)) * waterMag) {
            boolean sod = wgh < archalyteThresh - waterThresh && Ridged.noise3d(seed + (int) sodilateSeed, position.x + 2f, position.y + 8f, position.z + 1f, (int) sodilateOct, sodilateScl) > sodilateThresh;
            return sod ? (wgh > 0.15f ? hardWater : darkHardWater) : Blocks.water;
        }

        if(wgh < 0.6){
            if(result == legartyteStone || result == darkLegartyteStone || result == archalyteStone){
                return agaryteStone;
            }
        }
        */
        position = Tmp.v32;
        /*
        if(wgh < archalyteThresh - waterThresh && Ridged.noise3d(seed + (int) sodilateSeed, position.x + 2f, position.y + 8f, position.z + 1f, (int) sodilateOct, sodilateScl) > sodilateThresh){
            result = sodilateStone;
        }

        if(wgh > archalyteThresh){
            result = archalyteStone;
        }else if(wgh > archalyteThresh - 0.4f){
            result = darkArchalyteStone;
        }*/

        return result;
    }

    Block getVisualBlock(Vec3 position){
        float wgh = rawWeight(position);
        Tmp.v32.set(position);
        float height = rawHeight(position);
        Tmp.v31.set(position);

        height *= 1.5f;
        height = Mathf.clamp(height);

        Block result = visualTerrain[Mathf.clamp((int)(height * visualTerrain.length), 0, visualTerrain.length - 1)];

        if(height <= oceanLevel-0.15f) {
            return Blocks.deepwater;
        } else if (height <= oceanLevel-0.05f) {
            return Blocks.water;
        } else if(height <= oceanLevel) {
            return Blocks.darksandWater;
        }

        /*if(height <= oceanLevel || wgh < 0.3 + Math.abs(Ridged.noise3d(seed + (int) waterSeed, position.x + 4f, position.y + 8f, position.z + 1f, (int) waterOct, waterScl)) * waterMag) {
            boolean sod = wgh < archalyteThresh - waterThresh && Ridged.noise3d(seed + (int) sodilateSeed, position.x + 2f, position.y + 8f, position.z + 1f, (int) sodilateOct, sodilateScl) > sodilateThresh;
            return sod ? (wgh > 0.15f ? hardWater : darkHardWater) : Blocks.water;
        }

        if(wgh < 0.6){
            if(result == legartyteStone || result == darkLegartyteStone || result == archalyteStone){
                return agaryteStone;
            }
        }
        */
        position = Tmp.v32;
        /*
        if(wgh < archalyteThresh - waterThresh && Ridged.noise3d(seed + (int) sodilateSeed, position.x + 2f, position.y + 8f, position.z + 1f, (int) sodilateOct, sodilateScl) > sodilateThresh){
            result = sodilateStone;
        }

        if(wgh > archalyteThresh){
            result = archalyteStone;
        }else if(wgh > archalyteThresh - 0.4f){
            result = darkArchalyteStone;
        }*/

        return result;
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position.scl(8f));

        tile.block = tile.floor.asFloor().wall;
        if(tile.block == Blocks.sandWall)
            tile.block = legartyteWall;
        if(tile.block == Blocks.duneWall)
            tile.block = archalyteWall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, airScl) > airThresh || Simplex.noise3d(seed + 1, heightOct, heightPers, airScl, position.x, position.y, position.z) > airThresh){
            tile.block = Blocks.air;
        }

        if(Ridged.noise3d(seed + 2, position.x, position.y + 4f, position.z, 3, 0.2f) > 0.6){
            tile.floor = Blocks.water;
        }
    }

    @Override
    protected void generate() {

        cells(4);

        float length = width/2.6f;
        Vec2 trns = Tmp.v1.trns(rand.random(360f), length);
        int
                spawnX = (int)(trns.x + width/2f), spawnY = (int)(trns.y + height/2f),
                endX = (int)(-trns.x + width/2f), endY = (int)(-trns.y + height/2f);
        float maxd = Mathf.dst(width/2f, height/2f);

        erase(spawnX, spawnY, 15);
        brush(pathfind(spawnX, spawnY, endX, endY, tile -> (tile.solid() ? 300f : 0f) + maxd - tile.dst(width/2f, height/2f)/10f, Astar.manhattan), 9);
        erase(endX, endY, 15);

        pass((x, y) -> {
            if(floor != sodilateStone) return;

            if(nearWall(x, y)) return;

            float noise = noise(x + 300, y - x*1.6f + 100, 4, 0.8f, liqScl, 1f);

            if(noise > liqThresh){
                floor = darkHardWater;
            }
        });

        median(2, 0.6, sodilateStone);
        median(2, 0.6, darkHardWater);

        blend(darkHardWater, hardWater, 4);
        blend(sodilateStone, darkSodilateFloor, 4);

        distort(10f, 12f);
        distort(5f, 7f);

        median(2, 0.6, archalyteStone);

        inverseFloodFill(tiles.getn(spawnX, spawnY));

        blend(archalyteStone, darkArchalyteStone, 4);
        blend(legartyteStone, darkLegartyteStone, 4);


        erase(endX, endY, 6);
        tiles.getn(endX, endY).setOverlay(Blocks.spawn);

        pass((x, y) -> {
            if(block != Blocks.air) {
                if (nearAir(x, y)) {
                    if (block == sodilateWall && noise(x + 78, y, 4, 0.7f, 33f, 1f) > 0.52f) {
                        ore = wallOreSpaclanium;
                    } else if (block != sodilateWall && noise(x + 782, y, 4, 0.8f, 38f, 1f) > 0.665f) {
                        ore = wallOreCorallite;
                    }

                }
            }
            else if(!nearWall(x, y)){

                if (noise(x + 78, y, 4, 0.7f, 15f, 1f) > 0.72f) {
                    ore = oreSpaclanium;
                }

                if (noise(x + 782, y, 4, 0.8f, 20f, 1f) > 0.665f) {
                    ore = oreCorallite;
                }

                if(noise(x + 150, y + x*2 + 100, 4, 0.8f, 55f, 1f) > 0.76f){
                    ore = oreIridium;
                }

                if(noise(x + 999, y + 600 - x, 4, 0.63f, 45f, 1f) < 0.27f && floor == agaryteStone){
                    ore = oreChromium;
                }

            }

            if(block == Blocks.air && (floor == archalyteStone || floor == darkArchalyteStone) && rand.chance(0.09) && nearWall(x, y)
                    && !near(x, y, 4, archalyteSpikes)){
                block = archalyteSpikes;
                ore = Blocks.air;
            }

            if(block == sodilateWall && rand.chance(0.23) && nearAir(x, y) && !near(x, y, 3,sodilateBlocks)){
                block = sodilateBlocks;
                ore = Blocks.air;
            }

            if(block == agaryteWall && rand.chance(0.3) && nearAir(x, y) && !near(x, y, 3, agaryteBlocks)){
                block = agaryteBlocks;
                ore = Blocks.air;
            }
        });

        pass((x, y) -> {
            if(ore.asFloor().wallOre || block.itemDrop != null || (block == Blocks.air && ore != Blocks.air)){
                removeWall(x, y, 3, b -> b instanceof TallBlock);
            }
        });

        trimDark();

        int minVents = rand.random(6, 9);
        int ventCount = 0;
        outer:
        for(Tile tile : tiles){
            var floor = tile.floor();
            if((floor == legartyteStone || floor == darkLegartyteStone) && rand.chance(0.002)){
                int radius = 2;
                for(int x = -radius; x <= radius; x++){
                    for(int y = -radius; y <= radius; y++){
                        Tile other = tiles.get(x + tile.x, y + tile.y);
                        if(other == null || (other.floor() != legartyteStone && other.floor() != darkLegartyteStone) || other.block().solid){
                            continue outer;
                        }
                    }
                }

                ventCount ++;
                for(var pos : SteamVent.offsets){
                    Tile other = tiles.get(pos.x + tile.x + 1, pos.y + tile.y + 1);
                    other.setFloor(crudesQuarry.asFloor());
                }
            }
        }

        for(Tile tile : tiles){
            if(tile.overlay().needsSurface && !tile.floor().hasSurface()){
                tile.setOverlay(Blocks.air);
            }
        }

        pass((x,y) -> {
            if(block == Blocks.sandWall)
                block = legartyteWall;
            if(block == Blocks.duneWall)
                block = archalyteWall;
        });

        decoration(0.017f);

        Schematics.placeLaunchLoadout(spawnX, spawnY);
    }
}
