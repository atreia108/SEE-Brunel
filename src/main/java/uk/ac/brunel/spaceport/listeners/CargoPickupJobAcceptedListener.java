package uk.ac.brunel.spaceport.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGCargoPickupJobAccepted;
import uk.ac.brunel.spaceport.systems.VehicleAssignmentRequestSystem;

public class CargoPickupJobAcceptedListener implements InteractionListener {
    private final VehicleAssignmentRequestSystem system;

    public CargoPickupJobAcceptedListener(VehicleAssignmentRequestSystem system) {
        this.system = system;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGCargoPickupJobAccepted jobAccepted
                && jobAccepted.getRequestingObject().equals(system.getEntityName())) {
            system.vehicleAssigned(jobAccepted.getAssignedRover());
        }
    }
}
