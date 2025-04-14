package subvoyage.content.other;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;

public class SvStat {
    public static final StatCat
        laser = new StatCat("laser");
    public static final Stat
        laserUse = new Stat("laserUse", laser),
        laserOutput = new Stat("laserOutput", laser),
        laserCapacity = new Stat("laserCapacity", laser);
    public static final StatUnit
        laserPower = new StatUnit("laserPower",  "[red]"  +"\uEAD1" + "[]");
}
