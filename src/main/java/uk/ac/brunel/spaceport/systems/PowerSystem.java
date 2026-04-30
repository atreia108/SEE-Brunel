package uk.ac.brunel.spaceport.systems;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.interactions.UCFPowerRequest;
import uk.ac.brunel.spaceport.Powerable;

public class PowerSystem extends AbstractSimulationSystem {
    private static final Logger logger = LoggerFactory.getLogger(PowerSystem.class);

    private static final short COOLDOWN_LIMIT = 8;

    private final String entityName;
    private final Powerable entity;
    private final SKBaseFederate federate;

    private double reserveAmount;
    private short powerRequestCooldownTimer;

    public PowerSystem(String entityName, Powerable entity, SKBaseFederate federate) {
        this.entityName = entityName;
        this.entity = entity;
        this.federate = federate;

        // TODO - Change to 0 again!!
        // reserveAmount = 0.0;
        reserveAmount = Double.MAX_VALUE;

        powerRequestCooldownTimer = 0;

        enable();
    }

    @Override
    public void update() {
        if (embargoInEffect()) {
            powerRequestCooldownTimer--;
            return;
        }

        if (reserveAmount < (0.5 * entity.powerConsumption())) {
            requestPower();
            powerRequestCooldownTimer = COOLDOWN_LIMIT;
        }
    }

    private boolean embargoInEffect() {
        return powerRequestCooldownTimer > 0;
    }

    public synchronized double consume(double amount) {
        double threshold = 0.3 * reserveAmount;

        // Go into reserve mode where until a new power request brings in more power.
        if ((reserveAmount - amount) > threshold) {
            reserveAmount -= amount;
            return amount;
        } else {
            return 0;
        }
    }

    public synchronized void allocate(double amount) {
        logger.info("{} was allocated <{}> kW.", entityName, amount);
        reserveAmount += amount;
    }

    private void requestPower() {
        double amount = entity.powerConsumption();
        int priorityLevel = entity.powerPriorityLevel();
        UCFPowerRequest powerRequest = new UCFPowerRequest(entityName, amount, priorityLevel);
        dispatchInteraction(powerRequest);

        logger.info("Requested <{}> kW of power with priority level {}. Residual amount: {}.", amount, priorityLevel, reserveAmount);
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }

    public String getEntityName() {
        return entityName;
    }
}
