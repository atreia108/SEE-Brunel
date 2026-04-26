package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.UCFPowerAllocation;
import uk.ac.brunel.models.Spaceport;

public class PowerAllocationListener implements InteractionListener {
    private final Spaceport spaceport;

    public PowerAllocationListener(Spaceport spaceport) {
        this.spaceport = spaceport;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof UCFPowerAllocation allocation
                && allocation.getFederateID().equals(spaceport.getName())) {
            double amountAllocated = allocation.getKw();
            spaceport.powerAllocation(amountAllocated);
        }
    }
}
