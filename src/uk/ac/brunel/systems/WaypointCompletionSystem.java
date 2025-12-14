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
import uk.ac.brunel.components.*;
import uk.ac.brunel.lander.NavigationDirection;
import uk.ac.brunel.utils.ComponentMappers;

public class WaypointCompletionSystem extends IteratingSystem {

    public WaypointCompletionSystem() {
        super(Family.all(PositionComponent.class, NavigationComponent.class, MovementComponent.class, HLAObjectComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        PositionComponent positionComponent = ComponentMappers.position.get(entity);
        NavigationComponent navigationComponent = ComponentMappers.navigation.get(entity);
        MovementComponent movementComponent = ComponentMappers.movement.get(entity);
        HLAObjectComponent objectComponent = VegaUtilities.objectComponentMapper().get(entity);

        if (horizontalConditionMet(positionComponent, navigationComponent) && verticalConditionMet(positionComponent, navigationComponent)) {
            // Coordinate correction because we won't have perfect alignment with the current algorithm.
            positionComponent.pos.x = navigationComponent.waypoint.x;
            positionComponent.pos.y = navigationComponent.waypoint.y;
            positionComponent.pos.z = navigationComponent.waypoint.z;

            Engine engine = VegaUtilities.engine();

            if (navigationComponent.direction == NavigationDirection.ARRIVAL) {
                Entity interaction = engine.createEntity();
                HLAInteractionComponent interactionComponent = engine.createComponent(HLAInteractionComponent.class);
                interactionComponent.className = "HLAinteractionRoot.FederateMessage";

                FederateMessageComponent federateMessageComponent = engine.createComponent(FederateMessageComponent.class);
                federateMessageComponent.sender = objectComponent.instanceName;
                federateMessageComponent.receiver = "Spaceport";
                federateMessageComponent.type = "BRUNEL_LANDER_SPACEPORT_TOUCHDOWN";
                federateMessageComponent.content = "Landed at Spaceport.";

                interaction.add(interactionComponent);
                interaction.add(federateMessageComponent);

                HLAInteractionManager.sendInteraction(interaction);
            } else {
                HoldingPatternComponent holdingPatternComponent = engine.createComponent(HoldingPatternComponent.class);
                entity.add(holdingPatternComponent);
            }

            entity.remove(NavigationComponent.class);

            movementComponent.vel.x = 0;
            movementComponent.vel.y = 0;
            movementComponent.vel.z = 0;
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
