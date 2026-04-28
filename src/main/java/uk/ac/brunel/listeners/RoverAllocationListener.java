package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGCargoPickupJobAccepted;
import uk.ac.brunel.models.Spaceport;

public class RoverAllocationListener implements InteractionListener {
    private final Spaceport spaceport;

    public RoverAllocationListener(Spaceport spaceport) {
        this.spaceport = spaceport;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGCargoPickupJobAccepted jobAccepted) {
            String assignedRover = jobAccepted.getAssignedRover();
            String spaceportName = jobAccepted.getRequestingObject();

            if (spaceportName.equals(spaceport.getName())) {
                spaceport.onCargoPickupJobAccepted(assignedRover);
            }
        }
    }
}
