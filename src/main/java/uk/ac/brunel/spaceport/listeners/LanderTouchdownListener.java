package uk.ac.brunel.spaceport.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLanderTouchdown;
import uk.ac.brunel.spaceport.LanderLiaison;
import uk.ac.brunel.spaceport.systems.VehicleAssignmentRequestSystem;

public class LanderTouchdownListener implements InteractionListener {
    private final LanderLiaison landerLiaison;
    private final VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem;

    public LanderTouchdownListener(LanderLiaison landerLiaison, VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem) {
        this.landerLiaison = landerLiaison;
        this.vehicleAssignmentRequestSystem = vehicleAssignmentRequestSystem;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLanderTouchdown landerTouchdown
                && landerTouchdown.getLander().equals(landerLiaison.getOccupyingLander())
                && landerTouchdown.getSpaceport().equals(landerLiaison.getSpaceportName())) {
            vehicleAssignmentRequestSystem.enable();
        }
    }
}
