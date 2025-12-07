package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAInteractionQueue;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.components.HoldingPatternComponent;
import uk.ac.brunel.components.MovementComponent;
import uk.ac.brunel.components.NavigationComponent;
import uk.ac.brunel.lander.LanderSimulation;
import uk.ac.brunel.lander.NavigationDirection;
import uk.ac.brunel.utils.ComponentMappers;
import uk.ac.brunel.utils.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WaypointAllocSystem extends EntitySystem {
    private final Set<Entity> pendingLanders;

    private final Set<Entity> landers;

    public WaypointAllocSystem(LanderSimulation simulator) {
        pendingLanders = new HashSet<>();
        this.landers = simulator.getLanders();
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
        arrivalAllocation();
        departureAllocation();
    }

    private void arrivalAllocation() {
        ArrayList<Entity> federateMessages = HLAInteractionQueue.filter("HLAinteractionRoot.FederateMessage");

        for (Entity federateMessage : federateMessages) {
            FederateMessageComponent federateMessageComponent = ComponentMappers.federateMessage.get(federateMessage);

            Entity lander;

            if (federateMessageComponent != null && (lander = getLanderEntity(federateMessageComponent.receiver)) != null
            && federateMessageComponent.type.equals("BRUNEL_SPACEPORT_LANDER_ARRIVAL_ACKNOWLEDGED")) {
                calibrateLandingNavigation(lander, federateMessageComponent.content);
                deregister(lander);
                lander.remove(HoldingPatternComponent.class);

                System.out.println("Allocated " + federateMessageComponent.content + " to " + federateMessageComponent.receiver);
            }
        }
    }

    private void departureAllocation() {
        ArrayList<Entity> federateMessages = HLAInteractionQueue.filter("HLAinteractionRoot.FederateMessage");

        for (Entity federateMessage : federateMessages) {
            FederateMessageComponent federateMessageComponent = ComponentMappers.federateMessage.get(federateMessage);
            String landerName =  federateMessageComponent.receiver;

            Entity lander = findLander(landerName);
            if (lander != null) {
                HLAObjectComponent objectComponent = VegaUtilities.objectComponentMapper().get(lander);
                NavigationComponent navigationComponent = ComponentMappers.navigation.get(lander);

                if (federateMessageComponent.receiver.equals(objectComponent.instanceName)
                        && federateMessageComponent.type.equals("BRUNEL_SPACEPORT_LANDER_REQUEST_DEPARTURE")
                        && navigationComponent == null) {
                    calibrateDepartureNavigation(lander);
                }
                /*
                if (federateMessageComponent.receiver.equals(objectComponent.instanceName) &&
                        navigationComponent != null && !(navigationComponent.direction == NavigationDirection.ARRIVAL || navigationComponent.direction == NavigationDirection.DEPARTURE)) {
                    calibrateDepartureNavigation(lander);
                }
                 */
            }
        }
    }

    private Vector3 getRandomDestination() {
        Random rand = new Random();
        int selection = rand.nextInt(0, 4);

        switch (selection) {
            case 0:
                return World.POINT_CHARLIE.cpy();
            case 1:
                return World.POINT_CHARLIE.cpy();
            case 2:
                return World.POINT_CHARLIE.cpy();
            default:
                // Origin of Aitken Basin reference frame
                return World.POINT_CHARLIE.cpy();
        }
    }

    private Entity findLander(String instanceName) {
        for (Entity lander : landers) {
            HLAObjectComponent objectComponent = VegaUtilities.objectComponentMapper().get(lander);
            if (objectComponent != null && objectComponent.instanceName.equals(instanceName)) {
                return lander;
            }
        }

        return null;
    }

    private void calibrateLandingNavigation(Entity lander, String launchPadName) {
        Engine engine = VegaUtilities.engine();
        NavigationComponent navigationComponent = engine.createComponent(NavigationComponent.class);
        navigationComponent.direction = NavigationDirection.ARRIVAL;
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

    private void calibrateDepartureNavigation(Entity lander) {
        Engine engine = VegaUtilities.engine();
        NavigationComponent navigationComponent = engine.createComponent(NavigationComponent.class);
        navigationComponent.waypoint = getRandomDestination();
        navigationComponent.direction = NavigationDirection.DEPARTURE;

        lander.add(navigationComponent);

        MovementComponent movementComponent = ComponentMappers.movement.get(lander);
        movementComponent.vel.x = 10.0f;
        movementComponent.vel.y = 10.0f;
        movementComponent.vel.z = 10.0f;

        System.out.println("Lander is headed to " + navigationComponent.direction);
    }
}
