package subvoyage.type.block.distribution;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Queue;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Timer;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.PayloadMassDriver;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import subvoyage.SubvoyageMod;
import subvoyage.draw.visual.SvFx;
import subvoyage.utility.SvMath;

import static mindustry.Vars.*;
import static mindustry.world.blocks.payloads.PayloadMassDriver.PayloadDriverState.*;

public class PayloadLaunchPad extends PayloadBlock {
    public float range = 160f;
    public float launchTime = 20f;
    public float transportationTime = 60f;
    public float maxPayloadSize = 3;

    public Effect shootEffect = Fx.shootBig2;
    public Effect smokeEffect = Fx.shootPayloadDriver;
    public Effect receiveEffect = Fx.payloadReceive;
    public Sound shootSound = Sounds.shootBig;

    public float shake = 3f;

    public TextureRegion rocketRegion, rocketOpenRegion, side1Region,side2Region;

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top", SubvoyageMod.ID+"-"+"factory-top-" + size + regionSuffix);
        outRegion = Core.atlas.find(name + "-out", SubvoyageMod.ID+"-"+"factory-out-" + size + regionSuffix);
        inRegion = Core.atlas.find(name + "-in", SubvoyageMod.ID+"-"+"factory-in-" + size + regionSuffix);
        side1Region = Core.atlas.find(name+"-side1");
        side2Region = Core.atlas.find(name+"-side2",side1Region);
        rocketRegion = Core.atlas.find(name+"-rocket");
        rocketOpenRegion = Core.atlas.find(name+"-rocket-open");
        SvFx.payloadLaunchPadRocketLaunch.lifetime = SvFx.payloadLaunchPadRocketLand.lifetime = transportationTime/3f;
    }

    public PayloadLaunchPad(String name) {
        super(name);
        update = true;
        solid = true;
        regionSuffix = "-fortified";
        configurable = true;
        hasPower = true;
        outlineIcon = true;
        sync = true;
        outputsPayload = true;
        rotateDraw = false;
        regionRotated1 = 1;
        rotate = true;
        group = BlockGroup.units;

        config(Point2.class, (PayloadLaunchPadBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (PayloadLaunchPadBuild tile, Integer point) -> tile.link = point);
    }

    @Override
    public void init(){
        super.init();
        updateClipRadius(range);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.payloadCapacity, StatValues.squared(maxPayloadSize, StatUnit.blocksSquared));
        stats.add(Stat.reload, 60f / (launchTime+transportationTime), StatUnit.perSecond);
        stats.add(Stat.shootRange, range / tilesize, StatUnit.blocks);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[] {outRegion,region,side1Region,rocketRegion};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        super.drawPlanRegion(plan, list);
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(rocketRegion, plan.drawx(), plan.drawy());
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (PayloadLaunchPadBuild build) -> new Bar(() -> Core.bundle.get("bar.launchcooldown"), () -> Pal.ammo, () -> Mathf.clamp(build.launchWarmup)));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashSquare(Pal.accent,x * tilesize, y * tilesize, range*2f);

        //check if a launch pad is selected while placing this pad
        if(!control.input.config.isShown()) return;
        Building selected = control.input.config.getSelected();
        if(selected == null || selected.block != this || !SvMath.withinSquare(selected,x*tilesize,y*tilesize,range)) return;

        //if so, draw a dotted line towards it while it is in range
        float sin = Mathf.absin(Time.time, 6f, 1f);
        Tmp.v1.set(x * tilesize + offset, y * tilesize + offset).sub(selected.x, selected.y).limit((size / 2f + 1) * tilesize + sin + 0.5f);
        float x2 = x * tilesize - Tmp.v1.x, y2 = y * tilesize - Tmp.v1.y,
                x1 = selected.x + Tmp.v1.x, y1 = selected.y + Tmp.v1.y;
        int segs = (int)(selected.dst(x * tilesize, y * tilesize) / tilesize);

        Lines.stroke(4f, Pal.gray);
        Lines.dashLine(x1, y1, x2, y2, segs);
        Lines.stroke(2f, Pal.placing);
        Lines.dashLine(x1, y1, x2, y2, segs);
        Draw.reset();
    }

    public class PayloadLaunchPadBuild extends PayloadBlockBuild<Payload> {
        public int link = -1;
        public PayloadLaunchPadBuild lastOther;
        public PayloadMassDriver.PayloadDriverState state = idle;
        public Queue<Building> waitingShooters = new Queue<>();
        public float launchWarmup = 0f;
        public boolean loaded;
        public boolean inProgress = false;
        public float inProgressSmooth = 0f;

        @Override
        public void updateTile() {
            super.updateTile();
            Building link = world.build(this.link);
            boolean hasLink = linkValid();

            inProgressSmooth = Mathf.lerp(inProgressSmooth,(inProgress||payload == null) ? 1f : 0f,Time.delta/20f);

            if(hasLink){
                this.link = link.pos();
            }

            var current = currentShooter();

            //cleanup waiting shooters that are not valid
            if(current != null &&
                    !(
                            current instanceof PayloadLaunchPadBuild entity &&
                                    current.isValid() &&
                                    entity.efficiency > 0 && entity.block == block &&
                                    entity.link == pos() && SvMath.withinSquare(current,this,range)
                    )){
                waitingShooters.removeFirst();
            }

            if(state == idle){
                //start accepting when idle and there's space
                if(!waitingShooters.isEmpty() && payload == null){
                    state = accepting;
                }else if(hasLink){ //switch to shooting if there's a valid link.
                    state = shooting;
                }
            }

            if((state == idle || state == accepting) && payload != null){
                moveOutPayload();
            }

            if(state == accepting) {
                if(currentShooter() == null || payload != null){
                    state = idle;
                    return;
                }
            } else if (state == shooting) {
                //if there's nothing to shoot at OR someone wants to shoot at this thing, bail
                if(!hasLink || (!waitingShooters.isEmpty() && payload == null)){
                    state = idle;
                    return;
                }

                boolean movedOut = false;
                if(loaded){
                    movedOut = true;
                }else if(moveInPayload()){
                    loaded = true;
                }

                if(movedOut && payload != null && link.getPayload() == null){
                    var other = (PayloadLaunchPadBuild) link;

                    if(!other.waitingShooters.contains(this)){
                        other.waitingShooters.addLast(this);
                    }
                    //fire when it's the first in the queue and angles are ready.
                    launchWarmup += edelta()/launchTime;
                    other.launchWarmup += edelta()/launchTime;
                    if(other.currentShooter() == this &&
                            other.state == accepting &&
                            other.launchWarmup >= 0.99f && !inProgress){
                        if(other.launchWarmup >= 1f){
                            //effects
                            shootEffect.at(x, y, 0);
                            smokeEffect.at(x, y, 0);

                            inProgress = true;

                            Effect.shake(shake, shake, this);
                            Payload pay = payload;
                            payload = null;
                            //spawn rocket launching at this
                            Sounds.release.at(x,y,0.5f,0.05f);
                            Fx.launchPod.create(x,y,0,Pal.accent,new Object());
                            SvFx.payloadLaunchPadRocketLaunch.create(x,y,0,Pal.accent,new Object());

                            Timer.schedule(() -> {
                                //spawn rocket landing at other
                                loaded = false;
                                state = idle;
                                SvFx.payloadLaunchPadRocketLand.create(other.x,other.y,0,Pal.accent,new Object());
                            },transportationTime/60f/3f*2f);

                            Timer.schedule(() -> {
                                //transfer payload
                                other.handlePayload(this, pay);
                                other.lastOther = this;
                                other.loaded = true;
                                other.updatePayload();
                                Sounds.missileLaunch.at(x,y,0.5f,0.05f);

                                this.inProgress = false;

                                if(other.waitingShooters.size != 0 && other.waitingShooters.first() == this){
                                    other.waitingShooters.removeFirst();
                                }
                                other.state = idle;
                                other.launchWarmup = 0f;
                                launchWarmup = 0f;

                                //reset state after shooting immediately
                            },transportationTime/60f);
                        }
                    }
                }

            }
        }

        @Override
        public void draw() {
            Draw.rect(outRegion,x,y,rotation*90f);
            super.draw();
            Draw.rect(rotation >= 2 ? side2Region : side1Region,x,y,rotation*90f);
            Draw.rect(topRegion,x,y);
            Draw.z(Layer.blockOver);
            Draw.alpha(1f-inProgressSmooth);
            if(payload != null) {
                Draw.scl(8f/payload.size());
                Draw.rect(payload.icon(),x,y);
                Draw.scl();
            }
            Draw.rect(rocketOpenRegion,x,y);
            Draw.reset();
        }

        public Building currentShooter(){
            return waitingShooters.isEmpty() ? null : waitingShooters.first();
        }

        @Override
        public void updatePayload(){
            if(payload != null){
                if(loaded){
                    payload.set(x, y, payRotation);
                }else{
                    payload.set(x + payVector.x, y + payVector.y, payRotation);
                }
            }
        }

        @Override
        public void drawConfigure(){
            float sin = Mathf.absin(Time.time, 6f, 1f);

            Draw.color(Pal.accent);
            Lines.stroke(1f);
            Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.accent);

            for(var shooter : waitingShooters){
                Drawf.circles(shooter.x, shooter.y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(shooter.x, shooter.y, x, y, size * tilesize + sin, 4f + sin, Pal.place);
            }

            if(linkValid()){
                Building target = world.build(link);
                Drawf.circles(target.x, target.y, (target.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(x, y, target.x, target.y, size * tilesize + sin, 4f + sin);
            }

            Drawf.dashSquare(Pal.accent,x, y, range*2f);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return super.acceptPayload(source, payload) && payload.size() <= maxPayloadSize * tilesize && launchWarmup <= 0;
        }

        protected boolean linkValid(){
            return link != -1 && world.build(this.link) instanceof PayloadLaunchPadBuild other && other.block == block && other.team == team && SvMath.withinSquare(other,this,range);
        }

        @Override
        public Point2 config(){
            if(tile == null) return null;
            return Point2.unpack(link).sub(tile.x, tile.y);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(this == other){
                if(link == -1) deselect();
                configure(-1);
                return false;
            }

            if(link == other.pos()){
                configure(-1);
                return false;
            }else if(other.block instanceof PayloadLaunchPad && SvMath.withinSquare(other,this,range) && other.team == team){
                configure(other.pos());
                return false;
            }

            return true;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(link);
            write.b((byte)state.ordinal());

            write.bool(loaded);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            link = read.i();
            state = PayloadMassDriver.PayloadDriverState.all[read.b()];

            if(revision >= 1){
                loaded = read.bool();
            }
        }
    }
}
