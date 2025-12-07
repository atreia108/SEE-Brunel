package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAObjectManager;
import uk.ac.brunel.components.MovementComponent;
import uk.ac.brunel.components.NavigationComponent;
import uk.ac.brunel.components.PositionComponent;
import uk.ac.brunel.utils.ComponentMappers;

public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Family.all(PositionComponent.class, MovementComponent.class, NavigationComponent.class, HLAObjectComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = ComponentMappers.position.get(entity);
        NavigationComponent navigationComponent = ComponentMappers.navigation.get(entity);
        MovementComponent movementComponent = ComponentMappers.movement.get(entity);

        // Arrival
        if (navigationComponent.order == 0) {
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

                float MIN_Z_ALTITUDE = -5387.0f;
                if (positionComponent.pos.z > MIN_Z_ALTITUDE) {
                    positionComponent.pos.z -= movementComponent.vel.z;
                }
            }
        }

        if (horizontalConditionMet(positionComponent, navigationComponent) && !verticalConditionMet(positionComponent, navigationComponent)) {

            if (navigationComponent.waypoint.z > positionComponent.pos.z) {
                positionComponent.pos.z += movementComponent.vel.z;
            } else if (navigationComponent.waypoint.z < positionComponent.pos.z) {
                positionComponent.pos.z -= movementComponent.vel.z;
            }
        } else {
            // Departure
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
}
