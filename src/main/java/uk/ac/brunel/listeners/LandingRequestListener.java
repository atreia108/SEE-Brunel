package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLandingRequest;
import uk.ac.brunel.models.Spaceport;

public class LandingRequestListener implements InteractionListener {
    private final Spaceport spaceport;

    public LandingRequestListener(Spaceport spaceport) {
        this.spaceport = spaceport;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLandingRequest landingRequest) {
            String targetSpaceport = landingRequest.getSpaceport();
            if (targetSpaceport.equals(spaceport.getName())) {
                spaceport.acceptLander();
            }
        }
    }
}
