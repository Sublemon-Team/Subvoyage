package oceanic_dust.planets;

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
import static oceanic_dust.blocks.ODWorldBlocks.*;

public class AtlacianPlanetGenerator extends PlanetGenerator {
    BaseGenerator basegen = new BaseGenerator();
    float scl = 5f;
    float ocean = -0.4f;


    Block[][] arr = {
            {Blocks.water, Blocks.water, Blocks.stone, Blocks.water, Blocks.stone, Blocks.ice, Blocks.ice, Blocks.water, Blocks.sand, Blocks.stone, Blocks.water, Blocks.water, Blocks.water},
            {Blocks.ice, Blocks.stone, Blocks.water, Blocks.water, Blocks.water, Blocks.hotrock, Blocks.water, Blocks.water, Blocks.stone, Blocks.stone, Blocks.ice, Blocks.stone, Blocks.sand},
            {Blocks.salt, Blocks.water, Blocks.stone, Blocks.stone, Blocks.ice, Blocks.stone, Blocks.water, Blocks.water, Blocks.ice, Blocks.water, Blocks.ice, Blocks.water, Blocks.water},
            {Blocks.water, Blocks.water, Blocks.stone, Blocks.ice, Blocks.stone, Blocks.water, Blocks.water, Blocks.ice, Blocks.ice, Blocks.water, Blocks.stone, Blocks.water, Blocks.water},
            {Blocks.ice, Blocks.ice, Blocks.ice, Blocks.water, Blocks.ice, Blocks.water, Blocks.ice, Blocks.water, Blocks.water, Blocks.basalt, Blocks.water, Blocks.water, Blocks.water},
            {Blocks.ice, Blocks.ice, Blocks.ice, Blocks.water, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.water, Blocks.sand, Blocks.water},
            {Blocks.ice, Blocks.water, Blocks.sand, Blocks.ice, Blocks.sand, Blocks.hotrock, Blocks.ice, Blocks.ice, Blocks.water, Blocks.ice, Blocks.water, Blocks.ice, Blocks.sand},
            {Blocks.water, Blocks.water, Blocks.sand, Blocks.hotrock, Blocks.ice, Blocks.ice, Blocks.basalt, Blocks.ice, Blocks.water, Blocks.sand, Blocks.ice, Blocks.water, Blocks.ice},
            {Blocks.water, Blocks.water, Blocks.sand, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.water, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.water},
            {Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.water, Blocks.water, Blocks.water, Blocks.water, Blocks.basalt, Blocks.water, Blocks.water, Blocks.ice, Blocks.basalt},
            {Blocks.water, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.water, Blocks.water, Blocks.basalt, Blocks.ice, Blocks.ice, Blocks.hotrock, Blocks.water, Blocks.ice, Blocks.basalt},
            {Blocks.water, Blocks.ice, Blocks.ice, Blocks.water, Blocks.ice, Blocks.sand, Blocks.ice, Blocks.water, Blocks.water, Blocks.water, Blocks.ice, Blocks.basalt, Blocks.ice},
            {Blocks.ice, Blocks.water, Blocks.water, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.sand, Blocks.water, Blocks.water, Blocks.sand, Blocks.ice, Blocks.basalt, Blocks.ice}
    };


    ObjectMap<Block, Block> dec = ObjectMap.of(
            Blocks.water, Blocks.whiteTreeDead,
            Blocks.water, Blocks.deepwater,
            Blocks.water, Blocks.waterExtractor
    );

    ObjectMap<Block, Block> tars = ObjectMap.of(
            Blocks.sandWall, Blocks.water
    );

    @Override
    public void genTile(Vec3 position, TileGen tile){

        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;
        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) > 0.31){
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
        float tar = Simplex.noise3d(seed, 4, 0.55f, 1f/2f, position.x, position.y + 999f, position.z) * 0.3f + Tmp.v31.dst(0, 0, 1f) * 0.2f;
        Block res = arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
        if(getRawHeight(position)+0.3f <= ocean) return Blocks.water;
        if(tar > 0.5f){
            return tars.get(res, res);
        }else{
            return res;
        }
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        Vec3 v = sector.rect.project(x, y).scl(5f);
        return Simplex.noise3d(seed, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }


    // overall planet gen yknow
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

        /*if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 1, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.5f*addscl){
            ores.add(Blocks.oreTitanium);
        }

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 2, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.7f*addscl){
            ores.add(Blocks.oreThorium);
        }

        if(rand.chance(0.25)){
            ores.add(Blocks.oreScrap);
        }*/

        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.1f, 0.01f) - i * 0.01f + poles * 0.04f);
        }

        pass((x, y) -> {
            if(!floor.asFloor().hasSurface()) return;

            int offsetX = x - 4, offsetY = y + 23;
            for(int i = ores.size - 1; i >= 0; i--){
                Block entry = ores.get(i);
                float freq = frequencies.get(i);
                if(Math.abs(0.5f - noise(offsetX, offsetY + i*999, 2, 0.7, (40 + i * 2))) > 0.22f + i*0.01 &&
                        Math.abs(0.5f - noise(offsetX, offsetY - i*999, 1, 1, (30 + i * 4))) > 0.37f + freq){
                    ore = entry;
                    break;
                }
            }

            if(ore == Blocks.oreScrap && rand.chance(0.33)){
                floor = Blocks.metalFloorDamaged;
            }
        });

        trimDark();
        median(1);
        inverseFloodFill(tiles.getn(spawn.x, spawn.y));
        tech();
        pass((x, y) -> {
            if(ore.asFloor().wallOre || block.itemDrop != null || (block == Blocks.air && ore != Blocks.air)){
                removeWall(x, y, 3, b -> b instanceof TallBlock);
            }
        });

        pass((x, y) -> {
            if(floor == Blocks.hotrock){
                if(Math.abs(0.5f - noise(x - 90, y, 4, 0.8, 80)) > 0.05){
                    floor = Blocks.basalt;
                }else{
                    ore = Blocks.air;
                    boolean all = true;
                    for(Point2 p : Geometry.d4){
                        Tile other = tiles.get(x + p.x, y + p.y);
                        if(other == null || (other.floor() != Blocks.hotrock && other.floor() != Blocks.magmarock)){
                            all = false;
                        }
                    }
                    if(all){
                        floor = Blocks.magmarock;
                    }
                }
            }

            if(rand.chance(0.0075)){
                boolean any = false;
                boolean all = true;
                for(Point2 p : Geometry.d4){
                    Tile other = tiles.get(x + p.x, y + p.y);
                    if(other != null && other.block() == Blocks.air){
                        any = true;
                    }else{
                        all = false;
                    }
                }

                if(any && ((block == Blocks.sandWall || block == Blocks.duneWall) || (all && block == Blocks.air && floor == Blocks.water && rand.chance(0.06)))){
                    block = rand.chance(0.1) ? Blocks.boulder : Blocks.pine;
                }
            }
            
            dec: {
                for(int i = 0; i < 4; i++){
                    Tile near = world.tile(x + Geometry.d4[i].x, y + Geometry.d4[i].y);
                    if(near != null && near.block() != Blocks.air){
                        break dec;
                    }
                }

                if(rand.chance(0.005) && floor.asFloor().hasSurface() && block == Blocks.air){
                    block = dec.get(floor, floor.asFloor().decoration);
                }
            }
        });

        float difficulty = sector.threat;
        ints.clear();
        ints.ensureCapacity(width * height / 4);
        int ruinCount = rand.random(-2, 4);
        if(ruinCount > 0){
            int padding = 25;
            for(int x = padding; x < width - padding; x++){
                for(int y = padding; y < height - padding; y++){
                    Tile tile = tiles.getn(x, y);
                    if(!tile.solid() && (tile.drop() != null || tile.floor().liquidDrop != null)){
                        ints.add(tile.pos());
                    }
                }
            }

            ints.shuffle(rand);
            int placed = 0;
            float diffRange = 0.4f;
            for(int i = 0; i < ints.size && placed < ruinCount; i++){
                int val = ints.items[i];
                int x = Point2.x(val), y = Point2.y(val);
                if(Mathf.within(x, y, spawn.x, spawn.y, 18f)){
                    continue;
                }

                float range = difficulty + rand.random(diffRange);
                Tile tile = tiles.getn(x, y);
                BaseRegistry.BasePart part = null;
                if(tile.overlay().itemDrop != null){
                    part = bases.forResource(tile.drop()).getFrac(range);
                }else if(tile.floor().liquidDrop != null && rand.chance(0.05)){
                    part = bases.forResource(tile.floor().liquidDrop).getFrac(range);
                }else if(rand.chance(0.05)){ //ore-less parts are less likely to occur.
                    part = bases.parts.getFrac(range);
                }

                if(part != null && BaseGenerator.tryPlace(part, x, y, Team.derelict, (cx, cy) -> {
                    Tile other = tiles.getn(cx, cy);
                    if(other.floor().hasSurface()){
                        other.setOverlay(Blocks.oreScrap);
                        for(int j = 1; j <= 2; j++){
                            for(Point2 p : Geometry.d8){
                                Tile t = tiles.get(cx + p.x*j, cy + p.y*j);
                                if(t != null && t.floor().hasSurface() && rand.chance(j == 1 ? 0.4 : 0.2)){
                                    t.setOverlay(Blocks.oreScrap);
                                }
                            }
                        }
                    }
                })){
                    placed ++;
                    int debrisRadius = Math.max(part.schematic.width, part.schematic.height)/2 + 3;
                    Geometry.circle(x, y, tiles.width, tiles.height, debrisRadius, (cx, cy) -> {
                        float dst = Mathf.dst(cx, cy, x, y);
                        float removeChance = Mathf.lerp(0.05f, 0.5f, dst / debrisRadius);

                        Tile other = tiles.getn(cx, cy);
                        if(other.build != null && other.isCenter()){
                            if(other.team() == Team.derelict && rand.chance(removeChance)){
                                other.remove();
                            }else if(rand.chance(0.5)){
                                other.build.health = other.build.health - rand.random(other.build.health * 0.9f);
                            }
                        }
                    });
                }
            }
        }

        //remove invalid ores
        for(Tile tile : tiles){
            if(tile.overlay().needsSurface && !tile.floor().hasSurface()){
                tile.setOverlay(Blocks.air);
            }
        }

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);
        for(Room espawn : enemies){
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        }

        if(sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), state.rules.waveTeam, sector, difficulty);
            state.rules.attackMode = sector.info.attack = true;
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
        float actualNoise = (float) (noise * waveNoise);
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