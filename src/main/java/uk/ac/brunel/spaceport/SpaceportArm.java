package uk.ac.brunel.spaceport;

import org.see.skf.annotations.ObjectClass;
import uk.ac.brunel.models.PhysicalInterface;
import uk.ac.brunel.core.SimulationEntity;
import uk.ac.brunel.spaceport.systems.PowerSystem;

import java.util.concurrent.atomic.AtomicBoolean;

@ObjectClass(name = "HLAobjectRoot.PhysicalInterface")
public class SpaceportArm extends PhysicalInterface implements SimulationEntity {
    // Power load of the arm that is incurred during its operational stages in kilowatts (kW).
    public static final double IDLE_POWER_RATING = 0.435;
    public static final double PEAK_POWER_RATING = 2.000;
    private static final int TRANSFER_OPERATION_TIME  = 10;
    private static final int CLEANUP_PERIOD = 10;

    private final PowerSystem powerSystem;
    private final AtomicBoolean running;

    private short cleanupPeriodCounter;
    private short transferOperationCounter;

    public SpaceportArm(String name, PowerSystem powerSystem) {
        this.powerSystem = powerSystem;

        running = new AtomicBoolean(false);
        cleanupPeriodCounter = 0;
        transferOperationCounter = 0;

        setName(name);
    }

    public void start() {
        running.set(true);
        transferOperationCounter = TRANSFER_OPERATION_TIME;
    }

    public void stop() {
        running.set(false);
        cleanupPeriodCounter = CLEANUP_PERIOD;
    }

    public boolean cleanupPeriodActive() {
        return cleanupPeriodCounter > 0;
    }

    @Override
    public void update() {
        if (running.get()) {
            runTransferOperation();
        } else {
            powerSystem.consume(idleConsumptionAmount());
        }

        if (cleanupPeriodActive()) {
            cleanupPeriodCounter--;
        }
    }

    private void runTransferOperation() {
        double consumptionAmount = peakConsumptionAmount();
        double allocatedWattage = powerSystem.consume(consumptionAmount);

        if (allocatedWattage >= consumptionAmount) {
            if (transferOperationCounter > 0) {
                transferOperationCounter--;
            } else {
                stop();
            }
        }
    }

    private double peakConsumptionAmount() {
        return 0.2 * PEAK_POWER_RATING;
    }

    private double idleConsumptionAmount() {
        return 0.2 * IDLE_POWER_RATING;
    }

    public boolean isRunning() {
        return running.get();
    }
}
