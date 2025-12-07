package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAInteractionQueue;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.components.HoldingPatternComponent;
import uk.ac.brunel.components.MovementComponent;
import uk.ac.brunel.components.NavigationComponent;
import uk.ac.brunel.utils.ComponentMappers;
import uk.ac.brunel.utils.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WaypointAllocSystem extends EntitySystem {
    private final Set<Entity> pendingLanders;

    public WaypointAllocSystem() {
        pendingLanders = new HashSet<>();
    }

    public void register(Entity lander) {
        pendingLanders.add(lander);
    }

    public void deregister(Entity lander) {
        pendingLanders.remove(lander);
        lander.remove(HoldingPatternComponent.class);
    }

    public boolean isRegistered(Entity lander) {
        return pendingLanders.contains(lander);
    }

    public Entity getLanderEntity(String instanceName) {
        for (Entity entity : pendingLanders) {
            HLAObjectComponent objectComponent = VegaUtilities.objectComponentMapper().get(entity);
            if (objectComponent != null && objectComponent.instanceName.equals(instanceName)) {
                return entity;
            }
        }

        return null;
    }

    @Override
    public void update(float deltaTime) {
        ArrayList<Entity> federateMessages = HLAInteractionQueue.filter("HLAinteractionRoot.FederateMessage");

        for (Entity federateMessage : federateMessages) {
            FederateMessageComponent federateMessageComponent = ComponentMappers.federateMessage.get(federateMessage);

            Entity lander;
            if (federateMessageComponent != null && (lander = getLanderEntity(federateMessageComponent.receiver)) != null) {
                calibrateNavigation(lander, federateMessageComponent.content);
                deregister(lander);
                lander.remove(HoldingPatternComponent.class);

                System.out.println("Allocated " + federateMessageComponent.content + " to " + federateMessageComponent.receiver);
            }
        }
    }

    private void calibrateNavigation(Entity lander, String launchPadName) {
        Engine engine = VegaUtilities.engine();
        NavigationComponent navigationComponent = engine.createComponent(NavigationComponent.class);
        MovementComponent movementComponent = ComponentMappers.movement.get(lander);
        movementComponent.vel.x = 10.0f;
        movementComponent.vel.y = 10.0f;
        movementComponent.vel.z = 10.0f;

        if (launchPadName.equals("LPAD_1")) {
            navigationComponent.waypoint = World.LPAD_1.cpy();
        } else {
            navigationComponent.waypoint = World.LPAD_2.cpy();
        }

        lander.add(navigationComponent);
    }
}
