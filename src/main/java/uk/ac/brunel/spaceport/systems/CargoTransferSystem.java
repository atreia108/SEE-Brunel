package uk.ac.brunel.spaceport.systems;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.interactions.MSGCargoTransferComplete;
import uk.ac.brunel.spaceport.SpaceportArm;
import uk.ac.brunel.spaceport.listeners.CargoTransferReadyListener;

public class CargoTransferSystem extends AbstractSimulationSystem {
    private final String entityName;
    private final SpaceportArm arm;
    private final SKBaseFederate federate;

    private String assignedVehicle;

    public CargoTransferSystem(String entityName, SpaceportArm arm, SKBaseFederate federate) {
        this.entityName = entityName;
        this.arm = arm;
        this.federate = federate;

        assignedVehicle = "";

        createEventListeners();
    }

    private void createEventListeners() {
        federate.addInteractionListener(new CargoTransferReadyListener(this));
    }

    @Override
    public void update() {
        if (!arm.isRunning() && !arm.cleanupPeriodActive()) {
            dispatchTransferCompletionNotification();
            disable();
        }
    }

    private void dispatchTransferCompletionNotification() {
        MSGCargoTransferComplete transferComplete = new MSGCargoTransferComplete(assignedVehicle, entityName);
        dispatchInteraction(transferComplete);
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized void vehicleAssigned(String vehicleName) {
        assignedVehicle = vehicleName;
    }

    public synchronized void initiateCargoTransfer() {
        arm.start();
        enable();
    }

    public String getEntityName() {
        return entityName;
    }

    public String getAssignedVehicle() {
        return assignedVehicle;
    }
}
