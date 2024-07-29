package subvoyage.utility;

import mindustry.gen.Building;

public class SvMath {
    /**
     * @param in [0,1]
     * @return out [-1,1]
     */
    public static float linSin(float in) {
        in %= 1f;
        return (in > 0.5f ? 1f-in : in)*4f-1f;
    }

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
}
