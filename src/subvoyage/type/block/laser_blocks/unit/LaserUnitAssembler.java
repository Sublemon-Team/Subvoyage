package subvoyage.type.block.laser_blocks.unit;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ai.types.AssemblerAI;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.EntityCollisions;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.io.TypeIO;
import mindustry.logic.LAccess;
import mindustry.type.PayloadSeq;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.ui.ItemImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitAssemblerModule;
import mindustry.world.consumers.ConsumePayloadDynamic;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import subvoyage.SubvoyageMod;
import subvoyage.content.SvUnits;
import subvoyage.content.other.SvStat;
import subvoyage.network.LaserAssemblerDroneSpawnedCallPacket;
import subvoyage.network.LaserAssemblerUnitSpawnedCallPacket;
import subvoyage.utility.SvCall;

import static mindustry.Vars.*;

public class LaserUnitAssembler extends LaserPayloadBlock {
    public TextureRegion sideRegion1;
    public TextureRegion sideRegion2;

    public int areaSize = 11;
    public UnitType droneType = SvUnits.pisun;
    public int dronesCreated = 4;
    public float droneConstructTime = 60f * 4f;

    public float consumeLaserTier0 = 0f;
    public float consumeLaserTier1 = 0f;

    public Seq<UnitAssembler.AssemblerUnitPlan> plans = new Seq<>(4);

    protected @Nullable ConsumePayloadDynamic consPayload;
    public LaserUnitAssembler(String name) {
        super(name);
        update = solid = true;
        rotate = true;
        rotateDraw = false;
        acceptsPayload = true;
        flags = EnumSet.of(BlockFlag.unitAssembler);
        regionRotated1 = 1;
        sync = true;
        group = BlockGroup.units;
        commandable = true;
        quickRotate = false;
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top", SubvoyageMod.ID+ "-factory-top-" + size + regionSuffix);
        outRegion = Core.atlas.find(name + "-out", SubvoyageMod.ID+"-factory-out-" + size + regionSuffix);
        inRegion = Core.atlas.find(name + "-in", SubvoyageMod.ID+"-factory-in-" + size + regionSuffix);
        sideRegion1 = Core.atlas.find(name+"-side1");
        sideRegion2 = Core.atlas.find(name+"-side2");
    }

    public Rect getRect(Rect rect, float x, float y, int rotation){
        rect.setCentered(x, y, areaSize * tilesize);
        float len = tilesize * (areaSize + size)/2f;

        rect.x += Geometry.d4x(rotation) * len;
        rect.y += Geometry.d4y(rotation) * len;

        return rect;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Rect rect = getRect(Tmp.r1, x, y, rotation);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        //overlapping construction areas not allowed; grow by a tiny amount so edges can't overlap either.
        Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, rotation).grow(0.1f);
        return !indexer.getFlagged(team, BlockFlag.unitAssembler).contains(b -> getRect(Tmp.r2, b.x, b.y, b.rotation).overlaps(rect));
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("progress", (LaserUnitAssemblerBuild e) -> new Bar("bar.progress", Pal.ammo, () -> e.progress));

        addBar("units", (LaserUnitAssemblerBuild e) ->
                new Bar(() ->
                        Core.bundle.format("bar.unitcap",
                                Fonts.getUnicodeStr(e.unit().name),
                                e.team.data().countType(e.unit()),
                                Units.getStringCap(e.team)
                        ),
                        () -> Pal.power,
                        () -> (float)e.team.data().countType(e.unit()) / Units.getCap(e.team)
                ));
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(plan.rotation >= 2 ? sideRegion2 : sideRegion1, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, sideRegion1, topRegion};
    }

    @Override
    public void init(){
        updateClipRadius(areaSize * tilesize);
        consume(consPayload = new ConsumePayloadDynamic((LaserUnitAssemblerBuild build) -> build.plan().requirements));

        consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));

        super.init();
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, table -> {
            table.row();

            int tier = 0;
            for(var plan : plans){
                int ttier = tier;
                table.table(Styles.grayPanel, t -> {

                    if(plan.unit.isBanned()){
                        t.image(Icon.cancel).color(Pal.remove).size(40).pad(10);
                        return;
                    }

                    if(plan.unit.unlockedNow()){
                        t.image(plan.unit.uiIcon).scaling(Scaling.fit).size(40).pad(10f).left();
                        t.table(info -> {
                            info.defaults().left();
                            info.add(plan.unit.localizedName);
                            info.row();
                            info.add(Strings.autoFixed(plan.time / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                            info.row();
                            info.add(SvStat.laserUse.localized()+": "+SvStat.laserPower.icon+Strings.fixed(ttier > 0 ? consumeLaserTier1 : consumeLaserTier0,0)).color(Color.lightGray);
                            if(ttier > 0){
                                info.row();
                                info.add(Stat.moduleTier.localized() + ": " + ttier).color(Color.lightGray);
                            }
                        }).left();

                        t.table(req -> {
                            req.right();
                            for(int i = 0; i < plan.requirements.size; i++){
                                if(i % 6 == 0){
                                    req.row();
                                }

                                PayloadStack stack = plan.requirements.get(i);
                                req.add(new ItemImage(stack)).pad(5);
                            }
                        }).right().grow().pad(10f);
                    }else{
                        t.image(Icon.lock).color(Pal.darkerGray).size(40).pad(10);
                    }
                }).growX().pad(5);
                table.row();
                tier++;
            }
        });
    }

    public static void assemblerUnitSpawned(Tile tile){
        if(tile == null || !(tile.build instanceof LaserUnitAssemblerBuild build)) return;
        build.spawned();
    }

    public static void assemblerDroneSpawned(Tile tile, int id){
        if(tile == null || !(tile.build instanceof LaserUnitAssemblerBuild build)) return;
        build.droneSpawned(id);
    }

    public class LaserUnitAssemblerBuild extends LaserPayloadBlockBuild<Payload> {
        protected IntSeq readUnits = new IntSeq();
        //holds drone IDs that have been sent, but not synced yet - add to list as soon as possible
        protected IntSeq whenSyncedUnits = new IntSeq();

        public @Nullable Vec2 commandPos;
        public Seq<Unit> units = new Seq<>();
        public Seq<LaserUnitAssemblerModule.LaserUnitAssemblerModuleBuild> modules = new Seq<>();
        public PayloadSeq blocks = new PayloadSeq();
        public float progress, warmup, droneWarmup, powerWarmup, sameTypeWarmup;
        public float invalidWarmup = 0f;
        public int currentTier = 0;
        public int lastTier = -2;
        public boolean wasOccupied = false;

        public float droneProgress, totalDroneProgress;


        public float getNeededLaser() {
            if(currentTier == 1) return consumeLaserTier1;
            return consumeLaserTier0;
        }

        @Override
        public float rawLaserEfficiency() {
            return getNeededLaser() <= 0f ? 1f : Mathf.clamp(lasers.power()/getNeededLaser());
        }

        public Vec2 getUnitSpawn(){
            float len = tilesize * (areaSize + size)/2f;
            float unitX = x + Geometry.d4x(rotation) * len, unitY = y + Geometry.d4y(rotation) * len;
            return Tmp.v4.set(unitX, unitY);
        }

        public boolean moduleFits(Block other, float ox, float oy, int rotation){
            float
                    dx = ox + Geometry.d4x(rotation) * (other.size/2f + 0.5f) * tilesize,
                    dy = oy + Geometry.d4y(rotation) * (other.size/2f + 0.5f) * tilesize;

            Vec2 spawn = getUnitSpawn();

            if(Tile.relativeTo(ox, oy, spawn.x, spawn.y) != rotation){
                return false;
            }

            float dst = Math.max(Math.abs(dx - spawn.x), Math.abs(dy - spawn.y));
            return Mathf.equal(dst, tilesize * areaSize / 2f - tilesize/2f);
        }

        public void updateModules(LaserUnitAssemblerModule.LaserUnitAssemblerModuleBuild build){
            modules.addUnique(build);
            checkTier();
        }

        public void removeModule(LaserUnitAssemblerModule.LaserUnitAssemblerModuleBuild build){
            modules.remove(build);
            checkTier();
        }

        public void checkTier(){
            modules.sort(b -> b.tier());
            int max = 0;
            for(int i = 0; i < modules.size; i++){
                var mod = modules.get(i);
                if(mod.tier() == max || mod.tier() == max + 1){
                    max = mod.tier();
                }else{
                    //tier gap, TODO warning?
                    break;
                }
            }

            currentTier = max;
        }

        public UnitType unit(){
            return plan().unit;
        }

        public UnitAssembler.AssemblerUnitPlan plan(){
            //clamp plan pos
            return plans.get(Math.min(currentTier, plans.size - 1));
        }

        @Override
        public boolean shouldConsume(){
            //liquid is only consumed when building is being done
            return enabled && !wasOccupied && Units.canCreate(team, plan().unit) && consPayload.efficiency(this) > 0;
        }

        @Override
        public void drawSelect(){
            for(var module : modules){
                Drawf.selected(module, Pal.accent);
            }

            Drawf.dashRect(Tmp.c1.set(Pal.accent).lerp(Pal.remove, invalidWarmup), getRect(Tmp.r1, x, y, rotation));
        }

        @Override
        public void display(Table table){
            super.display(table);

            if(team != player.team()) return;

            table.row();
            table.table(t -> {
                t.left().defaults().left();

                Block prev = null;
                for(int i = 0; i < modules.size; i++){
                    var mod = modules.get(i);
                    if(prev == mod.block) continue;
                    //TODO crosses for missing reqs?
                    t.image(mod.block.uiIcon).size(iconMed).padRight(4);

                    prev = mod.block;
                }

                t.label(() -> "[accent] -> []" + unit().emoji() + " " + unit().localizedName);
            }).pad(4).padLeft(0f).fillX().left();
        }

        @Override
        public void updateTile(){
            if(lasers == null) return;
            lasers.update(this);
            if(lasers.power() >= maxLaserPower) {
                Fx.hitMeltdown.create(x,y,0, Pal.accent,new Object());
                damage(10);
            }
            lasers.graph.consumers.each(consumer -> {
                if(consumer instanceof LaserBlockBuilding lb) {
                    lb.lasers.setPower(lasers.power());
                }
            });

            if(!readUnits.isEmpty()){
                units.clear();
                readUnits.each(i -> {
                    var unit = Groups.unit.getByID(i);
                    if(unit != null){
                        units.add(unit);
                    }
                });
                readUnits.clear();
            }

            if(lastTier != currentTier){
                if(lastTier >= 0f){
                    progress = 0f;
                }

                lastTier =
                        lastTier == -2 ? -1 : currentTier;
            }

            //read newly synced drones on client end
            if(units.size < dronesCreated && whenSyncedUnits.size > 0){
                whenSyncedUnits.each(id -> {
                    var unit = Groups.unit.getByID(id);
                    if(unit != null){
                        units.addUnique(unit);
                    }
                });
            }

            units.removeAll(u -> !u.isAdded() || u.dead || !(u.controller() instanceof AssemblerAI));

            //unsupported
            if(!allowUpdate()){
                progress = 0f;
                units.each(Unit::kill);
                units.clear();
            }

            float powerStatus = power == null ? 1f : power.status;
            powerWarmup = Mathf.lerpDelta(powerStatus, powerStatus > 0.0001f ? 1f : 0f, 0.1f);
            droneWarmup = Mathf.lerpDelta(droneWarmup, units.size < dronesCreated ? powerStatus : 0f, 0.1f);
            totalDroneProgress += droneWarmup * delta();

            if(units.size < dronesCreated && (droneProgress += delta() * state.rules.unitBuildSpeed(team) * powerStatus / droneConstructTime) >= 1f){
                if(!net.client()){
                    var unit = droneType.create(team);
                    if(unit instanceof BuildingTetherc bt){
                        bt.building(this);
                    }
                    unit.set(x, y);
                    unit.rotation = 90f;
                    unit.add();
                    units.add(unit);
                    //TODO: Call.assemblerDroneSpawned(tile, unit.id);
                    SvCall.assemblerDroneSpawned(tile,unit.id);
                }
            }

            if(units.size >= dronesCreated){
                droneProgress = 0f;
            }

            Vec2 spawn = getUnitSpawn();

            if(moveInPayload() && !wasOccupied){
                yeetPayload(payload);
                payload = null;
            }

            //arrange units around perimeter
            for(int i = 0; i < units.size; i++){
                var unit = units.get(i);
                var ai = (AssemblerAI)unit.controller();

                ai.targetPos.trns(i * 90f + 45f, areaSize / 2f * Mathf.sqrt2 * tilesize).add(spawn);
                ai.targetAngle = i * 90f + 45f + 180f;
            }

            wasOccupied = checkSolid(spawn, false);
            boolean visualOccupied = checkSolid(spawn, true);
            float eff = (units.count(u -> ((AssemblerAI)u.controller()).inPosition()) / (float)dronesCreated);

            sameTypeWarmup = Mathf.lerpDelta(sameTypeWarmup, wasOccupied && !visualOccupied ? 0f : 1f, 0.1f);
            invalidWarmup = Mathf.lerpDelta(invalidWarmup, visualOccupied ? 1f : 0f, 0.1f);

            var plan = plan();

            //check if all requirements are met
            if(!wasOccupied && efficiency > 0 && Units.canCreate(team, plan.unit)){
                warmup = Mathf.lerpDelta(warmup, efficiency, 0.1f);

                if((progress += laserEfficiency() * delta() * state.rules.unitBuildSpeed(team) * eff / plan.time) >= 1f){
                    //TODO: Call.assemblerUnitSpawned(tile);
                    SvCall.assemblerUnitSpawned(tile);
                }
            }else{
                warmup = Mathf.lerpDelta(warmup, 0f, 0.1f);
            }
        }

        public void droneSpawned(int id){
            Fx.spawn.at(x, y);
            droneProgress = 0f;
            if(net.client()){
                whenSyncedUnits.add(id);
            }
        }

        public void spawned(){
            var plan = plan();
            Vec2 spawn = getUnitSpawn();
            consume();

            if(!net.client()){
                var unit = plan.unit.create(team);
                if(unit != null && unit.isCommandable()){
                    unit.command().commandPosition(commandPos);
                }
                unit.set(spawn.x + Mathf.range(0.001f), spawn.y + Mathf.range(0.001f));
                unit.rotation = 90f;
                unit.add();
            }

            progress = 0f;
            Fx.unitAssemble.at(spawn.x, spawn.y, 0f, plan.unit);
            blocks.clear();
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            //draw input conveyors
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }

            Draw.rect(rotation >= 2 ? sideRegion2 : sideRegion1, x, y, rotdeg());

            Draw.z(Layer.blockOver);

            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y);

            if(isPayload()) return;

            //draw drone construction
            if(droneWarmup > 0.001f){
                Draw.draw(Layer.blockOver + 0.2f, () -> {
                    Drawf.construct(this, droneType.fullIcon, Pal.accent, 0f, droneProgress, droneWarmup, totalDroneProgress, 14f);
                });
            }

            Vec2 spawn = getUnitSpawn();
            float sx = spawn.x, sy = spawn.y;

            var plan = plan();

            //draw the unit construction as outline
            //TODO flashes when no gallium
            Draw.draw(Layer.blockBuilding, () -> {
                Draw.color(Pal.accent, warmup);

                Shaders.blockbuild.region = plan.unit.fullIcon;
                Shaders.blockbuild.time = Time.time;
                //margin due to units not taking up whole region
                Shaders.blockbuild.progress = Mathf.clamp(progress + 0.05f);

                Draw.rect(plan.unit.fullIcon, sx, sy);
                Draw.flush();
                Draw.color();
            });

            Draw.reset();

            Draw.z(Layer.buildBeam);

            //draw unit silhouette
            Draw.mixcol(Tmp.c1.set(Pal.accent).lerp(Pal.remove, invalidWarmup), 1f);
            Draw.alpha(Math.min(powerWarmup, sameTypeWarmup));
            Draw.rect(plan.unit.fullIcon, spawn.x, spawn.y);

            //build beams do not draw when invalid
            Draw.alpha(Math.min(1f - invalidWarmup, warmup));

            //draw build beams
            for(var unit : units){
                if(!((AssemblerAI)unit.controller()).inPosition()) continue;

                float
                        px = unit.x + Angles.trnsx(unit.rotation, unit.type.buildBeamOffset),
                        py = unit.y + Angles.trnsy(unit.rotation, unit.type.buildBeamOffset);

                Drawf.buildBeam(px, py, spawn.x, spawn.y, plan.unit.hitSize/2f);
            }

            //fill square in middle
            Fill.square(spawn.x, spawn.y, plan.unit.hitSize/2f);

            Draw.reset();

            Draw.z(Layer.buildBeam);

            float fulls = areaSize * tilesize/2f;

            //draw full area
            Lines.stroke(2f, Pal.accent);
            Draw.alpha(powerWarmup);
            Drawf.dashRectBasic(spawn.x - fulls, spawn.y - fulls, fulls*2f, fulls*2f);

            Draw.reset();

            float outSize = plan.unit.hitSize + 9f;

            if(invalidWarmup > 0){
                //draw small square for area
                Lines.stroke(2f, Tmp.c3.set(Pal.accent).lerp(Pal.remove, invalidWarmup).a(invalidWarmup));
                Drawf.dashSquareBasic(spawn.x, spawn.y, outSize);
            }

            Draw.reset();
        }

        public boolean checkSolid(Vec2 v, boolean same){
            var output = unit();
            float hsize = output.hitSize * 1.4f;
            return ((!output.flying && collisions.overlapsTile(Tmp.r1.setCentered(v.x, v.y, output.hitSize), EntityCollisions::solid)) ||
                    Units.anyEntities(v.x - hsize/2f, v.y - hsize/2f, hsize, hsize, u -> (!same || u.type != output) && !u.spawnedByCore &&
                            ((u.type.allowLegStep && output.allowLegStep) || (output.flying && u.isFlying()) || (!output.flying && u.isGrounded()))));
        }

        /** @return true if this block is ready to produce units, e.g. requirements met */
        public boolean ready(){
            return efficiency > 0 && !wasOccupied;
        }

        public void yeetPayload(Payload payload){
            var spawn = getUnitSpawn();
            blocks.add(payload.content(), 1);
            float rot = payload.angleTo(spawn);
            Fx.shootPayloadDriver.at(payload.x(), payload.y(), rot);
            Fx.payloadDeposit.at(payload.x(), payload.y(), rot, new UnitAssembler.YeetData(spawn.cpy(), payload.content()));
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return progress;
            return super.sense(sensor);
        }

        @Override
        public PayloadSeq getPayloads(){
            return blocks;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            var plan = plan();
            return (this.payload == null || source instanceof UnitAssemblerModule.UnitAssemblerModuleBuild) &&
                    plan.requirements.contains(b -> b.item == payload.content() && blocks.get(payload.content()) < Mathf.round(b.amount * state.rules.unitCost(team)));
        }

        @Override
        public Vec2 getCommandPosition(){
            return commandPos;
        }

        @Override
        public void onCommand(Vec2 target){
            commandPos = target;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(progress);
            write.b(units.size);
            for(var unit : units){
                write.i(unit.id);
            }

            blocks.write(write);
            TypeIO.writeVecNullable(write, commandPos);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            int count = read.b();
            readUnits.clear();
            for(int i = 0; i < count; i++){
                readUnits.add(read.i());
            }
            whenSyncedUnits.clear();

            blocks.read(read);
            if(revision >= 1){
                commandPos = TypeIO.readVecNullable(read);
            }
        }
    }
}
