package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import io.github.atreia108.vega.core.HLAInteractionQueue;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.components.NavigationComponent;
import uk.ac.brunel.utils.ComponentMappers;
import uk.ac.brunel.utils.World;

import java.util.ArrayList;

public class WaypointAllocSystem extends EntitySystem {
    private final Engine engine;

    public WaypointAllocSystem() {
        engine = VegaUtilities.engine();
    }

    @Override
    public void update(float deltaTime) {
        ArrayList<Entity> federateMessageInteractions = HLAInteractionQueue.filter("HLAinteractionRoot.FederateMessage");

        for (Entity federateMessage : federateMessageInteractions) {
            FederateMessageComponent messageComponent = ComponentMappers.federateMessage.get(federateMessage);

            if (messageComponent != null &&
                    (messageComponent.receiver.equals("brunel_lander_1")
                            || messageComponent.receiver.equals("brunel_lander_2")
                            || messageComponent.receiver.equals("brunel_lander_3"))) {

                NavigationComponent navComponent = engine.createComponent(NavigationComponent.class);

                if (messageComponent.type.equals("BRUNEL_SPACEPORT_LANDER_ARRIVAL_ACKNOWLEDGED")) {
                    if (messageComponent.content.equals("LPAD_1")) {
                        navComponent.waypoint = World.LPAD_1;
                    } else {
                        navComponent.waypoint = World.LPAD_2;
                    }
                }
            }
        }
    }
}
