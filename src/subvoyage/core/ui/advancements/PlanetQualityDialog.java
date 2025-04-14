package subvoyage.core.ui.advancements;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Button;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Scaling;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import subvoyage.type.world.AtlacianPlanetType;

import static arc.Core.*;
import static mindustry.Vars.ui;
import static subvoyage.content.world.SvPlanets.atlacian;

public class PlanetQualityDialog extends BaseDialog {

    public PlanetQualityDialog() {
        super(bundle.get("setting.subvoyage-planet-divisions.name"));
        addCloseButton();
        shouldPause = false;
    }

    public ImageButton[] btns = new ImageButton[10];

    public void rebuild() {
        cont.clear();

        cont.pane(p -> {
            Table all = p.table().growY().width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).pad(5f).get();

            all.top().left();

            mode(all, "low", 5);
            mode(all, "medium", 6);
            mode(all, "high", 8);
        }).pad(5f);
    }

    public void mode(Table all, String quality, int num) {
        var qua = bundle.get("setting.subvoyage-planet-divisions."+num);

        float width = Math.min(Core.graphics.getWidth() / 1.2f, 460f);

        all.add(qua).growX().center().top().padTop(3f);
        all.row();
        all.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(num == 5 ? Color.lime : num == 6 ? Pal.accent : Color.orange);
        all.row();

        var btn = all.button(new TextureRegionDrawable(atlas.find("subvoyage-atlacian-"+quality)),
                Styles.clearTogglei, () -> {
                    settings.put("subvoyage-planet-divisions", num);
                    for (ImageButton bb : btns) {
                        if(bb == null) continue;
                        bb.setChecked(bb == btns[num]);
                    }
                    ui.loadfrag.show();
                    ui.loadfrag.setProgress(0f);
                    atlacian.reloadMesh();
                    ui.loadfrag.setProgress(0.33f);
                    atlacian.cloudMesh = atlacian.cloudMeshLoader.get();
                    ui.loadfrag.setProgress(0.66f);
                    ((AtlacianPlanetType) atlacian).atmMesh = ((AtlacianPlanetType) atlacian).atmosphereMesh.get();
                    ui.loadfrag.setProgress(1f);
                    ui.loadfrag.hide();
                }).width(width-20f).height(width*0.5f-20f).padTop(3f).padBottom(3f).get();
        btns[num] = btn;
        btn.center();
        btn.setChecked(settings.getInt("subvoyage-planet-divisions") == num);
        btn.getImageCell().width(width-20f).height(width*0.5f-20f).padBottom(8f).scaling(Scaling.fit);

        all.row();
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
