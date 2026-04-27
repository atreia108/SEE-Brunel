package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLanderTouchdown;
import uk.ac.brunel.models.Spaceport;

/**
 * Notifies a spaceport that the lander previously granted permission to occupy it, has landed. Fired when a
 * MSGLanderTouchdown interaction is received.
 *
 * @author Hridyanshu Aatreya
 */
public class TouchdownListener implements InteractionListener {
    private final Spaceport spaceport;

    public TouchdownListener(Spaceport spaceport) {
        this.spaceport = spaceport;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLanderTouchdown touchdownNotification
                && touchdownNotification.getSpaceport().equals(spaceport.getName())) {
            String landerName = touchdownNotification.getLander();
            spaceport.onLanderTouchDown(landerName);
        }
    }
}
