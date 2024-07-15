package subvoyage.content.unit.ai;

import mindustry.ai.types.FlyingAI;
import mindustry.gen.Teamc;
import mindustry.world.meta.BlockFlag;

public class CryptalAI extends FlyingAI {
    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        return findMainTarget(x,y,range,air,ground);
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        return super.findMainTarget(x, y, range, air, ground);
    }

    @Override
    public Teamc targetFlag(float x, float y, BlockFlag flag, boolean enemy) {
        return super.targetFlag(x, y, flag, enemy);
    }
}
