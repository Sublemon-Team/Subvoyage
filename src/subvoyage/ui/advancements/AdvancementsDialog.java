package subvoyage.ui.advancements;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.ui.dialogs.BaseDialog;

import static arc.Core.*;
import static subvoyage.ui.advancements.AdvancementToastFragment.found;

public class AdvancementsDialog extends BaseDialog {

    public AdvancementsDialog() {
        super("Subvoyage: "+Core.bundle.get("advancements"));
        addCloseButton();
        shouldPause = false;
    }

    public void rebuild() {
        cont.clear();
        Table all = cont.table().grow().pad(20f).margin(10f).get();

        all.add(new Bar(bundle.get("stat.progress") + ": " + Mathf.floor(getProgress()*1000f)/10f + "%",
                        Pal.accent,
                        this::getProgress))
                .align(Align.top).height(18f).pad(4f).top().minWidth(320).maxWidth(640f).growX();
        all.row();
        all.pane((t) -> {
            t.top();
            for (Advancement adv : Advancement.all) {
                t.add(advancement(adv)).growX().pad(5f).top();
                t.row();
            }
        }).top().minWidth(320).maxWidth(640f).grow();

        all.align(Align.topLeft);
    }

    public float getProgress() {
        return (float) Advancement.all.count(Advancement::unlocked) / Advancement.all.size;
    }

    public Table advancement(Advancement adv) {
        TextButton.TextButtonStyle style = scene.getStyle(TextButton.TextButtonStyle.class);
        Table t = new Table(!Advancement.unlocked(adv) ? style.up : style.down);

        t.setTransform(true);
        t.top().left();

        t.row();
        t.image(() -> icon(adv)).padRight(3f).align(Align.left).margin(0f).width(48f).height(48f).growY();
        t.table(u -> {
            u.label(() -> title(adv)).marginLeft(50f).top().left();
            u.row();
            u.label(() -> description(adv)).marginTop(8f).top().left().fontScale(0.8f).growX().wrap().color(Pal.lightishGray);
        }).marginLeft(5f).top().growY().growX();
        return t;
    }

    public TextureRegion icon(Advancement adv) {
        return !Advancement.unlocked(adv) ? findCache("subvoyage-advancement-locked") : findCache(adv.icon);
    }
    public String title(Advancement adv) {
        return !Advancement.unlocked(adv) ? Core.bundle.get("sv_advancement.locked.name") : clamp(adv.title,20);
    }
    public String description(Advancement adv) {
        return !Advancement.unlocked(adv) ? Core.bundle.get("sv_advancement.locked.description") : clamp(adv.description,50);
    }

    public static String clamp(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength - 3) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    public TextureRegion findCache(String id) {
        if(found.containsKey(id)) return found.get(id,atlas.find(id));
        found.put(id,atlas.find(id));
        return findCache(id);
    };

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