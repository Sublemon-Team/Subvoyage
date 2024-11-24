package subvoyage.type.block.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.storage.CoreBlock;
import subvoyage.content.block.SvBlocks;
import subvoyage.content.block.cat.SvProduction;
import subvoyage.draw.visual.SvDraw;
import subvoyage.draw.visual.SvFx;

import static mindustry.Vars.*;
import static mindustry.content.Liquids.water;
import static subvoyage.content.SvItems.clay;

public class ProductionAnchor extends Block {

    public TextureRegion itemCircleRegion;
    public ItemStack[] itemBatches = new ItemStack[0];

    public ProductionAnchor(String name) {
        super(name);
        breakable = false;
        destructible = true;

        rotate = false;
        update = true;
        sync = true;

        hasItems = true;
        unloadable = false;

        fogRadius = 5;
    }

    @Override
    public boolean canBreak(Tile tile) {
        return false;
    }

    @Override
    public void load() {
        super.load();
        itemCircleRegion = Core.atlas.find("ring-item");
    }

    @Override
    public void init() {
        super.init();
        clipSize = 1000000f;
    }

    public class ProductionAnchorBuild extends Building {


        @Override
        public void update() {
            super.update();
            wasVisible = true;
        }

        @Override
        public boolean canPickup() {
            return false;
        }
        Team lastInputTeam;
        @Override
        public void handleItem(Building source, Item item) {
            super.handleItem(source, item);
            lastInputTeam = source.team;
        }

        float fullTime = 0f;
        boolean sent = false;
        boolean deposit = false;
        float waterCount = 0f;
        int clayCount = 0;

        @Override
        public void updateTile() {
            if(team != Team.derelict) {
                if(targetCore == null || targetCore.team != team) targetCore = team.cores().sort((b) -> Mathf.dst(x,y,b.x,b.y)).first();
            } else {

                for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    Point2 out = new Point2();
                    nearbySide(tile.x,tile.y,j,i,out);

                    Building building = world.build(out.x,out.y);
                    if(building != null && (building.block.hasItems || building.block.hasLiquids)) {
                        int remainingClay = 25-clayCount;
                        float remainingWater = 60f-waterCount;
                        if(building.block.hasItems && building.items.has(clay) && clayCount < 25) {
                            clayCount += Math.min(building.items.get(clay),remainingClay);
                            building.items.remove(clay,Math.min(building.items.get(clay),remainingClay));
                            lastInputTeam = building.team;
                        }
                        if(building instanceof Duct.DuctBuild b && b.current == clay) {
                            clayCount += Math.min(1,remainingClay);
                            b.current = null;
                            lastInputTeam = building.team;
                        }
                        if(building.block.hasLiquids && building.liquids.get(water) >= 1f  && waterCount < 60) {
                            waterCount += Math.min(building.liquids.get(water),remainingWater);
                            building.liquids.remove(water,Math.min(building.liquids.get(water),remainingWater));
                            lastInputTeam = building.team;
                        }
                    }
                }
                }

                if(clayCount >= 25 && waterCount >= 60f && lastInputTeam != null) {
                    team = lastInputTeam;
                    state.stats.placedBlockCount.put(SvProduction.productionAnchor,state.stats.placedBlockCount.get(SvProduction.productionAnchor,0)+1);
                }
            }
            if(targetCore != null) {
                float alpha = (Mathf.sinDeg(fullTime*2f-90f)+1)/2f;
                if(alpha <= 0.01f && !sent) {
                    sent = true;
                    deposit = false;
                    if(itemBatches.length != 0) {
                        ItemStack batch = itemBatches[genI%itemBatches.length];
                        items.add(batch.item,batch.amount);
                    }
                    genI++;
                }
                if(alpha >= 0.99f && !deposit) {
                    deposit = true;
                    sent = false;
                    items.each((i,c) -> {
                        targetCore.handleStack(i,c,this);
                    }); 
                    items.clear();
                }
                fullTime += Time.delta;
            }
        }

        @Override
        public boolean allowUpdate() {
            return this.block.supportsEnv(Vars.state.rules.env) && (!Vars.state.rules.limitMapArea || !Vars.state.rules.disableOutsideArea || Rect.contains((float)Vars.state.rules.limitX, (float)Vars.state.rules.limitY, (float)Vars.state.rules.limitWidth, (float)Vars.state.rules.limitHeight, this.tile.x, this.tile.y));
        }

        float teamProgress = 0f;
        float time = 0f;
        Color teamColor = Team.derelict.color.cpy();
        @Nullable
        CoreBlock.CoreBuild targetCore;
        int genI = 0;

        @Override
        public void draw() {
            wasVisible = true;
            Draw.rect(region, x, y);
            Draw.z(Layer.blockOver);
            if(team == Team.derelict) {
                Draw.color(teamColor.cpy().lerp(Color.white,0.9f));
            } else {
                SvDraw.applyBloomBasic(() -> {
                    Draw.color(teamColor);
                    Draw.rect(teamRegion, x, y);

                    float size = Time.time%60f;
                    float size2 = (Time.time+15f)%60f;
                    float fout = 1f-size/60f;
                    float fout2 = 1f-size2/60f;
                    Lines.stroke(fout,teamColor.cpy().a(fout));
                    Lines.circle(x,y,(block.size)*tilesize*teamProgress*Interp.pow2.apply(fout));
                    Lines.stroke(fout2,teamColor.cpy().a(fout2));
                    Lines.circle(x,y,(block.size)*tilesize*teamProgress*Interp.pow2.apply(fout2));
                });
            }
            teamColor.lerp(team.color,Time.delta/10f);
            teamProgress = Mathf.lerp(teamProgress,team == Team.derelict ? 0f : 1f,Time.delta/10f);
            time+=Time.delta;

            if(time >= 1f && targetCore != null) {
                float alpha = (Mathf.sinDeg(fullTime*2f-90f)+1)/2f;
                Tmp.v1.set(x,y).lerp(targetCore.x,targetCore.y,alpha);
                float rot = Tmp.v1.angleTo(x,y);
                SvFx.particle.create(Tmp.v1.x,Tmp.v1.y,rot,teamColor,new Object());
                time = 0f;
            } else if(targetCore != null) {
                float alpha = (Mathf.sinDeg(fullTime*2f-90f)+1)/2f;
                Tmp.v1.set(x,y).lerp(targetCore.x,targetCore.y,alpha);
                Draw.z(Layer.effect+1f);
                float rot = Tmp.v1.angleTo(x,y);
                if(items != null) {
                    Item item = items.first();
                    if (item != null) {
                        float size = (itemSize + Mathf.absin(fullTime, 5f, 1f));
                        Draw.color();
                        Draw.mixcol(Pal.accent, Mathf.absin(fullTime, 5f, 0.1f));
                        Draw.rect(item.fullIcon,
                                Tmp.v1.x,
                                Tmp.v1. y,
                                size, size, rot+90f);
                        Draw.mixcol();
                        if (isLocal() && !renderer.pixelator.enabled()) {
                            Fonts.outline.draw(items.get(item) + "",
                                    Tmp.v1.x,
                                    Tmp.v1.y - 3,
                                    Pal.accent, 0.25f / Scl.scl(1f), false, Align.center
                            );
                        }

                        Draw.reset();
                    }
                }
            }
            if(team != Team.derelict) {
                if(targetCore == null) targetCore = team.cores().sort((b) -> Mathf.dst(x,y,b.x,b.y)).first();
            }
            Draw.z(Layer.block);
        }

        @Override
        public boolean wasVisible() {
            return true;
        }

        Team cacheTeam = null;
        @Override
        public void damage(float damage) {
            if(team == Team.derelict) return;
            if (!this.dead()) {
                float dm = Vars.state.rules.blockHealth(team);
                lastDamageTime = Time.time;
                if (Mathf.zero(dm)) {
                    damage = health + 1f;
                } else {
                    damage /= dm;
                }

                if (!Vars.net.client()) {
                    health -= handleDamage(damage);
                }

                healthChanged();
                if (health <= 0f) {
                    health(maxHealth());
                    healthChanged();
                    if(team == player.team()) {
                        state.stats.placedBlockCount.put(SvProduction.productionAnchor,state.stats.placedBlockCount.get(SvProduction.productionAnchor,0)-1);
                    }
                    team(cacheTeam);
                    //Call.buildDestroyed(this);
                }
            }
        }

        @Override
        public void damage(Team source, float damage) {
            cacheTeam = source;
            damage(damage);
        }

        @Override
        public void write(Writes write) {
            write.i(clayCount);
            write.f(waterCount);
            if(lastInputTeam != null) write.i(lastInputTeam.id);
            else write.i(-1);
        }

        @Override
        public void read(Reads read, byte revision) {
            if(revision == 1) {
                clayCount = read.i();
                waterCount = read.f();
                int teamId = read.i();
                if(teamId == -1) lastInputTeam = null;
                else lastInputTeam = Team.get(teamId);
            }
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void team(Team team) {
            if(this.team != null && this.team.data().buildingTree != null) this.team.data().buildingTree.remove(this);
            super.team(team);
            if(this.team != null && this.team.data().buildingTree != null)this.team.data().buildingTree.insert(this);
        }
    }
}
