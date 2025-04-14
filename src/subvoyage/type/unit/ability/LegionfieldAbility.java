package subvoyage.type.unit.ability;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.StatusEffects;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import subvoyage.content.other.SvEffects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LegionfieldAbility extends Ability {

    public float radius = 2f;

    public LegionfieldAbility() {

    }

    @Override
    public void addStats(Table t) {
        t.add("[lightgray]" + Stat.range.localized() + ": [white]" + Math.round(radius) + " " + StatUnit.blocks.localized());
        t.row();
    }

    @Override
    public void displayBars(Unit unit, Table bars) {
        bars.add(new Bar(Core.bundle.get("stat.fieldpower"), Pal.accent, () -> {
            float mult = Mathf.clamp(radius(unit.team)/(radius),0f,3f);
            if(!Mathf.within(unit.x,unit.y,point(unit.team).x,point(unit.team).y,8f + 8f * radius(unit.team)))
                mult = 0;
            return mult/3;
        })).row();
    }

    public static ObjectMap<Team,Float> lastRadius = new ObjectMap<>();
    public static ObjectMap<Team,Float> lastCount = new ObjectMap<>();
    public static ObjectMap<Team,Vec2> point = new ObjectMap<>();
    public static ObjectMap<Team,Float> pointRad = new ObjectMap<>();
    public static int count(Team team) {
        return (int) (float) lastCount.get(team,0f);
    }
    public static void update() {
        List<Team> teams = new ArrayList<>();
        for (Unit unit : Groups.unit) if(!teams.contains(unit.team)) teams.add(unit.team);
        for (Team team : teams) {
            float count = 0;
            float rad = 0;
            Vec2 point = new Vec2();

            for (Unit u : Groups.unit) {
                if(Arrays.stream(u.abilities).noneMatch((a) -> a instanceof LegionfieldAbility)) continue;
                if(u.team != team) continue;
                LegionfieldAbility abil = (LegionfieldAbility) Arrays.stream(u.abilities)
                        .filter(a -> a instanceof LegionfieldAbility).findFirst().get();
                point.add(u.x,u.y);

                rad += abil.radius/(count/2f+1);
                count++;
            }

            point.div(new Vec2(count,count));

            float lastRad = Math.abs(rad);
            Vec2 lastPoint = point.cpy();

            for (Unit u : Groups.unit) {
                if(Arrays.stream(u.abilities).noneMatch((a) -> a instanceof LegionfieldAbility)) continue;
                if(u.team != team) continue;
                LegionfieldAbility abil = (LegionfieldAbility) Arrays.stream(u.abilities)
                        .filter(a -> a instanceof LegionfieldAbility).findFirst().get();
                if(!Mathf.within(u.x,u.y,lastPoint.x,lastPoint.y,lastRad*2+8f)) {
                    rad -= abil.radius/(count/2f+1);
                    count--;
                }
            }
            lastCount.put(team,count);
            LegionfieldAbility.point.put(team,point);
            LegionfieldAbility.pointRad.put(team,rad);
            lastRadius.put(team,Mathf.lerp(lastRadius(team),rad, Time.delta/30f));
        }
    }

    public static float lastRadius(Team team) {
        return lastRadius.get(team,0f);
    }
    public static float radius(Team team) {
        return pointRad.get(team,0f);
    }
    public static Vec2 point(Team team) {
        return point.get(team,new Vec2());
    }

    @Override
    public void update(Unit unit) {
        if(Mathf.within(unit.x,unit.y,point(unit.team).x,point(unit.team).y,radius(unit.team))) {
            float mult = Mathf.clamp(radius(unit.team)/(radius),1f,3f);
            unit.speedMultiplier(Mathf.clamp(radius(unit.team)/(radius),1f,3f));
            if(radius(unit.team) > radius) unit.apply(StatusEffects.shielded);

            if(mult >= 3f) {
                unit.unapply(SvEffects.buff);
                unit.apply(SvEffects.overpower);
            }
            else if(mult >= 1.5f) {
                unit.unapply(SvEffects.overpower);
                unit.apply(SvEffects.buff);
            }
        } else {
            unit.speedMultiplier(1f);
            unit.unapply(StatusEffects.shielded);
            unit.unapply(SvEffects.buff);
            unit.unapply(SvEffects.overpower);
        }
    }

    @Override
    public void draw(Unit unit) {
        if(Mathf.within(unit.x,unit.y,point(unit.team).x,point(unit.team).y,4f * radius(unit.team) + 8f)) {
            float z = Draw.z();
            float x = point(unit.team).x;
            float y = point(unit.team).y;
            float rad = 8f * lastRadius(unit.team);
            Color teamColor = Tmp.c2.set(unit.team.color).mul(1.1f);
            Color teamColor2 = Tmp.c3.set(unit.team.color).mul(1.1f);

            Draw.z(Layer.blockUnder-0.2f);

            lightInner(x,y, 6,rad * 0.9f,rad,0f,
                    teamColor.a(0),
                    teamColor2.a(1f/count(unit.team)));
            lightInner(x,y, 6,rad * 0.9f,0,0f,
                    teamColor.mul(1.25f).a(0),
                    teamColor2.mul(1.25f).a(0.5f/count(unit.team)));
            lightInner(x,y, 6,rad * 1.05f,rad,0f,
                    teamColor.mul(0.2f).a(0),
                    teamColor2.mul(0.2f).a(1f/count(unit.team)));

            Draw.z(z);
            Draw.reset();
        } else if (!Mathf.within(unit.x,unit.y,point(unit.team).x,point(unit.team).y,4f * radius(unit.team) + 8f)) {
            float z = Draw.z();
            float x = unit.x;
            float y = unit.y;
            float rad = 8f + 8f * radius;
            Color teamColor = Tmp.c2.set(unit.team.color).saturation(0f).mul(0.75f);
            Color teamColor2 = Tmp.c3.set(unit.team.color).saturation(0f).mul(0.75f);

            Draw.z(Layer.blockUnder-0.2f);

            lightInner(x,y, 6,rad * 0.9f,rad,0f,
                    teamColor.a(0),
                    teamColor2.a(0.5f));
            lightInner(x,y, 6,rad * 0.9f,0,0f,
                    teamColor.mul(1.25f).a(0),
                    teamColor2.mul(1.25f).a(0.25f));
            lightInner(x,y, 6,rad * 1.05f,rad,0f,
                    teamColor.mul(0.2f).a(0),
                    teamColor2.mul(0.2f).a(0.5f));

            Draw.z(z);
            Draw.reset();
        }
    }

    public static void lightInner(float x, float y, int sides, float innerRadius, float radius, float rotation, Color center, Color edge){
        float centerf = center.toFloatBits(), edgef = edge.toFloatBits();

        float space = 360f / sides;

        for(int i = 0; i < sides; i ++){
            float px = Angles.trnsx(space * i + rotation, radius);
            float py = Angles.trnsy(space * i + rotation, radius);
            float px2 = Angles.trnsx(space * (i + 1) + rotation, radius);
            float py2 = Angles.trnsy(space * (i + 1) + rotation, radius);
            Fill.quad(
                    x + Angles.trnsx(space * i + rotation, innerRadius), y + Angles.trnsy(space * i + rotation, innerRadius), centerf,
                    x + px, y + py, edgef,
                    x + px2, y + py2, edgef,
                    x + Angles.trnsx(space * (i+1) + rotation, innerRadius), y + Angles.trnsy(space * (i+1) + rotation, innerRadius), centerf
            );
        }
    }
}
