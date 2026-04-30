package uk.ac.brunel.lander.systems;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.interactions.MSGLandingRequest;
import uk.ac.brunel.lander.Lander;

import java.util.Set;

public class SpaceportAllocationRequestSystem extends AbstractSimulationSystem {
    private static final short COOLDOWN_LIMIT = 10;

    private final Lander lander;
    private final Set<PhysicalEntity> spaceports;
    private final SKBaseFederate federate;

    private short requestCooldownTimer;

    public SpaceportAllocationRequestSystem(Lander lander, Set<PhysicalEntity> spaceports, SKBaseFederate federate) {
        this.lander = lander;
        this.spaceports = spaceports;
        this.federate = federate;

        requestCooldownTimer = 0;
        enable();
    }

    @Override
    public void update() {
        if (embargoInEffect()) {
            requestCooldownTimer--;
            return;
        }

        requestSpaceportAllocation();
    }

    private boolean embargoInEffect() {
        return requestCooldownTimer > 0;
    }

    private void requestSpaceportAllocation() {
        for (PhysicalEntity spaceport : spaceports) {
            String landerName = lander.getName();
            String spaceportStatus = spaceport.getStatus();

            if (spaceportStatus.equals("Available")) {
                String spaceportName = spaceport.getName();
                MSGLandingRequest landingRequest = new MSGLandingRequest(landerName, spaceportName);
                dispatchInteraction(landingRequest);

                requestCooldownTimer = COOLDOWN_LIMIT;
                return;
            }
        }
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }
}
