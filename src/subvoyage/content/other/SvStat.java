package subvoyage.content.other;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class SvStat {
    public static final StatCat
        laser = new StatCat("laser");
    public static final Stat
        laserUse = new Stat("laserUse", laser),
        laserOutput = new Stat("laserOutput", laser);
}
