package uk.ac.brunel.spaceport;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.interactions.MSGLanderDepartureRequest;
import uk.ac.brunel.interactions.MSGLandingPermission;
import uk.ac.brunel.types.OperationalVerdict;

import java.util.concurrent.atomic.AtomicBoolean;

public class LanderLiaison {
    private static final Logger logger = LoggerFactory.getLogger(LanderLiaison.class);

    private static final short COMMITMENT_REPLY_WINDOW = 5;

    private final Spaceport spaceport;
    private final SKBaseFederate federate;
    private final AtomicBoolean awaitingCommitment;
    private short commitmentReplyWindowCounter;
    private String occupyingLander;

    public LanderLiaison(Spaceport spaceport, SKBaseFederate federate) {
        this.spaceport = spaceport;
        this.federate = federate;

        occupyingLander = "";
        awaitingCommitment = new AtomicBoolean(false);
        commitmentReplyWindowCounter = 0;

        // enable();
    }

    public synchronized void processLandingRequest(String landerName) {
        boolean outcome = false;

        if (!occupied()) {
            occupyingLander = landerName;
            outcome = true;
            updateSpaceportStatusAtRti("Occupied");
            // logger.info("<{}> is now conditionally assigned to {}.", spaceport.getName(), landerName);

            // beginCommitmentReplyWindow();
        } else {
            logger.info("<{}>'s landing request was rejected by {}", landerName, spaceport.getName());
        }

        dispatchPermission(landerName, outcome);
    }

    private void beginCommitmentReplyWindow() {
        awaitingCommitment.set(true);
        commitmentReplyWindowCounter = COMMITMENT_REPLY_WINDOW;
    }

    private void endCommitmentReplyWindow() {
        awaitingCommitment.set(false);
        commitmentReplyWindowCounter = 0;
    }

    public synchronized void landerCommitAction(String landerName) {
        awaitingCommitment.set(false);
        commitmentReplyWindowCounter = 0;

        logger.info("<{}> is now officially assigned to {}.", landerName, spaceport.getName());
    }

    private boolean embargoInEffect() {
        return commitmentReplyWindowCounter > 0;
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
        // endCommitmentReplyWindow();
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

    /*
    @Override
    public void update() {
        if (embargoInEffect()) {
            commitmentReplyWindowCounter--;
            return;
        }

        if (awaitingCommitment.get()) {
            logger.info("{} failed to commit to {} within the reply time window.", occupyingLander, spaceport.getName());
            free();
        }
    }
     */
}
