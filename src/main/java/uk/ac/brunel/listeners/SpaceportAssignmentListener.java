package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.federates.LanderFederate;
import uk.ac.brunel.interactions.MSGLandingPermission;
import uk.ac.brunel.models.Lander;
import uk.ac.brunel.models.Spaceport;
import uk.ac.brunel.types.OperationalVerdict;

/**
 * Notifies a lander that a spaceport has given it clearance to land. Fired when a MSGLandingPermission interaction is
 * received.
 *
 * @author Hridyanshu Aatreya
 */
public class SpaceportAssignmentListener implements InteractionListener {
    private final Lander lander;
    private final LanderFederate federate;

    public SpaceportAssignmentListener(Lander lander, LanderFederate federate) {
        this.lander = lander;
        this.federate = federate;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLandingPermission) {
            MSGLandingPermission permission = (MSGLandingPermission) interaction;
            if (permission.getLander().equals(lander.getName()) && permission.getVerdict() == OperationalVerdict.ACCEPTED) {
                String spaceportName = permission.getSpaceport();
                Object spaceportQuery = federate.queryRemoteObjectInstance(spaceportName);

                if (spaceportQuery instanceof Spaceport) {
                    Spaceport spaceport = (Spaceport) spaceportQuery;
                    lander.setWaypoint(spaceport.getState().getPosition());
                }
            }
        }
    }
}
