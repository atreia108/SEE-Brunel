package uk.ac.brunel.spaceport.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGCargoTransferReady;
import uk.ac.brunel.spaceport.systems.CargoTransferSystem;

public class CargoTransferReadyListener implements InteractionListener {
    private final CargoTransferSystem system;

    public CargoTransferReadyListener(CargoTransferSystem system) {
        this.system = system;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGCargoTransferReady transferReady
                && transferReady.getRequestingObject().equals(system.getEntityName())
                && transferReady.getRover().equals(system.getAssignedVehicle())) {
            system.initiateCargoTransfer();
        }
    }
}
