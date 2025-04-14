package subvoyage.core.draw;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.Effect;

public class DataEffect extends Effect {
    public Effect effect = Fx.none;
    public Object data;

    public DataEffect(){
    }

    public DataEffect(Effect effect, Object data){
        this.effect = effect;
        this.data = data;
    }

    @Override
    public void init(){
        effect.init();
        clip = effect.clip;
        lifetime = effect.lifetime;
    }

    @Override
    public void render(EffectContainer e){

    }

    @Override
    public void create(float x, float y, float rotation, Color color, Object data){
        effect.create(x, y, rotation, color, this.data != null ? this.data : data);
    }
}
