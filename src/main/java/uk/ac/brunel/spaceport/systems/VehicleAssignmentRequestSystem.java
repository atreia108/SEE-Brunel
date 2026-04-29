package uk.ac.brunel.spaceport.systems;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.core.SKBaseFederate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.interactions.MSGCargoPickupJob;
import uk.ac.brunel.spaceport.Spaceport;
import uk.ac.brunel.types.CargoType;

import java.util.concurrent.atomic.AtomicInteger;

public class VehicleAssignmentRequestSystem extends AbstractSimulationSystem {
    private static final Logger logger = LoggerFactory.getLogger(VehicleAssignmentRequestSystem.class);

    private static final short COOLDOWN_LIMIT = 10;
    private static final Vector3D WAREHOUSE_COORDINATES = Vector3D.of(801, 3053, -5532);

    private final String spaceportName;
    private final Vector3D entityCoordinates;
    private final SKBaseFederate federate;

    private final AtomicInteger operatingMode;
    private short vehicleRequestCooldownTimer;

    public VehicleAssignmentRequestSystem(Spaceport spaceport, Vector3D spaceportCoordinates, SKBaseFederate federate) {
        this.entityCoordinates = spaceportCoordinates;
        this.federate = federate;

        vehicleRequestCooldownTimer = 0;
        spaceportName = spaceport.getName();
        operatingMode = spaceport.getOperatingMode();
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
        job.setRequestingObject(spaceportName);
        job.setCargoType(resourceName);

        if (operatingMode.get() == 0) {
            job.setPickupLocation(entityCoordinates);
            job.setDeliveryLocation(WAREHOUSE_COORDINATES);
        } else if (operatingMode.get() == 1){
            job.setPickupLocation(WAREHOUSE_COORDINATES);
            job.setDeliveryLocation(entityCoordinates);
        }

        dispatchInteraction(job);
        vehicleRequestCooldownTimer = COOLDOWN_LIMIT;

        if (operatingMode.get() == 0) {
            logger.info("<{}> dispatched a cargo pickup mission.", spaceportName);
        } else {
            logger.info("<{}> dispatched a cargo delivery mission.", spaceportName);
        }
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

    public String getSpaceportName() {
        return spaceportName;
    }
}
