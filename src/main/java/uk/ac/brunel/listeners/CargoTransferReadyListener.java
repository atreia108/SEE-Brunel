package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGCargoTransferReady;
import uk.ac.brunel.models.Spaceport;

public class CargoTransferReadyListener implements InteractionListener {
    private final Spaceport spaceport;

    public CargoTransferReadyListener(Spaceport spaceport) {
        this.spaceport = spaceport;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGCargoTransferReady transferReady
                && transferReady.getRequestingObject().equals(spaceport.getName())) {
            spaceport.initiateCargoTransfer();
        }
    }
}
