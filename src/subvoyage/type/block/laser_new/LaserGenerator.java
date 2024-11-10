package subvoyage.type.block.laser_new;

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
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import subvoyage.anno.LoadAnno;
import subvoyage.type.block.laser.LaserUtil;

import static mindustry.Vars.*;
import static mindustry.Vars.player;

public class LaserGenerator extends Block implements LaserBlock {
    public TextureRegion heatRegion;
    public TextureRegion laserRegion;
    public TextureRegion laserTopRegion;
    public TextureRegion laserStartRegion;

    public IntSeq inputs = IntSeq.with();
    public IntSeq outputs = IntSeq.with(0);

    public short inputRange = 0,outputRange = 8;
    public byte maxSuppliers = 4;

    public float capacity = 60f;

    public float laserOutput = 10f;

    public @LoadAnno("@-top1") TextureRegion top1;
    public @LoadAnno(value = "@-top2",def = "@-top1") TextureRegion top2;

    public float itemDuration = 120f;

    public LaserGenerator(String name) {
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
    public void init() {
        super.init();
        clipSize = Math.max(clipSize, Math.max(inputRange(),outputRange()) * tilesize);
    }
    @Override
    public void load() {
        super.load();
        laserRegion = Core.atlas.find(name+"-laser","subvoyage-power-laser");
        laserTopRegion = Core.atlas.find(name+"-laser-top","subvoyage-power-laser-top");
        laserStartRegion = Core.atlas.find(name+"-laser-start","subvoyage-power-laser-start");
        heatRegion = Core.atlas.find(name+"-heat");
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
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        super.drawPlanConfig(plan, list);
        drawLinks(this,plan.x,plan.y,plan.rotation,false,true);
    }

    @Override public short inputRange() {return inputRange;}
    @Override public short outputRange() {return outputRange;}

    @Override public byte maxSuppliers() {return maxSuppliers;}

    @Override public IntSeq inputs() {return inputs;}
    @Override public IntSeq outputs() {return outputs;}

    @Override public TextureRegion laserRegion() {return laserRegion;}
    @Override public TextureRegion laserStartRegion() {return laserStartRegion;}
    @Override public TextureRegion laserTopRegion() {return laserTopRegion;}



    public class LaserGeneratorBuild extends Building implements LaserBuild {

        LaserGraph graph;
        float generationTime = 0f;

        @Override
        public void updateTile() {
            super.updateTile();
            boolean valid = this.efficiency > 0.0F;
            if (hasItems && valid && this.generationTime <= 0.0F) {
                this.consume();
                generationTime = 1.0F;
            }
            generationTime-=delta()/itemDuration;
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
                drawLaser(x,y,consumer.x,consumer.y,size,consumer.block.size,smthScl);
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
            return graph().broken() ? 0f : laserOutput*efficiency;
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
            return false;
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
