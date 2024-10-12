package subvoyage.type.block.laser_blocks.unit;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import subvoyage.Subvoyage;

import static mindustry.Vars.*;
import static mindustry.Vars.world;

public class LaserUnitAssemblerModule extends LaserPayloadBlock {
    public int tier = 1;
    public LaserUnitAssemblerModule(String name) {
        super(name);
        rotate = true;
        rotateDraw = false;
        acceptsPayload = true;
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top", Subvoyage.ID+ "-factory-top-" + size + regionSuffix);
        outRegion = Core.atlas.find(name + "-out", Subvoyage.ID+"-factory-out-" + size + regionSuffix);
        inRegion = Core.atlas.find(name + "-in", Subvoyage.ID+"-factory-in-" + size + regionSuffix);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.moduleTier, tier);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        var link = getLink(player.team(), x, y, rotation);
        if(link != null){
            link.block.drawPlace(link.tile.x, link.tile.y, link.rotation, true);
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return getLink(team, tile.x, tile.y, rotation) != null;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    public @Nullable LaserUnitAssembler.LaserUnitAssemblerBuild getLink(Team team, int x, int y, int rotation){
        var results = Vars.indexer.getFlagged(team, BlockFlag.unitAssembler).<LaserUnitAssembler.LaserUnitAssemblerBuild>as();
        return results.find(b -> b.moduleFits(this, x * tilesize + offset, y * tilesize + offset, rotation));
    }

    public class LaserUnitAssemblerModuleBuild extends LaserPayloadBlockBuild<Payload> {
        public LaserUnitAssembler.LaserUnitAssemblerBuild link;
        public int lastChange = -2;

        public void findLink(){
            if(link != null){
                link.removeModule(this);
            }
            link = getLink(team, tile.x, tile.y, rotation);
            if(link != null){
                link.updateModules(this);
            }
        }

        public int tier(){
            return tier;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            //draw input conveyors
           /* for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }*/

            Draw.z(Layer.blockOver);
            payRotation = rotdeg();
            drawPayload();
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return link != null && this.payload == null && link.acceptPayload(this, payload);
        }

        @Override
        public void drawSelect(){
            //TODO draw area?
            if(link != null){
                Drawf.selected(link, Pal.accent);
            }
        }

        @Override
        public void onRemoved(){
            super.onRemoved();

            if(link != null){
                link.removeModule(this);
            }
        }

        @Override
        public void updateTile(){
            if(lastChange != world.tileChanges){
                lastChange = world.tileChanges;
                findLink();
            }

            if(moveInPayload() && link != null && link.moduleFits(block, x, y, rotation) && !link.wasOccupied && link.acceptPayload(this, payload) && efficiency > 0){
                link.yeetPayload(payload);
                payload = null;
            }
        }
    }
}
