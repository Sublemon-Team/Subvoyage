package subvoyage.content.unit.entity;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Leg;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.StatusEntry;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.InputHandler;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.*;
import subvoyage.content.unit.*;
import subvoyage.content.unit.type.HydromechState;
import subvoyage.content.unit.type.HydromechUnitType;
import subvoyage.content.unit.type.UnitStatState;

import java.util.HashMap;
import java.util.Iterator;

public class HydromechUnitEntity extends LegsUnit {
    protected transient Trail tleft;
    protected transient Color trailColor;
    protected transient Trail tright;

    public float liquidedSmooth = 0f;
    public HashMap<WeaponMount,Float> mountSpeedMultiplier = new HashMap<>();

    @Override
    public int classId() {
        return SvUnits.mapHMech;
    }

    protected HydromechUnitEntity() {
        this.tleft = new Trail(1);
        this.trailColor = Blocks.water.mapColor.cpy().mul(1.5F);
        this.tright = new Trail(1);
    }

    public boolean sameStateAs(HydromechState state) {
        if(state == HydromechState.ANY) return true;
        else return getState() == state;
    }
    public HydromechState getState() {
        return liquidedSmooth() > 0.5f ? HydromechState.WATER : HydromechState.GROUND;
    }
    public UnitStatState getStatState() {
        return ((HydromechUnitType) type).states.get(getState());
    }
    public UnitStatState getStatState(UnitStatState def) {
        return ((HydromechUnitType) type).states.getOrDefault(getState(),def);
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
        float v = Mathf.lerp(unliquidedSmooth(),isOnLiquid() ? 0f : 1f,Time.delta/60f);
        return super.legOffset(out, index).times(new Vec2(v,v));
    }






    public float liquidedSmooth() {
        return liquidedSmooth;
    }
    public float unliquidedSmooth() {
        return 1-liquidedSmooth();
    }
    public boolean isOnLiquid() {
        return floorOn().isLiquid && tileOn().block().isAir();
    }

    @Override
    public void damage(float amount) {
        super.damage(amount*getStatState().inwardsDamageMul);
    }

    @Override
    public boolean canDrown() {
        return false;
    }

    @Override
    public void updateDrowning() {

    }

    @Override
    public boolean emitWalkSound() {
        return false;
    }

    @Override
    public void draw() {
        this.type.draw(this);
        if(isOnLiquid()) {
            float z = Draw.z();
            Draw.z(20.0F);
            Floor floor = this.tileOn() == null ? Blocks.air.asFloor() : this.tileOn().floor();
            Color color = Tmp.c1.set(floor.mapColor.equals(Color.black) ? Blocks.water.mapColor : floor.mapColor).mul(1.5F);
            this.trailColor.lerp(color, Mathf.clamp(Time.delta * 0.04F));
            this.tleft.draw(this.trailColor, this.type.trailScl);
            this.tright.draw(this.trailColor, this.type.trailScl);
            Draw.z(z);
        }
    }





    @Override
    public float legAngle(int index) {
        float v = super.legAngle(index);
        return Mathf.lerp(v,(180+v)%360,liquidedSmooth());
    }

    @Override
    public void update() {
        float offset;
        float range;
        if (!Vars.net.client() || this.isLocal()) {
            offset = this.x;
            range = this.y;
            this.move(this.vel.x * Time.delta, this.vel.y * Time.delta);
            if (Mathf.equal(offset, this.x)) {
                this.vel.x = 0.0F;
            }

            if (Mathf.equal(range, this.y)) {
                this.vel.y = 0.0F;
            }

            this.vel.scl(Math.max(1.0F - this.drag * Time.delta, 0.0F));
        }

        float cx;
        float cy;
        if (this.type.bounded) {
            offset = 0.0F;
            range = 0.0F;
            cx = (float)Vars.world.unitHeight();
            cy = (float)Vars.world.unitWidth();
            if (Vars.state.rules.limitMapArea && !this.team.isAI()) {
                offset = (float)(Vars.state.rules.limitY * 8);
                range = (float)(Vars.state.rules.limitX * 8);
                cx = (float)(Vars.state.rules.limitHeight * 8) + offset;
                cy = (float)(Vars.state.rules.limitWidth * 8) + range;
            }

            if (!Vars.net.client() || this.isLocal()) {
                float dx = 0.0F;
                float dy = 0.0F;
                if (this.x < range) {
                    dx += -(this.x - range) / 30.0F;
                }

                if (this.y < offset) {
                    dy += -(this.y - offset) / 30.0F;
                }

                if (this.x > cy) {
                    dx -= (this.x - cy) / 30.0F;
                }

                if (this.y > cx) {
                    dy -= (this.y - cx) / 30.0F;
                }

                this.velAddNet(dx * Time.delta, dy * Time.delta);
            }

            if (this.isGrounded()) {
                this.x = Mathf.clamp(this.x, range, cy - 8.0F);
                this.y = Mathf.clamp(this.y, offset, cx - 8.0F);
            }

            if (this.x < -250.0F + range || this.y < -250.0F + offset || this.x >= cy + 250.0F || this.y >= cx + 250.0F) {
                this.kill();
            }
        }

        this.updateBuildLogic();
        Floor floor = this.floorOn();
        if (this.isFlying() != this.wasFlying) {
            if (this.wasFlying && this.tileOn() != null) {
                Fx.unitLand.at(this.x, this.y, this.floorOn().isLiquid ? 1.0F : 0.5F, this.tileOn().floor().mapColor);
            }

            this.wasFlying = this.isFlying();
        }

        if (!this.hovering && this.isGrounded() && (this.splashTimer += Mathf.dst(this.deltaX(), this.deltaY())) >= 7.0F + this.hitSize() / 8.0F) {
            floor.walkEffect.at(this.x, this.y, this.hitSize() / 8.0F, floor.mapColor);
            this.splashTimer = 0.0F;
            if (this.emitWalkSound()) {
                floor.walkSound.at(this.x, this.y, Mathf.random(floor.walkSoundPitchMin, floor.walkSoundPitchMax), floor.walkSoundVolume);
            }
        }

        this.updateDrowning();
        this.hitTime -= Time.delta / 9.0F;
        this.stack.amount = Mathf.clamp(this.stack.amount, 0, this.itemCapacity());
        this.itemTime = Mathf.lerpDelta(this.itemTime, (float)Mathf.num(this.hasItem()), 0.05F);
        if (Mathf.dst(this.deltaX(), this.deltaY()) > 0.001F) {
            this.baseRotation = Angles.moveToward(this.baseRotation, Mathf.angle(this.deltaX(), this.deltaY()), this.type.rotateSpeed);
        }

        if (this.type.lockLegBase) {
            this.baseRotation = this.rotation;
        }

        offset = this.type.legLength;
        if (this.legs.length != this.type.legCount) {
            this.resetLegs();
        }

        range = this.type.legSpeed;
        int div = Math.max(this.legs.length / this.type.legGroupSize, 2);
        this.moveSpace = offset / 1.6F / ((float)div / 2.0F) * this.type.legMoveSpace;
        this.totalLength += this.type.legContinuousMove ? this.type.speed * this.speedMultiplier * Time.delta : Mathf.dst(this.deltaX(), this.deltaY());
        cy = this.moveSpace * 0.85F * this.type.legForwardScl;
        boolean moving = this.moving();
        Vec2 moveOffset = !moving ? Tmp.v4.setZero() : Tmp.v4.trns(Angles.angle(this.deltaX(), this.deltaY()), cy);
        moveOffset = this.curMoveOffset.lerpDelta(moveOffset, 0.1F);
        this.lastDeepFloor = null;
        int deeps = 0;

        for(int i = 0; i < this.legs.length; ++i) {
            float dstRot = this.legAngle(i);
            Vec2 baseOffset = this.legOffset(Tmp.v5, i).add(this.x, this.y);
            Leg l = this.legs[i];
            l.joint.sub(baseOffset).clampLength(this.type.legMinLength * offset / 2.0F, this.type.legMaxLength * offset / 2.0F).add(baseOffset);
            l.base.sub(baseOffset).clampLength(this.type.legMinLength * offset, this.type.legMaxLength * offset).add(baseOffset);
            float stageF = (this.totalLength + (float)i * this.type.legPairOffset) / this.moveSpace;
            int stage = (int)stageF;
            int group = stage % div;
            boolean move = i % div == group;
            boolean side = i < this.legs.length / 2;
            boolean backLeg = Math.abs((float)i + 0.5F - (float)this.legs.length / 2.0F) <= 0.501F;
            if (backLeg && this.type.flipBackLegs) {
                side = !side;
            }

            if (this.type.flipLegSide) {
                side = !side;
            }

            l.moving = move;
            l.stage = moving ? stageF % 1.0F : Mathf.lerpDelta(l.stage, 0.0F, 0.1F);
            floor = Vars.world.floorWorld(l.base.x, l.base.y);
            if (floor.isDeep()) {
                ++deeps;
                this.lastDeepFloor = floor;
            }

            if (l.group != group && liquidedSmooth() < 0.5f) {
                if (!move && (moving || !this.type.legContinuousMove) && i % div == l.group) {
                    if (!Vars.headless && !this.inFogTo(Vars.player.team())) {
                        if (!floor.isLiquid)
                            Fx.unitLandSmall.at(l.base.x, l.base.y, this.type.rippleScale, floor.mapColor);

                        if (this.type.stepShake > 0.0F) {
                            Effect.shake(this.type.stepShake, this.type.stepShake, l.base);
                        }
                    }

                    if (this.type.legSplashDamage > 0.0F) {
                        Damage.damage(this.team, l.base.x, l.base.y, this.type.legSplashRange, this.type.legSplashDamage * Vars.state.rules.unitDamage(this.team), false, true);
                    }
                }

                l.group = group;
            }

            Vec2 legDest = Tmp.v1.trns(dstRot, offset * this.type.legLengthScl).add(baseOffset).add(moveOffset);
            Vec2 jointDest = Tmp.v2;
            InverseKinematics.solve(offset / 2.0F, offset / 2.0F, Tmp.v6.set(l.base).sub(baseOffset), side, jointDest);
            jointDest.add(baseOffset);
            Tmp.v6.set(baseOffset).lerp(l.base, 0.5F);
            if (move) {
                float moveFract = stageF % 1.0F;
                l.base.lerpDelta(legDest, moveFract);
                l.joint.lerpDelta(jointDest, moveFract / 2.0F);
            }

            l.joint.lerpDelta(jointDest, range / 4.0F);
            l.joint.sub(baseOffset).clampLength(this.type.legMinLength * offset / 2.0F, this.type.legMaxLength * offset / 2.0F).add(baseOffset);
            l.base.sub(baseOffset).clampLength(this.type.legMinLength * offset, this.type.legMaxLength * offset).add(baseOffset);
        }

        if (deeps != this.legs.length || !this.floorOn().isDeep()) {
            this.lastDeepFloor = null;
        }

        if (this.mineTile != null) {
            Building core = this.closestCore();
            Item item = this.getMineResult(this.mineTile);
            if (core != null && item != null && !this.acceptsItem(item) && this.within(core, 220.0F) && !this.offloadImmediately()) {
                div = core.acceptStack(this.item(), this.stack().amount, this);
                if (div > 0) {
                    Call.transferItemTo(this, this.item(), div, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), core);
                    this.clearItem();
                }
            }

            if ((!Vars.net.client() || this.isLocal()) && !this.validMine(this.mineTile)) {
                this.mineTile = null;
                this.mineTimer = 0.0F;
            } else if (this.mining() && item != null) {
                this.mineTimer += Time.delta * this.type.mineSpeed;
                if (Mathf.chance(0.06 * (double)Time.delta)) {
                    Fx.pulverizeSmall.at(this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), 0.0F, item.color);
                }

                if (this.mineTimer >= 50.0F + (this.type.mineHardnessScaling ? (float)item.hardness * 15.0F : 15.0F)) {
                    this.mineTimer = 0.0F;
                    if (Vars.state.rules.sector != null && this.team() == Vars.state.rules.defaultTeam) {
                        Vars.state.rules.sector.info.handleProduction(item, 1);
                    }

                    if (core != null && this.within(core, 220.0F) && core.acceptStack(item, 1, this) == 1 && this.offloadImmediately()) {
                        if (this.item() == item && !Vars.net.client()) {
                            this.addItem(item);
                        }

                        Call.transferItemTo(this, item, 1, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), core);
                    } else if (this.acceptsItem(item)) {
                        InputHandler.transferItemToUnit(item, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), this);
                    } else {
                        this.mineTile = null;
                        this.mineTimer = 0.0F;
                    }
                }

                if (!Vars.headless) {
                    Vars.control.sound.loop(this.type.mineSound, this, this.type.mineSoundVolume);
                }
            }
        }

        this.shieldAlpha -= Time.delta / 15.0F;
        if (this.shieldAlpha < 0.0F) {
            this.shieldAlpha = 0.0F;
        }

        floor = this.floorOn();
        if (this.isGrounded() && !this.type.hovering) {
            this.apply(floor.status, floor.statusDuration);
        }

        this.applied.clear();
        this.speedMultiplier = this.damageMultiplier = this.healthMultiplier = this.reloadMultiplier = this.buildSpeedMultiplier = this.dragMultiplier = 1.0F;
        this.disarmed = false;
        int index;
        if (!this.statuses.isEmpty()) {
            index = 0;

            label416:
            while(true) {
                while(true) {
                    if (index >= this.statuses.size) {
                        break label416;
                    }

                    StatusEntry entry = (StatusEntry)this.statuses.get(index++);
                    entry.time = Math.max(entry.time - Time.delta, 0.0F);
                    if (entry.effect != null && (!(entry.time <= 0.0F) || entry.effect.permanent)) {
                        this.applied.set(entry.effect.id);
                        this.speedMultiplier *= entry.effect.speedMultiplier;
                        this.healthMultiplier *= entry.effect.healthMultiplier;
                        this.damageMultiplier *= entry.effect.damageMultiplier;
                        this.reloadMultiplier *= entry.effect.reloadMultiplier;
                        this.buildSpeedMultiplier *= entry.effect.buildSpeedMultiplier;
                        this.dragMultiplier *= entry.effect.dragMultiplier;
                        this.disarmed |= entry.effect.disarm;
                        entry.effect.update(this, entry.time);
                    } else {
                        Pools.free(entry);
                        --index;
                        this.statuses.remove(index);
                    }
                }
            }
        }

        if (Vars.net.client() && !this.isLocal() || this.isRemote()) {
            this.interpolate();
        }

        this.type.update(this);
        if (this.wasHealed && this.healTime <= -1.0F) {
            this.healTime = 1.0F;
        }

        this.healTime -= Time.delta / 20.0F;
        this.wasHealed = false;
        if (this.team.isOnlyAI() && Vars.state.isCampaign() && Vars.state.getSector().isCaptured()) {
            this.kill();
        }

        if (!Vars.headless && this.type.loopSound != Sounds.none) {
            Vars.control.sound.loop(this.type.loopSound, this, this.type.loopSoundVolume);
        }

        if (!this.type.supportsEnv(Vars.state.rules.env) && !this.dead) {
            Call.unitEnvDeath(this);
            this.team.data().updateCount(this.type, -1);
        }

        if (Vars.state.rules.unitAmmo && this.ammo < (float)this.type.ammoCapacity - 1.0E-4F) {
            this.resupplyTime += Time.delta;
            if (this.resupplyTime > 10.0F) {
                this.type.ammoType.resupply(this);
                this.resupplyTime = 0.0F;
            }
        }

        Ability[] var25 = this.abilities;
        index = var25.length;

        for(div = 0; div < index; ++div) {
            Ability a = var25[div];
            a.update(this);
        }

        if (this.trail != null) {
            this.trail.length = this.type.trailLength;
            offset = this.type.useEngineElevation ? this.elevation : 1.0F;
            range = this.type.engineOffset / 2.0F + this.type.engineOffset / 2.0F * offset;
            cx = this.x + Angles.trnsx(this.rotation + 180.0F, range);
            cy = this.y + Angles.trnsy(this.rotation + 180.0F, range);
            this.trail.update(cx, cy);
        }

        this.drag = this.type.drag * (this.isGrounded() ? this.floorOn().dragMultiplier : 1.0F) * this.dragMultiplier * Vars.state.rules.dragMultiplier;
        if (this.team != Vars.state.rules.waveTeam && Vars.state.hasSpawns() && (!Vars.net.client() || this.isLocal()) && this.hittable()) {
            offset = Vars.state.rules.dropZoneRadius + this.hitSize / 2.0F + 1.0F;
            Iterator var32 = Vars.spawner.getSpawns().iterator();

            while(var32.hasNext()) {
                Tile spawn = (Tile)var32.next();
                if (this.within(spawn.worldx(), spawn.worldy(), offset)) {
                    this.velAddNet(Tmp.v1.set(this).sub(spawn.worldx(), spawn.worldy()).setLength(1.1F - this.dst(spawn) / offset).scl(0.45F * Time.delta));
                }
            }
        }

        if (this.dead || this.health <= 0.0F) {
            this.drag = 0.01F;
            if (Mathf.chanceDelta(0.1)) {
                Tmp.v1.rnd(Mathf.range(this.hitSize));
                this.type.fallEffect.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y);
            }

            if (Mathf.chanceDelta(0.2)) {
                offset = this.type.engineOffset / 2.0F + this.type.engineOffset / 2.0F * this.elevation;
                range = Mathf.range(this.type.engineSize);
                this.type.fallEngineEffect.at(this.x + Angles.trnsx(this.rotation + 180.0F, offset) + Mathf.range(range), this.y + Angles.trnsy(this.rotation + 180.0F, offset) + Mathf.range(range), Mathf.random());
            }

            this.elevation -= this.type.fallSpeed * Time.delta;
            if (this.isGrounded() || this.health <= -this.maxHealth) {
                Call.unitDestroy(this.id);
            }
        }

        Tile tile = this.tileOn();
        floor = this.floorOn();
        if (tile != null && this.isGrounded() && !this.type.hovering) {
            if (tile.build != null) {
                tile.build.unitOn(this);
            }

            if (floor.damageTaken > 0.0F) {
                this.damageContinuous(floor.damageTaken);
            }
        }

        if (tile != null && !this.canPassOn()) {
            if (this.type.canBoost) {
                this.elevation = 1.0F;
            } else if (!Vars.net.client()) {
                this.kill();
            }
        }

        if (!Vars.net.client() && !this.dead) {
            this.controller.updateUnit();
        }

        if (!this.controller.isValidController()) {
            this.resetController();
        }

        if (this.spawnedByCore && !this.isPlayer() && !this.dead) {
            Call.unitDespawn(this);
        }

        WeaponMount[] var31 = this.mounts;
        index = var31.length;

        for(div = 0; div < index; ++div) {
            WeaponMount mount = var31[div];
            mount.weapon.update(this, mount);
        }
        for(int i = 0; i < 2; ++i) {
            Trail t = i == 0 ? this.tleft : this.tright;
            t.length = this.type.trailLength;
            int sign = i == 0 ? -1 : 1;
            cx = Angles.trnsx(this.rotation - 90.0F, this.type.waveTrailX * (float)sign, this.type.waveTrailY) + this.x;
            cy = Angles.trnsy(this.rotation - 90.0F, this.type.waveTrailX * (float)sign, this.type.waveTrailY) + this.y;
            t.update(cx, cy, isOnLiquid() ? 0.5F : 0.0F);
        }
        liquidedSmooth = Mathf.lerp(liquidedSmooth,isOnLiquid() ? 1f : 0f, Time.delta/10f);
    }
    @Override
    public float speed() {
        float base = getStatState().speed;
        for (float v : mountSpeedMultiplier.values())
            base *= v;
        return base;
    }
}
