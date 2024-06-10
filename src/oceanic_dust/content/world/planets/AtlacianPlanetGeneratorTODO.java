package oceanic_dust.content.world.planets;

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
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;
import static oceanic_dust.content.blocks.ODWorldBlocks.*;

public class AtlacianPlanetGeneratorTODO extends PlanetGenerator{
    BaseGenerator basegen = new BaseGenerator();
    float scl = 5f;
    float ocean = -0.4f;
    Block[][] arr = {
    {Blocks.water, Blocks.water, Blocks.carbonStone, Blocks.carbonStone, Blocks.carbonStone, Blocks.carbonStone, Blocks.carbonStone, Blocks.water, Blocks.carbonStone, Blocks.carbonStone, Blocks.water, Blocks.water, Blocks.water},
    {Blocks.carbonStone, Blocks.water, Blocks.carbonStone, Blocks.water, Blocks.carbonStone, Blocks.carbonStone, Blocks.carbonStone, Blocks.carbonStone, Blocks.carbonStone, Blocks.stone, Blocks.carbonStone, Blocks.water, Blocks.water},
    {Blocks.water, Blocks.water, Blocks.carbonStone, Blocks.water, Blocks.carbonStone, Blocks.carbonStone, Blocks.carbonStone, Blocks.water, Blocks.carbonStone, Blocks.carbonStone, Blocks.water, Blocks.water, Blocks.water},
    };

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;
        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 16) > 0.31){
            tile.block = Blocks.air;
        }
    }

    Block getBlock(Vec3 position){
        float height = getRawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(scl);
        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2f) / (rad));
        float tnoise = Simplex.noise3d(seed, 7, 0.56, 1f/3f, position.x, position.y + 999f, position.z);
        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);
        Block res = arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
        if(getRawHeight(position)+0.3f <= ocean) return Blocks.water;
        return res;
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        Vec3 v = sector.rect.project(x, y).scl(5f);
        return Simplex.noise3d(seed, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }

    @Override
    protected void generate(){
        defaultLoadout = ODLoadouts.basicPuffer;
        class Room {
            int x, y, radius;
            ObjectSet<Room> connected = new ObjectSet<>();
            Room(int x, int y, int radius) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                connected.add(this);
            }

            void join(int x1, int y1, int x2, int y2) {
                float nscl = rand.random(100f, 140f) * 6f;
                int stroke = rand.random(3, 9);
                brush(pathfind(x1, y1, x2, y2, tile -> (tile.solid() ? 50f : 0f) + noise(tile.x, tile.y, 2, 0.4f, 1f / nscl) * 500, Astar.manhattan), stroke);
            }

            void connect(Room to) {
                if (!connected.add(to) || to == this) return;
                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();
                midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                midpoint.sub(width / 2f, height / 2f).limit(width / 2f / Mathf.sqrt3).add(width / 2f, height / 2f);
                int mx = (int) midpoint.x, my = (int) midpoint.y;
                join(x, y, mx, my);
                join(mx, my, to.x, to.y);
            }
        }

        cells(3);
        distort(9, 11);
        float constraint = 1.3f;
        float radius = width / 2f / Mathf.sqrt3;
        int rooms = rand.random(2, 5);
        Seq<Room> roomseq = new Seq<>();
        for(int i = 0; i < rooms; i++){
            Tmp.v1.trns(rand.random(360f), rand.random(radius / constraint));
            float rx = (width/2f + Tmp.v1.x);
            float ry = (height/2f + Tmp.v1.y);
            float maxrad = radius - Tmp.v1.len();
            float rrad = Math.min(rand.random(9f, maxrad / 2f), 30f);
            roomseq.add(new Room((int)rx, (int)ry, (int)rrad));
        }

        //check positions on the map to place the player spawn. this needs to be in the corner of the map
        Room spawn = null;
        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.random(1, Math.max((int)(sector.threat * 4), 1));
        if(sector.id == 7) {
            enemySpawns = 0;
            sector.info.attack = false;
            sector.generateEnemyBase = false;
        }

        if(sector.id == 15) {
            sector.info.attack = false;
            sector.generateEnemyBase = false;
        }

        if(sector.id == 47) {
            sector.info.attack = false;
            sector.generateEnemyBase = false;
        }

        float length = width/2.55f - rand.random(13, 23);
        int offset = rand.nextInt(360);
        int angleStep = 5;
        for(int i = 0; i < 360; i+= angleStep){
            int angle = offset + i;
            int cx = (int)(width/2 + Angles.trnsx(angle, length));
            int cy = (int)(height/2 + Angles.trnsy(angle, length));
            roomseq.add(spawn = new Room(cx, cy, rand.random(8, 15)));
            for(int j = 0; j < enemySpawns; j++){
                float enemyOffset = rand.range(60f);
                Tmp.v1.set(cx - width/2, cy - height/2).rotate(180f + enemyOffset).add(width/2, height/2);
                Room espawn = new Room((int)Tmp.v1.x, (int)Tmp.v1.y, rand.random(8, 16));
                roomseq.add(espawn);
                enemies.add(espawn);
            }

            break;
        }

        for(Room room : roomseq){
            erase(room.x, room.y, room.radius);
        }

        int connections = rand.random(Math.max(rooms - 1, 1), rooms + 3);
        for(int i = 0; i < connections; i++){
            roomseq.random(rand).connect(roomseq.random(rand));
        }

        for(Room room : roomseq){
            spawn.connect(room);
        }

        cells(1);
        distort(10f, 6f);
        Seq<Block> ores = Seq.with(orePhosphorus,orePhosphorus,oreCorallite);
        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float scl = 1f;
        float addscl = 1.3f;

        // Ores
        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.25f*addscl){
            ores.add(oreSulfur);
        }

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 1, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.65f*addscl){
            ores.add(oreIridium);
        }

        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.1f, 0.01f) - i * 0.01f + poles * 0.04f);
        }

        median(2, 0.6);
        pass((x, y) -> {
            if(floor == Blocks.carbonStone && noise(x, y, 2, 0.6f, 11, 1.4f) > 0.95f){
                floor = legartyteStone;
            }
        });

        trimDark();
        inverseFloodFill(tiles.getn(spawn.x, spawn.y));
        pass((x, y) -> {
            if(block.itemDrop != null || (block == Blocks.air && ore != Blocks.air)){
                removeWall(x, y, 3, b -> b instanceof TallBlock);
            }
        });

        blend(Blocks.water, Blocks.carbonStone, 4);
        blend(Blocks.water, Blocks.sandWater, 6);
        float difficulty = sector.threat;
        ints.clear();
        ints.ensureCapacity(width * height / 4);
        for(Tile tile : tiles){
            if(tile.overlay().needsSurface && !tile.floor().hasSurface()){
                tile.setOverlay(Blocks.air);
            }
        }

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);
        for(Room espawn : enemies){
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        }

        if(sector.hasEnemyBase() && sector.id != 47){
            try {
                basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), state.rules.waveTeam, sector, difficulty);
                state.rules.attackMode = sector.info.attack = true;
            } catch(Exception e) {

            }
        }else{
            state.rules.winWave = sector.info.winWave = 10 + 5 * (int)Math.max(difficulty * 10, 1);
        }

        float waveTimeDec = 0.4f;
        state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60f * 60f * 1f, Math.max(difficulty - waveTimeDec, 0f));
        state.rules.waves = true;
        state.rules.env = sector.planet.defaultEnv;
        state.rules.enemyCoreBuildRadius = 600f;
        state.rules.spawns = Waves.generate(difficulty, new Rand(sector.id), state.rules.attackMode, state.rules.attackMode && spawner.countGroundSpawns() == 0, true);
    }

    @Override
    public void postGenerate(Tiles tiles){
        if(sector.hasEnemyBase() && sector.id != 7){
            basegen.postGenerate();
            if(spawner.countGroundSpawns() == 0){
                state.rules.spawns = Waves.generate(sector.threat, new Rand(sector.id), state.rules.attackMode, true, false);
            }
        }
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
}