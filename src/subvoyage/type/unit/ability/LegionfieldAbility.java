package subvoyage.type.unit.ability;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Time;
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
import subvoyage.content.SvEffects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LegionfieldAbility extends Ability {

    public float radius = 2f;

    public LegionfieldAbility() {

    };

    @Override
    public void addStats(Table t) {
        t.add("[lightgray]" + Stat.range.localized() + ": [white]" + Math.round(radius) + " " + StatUnit.blocks.localized());
        t.row();
    }

    @Override
    public void displayBars(Unit unit, Table bars) {
        bars.add(new Bar("stat.fieldpower", Pal.accent, () -> {
            float mult = Mathf.clamp(radius(unit.team)/(radius),0f,3f);
            if(!Mathf.within(unit.x,unit.y,point(unit.team).x,point(unit.team).y,8f + 8f * radius(unit.team)))
                mult = 0;
            return mult/3;
        })).row();
    }

    public static ObjectMap<Team,Float> lastRadius = new ObjectMap<>();
    public static ObjectMap<Team,Vec2> point = new ObjectMap<>();
    public static ObjectMap<Team,Float> pointRad = new ObjectMap<>();

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

                rad += abil.radius/(count/2+1);
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
                if(!Mathf.within(u.x,u.y,lastPoint.x,lastPoint.y,lastRad*2)) {
                    rad -= abil.radius/(count/2+1);
                    count--;
                }
            }
            LegionfieldAbility.point.put(team,point);
            LegionfieldAbility.pointRad.put(team,rad);
            lastRadius.put(team,Mathf.lerp(lastRadius(team),rad, Time.delta/10f));
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
        if(Mathf.within(unit.x,unit.y,point(unit.team).x,point(unit.team).y,8f + 8f * radius(unit.team))) {
            float mult = Mathf.clamp(radius(unit.team)/(radius),1f,3f);
            unit.speedMultiplier(Mathf.clamp(radius(unit.team)/(radius),1f,3f));
            unit.apply(StatusEffects.shielded);

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
        if(Mathf.within(unit.x,unit.y,point(unit.team).x,point(unit.team).y,8f + 8f * radius(unit.team))) {
            Draw.z(Layer.shields);
            Lines.stroke(3f, unit.team.color);
            Lines.circle(point(unit.team).x, point(unit.team).y, 8f + 8f * lastRadius(unit.team));
            Draw.z();
            Draw.reset();
        }
    }
}
