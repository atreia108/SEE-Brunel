package uk.ac.brunel.spaceport.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGCargoPickupJobAccepted;
import uk.ac.brunel.spaceport.systems.CargoTransferSystem;
import uk.ac.brunel.spaceport.systems.VehicleAssignmentRequestSystem;

public class CargoPickupJobAcceptedListener implements InteractionListener {
    private final VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem;
    private final CargoTransferSystem cargoTransferSystem;

    public CargoPickupJobAcceptedListener(VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem, CargoTransferSystem cargoTransferSystem) {
        this.vehicleAssignmentRequestSystem = vehicleAssignmentRequestSystem;
        this.cargoTransferSystem = cargoTransferSystem;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGCargoPickupJobAccepted jobAccepted
                && jobAccepted.getRequestingObject().equals(vehicleAssignmentRequestSystem.getSpaceportName())) {
            vehicleAssignmentRequestSystem.disable();

            cargoTransferSystem.vehicleAssigned(jobAccepted.getAssignedRover());
        }
    }
}
