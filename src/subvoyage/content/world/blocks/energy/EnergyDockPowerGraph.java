package subvoyage.content.world.blocks.energy;

import arc.math.Mathf;
import arc.math.WindowedMean;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.blocks.power.PowerGraph;

public class EnergyDockPowerGraph extends PowerGraph {
    private final WindowedMean powerBalance = new WindowedMean(60);
    private final WindowedMean powerBalanceVisual = new WindowedMean(60);
    private float lastPowerProduced, lastPowerNeeded, lastPowerStored;
    private float lastScaledPowerIn, lastScaledPowerOut, lastCapacity;

    private float powerNeededScheduled, powerProducedScheduled,deltaTimeScheduled,
                energyDeltaScheduled, transferPowerScheduled;

    public float timePassed = 0f;
    public int transferTime = 60;
    public boolean isInProgress = false;
    public boolean isTransferring = false;

    //diodes workaround for correct energy production info
    private float energyDelta = 0f;

    public boolean isSupplier() {
        return transferPowerScheduled > 0;
    }


    @Override
    public float getLastScaledPowerIn() {
        return lastScaledPowerIn;
    }

    @Override
    public float getLastScaledPowerOut() {
        return lastScaledPowerOut;
    }

    @Override
    public float getLastPowerStored() {
        return lastPowerStored;
    }

    @Override
    public float getLastCapacity() {
        return lastCapacity;
    }

    @Override
    public float getPowerBalance() {
        return powerBalance.rawMean();
    }

    public float getPowerBalanceVisual() {
        return powerBalanceVisual.rawMean();
    }

    @Override
    public void transferPower(float amount) {
        isTransferring = true;
        isInProgress = true;

        energyDeltaScheduled+=amount;
        transferPowerScheduled+=amount;

        if(timePassed <= transferTime) return;
        timePassed %= 60;
        isTransferring = false;

        if(transferPowerScheduled > 0){
            chargeBatteries(transferPowerScheduled);
        }else{
            useBatteries(-transferPowerScheduled);
        }
        energyDelta += energyDeltaScheduled;
        transferPowerScheduled = 0;
        energyDeltaScheduled = 0;
    }

    @Override
    public void update() {
        if(!consumers.isEmpty() && consumers.first().cheating()){
            //when cheating, just set status to 1
            for(Building tile : consumers){
                tile.power.status = 1f;
            }

            lastPowerNeeded = lastPowerProduced = 1f;
            return;
        }

        float powerNeeded = getPowerNeeded();
        float powerProduced = getPowerProduced();

        powerBalanceVisual.add((powerProduced - powerNeeded + energyDelta) / Time.delta);

        if(isTransferring) timePassed+=Time.delta;
        if((powerNeeded == 0 && powerProduced == 0) || isTransferring) {
            isInProgress = isTransferring;
            return;
        }
        powerNeededScheduled+=powerNeeded;
        powerProducedScheduled+=powerProduced;

        deltaTimeScheduled+=Time.delta;
        timePassed+=Time.delta;
        if(timePassed < transferTime) return;

        timePassed %= transferTime;
        lastPowerNeeded = powerNeededScheduled;
        lastPowerProduced = powerProducedScheduled;

        lastScaledPowerIn = (powerProducedScheduled + energyDelta) / deltaTimeScheduled;
        lastScaledPowerOut = powerNeededScheduled / deltaTimeScheduled;
        lastCapacity = getTotalBatteryCapacity();
        lastPowerStored = getBatteryStored();

        powerBalance.add((lastPowerProduced - lastPowerNeeded + energyDelta) / deltaTimeScheduled);
        energyDelta = 0f;

        isInProgress = true;

        if(!(consumers.size == 0 && producers.size == 0 && batteries.size == 0)){
            boolean charged = false;

            if(!Mathf.equal(powerNeededScheduled, powerProducedScheduled)){
                if(powerNeededScheduled > powerProducedScheduled){
                    float powerBatteryUsed = useBatteries(powerNeededScheduled - powerProducedScheduled);
                    powerProducedScheduled += powerBatteryUsed;
                    lastPowerProduced += powerBatteryUsed;
                }else if(powerProducedScheduled > powerNeededScheduled){
                    charged = true;
                    powerProducedScheduled -= chargeBatteries(powerProducedScheduled - powerNeededScheduled);
                }
            }

            distributePower(powerNeededScheduled, powerProducedScheduled, charged);
        }
    }

}
