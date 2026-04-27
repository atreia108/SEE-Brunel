package uk.ac.brunel.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.UCFPowerAllocation;
import uk.ac.brunel.models.Spaceport;

/**
 * The amount of power allotted to the spaceport for a single time step by the UCF Power System. Fired when a
 * PowerAllocation interaction is received.
 *
 * @author Hridyanshu Aatreya
 */
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
            spaceport.setAllocatedPower(amountAllocated);
        }
    }
}
