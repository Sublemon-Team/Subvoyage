package subvoyage.ui.advancements;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class AdvancementsDialog extends BaseDialog {

    public AdvancementsDialog() {
        super("Subvoyage: "+Core.bundle.get("advancements"));
        addCloseButton();
        shouldPause = false;
    }

    public void rebuild() {
        cont.clear();
        Table all = cont.table().grow().pad(20f).get();

        for (Advancement advancement : Advancement.all) {
            all.label(() -> advancement.title + " " + (Advancement.unlocked(advancement) ? "Unlocked" : "Locked"));
        }
        
        all.align(Align.topLeft);
    }

    public void showDialog() {
        rebuild();
        show(Core.scene);
    }

    @Override
    public void addCloseButton(float width) {
        buttons.defaults().size(width, 64f);
        buttons.button("@back", Icon.left, this::hide).size(width, 64f).align(Align.top);
        addCloseListener();
    }
}