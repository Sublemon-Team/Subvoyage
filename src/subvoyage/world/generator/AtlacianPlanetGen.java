package subvoyage.world.generator;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.type.Sector;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import subvoyage.content.other.*;
import subvoyage.world.planets.atlacian.*;

import static mindustry.Vars.state;
import static subvoyage.content.block.cat.SvEnvironment.*;

public class AtlacianPlanetGen extends PlanetGenerator {
    /*      GENERATION PARAMETERS       */
    /**/float oceanLevel = 0.4f;
    /**/float sodilateBiomeWeight = 0.3f;
    /**/float archalyteBiomeWeight = 0.4f;

    {
        defaultLoadout = SvLoadouts.basicPuffer;
        baseSeed = 1;
    }

    @Override
    public void generateSector(Sector sector) {

    }

    @Override
    public float getHeight(Vec3 position) {
        float noise = Simplex.noise3d(seed+10,4,0.9,1f,position.z/10f,position.y,position.x/2f);
        float puddleNoise = Simplex.noise3d(seed+12,2,0.9,1f,position.x/12f,position.y/12f,position.z/12f);
        float waveNoise = Ridged.noise3d(seed+10,position.y/2f+noise*4,0,0,1/4f);
        float actualNoise = noise*1.2f;
        if(waveNoise > 0.7f || puddleNoise > 0.7f) return oceanLevel;
        return Math.max(actualNoise, oceanLevel);
    }
    @Override
    public Color getColor(Vec3 position) {
        float biomeMask = Simplex.noise3d(seed,3,0.4,1f,position.z,position.y,position.x);
        float patternMask = Simplex.noise3d(seed,1,0.6,2f,position.z,position.y,position.x);

        if(getHeight(position) <= oceanLevel) return SvPal.atlacianOcean;
        if(biomeMask > 0.6  && patternMask < 0.6f) return SvPal.agaryte;
        if(biomeMask > 0.5) return SvPal.legartyteLightish;
        if(biomeMask > 0.4f && patternMask < 0.3f) return sodilateFloor.mapColor;
        if(biomeMask > 0.2) return SvPal.legartyte;
        return Liquids.arkycite.color;
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

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 1, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.45f*addscl){
            ores.add(oreChromium);
        }

        return ores;
    };


    @Override
    protected void genTile(Vec3 position, TileGen tile) {
        super.genTile(position, tile);
    }

    @Override
    protected void generate() {
        float spawnDegree = rand.random(360f);
        float length = width / 3f;
        Vec2 trns = Tmp.v1.trns(spawnDegree, length);
        Seq<Block> ores = getOres();
        FloatSeq frequencies = new FloatSeq();
        float poles = Math.abs(sector.tile.v.y);
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.1f, 0.01f) + i * 0.01f + poles * 0.04f);
        }
        int
                spawnX = (int)(trns.x + width / 2f),
                spawnY = (int)(trns.y + height / 2f),
                endX = (int)(-trns.x + width / 2f),
                endY = (int)(-trns.y + height / 2f);
        float maxd = Mathf.dst(width / 2f, height / 2f);
        pass((x,y) -> floor = legartyteStone);
        cells(16);
        terrain(agaryteWall,40f,.5f,.95f);
        terrain(legartyteWall,31f,1.3f,.8f);
        distort(1.1f,.5f);
        terrain(agaryteWall,40f,1.3f,.9f);
        noiseReplace(legartyteStone, Blocks.water,2,.2f,60,.6f);
        rnoiseReplace(legartyteStone, Blocks.water,2,.2f,50,.1f,1f);

        Seq<Tile> path = pathfind(spawnX, spawnY, endX, endY, tile -> (tile.solid() ? 300f : 0f) + rand.random(-200,200) + maxd - tile.dst(width / 2f, height / 2f) / 10f, Astar.manhattan);
        erase(spawnX,spawnY, 30);
        brush(path, 10);
        brushWithBlock(path, 8, Blocks.water);
        erase(endX, endY, 12);

        inverseFloodFill(tiles.get(spawnX,spawnY));

        distort(10f,2f);
        rnoiseReplace(Blocks.water, legartyteStone,2,.2f,100,.2f,1f);
        rnoiseReplace(Blocks.water, Blocks.deepwater,1,.5f,20,0f,1f);
        blend(Blocks.water,Blocks.deepwater,Blocks.darksandWater,4f);
        blend(legartyteStone,darkLegartyteStone,2f);
        pass((x,y) -> {
            float firstNoise = noise(x,y,5,0.6f,80,1f);
            float secondNoise = noise(x,y,4,0.4f,100,0.95f);

            if(secondNoise < sodilateBiomeWeight) {
                if(!floor.asFloor().isLiquid) floor = sodilateFloor;
                else {
                    if(floor == Blocks.water) floor = hardWater;
                    if(floor == Blocks.deepwater || floor == Blocks.darksandWater) floor = darkHardWater;
                }
                if(block.solid) block = sodilateWall;
                return;
            }
            if(firstNoise < archalyteBiomeWeight) {
                if(!floor.asFloor().isLiquid) floor = archalyteStone;
                if(block.solid) {
                    block = archalyteWall;
                }
                return;
            }
            return;
        });
        blend(archalyteStone,darkArchalyteStone,2f);

        for (Tile tile : tiles) {
            if(rand.chance(0.0001f)) {
                int rad = rand.random(7,16);
                Geometry.circle(tile.x,tile.y,rad,(x,y) -> {
                    Tile puddleTile = tiles.get(x,y);
                    if(puddleTile == null) return;
                    if(!(puddleTile.floor() == Blocks.water && !puddleTile.block().solid))
                        puddleTile.setFloor((Floor) Blocks.darksandWater);
                    puddleTile.setBlock(Blocks.air);
                });
                Geometry.circle(tile.x,tile.y,rad-2,(x,y) -> {
                    Tile puddleTile = tiles.get(x,y);
                    if(puddleTile == null) return;
                    puddleTile.setFloor((Floor) Blocks.water);
                    puddleTile.setBlock(Blocks.air);
                });
                if(rad > 10) Geometry.circle(tile.x,tile.y,rad-5,(x,y) -> {
                    Tile puddleTile = tiles.get(x,y);
                    if(puddleTile == null) return;
                    puddleTile.setFloor((Floor) Blocks.deepwater);
                    puddleTile.setBlock(Blocks.air);
                });
            }
        }

        distort(10f,1f);
        median(4,0.7f);

        scatterBlock(Blocks.water,Blocks.yellowCoral,0.001f);
        scatterBlock(legartyteStone, hauntedTree, 0.005f/3f);
        scatterBlock(darkLegartyteStone, hauntedTree, 0.005f/3f);
        //blendRand(agaryteWall,agaryteBoulder,2f,0.3f);
        //blendRand(agaryteBoulder,agaryteBlocks,1f,0.2f);

        pass((x,y) -> {
            if(block == agaryteWall && rand.chance(0.23) && nearAir(x, y) && !near(x, y, 3, agaryteBlocks)){
                block = agaryteBlocks;
                ore = Blocks.air;
            }

            if(block == sodilateWall && rand.chance(0.42) && nearAir(x, y) && !near(x, y, 3, sodilateBlocks)){
                block = sodilateBlocks;
                ore = Blocks.air;
            }

            if(block == archalyteWall && rand.chance(0.33) && nearAir(x, y)){
                block = archalyteSpikes;
                ore = Blocks.air;
            }
        });

        pass((x, y) -> {
            int offsetX = x - 4, offsetY = y + 23;
            for(int i = ores.size - 1; i >= 0; i--){
                Block entry = ores.get(i);
                float freq = frequencies.get(i);
                if(block == Blocks.air && Math.abs(0.5f - noise(offsetX, offsetY + i * 888, 2, 0.7f, (10 + i * 2),1f)) > 0.24f + i * 0.01 &&
                        Math.abs(0.5f - noise(offsetX, offsetY - i * 888, 1, 1, (15 + i * 4),1f)) > 0.37f + freq){
                    ore = entry;
                    break;
                }
            }
        });

        pass((x,y) -> {
           if(block == sodilateWall) {
               if(!nearAir(x,y)) return;
               for(int i = 0; i < ores.size; i++){
                   Block entry = ores.get(i);
                   if(!floorToWallOre.containsKey(entry)) continue;
                   if(noise(x/10f,y/10f,2,0.3f,1f,1f) > 0.5f+0.1f*i) continue;
                   ore = floorToWallOre.getOrDefault(entry,entry);
                   break;
               }
           };
        });

        outer:
        for (Tile tile : tiles) {
            var floor = tile.floor();

            if(floor == legartyteStone && rand.chance(0.005f)) {
                int radius = 2;
                for(int x = -radius; x <= radius; x++){
                    for(int y = -radius; y <= radius; y++){
                        Tile other = tiles.get(x + tile.x, y + tile.y);
                        if(other == null || (other.floor() != legartyteStone) || other.block().solid){
                            continue outer;
                        }
                    }
                }
                for(var pos : SteamVent.offsets){
                    Tile other = tiles.get(pos.x + tile.x + 1, pos.y + tile.y + 1);
                    other.setFloor(crudesQuarry.asFloor());
                }
            }
        }
        decoration(0.017f);

        Schematics.placeLaunchLoadout(spawnX, spawnY);
        tiles.get(endX,endY).setOverlay(Blocks.spawn);
        Geometry.circle(spawnX,spawnY,6,(x,y) -> {
            if(tiles.get(x,y) != tiles.get(spawnX,spawnY) && rand.chance(0.3f)) tiles.get(x,y).setOverlay(oreSpaclanium);
        });

        Seq<SpawnGroup> spawns = AtlacianWaves.generate(sector.threat * 1.3f, new Rand(sector.id), state.rules.attackMode, rand.chance(0.3f));
        state.rules.spawns = spawns;
        state.rules.waves = true;
        state.rules.winWave = sector.info.winWave = 10 + 5 * (int)Math.max(sector.threat * 10, 1);
        state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60f * 60f * 0.8f, Math.max(sector.threat, 0f));
        sector.generateEnemyBase = false;
        state.rules.attackMode = false;
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
                    if(block == Blocks.water || block.isFloor()) other.setFloor(block.asFloor());
                    else other.setBlock(block);
                }
            }
        }
    }

    public void scatterBlock(Block target, Block dst, float chance){
        pass((x, y) -> {
            if(!Mathf.chance(chance)) return;
            if(floor == target && !block.solid){
                block = dst;
            }
        });
    }

    public void blendRand(Block floor, Block around, float radius, float chance){
        float r2 = radius*radius;
        int cap = Mathf.ceil(radius);
        int max = tiles.width * tiles.height;
        Block dest = around;

        for(int i = 0; i < max; i++){
            Tile tile = tiles.geti(i);
            if(tile.floor() == floor || tile.block() == floor){
                for(int cx = -cap; cx <= cap; cx++){
                    for(int cy = -cap; cy <= cap; cy++){
                        if(cx*cx + cy*cy <= r2){
                            Tile other = tiles.get(tile.x + cx, tile.y + cy);

                            if(other != null && other.block() != floor && noise(tile.x,tile.y,2,0.5f,10f,1f) < chance){
                                other.setBlock(dest);
                            }
                        }
                    }
                }
            }
        }
    }

    public void blend(Block floor, Block secondaryFloor, Block around, float radius){
        float r2 = radius*radius;
        int cap = Mathf.ceil(radius);
        int max = tiles.width * tiles.height;
        Floor dest = around.asFloor();

        for(int i = 0; i < max; i++){
            Tile tile = tiles.geti(i);
            if(tile.floor() == floor || tile.block() == floor || tile.floor() == secondaryFloor || tile.block() == secondaryFloor){
                for(int cx = -cap; cx <= cap; cx++){
                    for(int cy = -cap; cy <= cap; cy++){
                        if(cx*cx + cy*cy <= r2){
                            Tile other = tiles.get(tile.x + cx, tile.y + cy);

                            if(other != null && other.floor() != floor && other.floor() != secondaryFloor){
                                other.setFloor(dest);
                            }
                        }
                    }
                }
            }
        }
    }

    protected float rnoise(float x, float y, int octaves, float scl, float falloff, float mag){
        return Ridged.noise2d(seed + 18, (int)(x), (int)(y), octaves, falloff, 1f / scl) * mag;
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag) {
        Vec3 v = sector.rect.project(x, y);
        return Simplex.noise3d(seed+16, octaves, falloff, 1f / scl, x/3f, y/3f, v.z/2f) * (float)mag;
    }

    public void rnoiseReplace(Block target, Block block, int octaves, float falloff, float scl, float threshold, float mag){
        pass((x, y) -> {
            if(rnoise(x, y, octaves, scl, falloff, mag) > threshold){
                Tile tile = tiles.getn(x, y);
                boolean is = target.isFloor() && tile.floor() == target.asFloor();
                if(!target.isFloor() && tile.block() == target) is = true;
                if(is) {
                    if(block.isFloor()) this.floor = block;
                    else this.block = block;
                }
            }
        });
    }
    public void noiseReplace(Block target, Block block, int octaves, float falloff, float scl, float threshold){
        pass((x, y) -> {
            if(noise(x, y, octaves, falloff, scl) > threshold){
                Tile tile = tiles.getn(x, y);
                boolean is = target.isFloor() && tile.floor() == target.asFloor();
                if(!target.isFloor() && tile.block() == target) is = true;
                if(is) {
                    if(block.isFloor()) this.floor = block;
                    else this.block = block;
                }
            }
        });
    }
}
