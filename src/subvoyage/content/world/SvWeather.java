package subvoyage.content.world;

import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.gen.WeatherState;
import mindustry.type.Weather;
import mindustry.type.weather.RainWeather;
import mindustry.world.meta.Attribute;


public class SvWeather {
    public static Weather
        rainstorm
            ;

    public static void load() {
        rainstorm = new RainWeather("rainstorm") {{
            attrs.set(Attribute.light, 0.2f);
            attrs.set(Attribute.water, 0.5f);
            status = StatusEffects.wet;
            sound = Sounds.rain;
            soundVol = 0.25f;

            sizeMin = 30f;
            sizeMax = 50f;
            xspeed = 4f;
            yspeed = 10f;
            padding = 32f;

            density = 2400f;
            stroke = 1.25f;
            duration = 4f * Time.toMinutes;
        }

            @Override
            public void update(WeatherState state) {
                float speed = 0.2f * state.intensity * Time.delta;
                if(speed > 0.001f){
                    float windx = state.windVector.x * speed, windy = state.windVector.y * speed;

                    for(Unit unit : Groups.unit){
                        unit.impulse(windx, windy);
                    }
                }
                state.windVector.rotate(Time.delta/10f);
            }
        };
    }
}
