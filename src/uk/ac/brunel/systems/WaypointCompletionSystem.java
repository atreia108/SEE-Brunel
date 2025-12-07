package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAObjectManager;
import uk.ac.brunel.components.MovementComponent;
import uk.ac.brunel.components.NavigationComponent;
import uk.ac.brunel.components.PositionComponent;
import uk.ac.brunel.utils.ComponentMappers;

public class WaypointCompletionSystem extends IteratingSystem {

    public WaypointCompletionSystem() {
        super(Family.all(PositionComponent.class, NavigationComponent.class, MovementComponent.class, HLAObjectComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        PositionComponent positionComponent = ComponentMappers.position.get(entity);
        NavigationComponent navigationComponent = ComponentMappers.navigation.get(entity);

        if (horizontalConditionMet(positionComponent, navigationComponent) && verticalConditionMet(positionComponent, navigationComponent)) {
            // Coordinate correction because we won't have perfect alignment with the current algorithm.
            positionComponent.pos.x = navigationComponent.waypoint.x;
            positionComponent.pos.y = navigationComponent.waypoint.y;
            positionComponent.pos.z = navigationComponent.waypoint.z;

            entity.remove(NavigationComponent.class);
            System.out.println("Mission Complete!");
        }

        HLAObjectManager.sendInstanceUpdate(entity);
    }

    private boolean horizontalConditionMet(PositionComponent positionComponent, NavigationComponent navigationComponent) {
        boolean conditionX = Math.abs((navigationComponent.waypoint.x - positionComponent.pos.x)) < 20.0f;
        boolean conditionY = Math.abs((navigationComponent.waypoint.y - positionComponent.pos.y)) < 20.0f;

        return conditionX && conditionY;
    }

    private boolean verticalConditionMet(PositionComponent positionComponent, NavigationComponent navigationComponent) {
        return Math.abs((navigationComponent.waypoint.z - positionComponent.pos.z)) <= 10.0f;
    }
}
