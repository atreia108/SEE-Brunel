package uk.ac.brunel.spaceport.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLandingRequest;
import uk.ac.brunel.spaceport.LanderLiaison;

public class LandingRequestListener implements InteractionListener {
    private final LanderLiaison landerLiaison;

    public LandingRequestListener(LanderLiaison landerLiaison) {
        this.landerLiaison = landerLiaison;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLandingRequest landingRequest
                && landingRequest.getSpaceport().equals(landerLiaison.getSpaceportName())) {
            landerLiaison.processLandingRequest(landingRequest.getLander());
        }
    }
}
