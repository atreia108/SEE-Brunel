package uk.ac.brunel.spaceport.systems;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.interactions.UCFPowerRequest;
import uk.ac.brunel.spaceport.Powerable;
import uk.ac.brunel.spaceport.listeners.PowerAllocationListener;

public class PowerSystem extends AbstractSimulationSystem {
    private static final short COOLDOWN_LIMIT = 8;

    private final String entityName;
    private final Powerable entity;
    private final SKBaseFederate federate;

    private double allocatedPower;
    private short powerRequestCooldownTimer;

    public PowerSystem(String entityName, Powerable entity, SKBaseFederate federate) {
        this.entityName = entityName;
        this.entity = entity;
        this.federate = federate;

        allocatedPower = 0.0;
        powerRequestCooldownTimer = 0;

        createEventListeners();
        enable();
    }

    private void createEventListeners() {
        federate.addInteractionListener(new PowerAllocationListener(this));
    }

    @Override
    public void update() {
        if (embargoInEffect()) {
            powerRequestCooldownTimer--;
            return;
        }

        if (allocatedPower < (0.5 * entity.powerConsumption())) {
            requestPower();
            powerRequestCooldownTimer = COOLDOWN_LIMIT;
        }
    }

    private boolean embargoInEffect() {
        return powerRequestCooldownTimer > 0;
    }

    public synchronized double consume(double amount) {
        double threshold = 0.3 * allocatedPower;

        // Go into reserve mode where until a new power request brings in more power.
        if ((allocatedPower - amount) > threshold) {
            allocatedPower -= amount;
            return amount;
        } else {
            return 0;
        }
    }

    public synchronized void allocate(double amount) {
        allocatedPower += amount;
    }

    private void requestPower() {
        double amount = entity.powerConsumption();
        int priorityLevel = entity.powerPriorityLevel();
        UCFPowerRequest powerRequest = new UCFPowerRequest(entityName, amount, priorityLevel);
        dispatchInteraction(powerRequest);
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
