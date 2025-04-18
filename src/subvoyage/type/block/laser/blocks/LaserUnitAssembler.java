package subvoyage.type.block.laser.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.Element;
import arc.struct.IntSeq;
import arc.util.Reflect;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.core.Version;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.PayloadStack;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import subvoyage.content.other.SvStat;
import subvoyage.type.block.laser.LaserBlock;
import subvoyage.type.block.laser.LaserBuild;
import subvoyage.type.block.laser.LaserGraph;
import subvoyage.type.block.laser.LaserUtil;

import java.lang.reflect.InvocationTargetException;

public class LaserUnitAssembler extends UnitAssembler implements LaserBlock {
    public float consumeLaserTier0 = 0f;
    public float consumeLaserTier1 = 0f;

    public IntSeq inputs = IntSeq.with(0,1,2,3);
    public IntSeq outputs = IntSeq.with();

    public short inputRange = 8,outputRange = 0;
    public byte maxSuppliers = 1;

    public float capacity = 300f;

    public float laserMaxEfficiency = 1f;
    public float laserOverpowerScale = 1f;

    public boolean drawInputs = false;
    public boolean drawOutputs = true;

    public LaserUnitAssembler(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("laser", (entity) -> {
            if(entity instanceof LaserBuild lb)
                return new Bar(
                        () -> Core.bundle.format("bar.laserpercent", (int)(lb.rawLaser() + 0.01F), (int)(entity.efficiencyScale() * 100.0F + 0.01F)),
                        () -> LaserUtil.getLaserColor(lb.rawLaser()),
                        () -> lb.laser() / lb.laserRequirement());
            return new Bar();
        });
    }



    @Override public short inputRange() {return inputRange;}
    @Override public short outputRange() {return outputRange;}

    @Override public byte maxSuppliers() {return maxSuppliers;}

    @Override public IntSeq inputs() {return inputs;}
    @Override public IntSeq outputs() {return outputs;}

    @Override
    public void setStats(){
        stats.add(Stat.size, "@x@", size, size);

        if(synthetic()){
            stats.add(Stat.health, health, StatUnit.none);
            if(armor > 0){
                stats.add(Stat.armor, armor, StatUnit.none);
            }
        }

        if(canBeBuilt() && requirements.length > 0){
            stats.add(Stat.buildTime, buildTime / 60, StatUnit.seconds);
            stats.add(Stat.buildCost, StatValues.items(false, requirements));
        }

        if(instantTransfer){
            stats.add(Stat.maxConsecutive, 2, StatUnit.none);
        }

        for(var c : consumers){
            c.display(stats);
        }

        //Note: Power stats are added by the consumers.
        if(hasLiquids) stats.add(Stat.liquidCapacity, liquidCapacity, StatUnit.liquidUnits);
        if(hasItems && itemCapacity > 0) stats.add(Stat.itemCapacity, itemCapacity, StatUnit.items);

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
                                if(Version.isAtLeast("147")) {
                                    req.add((Element)
                                            Reflect.invoke(StatValues.class,"stack",new Object[] {stack},PayloadStack.class)).pad(5);
                                } else {
                                    try {
                                        Class<?> itemImage = Class.forName("mindustry.ui.ItemImage");
                                        req.add((Element) itemImage.getConstructor(PayloadStack.class).newInstance(stack)).pad(5);
                                    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                                             InstantiationException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
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
        if(laserMaxEfficiency > 0) stats.add(Stat.maxEfficiency,laserMaxEfficiency*100f, StatUnit.percent);
    }


    public class LaserAssemblerBuild extends UnitAssemblerBuild implements LaserBuild {
        LaserGraph graph;

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
        public float efficiencyScale() {
            float over = Math.max(laser() - laserRequirement(), 0.0F);
            return Math.min(Mathf.clamp(laser() / laserRequirement()) + over / laserRequirement() * laserOverpowerScale, laserMaxEfficiency);
        }

        @Override
        public float laser() {
            return graph().broken() ? 0f : inputLaser(this);
        }
        @Override
        public float rawLaser() {
            return inputLaser(this);
        }

        @Override
        public float laserRequirement() {
            return getNeededLaser();
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
            return false;
        }

        @Override
        public LaserGraph graph() {
            return graph;
        }

        public float getNeededLaser() {
            if(currentTier == 1) return consumeLaserTier1;
            return consumeLaserTier0;
        }

        @Override
        public void draw() {
            drawStatus(this);
            super.draw();
        }
        @Override
        public void updateTile() {
            super.updateTile();
            updateLaser(this);
        }
    }
}
