package subvoyage.type.block.laser;

import arc.graphics.Color;
import subvoyage.content.other.SvPal;

import java.util.LinkedHashMap;

public class LaserUtil {

    public static LinkedHashMap<Float,Color> laserPowerColors = new LinkedHashMap<>();

    static {
        laserPowerColors.put(0f, SvPal.laserGrey);
        laserPowerColors.put(10f, SvPal.laserRed);
        laserPowerColors.put(30f,SvPal.laserGreen);
        laserPowerColors.put(90f,SvPal.laserBlue);
        laserPowerColors.put(270f,SvPal.laserViolet);
        laserPowerColors.put(1000f,Color.valueOf("D8D1FF"));
    }


    public static Color getLaserColor(float power) {
        if(power <= 0.05f) return laserPowerColors.get(0f);
        final Object[] i_color = {0,laserPowerColors.get(0f),false};
        laserPowerColors.forEach((f,c) -> {
            if((int) i_color[0] == 0 || (boolean) i_color[2]) {
                i_color[0] = (int) i_color[0]+1;
                return;
            }
            if(power == f) i_color[1] = c;
            if(power < f) {
                Color prevColor = (Color) laserPowerColors.values().toArray()[(int) i_color[0]-1];
                float prevValue = (float) laserPowerColors.keySet().toArray()[(int) i_color[0]-1];
                float progress = (power-prevValue)/(f-prevValue);
                i_color[1] = prevColor.cpy().lerp(c.cpy(),progress);
                i_color[2] = true;
            }
            i_color[0] = (int) i_color[0]+1;
        });
        return (Color) i_color[1];
    }
}
