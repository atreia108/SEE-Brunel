package uk.ac.brunel.spaceport;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.interactions.MSGLanderDepartureRequest;
import uk.ac.brunel.interactions.MSGLandingPermission;
import uk.ac.brunel.lander.LanderFederate;
import uk.ac.brunel.types.OperationalVerdict;

import java.util.Set;

public class LanderLiaison {
    private static final Logger logger = LoggerFactory.getLogger(LanderLiaison.class);

    private final Spaceport spaceport;
    private final Set<PhysicalEntity> landers;
    private final SKBaseFederate federate;

    private String occupyingLander;

    public LanderLiaison(Spaceport spaceport, Set<PhysicalEntity> landers, SKBaseFederate federate) {
        this.spaceport = spaceport;
        this.landers = landers;
        this.federate = federate;

        occupyingLander = "";
    }

    public synchronized void processLandingRequest(String landerName) {
        boolean outcome = false;

        PhysicalEntity lander = queryLander(landerName);

        if (!occupied() && lander != null && lander.getStatus().equals("Approaching")) {
            occupyingLander = landerName;
            outcome = true;
            spaceport.setStatus("Occupied");
            updateSpaceportAtRti();

            logger.info("<{}> is now assigned to {}.", spaceport.getName(), landerName);
        }

        logger.info("<{}>'s landing request was rejected by {}", landerName, spaceport.getName());

        dispatchPermission(landerName, outcome);
    }

    private PhysicalEntity queryLander(String landerName) {
        PhysicalEntity lander = isLanderAlreadyAvailable(landerName);

        if (lander == null) {
            lander = (PhysicalEntity) federate.queryRemoteObjectInstance(landerName);
            landers.add(lander);

            return lander;
        }

        return null;
    }

    private PhysicalEntity isLanderAlreadyAvailable(String landerName) {
        for (PhysicalEntity p : landers) {
            if (p.getName().equals(landerName)) {
                return p;
            }
        }

        return null;
    }

    public synchronized void initiateDeparture() {
        MSGLanderDepartureRequest departureRequest = new MSGLanderDepartureRequest(spaceport.getName(), occupyingLander);
        dispatchInteraction(departureRequest);

        free();
        spaceport.setStatus("Available");
        federate.updateObjectInstance(spaceport);
    }

    private boolean occupied() {
        return !occupyingLander.isEmpty();
    }

    private void free() {
        occupyingLander = "";
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

    private void updateSpaceportAtRti() {
        federate.updateObjectInstance(spaceport);
    }
}
