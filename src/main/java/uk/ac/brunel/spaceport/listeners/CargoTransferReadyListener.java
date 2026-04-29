package uk.ac.brunel.spaceport.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGCargoTransferReady;
import uk.ac.brunel.spaceport.systems.CargoTransferSystem;
import uk.ac.brunel.spaceport.systems.VehicleAssignmentRequestSystem;

public class CargoTransferReadyListener implements InteractionListener {
    private final VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem;
    private final CargoTransferSystem cargoTransferSystem;

    public CargoTransferReadyListener(VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem, CargoTransferSystem cargoTransferSystem) {
        this.vehicleAssignmentRequestSystem = vehicleAssignmentRequestSystem;
        this.cargoTransferSystem = cargoTransferSystem;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGCargoTransferReady transferReady
                && transferReady.getRequestingObject().equals(cargoTransferSystem.getSpaceportName())
                && transferReady.getRover().equals(cargoTransferSystem.getAssignedVehicle())) {
            cargoTransferSystem.initiateCargoTransfer();
            vehicleAssignmentRequestSystem.disable();
        }
    }
}
