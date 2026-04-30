package uk.ac.brunel.spaceport.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGSpaceportArrivalCommitted;
import uk.ac.brunel.spaceport.LanderLiaison;


// TODO - Delete since we solved the spam problem.
public class SpaceportArrivalCommitmentListener implements InteractionListener {
    private final String spaceportName;
    private final LanderLiaison liaison;

    public SpaceportArrivalCommitmentListener(String spaceportName, LanderLiaison liaison) {
        this.spaceportName = spaceportName;
        this.liaison = liaison;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGSpaceportArrivalCommitted commitment
                && commitment.getSpaceport().equals(spaceportName)) {
            String landerName = commitment.getLander();
            liaison.landerCommitAction(landerName);
        }
    }
}
