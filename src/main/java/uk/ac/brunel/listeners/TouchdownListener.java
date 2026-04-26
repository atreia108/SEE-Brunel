package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLanderTouchdown;
import uk.ac.brunel.models.Spaceport;

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
            spaceport.landerTouchedDown(landerName);
        }
    }
}
