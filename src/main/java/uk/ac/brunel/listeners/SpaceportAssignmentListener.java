package uk.ac.brunel.listeners;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
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

    public SpaceportAssignmentListener(Lander lander) {
        this.lander = lander;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLandingPermission) {
            MSGLandingPermission permission = (MSGLandingPermission) interaction;

            if (permission.getLander().equals(lander.getName()) && permission.getVerdict() == OperationalVerdict.ACCEPTED) {
                String spaceportName = permission.getSpaceport();
                lander.acceptedBy(spaceportName);
            }
        }
    }
}
