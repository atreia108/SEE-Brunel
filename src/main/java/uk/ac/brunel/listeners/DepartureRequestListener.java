package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLanderDepartureRequest;
import uk.ac.brunel.models.Lander;

/**
 * Notifies a lander that it has been instructed to depart from a spaceport. Fired when a MSGLanderDepartureRequest
 * interaction is received.
 *
 * @author Hridyanshu Aatreya
 */
public class DepartureRequestListener implements InteractionListener {
    private final Lander lander;

    public DepartureRequestListener(Lander lander) {
        this.lander = lander;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLanderDepartureRequest departureRequest && departureRequest.getLander().equals(lander.getName())) {
            lander.depart(departureRequest.getSpaceport());
        }
    }
}
