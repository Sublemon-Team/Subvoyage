package subvoyage.type.block.defense;


import mindustry.graphics.Pal;
import subvoyage.content.block.SvBlocks;
import subvoyage.draw.visual.SvFx;

public class PowerRing {
    public float x,y;
    public float charge;
    public boolean hasTarget;
    public float targetX, targetY;


    public void shoot(PowerRingTurret.PowerRingTurretBuild build) {
        SvFx.colorRadExplosion.get(new Object[] {Pal.power, ((PowerRingTurret) build.block).ringRadius})
                .create(x,y,0f,Pal.power,new Object());


        x = build.x;
        y = build.y;
        hasTarget = false;
        targetX = build.x;
        targetY = build.y;
    }
}
