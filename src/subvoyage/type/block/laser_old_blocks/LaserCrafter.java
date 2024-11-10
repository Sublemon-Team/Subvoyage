package subvoyage.type.block.laser_old_blocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import subvoyage.type.block.laser_old.LaserBlock;
import subvoyage.type.block.laser.LaserUtil;

public class LaserCrafter extends LaserBlock {

    @Nullable
    public ItemStack outputItem;
    @Nullable
    public ItemStack[] outputItems;
    @Nullable
    public LiquidStack outputLiquid;
    @Nullable
    public LiquidStack[] outputLiquids;
    public int[] liquidOutputDirections = new int[]{-1};
    public boolean dumpExtraLiquid = true;
    public boolean ignoreLiquidFullness = false;
    public float craftTime = 80.0F;
    public Effect craftEffect;
    public Effect updateEffect;
    public float updateEffectChance;
    public float warmupSpeed;
    public boolean legacyReadWarmup;
    public DrawBlock drawer;
    public float maxLaserEfficiency = 1f;
    public float laserOverpowerScale = 1f;

    public LaserCrafter(String name) {
        super(name);
        this.craftEffect = Fx.none;
        this.updateEffect = Fx.none;
        this.updateEffectChance = 0.04F;
        this.warmupSpeed = 0.019F;
        this.legacyReadWarmup = false;
        this.drawer = new DrawDefault();
        this.update = true;
        this.solid = true;
        this.hasItems = true;
        this.ambientSound = Sounds.machine;
        this.sync = true;
        this.ambientSoundVolume = 0.03F;
        this.flags = EnumSet.of(new BlockFlag[]{BlockFlag.factory});
        this.drawArrow = false;
    }

    public void setStats() {
        this.stats.timePeriod = this.craftTime;
        super.setStats();
        if (this.hasItems && this.itemCapacity > 0 || this.outputItems != null) {
            this.stats.add(Stat.productionTime, this.craftTime / 60.0F, StatUnit.seconds);
        }

        if (this.outputItems != null) {
            this.stats.add(Stat.output, StatValues.items(this.craftTime, this.outputItems));
        }

        if (this.outputLiquids != null) {
            this.stats.add(Stat.output, StatValues.liquids(1.0F, this.outputLiquids));
        }

    }

    public void setBars() {
        super.setBars();
        if (this.outputLiquids != null && this.outputLiquids.length > 0) {
            this.removeBar("liquid");
            LiquidStack[] var1 = this.outputLiquids;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                LiquidStack stack = var1[var3];
                this.addLiquidBar(stack.liquid);
            }
        }
        this.removeBar("laser_power");
        this.addBar("laser", (entity) -> {
            if(entity instanceof LaserBlockBuilding lb)
                return new Bar(() -> {
                    return Core.bundle.format("bar.laserpercent", new Object[]{(int)(lb.lasers.power() + 0.01F), (int)(entity.efficiencyScale() * 100.0F + 0.01F)});
                }, () -> {
                    return LaserUtil.getLaserColor(lb.lasers.power());
                }, () -> {
                    return lb.lasers.power() / consumeLaserPower;
                });
            return new Bar();
        });

    }

    public boolean rotatedOutput(int x, int y) {
        return false;
    }

    public void load() {
        super.load();
        this.drawer.load(this);
    }

    public void init() {
        if (this.outputItems == null && this.outputItem != null) {
            this.outputItems = new ItemStack[]{this.outputItem};
        }

        if (this.outputLiquids == null && this.outputLiquid != null) {
            this.outputLiquids = new LiquidStack[]{this.outputLiquid};
        }

        if (this.outputLiquid == null && this.outputLiquids != null && this.outputLiquids.length > 0) {
            this.outputLiquid = this.outputLiquids[0];
        }

        this.outputsLiquid = this.outputLiquids != null;
        if (this.outputItems != null) {
            this.hasItems = true;
        }

        if (this.outputLiquids != null) {
            this.hasLiquids = true;
        }

        super.init();
    }

    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        this.drawer.drawPlan(this, plan, list);
    }

    public TextureRegion[] icons() {
        return this.drawer.finalIcons(this);
    }

    public boolean outputsItems() {
        return this.outputItems != null;
    }

    public void getRegionsToOutline(Seq<TextureRegion> out) {
        this.drawer.getRegionsToOutline(this, out);
    }

    public void drawOverlay(float x, float y, int rotation) {
        if (this.outputLiquids != null) {
            for(int i = 0; i < this.outputLiquids.length; ++i) {
                int dir = this.liquidOutputDirections.length > i ? this.liquidOutputDirections[i] : -1;
                if (dir != -1) {
                    Draw.rect(this.outputLiquids[i].liquid.fullIcon, x + (float) Geometry.d4x(dir + rotation) * ((float)(this.size * 8) / 2.0F + 4.0F), y + (float)Geometry.d4y(dir + rotation) * ((float)(this.size * 8) / 2.0F + 4.0F), 8.0F, 8.0F);
                }
            }
        }

    }
    

    public class LaserCrafterBuild extends LaserBlockBuilding {
        public float progress;
        public float totalProgress;
        public float warmup;

        public void draw() {
            drawer.draw(this);
        }

        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }

        public boolean shouldConsume() {
            int var3;
            if (outputItems != null) {
                ItemStack[] var1 = outputItems;
                int var2 = var1.length;

                for(var3 = 0; var3 < var2; ++var3) {
                    ItemStack output = var1[var3];
                    if (this.items.get(output.item) + output.amount > itemCapacity) {
                        return false;
                    }
                }
            }

            if (outputLiquids != null && !ignoreLiquidFullness) {
                boolean allFull = true;
                LiquidStack[] var7 = outputLiquids;
                var3 = var7.length;

                for(int var8 = 0; var8 < var3; ++var8) {
                    LiquidStack outputx = var7[var8];
                    if (this.liquids.get(outputx.liquid) >= liquidCapacity - 0.001F) {
                        if (!dumpExtraLiquid) {
                            return false;
                        }
                    } else {
                        allFull = false;
                    }
                }

                if (allFull) {
                    return false;
                }
            }

            return this.enabled;
        }

        public void updateTile() {
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
            if (this.efficiency > 0.0F) {
                this.progress += this.getProgressIncrease(craftTime);
                this.warmup = Mathf.approachDelta(this.warmup, this.warmupTarget(), warmupSpeed);
                if (outputLiquids != null) {
                    float inc = this.getProgressIncrease(1.0F);
                    LiquidStack[] var2 = outputLiquids;
                    int var3 = var2.length;

                    for(int var4 = 0; var4 < var3; ++var4) {
                        LiquidStack output = var2[var4];
                        this.handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - this.liquids.get(output.liquid)));
                    }
                }

                if (this.wasVisible && Mathf.chanceDelta((double)updateEffectChance)) {
                    updateEffect.at(this.x + Mathf.range((float)size * 4.0F), this.y + (float)Mathf.range(size * 4));
                }
            } else {
                this.warmup = Mathf.approachDelta(this.warmup, 0.0F, warmupSpeed);
            }

            this.totalProgress += this.warmup * Time.delta;
            if (this.progress >= 1.0F) {
                this.craft();
            }

            this.dumpOutputs();
        }

        @Override
        public float efficiencyScale() {
            float over = Math.max(lasers.power() - consumeLaserPower, 0.0F);
            return Math.min(Mathf.clamp(lasers.power() / consumeLaserPower) + over / consumeLaserPower * laserOverpowerScale, maxLaserEfficiency);
        }

        public float warmupTarget() {
            return Mathf.clamp(lasers.power() / consumeLaserPower);
        }

        public float getProgressIncrease(float baseTime) {
            if (ignoreLiquidFullness) {
                return super.getProgressIncrease(baseTime);
            } else {
                float scaling = 1.0F;
                float max = 1.0F;
                if (outputLiquids != null) {
                    max = 0.0F;
                    LiquidStack[] var4 = outputLiquids;
                    int var5 = var4.length;

                    for(int var6 = 0; var6 < var5; ++var6) {
                        LiquidStack s = var4[var6];
                        float value = (liquidCapacity - this.liquids.get(s.liquid)) / (s.amount * this.edelta());
                        scaling = Math.min(scaling, value);
                        max = Math.max(max, value);
                    }
                }

                return super.getProgressIncrease(baseTime) * (dumpExtraLiquid ? Math.min(max, 1.0F) : scaling);
            }
        }

        public float warmup() {
            return this.warmup;
        }

        public float totalProgress() {
            return this.totalProgress;
        }

        public void craft() {
            this.consume();
            if (outputItems != null) {
                ItemStack[] var1 = outputItems;
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    ItemStack output = var1[var3];

                    for(int i = 0; i < output.amount; ++i) {
                        this.offload(output.item);
                    }
                }
            }

            if (this.wasVisible) {
                craftEffect.at(this.x, this.y);
            }

            this.progress %= 1.0F;
        }

        public void dumpOutputs() {
            int dir;
            if (outputItems != null && this.timer(timerDump, 5.0F / this.timeScale)) {
                ItemStack[] var1 = outputItems;
                dir = var1.length;

                for(int var3 = 0; var3 < dir; ++var3) {
                    ItemStack output = var1[var3];
                    this.dump(output.item);
                }
            }

            if (outputLiquids != null) {
                for(int i = 0; i < outputLiquids.length; ++i) {
                    dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;
                    this.dumpLiquid(outputLiquids[i].liquid, 2.0F, dir);
                }
            }

        }

        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress) {
                return (double)this.progress();
            } else {
                return sensor == LAccess.totalLiquids && outputLiquid != null ? (double)this.liquids.get(outputLiquid.liquid) : super.sense(sensor);
            }
        }

        public float progress() {
            return Mathf.clamp(this.progress);
        }

        public int getMaximumAccepted(Item item) {
            return itemCapacity;
        }

        public boolean shouldAmbientSound() {
            return this.efficiency > 0.0F;
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.progress);
            write.f(this.warmup);
            if (legacyReadWarmup) {
                write.f(0.0F);
            }

        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.progress = read.f();
            this.warmup = read.f();
            if (legacyReadWarmup) {
                read.f();
            }

        }
    }
}
