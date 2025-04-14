package subvoyage.type.block.laser;

import arc.graphics.Color;
import arc.math.Mathf;
import subvoyage.core.draw.SvPal;

import java.util.LinkedHashMap;

public class LaserUtil {

    public static LinkedHashMap<Float,Color> laserPowerColors = new LinkedHashMap<>();

    static {
        laserPowerColors.put(0f, SvPal.laserRed.cpy().mul(0.2f));
        laserPowerColors.put(10f, SvPal.laserRed);
        laserPowerColors.put(100f,SvPal.laserGreen);
        laserPowerColors.put(270f,SvPal.laserBlue);
        laserPowerColors.put(500f,SvPal.laserViolet);
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

                float[] hsv = prevColor.toHsv(new float[3]);
                float[] hsv2 = c.toHsv(new float[3]);

                float[] newHsv = new float[] {
                    Mathf.lerp(hsv[0],hsv2[0],progress),
                    Mathf.lerp(hsv[1],hsv2[1],progress),
                    Mathf.lerp(hsv[2],hsv2[2],progress)
                };

                i_color[1] = prevColor.cpy().fromHsv(newHsv);
                i_color[2] = true;
            }
            i_color[0] = (int) i_color[0]+1;
        });
        return (Color) i_color[1];
    }
}
