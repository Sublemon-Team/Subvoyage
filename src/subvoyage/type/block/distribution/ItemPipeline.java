package subvoyage.type.block.distribution;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.core.Renderer;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ItemBridge;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class ItemPipeline extends ItemBridge {
    public ItemPipeline(String name) {
        super(name);

        swapDiagonalPlacement = true;
        bridgeWidth = 8f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

        Tile link = findLink(x, y);

        for(int i = 0; i < 4; i++){
            Drawf.dashLine(Pal.placing,
                    x * tilesize + Geometry.d4[i].x * (tilesize / 2f + 2),
                    y * tilesize + Geometry.d4[i].y * (tilesize / 2f + 2),
                    x * tilesize + Geometry.d4[i].x * (range) * tilesize,
                    y * tilesize + Geometry.d4[i].y * (range) * tilesize);
        }

        Draw.reset();
        Draw.color(Pal.placing);
        Lines.stroke(1f);
        if(link != null && Math.abs(link.x - x) + Math.abs(link.y - y) > 1){
            int rot = link.absoluteRelativeTo(x, y);
            float w = (link.x == x ? tilesize : Math.abs(link.x - x) * tilesize - tilesize);
            float h = (link.y == y ? tilesize : Math.abs(link.y - y) * tilesize - tilesize);

            Lines.dashLine(x,y,link.x,link.y,6);
            //Draw.rect("bridge-arrow", link.x * tilesize + Geometry.d4(rot).x * tilesize, link.y * tilesize + Geometry.d4(rot).y * tilesize, link.absoluteRelativeTo(x, y) * 90);
        }
        Draw.reset();
    }

    @Override
    public boolean positionsValid(int x1, int y1, int x2, int y2) {
        return Mathf.dst(x1, y1, x2, y2) <= range;
    }
    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> Mathf.dst(point.x,point.y,other.x,other.y) <= range);
    }

    public class ItemPipelineBuild  extends ItemBridgeBuild {
        @Override
        public void draw(){
            Draw.rect(region, x, y, drawrot());

            drawTeamTop();

            Draw.z(Layer.power);

            Tile other = world.tile(link);
            if(!linkValid(tile, other)) {
                Draw.rect(arrowRegion,x,y,0f);
                Draw.rect(arrowRegion,x,y,90f);
                Draw.rect(arrowRegion,x,y,180f);
                Draw.rect(arrowRegion,x,y,270f);
                return;
            }

            if(Mathf.zero(Renderer.bridgeOpacity)) return;

            int i = relativeTo(other.x, other.y);

            if(pulse){
                Draw.color(Color.white, Color.black, Mathf.absin(Time.time, 6f, 0.07f));
            }

            float warmup = hasPower ? this.warmup : 1f;

            Draw.alpha((fadeIn ? Math.max(warmup, 0.25f) : 1f) * Renderer.bridgeOpacity);

            Draw.rect(endRegion, x, y, Angles.angle(other.drawx(),other.drawy(),x,y)-90);
            Draw.rect(endRegion, other.drawx(), other.drawy(), Angles.angle(x,y,other.drawx(),other.drawy())-90);

            Lines.stroke(bridgeWidth);

            Tmp.v1.set(x, y).sub(other.worldx(), other.worldy()).setLength(tilesize/2f).scl(-1f);

            Lines.line(bridgeRegion,
                    x + Tmp.v1.x,
                    y + Tmp.v1.y,
                    other.worldx() - Tmp.v1.x,
                    other.worldy() - Tmp.v1.y, false);

            int dist = Math.max(Math.abs(other.x - tile.x), Math.abs(other.y - tile.y)) - 1;

            Draw.color();

            Draw.rect(arrowRegion,x,y, Angles.angle(x,y,other.drawx(),other.drawy()));

            Draw.reset();
        }

        @Override
        protected boolean checkDump(Building to){
            return true;
        }
    }
}
