package subvoyage.content.unit.entity;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.LegsUnit;
import mindustry.gen.MechUnit;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.world.blocks.environment.Floor;
import subvoyage.content.unit.SvUnits;

public class HydromechUnitEntity extends LegsUnit {

    protected transient Trail tleft;
    protected transient Color trailColor;
    protected transient Trail tright;

    @Override
    public int classId() {
        return SvUnits.mapHMech;
    }

    protected HydromechUnitEntity() {
        this.tleft = new Trail(1);
        this.trailColor = Blocks.water.mapColor.cpy().mul(1.5F);
        this.tright = new Trail(1);
    }

    public static HydromechUnitEntity create() {
        return  new HydromechUnitEntity();
    }


    @Override
    public void controlWeapons(boolean rotate, boolean shoot) {
        super.controlWeapons(rotate, shoot);
    }

    @Override
    public Vec2 legOffset(Vec2 out, int index) {
        return isOnLiquid() ? Vec2.ZERO : super.legOffset(out, index);
    }

    public boolean isOnLiquid() {
        return floorOn().isLiquid && tileOn().block().isAir();
    }

    @Override
    public void damage(float amount) {
        super.damage(amount*(isOnLiquid() ? 1f : 1.2f));
    }

    @Override
    public boolean emitWalkSound() {
        return false;
    }

    @Override
    public void draw() {
        super.draw();
        if(isOnLiquid()) {
            Draw.z(Layer.groundUnit-5f);
            tleft.draw(trailColor,8f);
            tright.draw(trailColor,8f);
        }
    }

    @Override
    public void update() {
        super.update();


        for(int i = 0; i < 2; ++i) {
            Trail t = i == 0 ? this.tleft : this.tright;
            t.length = this.type.trailLength;
            int sign = i == 0 ? -1 : 1;
            float cx = Angles.trnsx(this.rotation - 90.0F, this.type.waveTrailX * (float)sign, this.type.waveTrailY) + this.x;
            float cy = Angles.trnsy(this.rotation - 90.0F, this.type.waveTrailX * (float)sign, this.type.waveTrailY) + this.y;
            t.update(cx, cy, isOnLiquid() ? 0.5F : 0.0F);
        }
    }

    @Override
    public float speed() {
        return super.speed() * (isOnLiquid() ? 1f : 0.5f);
    }
}
