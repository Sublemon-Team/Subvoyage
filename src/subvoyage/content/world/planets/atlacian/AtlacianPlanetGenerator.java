package subvoyage.content.world.planets.atlacian;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.storage.*;
import subvoyage.content.SvPal;
import subvoyage.content.blocks.*;
import subvoyage.content.world.planets.SvLoadouts;
import subvoyage.content.world.sectors.SvSectorPresets;

import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.*;
import static subvoyage.content.blocks.SvWorldBlocks.*;

public class AtlacianPlanetGenerator extends PlanetGenerator {
    float ocean = -0.3f;
    public static float liqThresh = 0.2f, liqScl = 60f;
    Block[][] arr = {
    {Blocks.carbonStone, legartyteStone, agaryteStone,Blocks.carbonStone, legartyteStone, Blocks.carbonStone,
            agaryteStone, Blocks.carbonStone, Blocks.carbonStone,Blocks.carbonStone, legartyteStone,
            agaryteStone,Blocks.carbonStone,Blocks.carbonStone,legartyteStone, agaryteStone}
    };

    {
        defaultLoadout = SvLoadouts.basicPuffer;
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

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 1, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.5f*addscl){
            ores.add(oreChromium);
        }

        return ores;
    };

    @Override
    public void generateSector(Sector sector){
        PlanetGrid.Ptile tile = sector.tile;

        boolean any = false;
        float noise = Noise.snoise3(tile.v.x, tile.v.y, tile.v.z, 0.001f, 0.5f);

        if(noise > 0.027){
            any = true;
        }

        if(noise < 0.15){
            for(PlanetGrid.Ptile other : tile.tiles){
                //no sectors near start sector!
                if(sector.planet.getSector(other).id == sector.planet.startSector){
                    return;
                }

                if(sector.planet.getSector(other).generateEnemyBase){
                    any = false;
                    break;
                }
            }
        }

        if(any){
            sector.generateEnemyBase = true;
        }
    }

    @Override
    public float getHeight(Vec3 position) {
        return getRawHeight(position);
    }

    private float getRawHeight(Vec3 position) {
        float noise = Simplex.noise3d(seed,4,0.7,1f,position.x,position.x,position.z);
        float waveNoise = Ridged.noise3d(seed,position.x/2f+noise*4,position.y,position.z+Math.sin(noise)*6,1/4f);
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

        if(getRawHeight(position) <= ocean) return SvPal.atlacianOcean;
        if(invertMask > 0.6) return SvPal.agaryte;
        if(invertMask > 0.5) return SvPal.legartyteLightish;
        if(invertMask > 0.2) return SvPal.legartyte;
        return Liquids.arkycite.color;
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
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
            if(value < 0.17f) return;
            floor = Blocks.water;
        });

        float difficulty = sector.threat*1.2f;
        boolean isOffloaded = sector.generateEnemyBase;
        float length = width / 2.6f;
        float spawnDegree = rand.random(360f);
        Vec2 trns = Tmp.v1.trns(spawnDegree, length);

        int
        spawnX = (int)(trns.x + width / 2f),
        spawnY = (int)(trns.y + height / 2f),
        endX = (int)(-trns.x + width / 2f),
        endY = (int)(-trns.y + height / 2f);

        float maxd = Mathf.dst(width / 2f, height / 2f);

        Seq<Tile> path = pathfind(spawnX, spawnY, endX, endY, tile -> (tile.solid() ? 300f : 0f) + maxd - tile.dst(width / 2f, height / 2f) / 10f, Astar.manhattan);
        brush(path, 24);
        brushWithBlock(path, 22, Blocks.water);
        erase(endX, endY, 15);

        Seq<Vec2> offloadCorePositions = Seq.with();
        if(isOffloaded) {
            int coresCount = (int) (2+Math.max(0,rand.nextFloat()*8));
            Seq<Float> existingCores = Seq.with(spawnDegree,(spawnDegree+180)%360);
            for (int i = 0; i < coresCount; i++) {
                final float[] degree = {rand.random(360f)};
                final boolean[] contains = {false};
                for (Float e : existingCores) {
                    if (Math.abs(e - degree[0]) < (e.equals(spawnDegree) ? 80 : 10)) {
                        contains[0] = true;
                        break;
                    }
                }
                int attempts = 0;
                while (contains[0]) {
                    if(attempts > 36) break;
                    degree[0] = rand.random(360f);
                    for (Float e : existingCores) {
                        if (Math.abs(e - degree[0]) < (e.equals(spawnDegree) ? 80 : 10)) {
                            contains[0] = true;
                            break;
                        }
                    }
                    attempts++;
                }
                if(attempts >= 36) continue;

                Vec2 trnsE = Tmp.v1.trns(degree[0], length);
                int coreX = (int)(-trnsE.x + width/2f),coreY = (int)(-trnsE.y + height/2f);
                Seq<Tile> pathE = pathfind(endX,endY, coreX,coreY, tile -> (tile.solid() ? 300f : 0f) + maxd - tile.dst(width/2f, height/2f)/10f, Astar.manhattan);
                brush(pathE,9);
                brushWithBlock(pathE,7, Blocks.water);
                eraseWithBlock(coreX,coreY,12,Blocks.water);

                offloadCorePositions.add(new Vec2(coreX,coreY));
                existingCores.add(degree[0]);
            }

            state.rules.attackMode = true;
            sector.generateEnemyBase = true;
            state.rules.waveSending = false;
            state.rules.waves = false;
            state.rules.waveTimer = false;
            sector.info.attack = true;
        }

        median(5, 0.4);
        terrain(Blocks.carbonWall, 69.86f, 0.35f, 1.1f);
        erase(spawnX, spawnY, 15);


        distort(12f, 16f);
        blend(legartyteStone, darkLegartyteStone, 6);

        // rules
        state.rules.env = sector.planet.defaultEnv;
        state.rules.fog = true;
        if(!isOffloaded) {
            Seq<SpawnGroup> spawns = AtlacianWaves.generate(sector.threat * sector.threat * 1.3f, new Rand(sector.id), state.rules.attackMode, rand.chance(0.3f));
            state.rules.spawns = spawns;
            state.rules.waves = true;
            state.rules.winWave = sector.info.winWave = 10 + 5 * (int)Math.max(difficulty * 10, 1);
            state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60f * 60f * 0.8f, Math.max(difficulty, 0f));
            sector.generateEnemyBase = false;

            tiles.get(endX,endY).setOverlay(Blocks.spawn);
        }

        blend(Blocks.water, Blocks.darksandWater, 4);
        Seq<Block> ores = getOres();
        FloatSeq frequencies = new FloatSeq();
        float poles = Math.abs(sector.tile.v.y);
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.1f, 0.01f) + i * 0.01f + poles * 0.04f);
        }

        pass((x, y) -> {
            int offsetX = x - 4, offsetY = y + 23;
            if(Mathf.within(x - spawnX, y - spawnY, 6) && rand.chance(0.3)){
                ore = oreSpaclanium;
                return;
            }

            if(block == agaryteWall && rand.chance(0.6) && nearAir(x, y) && !near(x, y, 3, agaryteBlocks)){
                block = agaryteBlocks;
            }

            if(block == agaryteStone && rand.chance(0.052)){
                block = agaryteBoulder;
            }

            if(block == legartyteStone || block == darkLegartyteStone || block == agaryteStone && rand.chance(0.075)){
                block = hauntedTree;
            }

            boolean allowed = floor == Blocks.water || floor == Blocks.darksandWater;
            if(allowed && rand.chance(0.0005)){
                block = Blocks.redweed;
            }

            if(allowed && rand.chance(0.0015)){
                block = Blocks.purbush;
            }

            if(allowed && rand.chance(0.0025)){
                block = Blocks.yellowCoral;
            }

            for(int i = ores.size - 1; i >= 0; i--){
                Block entry = ores.get(i);
                float freq = frequencies.get(i);
                if(Math.abs(0.5f - noise(offsetX, offsetY + i * 888, 2, 0.7, (10 + i * 2))) > 0.24f + i * 0.01 &&
                Math.abs(0.5f - noise(offsetX, offsetY - i * 888, 1, 1, (15 + i * 4))) > 0.37f + freq){
                    ore = entry;
                    break;
                }
            }
        });

        //remove props near ores, they're too annoying
        pass((x, y) -> {
            if(ore.asFloor().wallOre || block.itemDrop != null || (block == Blocks.air && ore != Blocks.air)){
                removeWall(x, y, 3, b -> b instanceof TallBlock);
            }
        });

        for (Vec2 e : offloadCorePositions) {
            int coreX = (int) e.x;
            int coreY = (int) e.y;
            Tile tile = tiles.get(coreX,coreY);
            tile.setBlock(SvBlocks.offloadCore,Team.malis,0);
            if(tile.build instanceof CoreBlock.CoreBuild cb) state.teams.registerCore(cb);
        }
        if(isOffloaded) {
            Tile coreTile = tiles.get(endX, endY);
            coreTile.setBlock(SvBlocks.offloadCoreGuardian, Team.malis, 0);
            if (coreTile.build instanceof CoreBlock.CoreBuild cb) state.teams.registerCore(cb);
        }
        Schematics.placeLaunchLoadout(spawnX, spawnY);
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
