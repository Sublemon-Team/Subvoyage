package subvoyage.type.block;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.Bits;
import arc.util.Eachable;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.TargetPriority;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.StatUnit;
import mindustry.world.modules.ItemModule;

import java.util.Iterator;

public class DrawerBlock extends Block {

    public DrawBlock drawer = new DrawDefault();

    public DrawerBlock(String name) {
        super(name);

        breakable = false;
        destructible = false;

        priority = -1000f;
        targetable = false;
    }

    @Override
    public boolean hasBuilding() {
        return true;
    }

    @Override
    public void setBars() {

    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }
    @Override
    public boolean canBreak(Tile tile) {
        return false;
    }
    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    public class DrawerBlockBuild extends Building {

        @Override
        public void draw(){
            drawer.draw(this);
        }


        @Override
        public void display(Table table) {
            table.remove();
        }

        @Override
        public void damage(float damage) {

        }

        @Override
        public void onDestroyed() {

        }

        @Override
        public void afterDestroyed() {

        }

        @Override
        public void drawTeam() {

        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(team == Team.derelict) team = Team.get(255);
        }

        @Override
        public void update() {
            super.update();
            if(team == Team.derelict) team = Team.get(255);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }
    }
}
