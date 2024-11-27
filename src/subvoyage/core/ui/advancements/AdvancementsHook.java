package subvoyage.core.ui.advancements;


import arc.Core;
import arc.scene.ui.TextButton;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.PlanetDialog;
import subvoyage.util.Var;

import static mindustry.Vars.ui;
import static subvoyage.content.world.SvPlanets.atlacian;
import static subvoyage.core.ui.SvUI.advancements;

public class AdvancementsHook {
    public static void register() {
        addAchievement();
    }
    static void addAchievement(){
        PlanetDialog p = ui.planet;
        Var<Boolean> btn = Var.bool();
        Var<TextButton> el = Var.init(null);
        ui.planet.update(() -> {
            if(p.state.planet == atlacian && (!btn.val || el.val == null)) {
                btn.val = true;
                el.val = btn();
            } else if(p.state.planet != atlacian && btn.val && el.val != null) {
                btn.val = false;
                el.val.remove();
                rebuildButtons();
            }
            if(p.state.planet == atlacian && el.val != null && !el.val.hasParent()) {
                btn.val = true;
                el.val = btn();
            }
        });
    }
    static void openAdvancements() {
        advancements.showDialog();
    }
    static TextButton btn() {
        return
                ui.planet.buttons.button(
                        "@advancements",
                        Icon.modePvp,
                        AdvancementsHook::openAdvancements)
                    .name("atl-advancements")
                    .size(250f, 54f)
                    .pad(2)
                    .bottom()
                .get();
    }
    static void rebuildButtons(){
        PlanetDialog p = ui.planet;
        p.buttons.clearChildren();

        p.buttons.bottom();

        if(Core.graphics.isPortrait()){
            p.buttons.add(p.sectorTop).colspan(2).fillX().row();
            addBack();
            addTech();
        }else{
            addBack();
            p.buttons.add().growX();
            p.buttons.add(p.sectorTop).minWidth(230f);
            p.buttons.add().growX();
            addTech();
        }
    }
    static void addBack(){
        ui.planet.buttons.button("@back", Icon.left, ui.planet::hide).size(200f, 54f).pad(2).bottom();
    }
    static void addTech(){
        ui.planet.buttons.button("@techtree", Icon.tree, () -> ui.research.show()).size(200f, 54f).pad(2).bottom();
    }
}
