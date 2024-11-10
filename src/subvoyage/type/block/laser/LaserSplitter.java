package subvoyage.type.block.laser;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import subvoyage.anno.LoadAnno;

import static mindustry.Vars.player;
import static mindustry.Vars.tilesize;

public class LaserSplitter extends Block implements LaserBlock {

    public TextureRegion heatRegion;

    public IntSeq inputs = IntSeq.range(0,4);
    public IntSeq outputs = IntSeq.range(0,4);

    public short inputRange = 8,outputRange = 8;
    public byte maxSuppliers = 4;

    public float capacity = 60f;

    public @LoadAnno("@-top1") TextureRegion top1;
    public @LoadAnno(value = "@-top2",def = "@-top1") TextureRegion top2;

    public LaserSplitter(String name) {
        super(name);
        destructible = true;
        regionRotated1 = 1;
        update = true;
        quickRotate = true;
        solid = true;
        group = BlockGroup.logic;
        sync = true;

        rotate = true;
        rotateDraw = true;
        replaceable = true;
        allowDiagonal = false;
        drawArrow = false;
    }

    @Override
    public void drawDefaultPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        TextureRegion reg = getPlanRegion(plan, list);
        Draw.rect(reg, plan.drawx(), plan.drawy(), 0f);

        if(plan.worldContext && player != null && teamRegion != null && teamRegion.found()){
            if(teamRegions[player.team().id] == teamRegion) Draw.color(player.team().color);
            Draw.rect(teamRegions[player.team().id], plan.drawx(), plan.drawy());
            Draw.color();
        }

        Draw.rect(plan.rotation > 1 ? top2 : top1, plan.drawx(),plan.drawy(),!rotate || !rotateDraw ? 0 : plan.rotation * 90);

        drawPlanConfig(plan, list);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        if(valid) drawLinks(this,x,y,rotation,true,true);
    }

    @Override
    public void init() {
        super.init();
        clipSize = Math.max(clipSize, Math.max(inputRange(),outputRange()) * tilesize);
    }
    @Override
    public void load() {
        super.load();
        heatRegion = Core.atlas.find(name+"-heat");
        top1 = Core.atlas.find(name+"-top1");
    }

    @Override public short inputRange() {return inputRange;}
    @Override public short outputRange() {return outputRange;}

    @Override public byte maxSuppliers() {return maxSuppliers;}

    @Override public IntSeq inputs() {return inputs;}
    @Override public IntSeq outputs() {return outputs;}

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[] {region,top1};
    }

    public class LaserNodeBuild extends Building implements LaserBuild {

        private LaserGraph graph;


        @Override
        public void updateTile() {
            super.updateTile();
            updateLaser(this);
        }

        float smthScl = 0f;
        @Override
        public void draw() {
            drawStatus(this);

            Draw.rect(this.block.region, this.x, this.y, 0f);
            this.drawTeamTop();
            Draw.rect(rotation > 1 ? top2 : top1,x,y,drawrot());

            if(graph() == null) return;
            float laser = laser();
            float scl = Mathf.clamp(laser);
            smthScl = Mathf.lerp(smthScl, scl, Time.delta/20f);
            Color color = LaserUtil.getLaserColor(laser);
            for (Building consumer : graph().consumers) {
                Draw.color(color);
                drawLaser(x,y,consumer.x,consumer.y,size,consumer.block.size,smthScl,Mathf.clamp((laser-300f)/700f));
            }
        }

        @Override
        public void onRemoved() {
            clearLaser(this);
        }
        @Override
        public void onDestroyed() {clearLaser(this);}

        @Override
        public void created() {
            graph = new LaserGraph();
        }



        @Override
        public float laser() {
            return graph().broken() ? 0f : inputLaser(this)/2f * efficiency;
        }
        @Override
        public float rawLaser() {
            return inputLaser(this)/2f * efficiency;
        }

        @Override
        public float laserRequirement() {
            return 0;
        }

        @Override
        public float maxPower() {
            return capacity;
        }

        @Override
        public boolean consumer() {
            return true;
        }

        @Override
        public boolean supplier() {
            return true;
        }

        @Override
        public LaserGraph graph() {
            return graph;
        }
    }
}
