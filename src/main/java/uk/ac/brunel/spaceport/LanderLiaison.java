package uk.ac.brunel.spaceport;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.interactions.MSGLanderDepartureRequest;
import uk.ac.brunel.interactions.MSGLandingPermission;
import uk.ac.brunel.types.OperationalVerdict;

public class LanderLiaison {
    private static final Logger logger = LoggerFactory.getLogger(LanderLiaison.class);

    private final Spaceport spaceport;
    private final SKBaseFederate federate;
    private String occupyingLander;

    public LanderLiaison(Spaceport spaceport, SKBaseFederate federate) {
        this.spaceport = spaceport;
        this.federate = federate;

        occupyingLander = "";
    }

    public synchronized void processLandingRequest(String landerName) {
        boolean outcome = false;

        if (!occupied()) {
            occupyingLander = landerName;
            outcome = true;
            updateSpaceportStatusAtRti("Occupied");
        } else {
            logger.info("<{}>'s landing request was rejected by {}", landerName, spaceport.getName());
        }

        dispatchPermission(landerName, outcome);
    }

    public synchronized void initiateDeparture() {
        MSGLanderDepartureRequest departureRequest = new MSGLanderDepartureRequest(spaceport.getName(), occupyingLander);
        dispatchInteraction(departureRequest);

        free();
        updateSpaceportStatusAtRti("Available");
    }

    private boolean occupied() {
        return !occupyingLander.isEmpty();
    }

    private void free() {
        occupyingLander = "";
        updateSpaceportStatusAtRti("Available");
    }

    private void dispatchPermission(String landerName, boolean accepted) {
        MSGLandingPermission landingPermission = new MSGLandingPermission();
        String spaceportName = spaceport.getName();
        landingPermission.setSpaceport(spaceportName);
        landingPermission.setLander(landerName);

        if (accepted) {
            landingPermission.setVerdict(OperationalVerdict.ACCEPTED);
        } else {
            landingPermission.setVerdict(OperationalVerdict.REJECTED);
        }

        dispatchInteraction(landingPermission);
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }

    public String getSpaceportName() {
        return spaceport.getName();
    }

    public String getOccupyingLander() {
        return occupyingLander;
    }

    private void updateSpaceportStatusAtRti(String status) {
        spaceport.setStatus(status);
        federate.updateObjectInstance(spaceport);
    }
}
