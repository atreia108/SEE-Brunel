package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.atreia108.vega.components.HLAInteractionComponent;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAInteractionManager;
import io.github.atreia108.vega.core.HLAObjectManager;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.components.MovementComponent;
import uk.ac.brunel.components.NavigationComponent;
import uk.ac.brunel.components.PositionComponent;
import uk.ac.brunel.lander.NavigationDirection;
import uk.ac.brunel.utils.ComponentMappers;

public class MovementSystem extends IteratingSystem {
    private static final float MIN_Z_ALTITUDE = -5387.0f;

    public MovementSystem() {
        super(Family.all(PositionComponent.class, MovementComponent.class, NavigationComponent.class, HLAObjectComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = ComponentMappers.position.get(entity);
        NavigationComponent navigationComponent = ComponentMappers.navigation.get(entity);
        MovementComponent movementComponent = ComponentMappers.movement.get(entity);
        HLAObjectComponent objectComponent = VegaUtilities.objectComponentMapper().get(entity);

        // Arrival
        if (navigationComponent.direction == NavigationDirection.ARRIVAL) {
            if (!horizontalConditionMet(positionComponent, navigationComponent)) {
                if (navigationComponent.waypoint.x > positionComponent.pos.x) {
                    positionComponent.pos.x += movementComponent.vel.x;
                } else if (navigationComponent.waypoint.x < positionComponent.pos.x) {
                    positionComponent.pos.x -= movementComponent.vel.x;
                }
                /*
                else {
                    movementComponent.vel.x = 0.0f;
                }

                 */

                if (navigationComponent.waypoint.y > positionComponent.pos.y) {
                    positionComponent.pos.y += movementComponent.vel.y;
                } else if (navigationComponent.waypoint.y < positionComponent.pos.y) {
                    positionComponent.pos.y -= movementComponent.vel.y;
                }
                /*
                else {
                    movementComponent.vel.y = 0.0f;
                }

                 */

                if (positionComponent.pos.z > MIN_Z_ALTITUDE) {
                    positionComponent.pos.z -= movementComponent.vel.z;
                }
            }
        }

        if (horizontalConditionMet(positionComponent, navigationComponent) && !verticalConditionMet(positionComponent, navigationComponent)) {

            if (navigationComponent.waypoint.z > positionComponent.pos.z) {
                positionComponent.pos.z += 10.0f;
            } else if (navigationComponent.waypoint.z < positionComponent.pos.z) {
                positionComponent.pos.z -= 10.0f;
            }
        } else {
            if (positionComponent.pos.z < MIN_Z_ALTITUDE) {
                positionComponent.pos.z += movementComponent.vel.z;
                return;
            } else if (positionComponent.pos.z == MIN_Z_ALTITUDE) {
                notifyDeparture(objectComponent.instanceName);
            }

            if (!horizontalConditionMet(positionComponent, navigationComponent)) {
                if (navigationComponent.waypoint.x > positionComponent.pos.x) {
                    positionComponent.pos.x += movementComponent.vel.x;
                } else if (navigationComponent.waypoint.x < positionComponent.pos.x) {
                    positionComponent.pos.x -= movementComponent.vel.x;
                }

                if (navigationComponent.waypoint.y > positionComponent.pos.y) {
                    positionComponent.pos.y += movementComponent.vel.y;
                } else if (navigationComponent.waypoint.y < positionComponent.pos.y) {
                    positionComponent.pos.y -= movementComponent.vel.y;
                }
            }

            if (!verticalConditionMet(positionComponent, navigationComponent)) {
                if (navigationComponent.waypoint.z > positionComponent.pos.z) {
                    positionComponent.pos.z += movementComponent.vel.z;
                } else if (navigationComponent.waypoint.z < positionComponent.pos.z) {
                    positionComponent.pos.z -= movementComponent.vel.z;
                }
            }
        }

        HLAObjectManager.sendInstanceUpdate(entity);
    }

    private boolean horizontalConditionMet(PositionComponent positionComponent, NavigationComponent navigationComponent) {
        boolean conditionX = Math.abs((navigationComponent.waypoint.x - positionComponent.pos.x)) < 10.0f;
        boolean conditionY = Math.abs((navigationComponent.waypoint.y - positionComponent.pos.y)) < 10.0f;

        if (conditionX && conditionY) {
            positionComponent.pos.x = navigationComponent.waypoint.x;
            positionComponent.pos.y = navigationComponent.waypoint.y;
        }

        return conditionX && conditionY;
    }

    private boolean verticalConditionMet(PositionComponent positionComponent, NavigationComponent navigationComponent) {
        boolean condition = Math.abs((navigationComponent.waypoint.z - positionComponent.pos.z)) <= 10.0f;

        if (condition) {
            positionComponent.pos.z = navigationComponent.waypoint.z;
        }

        return condition;
    }

    private void notifyDeparture(String instanceName) {
        Engine engine = VegaUtilities.engine();

        Entity interaction = engine.createEntity();
        HLAInteractionComponent interactionComponent = engine.createComponent(HLAInteractionComponent.class);
        interactionComponent.className = "HLAinteractionRoot.FederateMessage";
        FederateMessageComponent federateMessageComponent = engine.createComponent(FederateMessageComponent.class);
        federateMessageComponent.sender = instanceName;
        federateMessageComponent.receiver = "Spaceport";
        federateMessageComponent.content = "Departing to available holding position";
        federateMessageComponent.type = "BRUNEL_LANDER_SPACEPORT_DEPARTURE_COMPLETED";

        interaction.add(interactionComponent);
        interaction.add(federateMessageComponent);

        HLAInteractionManager.sendInteraction(interaction);
    }
}
