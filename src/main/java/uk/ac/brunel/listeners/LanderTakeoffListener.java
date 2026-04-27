package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLanderTakeoff;
import uk.ac.brunel.models.Spaceport;

public class LanderTakeoffListener implements InteractionListener {
    private final Spaceport spaceport;

    public LanderTakeoffListener(Spaceport spaceport) {
        this.spaceport = spaceport;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLanderTakeoff landerTakeoff
                && landerTakeoff.getSpaceport().equals(spaceport.getName())
                && landerTakeoff.getLander().equals(spaceport.getOccupyingLander())) {
            spaceport.landerTakeoff();
        }
    }
}
