package uk.ac.brunel.spaceport.systems;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.interactions.MSGCargoTransferComplete;
import uk.ac.brunel.spaceport.LanderLiaison;
import uk.ac.brunel.spaceport.Spaceport;
import uk.ac.brunel.spaceport.SpaceportArm;

import java.util.concurrent.atomic.AtomicInteger;

public class CargoTransferSystem extends AbstractSimulationSystem {
    private static final Logger logger = LoggerFactory.getLogger(CargoTransferSystem.class);

    private final Spaceport spaceport;
    private final String spaceportName;
    private final SpaceportArm arm;
    private final LanderLiaison landerLiaison;
    private final SKBaseFederate federate;

    private final AtomicInteger operatingMode;
    private String assignedVehicle;

    public CargoTransferSystem(Spaceport spaceport, SpaceportArm arm, SKBaseFederate federate) {
        this.spaceport = spaceport;
        this.arm = arm;
        this.federate = federate;

        assignedVehicle = "";
        spaceportName = spaceport.getName();
        landerLiaison = spaceport.getLanderLiaison();
        operatingMode = spaceport.getOperatingMode();
    }

    @Override
    public void update() {
        if (!arm.isRunning() && !arm.cleanupPeriodActive()) {
            dispatchTransferCompletionNotification();
            postExecutionTasks();
            disable();
        }
    }

    private void dispatchTransferCompletionNotification() {
        MSGCargoTransferComplete transferComplete = new MSGCargoTransferComplete(assignedVehicle, spaceportName);
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

    private void postExecutionTasks() {
        if (operatingMode.get() == 0) {
            VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem = spaceport.getVehicleAssignmentRequestSystem();
            vehicleAssignmentRequestSystem.enable();
        } else {
            landerLiaison.initiateDeparture();
        }

        spaceport.flipOperatingMode();
    }

    public synchronized void vehicleAssigned(String vehicleName) {
        assignedVehicle = vehicleName;
    }

    public synchronized void initiateCargoTransfer() {
        arm.start();
        enable();

        logger.info("{} is beginning cargo transfer process.", spaceportName);
    }

    public String getSpaceportName() {
        return spaceportName;
    }

    public String getAssignedVehicle() {
        return assignedVehicle;
    }
}
