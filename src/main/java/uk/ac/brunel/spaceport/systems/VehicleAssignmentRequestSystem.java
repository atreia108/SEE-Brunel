package uk.ac.brunel.spaceport.systems;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.interactions.MSGCargoPickupJob;
import uk.ac.brunel.spaceport.listeners.CargoPickupJobAcceptedListener;
import uk.ac.brunel.types.CargoType;

public class VehicleAssignmentRequestSystem extends AbstractSimulationSystem {
    private static final short COOLDOWN_LIMIT = 10;
    private static final Vector3D WAREHOUSE_COORDINATES = Vector3D.of(801, 3053, -5532);

    private final String entityName;
    private final Vector3D entityCoordinates;
    private final CargoTransferSystem transferSystem;
    private final SKBaseFederate federate;

    private short requestType;
    private short vehicleRequestCooldownTimer;

    public VehicleAssignmentRequestSystem(String entityName, Vector3D entityCoordinates, CargoTransferSystem transferSystem, SKBaseFederate federate) {
        this.entityName = entityName;
        this.entityCoordinates = entityCoordinates;
        this.transferSystem = transferSystem;
        this.federate = federate;
        requestType = 0;

        vehicleRequestCooldownTimer = 0;

        createEventListeners();
    }

    private void createEventListeners() {
        federate.addInteractionListener(new CargoPickupJobAcceptedListener(this));
    }

    @Override
    public void update() {
        if (isEmbargoInEffect()) {
            vehicleRequestCooldownTimer--;
            return;
        }

        dispatchCargoMission();
    }

    private void dispatchCargoMission() {
        String resourceName = CargoType.randomType().name();

        MSGCargoPickupJob job = new MSGCargoPickupJob();
        job.setRequestingObject(entityName);
        job.setCargoType(resourceName);

        if (requestType == 0) {
            job.setPickupLocation(entityCoordinates);
            job.setDeliveryLocation(WAREHOUSE_COORDINATES);
        } else if (requestType == 1){
            job.setPickupLocation(WAREHOUSE_COORDINATES);
            job.setDeliveryLocation(entityCoordinates);
        }

        dispatchInteraction(job);
        vehicleRequestCooldownTimer = COOLDOWN_LIMIT;
    }

    private boolean isEmbargoInEffect() {
        return vehicleRequestCooldownTimer > 0;
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }

    // Type 0: Pickup
    // Type 1: Delivery
    public synchronized void flipRequestType() {
        if (requestType == 0) {
            requestType = 1;
        } else {
            requestType = 0;
        }
    }

    public synchronized void vehicleAssigned(String vehicleName) {
        transferSystem.vehicleAssigned(vehicleName);
        flipRequestType();
        disable();
    }

    public String getEntityName() {
        return entityName;
    }
}
