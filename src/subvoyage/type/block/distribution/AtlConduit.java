package subvoyage.type.block.distribution;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.input.Placement;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.DirectionBridge;
import mindustry.world.blocks.distribution.DirectionLiquidBridge;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidJunction;

import static mindustry.Vars.renderer;
import static mindustry.Vars.world;

public class AtlConduit extends Conduit {
    static final float rotatePad = 6, hpad = rotatePad / 2f / 4f;
    static final float[][] rotateOffsets = {{hpad, hpad}, {-hpad, hpad}, {-hpad, -hpad}, {hpad, -hpad}};

    public boolean armored = false;

    public AtlConduit(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();
        if(junctionReplacement == Blocks.liquidJunction) junctionReplacement = null;
        if(armored) leaks = false;
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
        return otherblock.hasLiquids && ((otherblock.outputsLiquid && (!armored || blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock))) || (lookingAt(tile, rotation, otherx, othery, otherblock))) && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock) || otherblock instanceof LiquidJunction;
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans) {
        if(bridgeReplacement == null) return;

        if(rotBridgeReplacement instanceof DirectionBridge duct){
            Placement.calculateBridges(plans, duct, false, b -> b instanceof Conduit);
        }else{
            Placement.calculateBridges(plans, (ItemBridge)bridgeReplacement, false, b -> b instanceof Conduit);
        }
    }

    public class AtlConduitBuild extends ConduitBuild {

        @Override
        public void updateTile() {
            if(liquids.currentAmount() > 0.0001f && timer(timerFlow, 1)){
                moveLiquidForward(leaks, liquids.current());
                noSleep();
            } else {
                sleep();
            }
            if(front() instanceof ConduitBuild) {
                front().noSleep();
                if(timer(timerFlow, 1)) front().moveLiquidForward(leaks, front().liquids.current());
            }
            if(back() instanceof ConduitBuild) {
                back().noSleep();
                if(timer(timerFlow, 1)) back().moveLiquidForward(leaks, back().liquids.current());
            }
        }

        float[] smoothieLiquids = new float[4];
        @Override
        protected void drawAt(float x, float y, int bits, int rotation, SliceMode slice) {
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f);
            float angle = rotation * 90f;
            Draw.color(botColor);
            Draw.rect(sliced(botRegions[bits], slice), x, y, angle);

            int offset = yscl == -1 ? 3 : 0;

            int frame = liquids.current().getAnimationFrame();
            int gas = liquids.current().gas ? 1 : 0;
            float ox = 0f, oy = 0f;
            int wrapRot = (rotation + offset) % 4;
            TextureRegion liquidr = bits == 1 && padCorners ? rotateRegions[wrapRot][gas][frame] : renderer.fluidFrames[gas][frame];

            if(bits == 1 && padCorners){
                ox = rotateOffsets[wrapRot][0];
                oy = rotateOffsets[wrapRot][1];
            }

            //the drawing state machine sure was a great design choice with no downsides or hidden behavior!!!
            float xscl = Draw.xscl, yscl = Draw.yscl;
            Draw.scl(1f,1f);

            float smoothLiqs = 0f;
            int count = 0;
            for (float smoothieLiquid : smoothieLiquids) {
                smoothLiqs += smoothieLiquid;
                if(smoothieLiquid > 0.0001f) count++;
            }
            smoothLiqs /= Math.max(1,count);

            int rot = -1;
            for (Point2 dir : Geometry.d4) {
                rot++;
                if(!blends(tile,rotation,null,Mathf.mod(rotation-rot,4),true) && rot != rotation) {
                    smoothieLiquids[rot] = 0f;
                    continue;
                }

                float smLiq = liquidFullness(this, liquids.current());
                Building bui = world.build(tileX() + dir.x, tileY() + dir.y);
                if(rot != rotation && bui != null)
                    smLiq = liquidFullness(bui, liquids.current());
                smLiq = Mathf.clamp(smLiq);

                smoothieLiquids[rot] = Mathf.lerpDelta(smoothieLiquids[rot], Math.max(smLiq,0.0001f), 0.05f);

                Lines.stroke(smoothLiqs * 4.125f,liquids.current().color.write(Tmp.c2).a(Mathf.clamp(smoothLiqs * 2f)));
                Lines.line(x,y,x + dir.x * 4f, y + dir.y * 4f, false);
            }
            Draw.color();
            //Drawf.liquid(sliced(liquidr, slice), x + ox, y + oy, Mathf.clamp(smoothLiquid*2f), liquids.current().color.write(Tmp.c1).a(1f));
            Draw.scl(xscl, yscl);

            Draw.rect(sliced(topRegions[bits], slice), x, y, angle);
        }

        private float liquidFullness(Building building, Liquid liquid) {
            return building.liquids.currentAmount() / building.block.liquidCapacity;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if(armored) return super.acceptLiquid(source, liquid) && (tile == null || source.block instanceof Conduit || source.block instanceof DirectionLiquidBridge || source.block instanceof LiquidJunction ||
                    source.tile.absoluteRelativeTo(tile.x, tile.y) == rotation || !source.proximity.contains(this));
            return super.acceptLiquid(source, liquid);
        }
    }
}
