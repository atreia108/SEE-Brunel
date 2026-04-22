package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLandingRequest;
import uk.ac.brunel.models.Spaceport;

import java.util.Set;

public class LanderListener implements InteractionListener {
    private final Set<Spaceport> spaceports;
    public LanderListener(Set<Spaceport> spaceports) {
        this.spaceports = spaceports;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLandingRequest) {
            MSGLandingRequest landingRequest = (MSGLandingRequest) interaction;
            String targetSpaceportName = landingRequest.getSpaceport();
            Spaceport targetSpaceport = findSpaceport(targetSpaceportName);

            if (targetSpaceport != null) {
                targetSpaceport.acceptLander();
            }
        }
    }

    private Spaceport findSpaceport(String name) {
        return spaceports.stream()
                .filter(s -> s.getName().equals(name))
                .findAny()
                .orElse(null);
    }
}
