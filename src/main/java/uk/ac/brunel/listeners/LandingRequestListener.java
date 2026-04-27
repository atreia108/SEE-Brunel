package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLandingRequest;
import uk.ac.brunel.models.Spaceport;

/**
 * Notifies a spaceport that a lander is requesting to occupy it. Fired when a MSGLandingRequest interaction
 * is received.
 *
 * @author Hridyanshu Aatreya
 */
public class LandingRequestListener implements InteractionListener {
    private final Spaceport spaceport;

    public LandingRequestListener(Spaceport spaceport) {
        this.spaceport = spaceport;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLandingRequest landingRequest) {
            String targetSpaceport = landingRequest.getSpaceport();
            String landerName = landingRequest.getLander();
            if (targetSpaceport.equals(spaceport.getName())) {
                spaceport.processLandingRequest(landerName);
            }
        }
    }
}
