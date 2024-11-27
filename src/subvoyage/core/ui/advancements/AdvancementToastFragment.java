package subvoyage.core.ui.advancements;

import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.scene.Group;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.graphics.Pal;

import static arc.Core.atlas;
import static arc.Core.scene;

public class AdvancementToastFragment extends Table {

    public Seq<Advancement> queue = Seq.with();

    public @Nullable Advancement current = null;
    public float time = 0f;
    public float advancementTime = 3.5f*60f;
    public float fadeTime = 1f*60f;

    public AdvancementToastFragment() {
        setFillParent(true);
        visible(() -> {
            return true;
        });
        update(() -> {

        });
        setup();
    }
    public void setup() {
        setClip(false);
        setTransform(true);
        top().left(); fill(); marginTop(120f);
        TextButton.TextButtonStyle style = scene.getStyle(TextButton.TextButtonStyle.class);
        Table gr = table(style.down).width(320f).height(100f).pad(10f).get();
        gr.setTransform(true);
        gr.top().left();

        gr.row();
        gr.image(this::region).padRight(3f).align(Align.left).margin(0f).width(48f).height(48f).growY();
        gr.table(t -> {
            t.label(this::title).marginLeft(50f).top().left();
            t.row();
            t.label(this::description).marginTop(8f).top().left().fontScale(0.8f).growX().wrap().color(Pal.lightishGray);
        }).marginLeft(5f).top().growX().growY();

        gr.update(() -> {
            if(current == null && queue.firstOpt() != null) {
                current = queue.firstOpt();
                queue.remove(0);
            }
            if(current != null) {
                time += Time.delta;
                if(time > advancementTime) {
                    current = null;
                    time = 0;
                }
            }

            float t = 0f;
            if(time < fadeTime && time > 0) t = time/fadeTime;
            else if(time < advancementTime-fadeTime) t = 1f;
            else if(time < advancementTime) t = 1-(time-advancementTime+fadeTime)/fadeTime;
            if(current == null) t = 0f;
            t = Interp.pow3Out.apply(t);
            gr.setTranslation(-330*(1-t),0);
        });
    };

    public TextureRegion region() {
        return current == null ? findCache("error") : findCache(current.icon);
    }
    public static ObjectMap<String,TextureRegion> found = new ObjectMap<>();
    public TextureRegion findCache(String id) {
        if(found.containsKey(id)) return found.get(id,atlas.find(id));
        found.put(id,atlas.find(id));
        return findCache(id);
    };
    public String title() {
        return current == null ? "oh" :  clamp(current.title,20);
    }
    public String description() {
        return current == null ? "no" : clamp(current.description,70);
    }

    public static String clamp(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength - 3) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    public void build(Group parent){
        scene.add(this);
    }
}
