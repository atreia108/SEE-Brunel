package uk.ac.brunel.lander.listeners;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.InteractionListener;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.interactions.MSGLandingPermission;
import uk.ac.brunel.interactions.MSGSpaceportArrivalCommitted;
import uk.ac.brunel.lander.Lander;
import uk.ac.brunel.lander.systems.NavigationSystem;
import uk.ac.brunel.lander.systems.SpaceportAllocationRequestSystem;
import uk.ac.brunel.types.OperationalVerdict;

public class LandingPermissionListener implements InteractionListener {
    private final Lander lander;
    private final SKBaseFederate federate;
    private final SpaceportAllocationRequestSystem spaceportAllocationRequestSystem;
    private final NavigationSystem navigationSystem;

    public LandingPermissionListener(Lander lander, SKBaseFederate federate) {
        this.lander = lander;
        this.federate = federate;

        spaceportAllocationRequestSystem = lander.getSpaceportAllocationRequestSystem();
        navigationSystem = lander.getNavigationSystem();
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLandingPermission landingPermission
                && landingPermission.getLander().equals(lander.getName())
                && landingPermission.getVerdict().equals(OperationalVerdict.ACCEPTED)) {

            String spaceportName = landingPermission.getSpaceport();
            spaceportAllocationRequestSystem.disable();

            navigationSystem.spaceportAssigned(spaceportName);
            navigationSystem.enable();

            lander.setStatus("Landing");

            // TODO WATCH: This line causes a "benign" exception from time to time.
            try {
                federate.updateObjectInstance(lander);
            } catch (Exception e) {
                e.printStackTrace();
            }

            confirmArrival(spaceportName);
        }
    }

    private void confirmArrival(String spaceportName) {
        MSGSpaceportArrivalCommitted commitment = new MSGSpaceportArrivalCommitted(lander.getName(), spaceportName);
        dispatchInteraction(commitment);
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
