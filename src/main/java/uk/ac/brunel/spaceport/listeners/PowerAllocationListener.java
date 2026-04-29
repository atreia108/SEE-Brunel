package uk.ac.brunel.spaceport.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.UCFPowerAllocation;
import uk.ac.brunel.spaceport.systems.PowerSystem;

public class PowerAllocationListener implements InteractionListener {
    private final PowerSystem system;

    public PowerAllocationListener(PowerSystem system) {
        this.system = system;
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof UCFPowerAllocation allocation
                && allocation.getFederateID().equals(system.getEntityName())) {
            system.allocate(allocation.getKw());
        }
    }
}
