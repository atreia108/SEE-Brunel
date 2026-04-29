package uk.ac.brunel.lander.listeners;

import org.see.skf.core.InteractionListener;
import uk.ac.brunel.interactions.MSGLanderDepartureRequest;
import uk.ac.brunel.lander.Lander;
import uk.ac.brunel.lander.systems.NavigationSystem;

public class LanderDepartureRequestListener implements InteractionListener {
    private final Lander lander;
    private final NavigationSystem navigationSystem;

    public LanderDepartureRequestListener(Lander lander) {
        this.lander = lander;
        navigationSystem = lander.getNavigationSystem();
    }

    @Override
    public void received(Object interaction) {
        if (interaction instanceof MSGLanderDepartureRequest departureRequest && departureRequest.getLander().equals(lander.getName())) {
            navigationSystem.depart();
        }
    }
}
