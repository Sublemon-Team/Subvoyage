package subvoyage.utility;

import arc.math.Mathf;
import mindustry.gen.Building;

public class SvMath {
    public static boolean withinSquare(Building building, Building origin, float radius) {
        return withinSquare(origin.x,origin.y,building.x,building.y,radius);
    };

    public static boolean withinSquare(Building building, float originX, float originY, float radius) {
        return withinSquare(originX,originY,building.x,building.y,radius);
    };

    public static boolean withinSquare(float originX, float originY, float x, float y, float radius) {
        float   bxs = originX-radius,
                bys = originY-radius,
                bxe = originX+radius,
                bye = originY+radius;
        return bxs <= x & x <= bxe && bys <= y && y <= bye;
    };

    public static float cospow(float radians, float scl, float mag) {
        return Mathf.sqr(Mathf.cos(radians / scl)) * Mathf.sign(Mathf.cos(radians / scl)) * mag;
    }

    public static float sinpow(float radians, float scl, float mag) {
        return Mathf.sqr(Mathf.sin(radians / scl)) * Mathf.sign(Mathf.sin(radians / scl)) * mag;
    }
}
