package subvoyage.type.block.laser;

import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import subvoyage.core.draw.SvFx;

import java.util.ArrayList;
import java.util.List;

import static mindustry.Vars.tilesize;

public interface LaserBlock {
    short inputRange(); short outputRange();
    byte maxSuppliers();

    IntSeq inputs(); IntSeq outputs();


    
    default void drawLinks(Block block, int x, int y, int rotation, boolean drawInputs, boolean drawOutputs) {
        List<LaserLink> links = LaserGraph.getLinks(x,y,rotation,block);
        LaserLink[] consLinks = new LaserLink[4];
        LaserLink[] suppLinks = new LaserLink[4];
        int supps = Seq.with(links).count(f -> f.isSupplier);
        for (LaserLink link : links) {
            Building other = link.object;
            if(link.isConsumer) {
                Drawf.square(other.x,other.y,other.block.size/2f*tilesize,0, Pal.techBlue);
                consLinks[link.side] = link;
            }
            if(link.isSupplier) {
                Drawf.square(other.x,other.y,other.block.size/2f*tilesize,0,supps > maxSuppliers() ? Pal.remove : Pal.heal);
                suppLinks[link.side] = link;
            }
        }
        List<Integer> selfInputs = new ArrayList<>();
        List<Integer> selfOutputs = new ArrayList<>();
        inputs().each(input -> selfInputs.add((input+rotation)%4));
        outputs().each(output -> selfOutputs.add((output+rotation)%4));

        for (int i = 0; i < 4; i++) {
            Point2 dir = Geometry.d4[i];
            int dx = dir.x, dy = dir.y;

            float inputLen = inputRange();
            float outputLen = outputRange();
            LaserLink consLink = consLinks[i];
            LaserLink suppLink = suppLinks[i];

            if(!selfInputs.contains(i) || !drawInputs) inputLen = 0;
            if(!selfOutputs.contains(i) || !drawOutputs) outputLen = 0;
            if(consLink != null)
                outputLen = consLink.len - consLink.object.block.size/2f;
            if(suppLink != null)
                inputLen = suppLink.len - suppLink.object.block.size/2f;

            Drawf.dashLine(Pal.techBlue,
                    x * tilesize + dx*block.size/2f*tilesize,
                    y * tilesize + dy*block.size/2f*tilesize,
                    x * tilesize + dx*block.size/2f*tilesize + outputLen * tilesize * dx,
                    y * tilesize + dy*block.size/2f*tilesize + outputLen * tilesize * dy);
            Drawf.dashLine(Pal.heal,
                    x * tilesize + dx*block.size/2f*tilesize,
                    y * tilesize + dy*block.size/2f*tilesize,
                    x * tilesize + dx*block.size/2f*tilesize + inputLen * tilesize * dx,
                    y * tilesize + dy*block.size/2f*tilesize + inputLen * tilesize * dy);

            if(selfOutputs.contains(i) && drawOutputs) {
                Drawf.arrow(
                        x * tilesize  + dx*block.size/2f*tilesize,
                        y * tilesize + dy*block.size/2f*tilesize,
                        x * tilesize + dx*block.size*tilesize,
                        y * tilesize + dy*block.size*tilesize,
                        block.size/4f*tilesize,
                        block.size/4f*tilesize,
                        Pal.techBlue);
            }

            if(selfInputs.contains(i) && drawInputs) {
                Drawf.arrow(
                        x * tilesize  + dx*block.size*tilesize,
                        y * tilesize + dy*block.size*tilesize,
                        x * tilesize - dx*block.size/2f*tilesize,
                        y * tilesize - dy*block.size/2f*tilesize,
                        block.size/4f*tilesize,
                        block.size/4f*tilesize,
                        supps > maxSuppliers() ? Pal.remove : Pal.heal);
            }
        }
    }

    default <T extends Building> Func<T, Bar> bar() {
        return build -> {
            if(build instanceof LaserBuild lb) {
                return new Bar(
                        () -> Core.bundle.format("bar.sv_laser_power", Strings.fixed(lb.rawLaser(), 1)),
                        () -> LaserUtil.getLaserColor(lb.laser()),
                        () -> Mathf.clamp(lb.rawLaser())
                );
            } else return new Bar();
        };
    };


    default void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2, float scl, float bloomIntensity){
        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f, len2 = size2 * tilesize / 2f;
                //len1 = 0, len2 = 0;
        float layer = Draw.z();

        scl = Math.max(scl+bloomIntensity/2f,0.2f);
        Draw.z(Layer.blockOver);
        Fill.circle(x1 + vx * len1, y1 + vy * len1, 6f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Fill.circle(x2 - vx * len2, y2 - vy * len2, 6f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Lines.stroke(8f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Lines.line(x1 + vx * len1, y1 + vy * len1, x2 - vx * len2, y2 - vy * len2);
        Draw.color();
        Lines.stroke(4f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Fill.circle(x1 + vx * len1, y1 + vy * len1, 3f * scl + Mathf.cos(Time.time + 5, 10f, 0.5f) * (scl - 0.2f));
        Fill.circle(x2 - vx * len2, y2 - vy * len2, 3f * scl + Mathf.cos(Time.time, 10f, 0.5f) * (scl - 0.2f));
        Lines.line(x1 + vx * len1, y1 + vy * len1, x2 - vx * len2, y2 - vy * len2);
        Draw.z(layer);
    }
}
