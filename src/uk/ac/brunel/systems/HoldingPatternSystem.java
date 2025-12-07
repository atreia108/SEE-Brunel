package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.atreia108.vega.components.HLAObjectComponent;
import uk.ac.brunel.components.HoldingPatternComponent;
import uk.ac.brunel.components.MovementComponent;
import uk.ac.brunel.components.PositionComponent;
import uk.ac.brunel.utils.ComponentMappers;

import java.util.Random;

public class HoldingPatternSystem extends IteratingSystem {
    // private final float RADIUS = 50.0f;
    // private final float THETA = (float) Math.toRadians(45);

    private Random rand;

    public HoldingPatternSystem() {
        super(Family.all(HoldingPatternComponent.class, PositionComponent.class, MovementComponent.class, HLAObjectComponent.class).get());
        rand = new Random();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        int rng = rand.nextInt(0, 6) + rand.nextInt(0, 6) + rand.nextInt(0, 2);

        if (rng > 10) {
            entity.remove(HoldingPatternComponent.class);
        }

        /*
        PositionComponent positionComponent = ComponentMappers.position.get(entity);
        MovementComponent movementComponent = ComponentMappers.movement.get(entity);

        float angularVelX = movementComponent.angularVel.x;
        float angularVelY = movementComponent.angularVel.y;

        float deltaX = positionComponent.pos.x + (RADIUS * angularVelX * (float) (Math.cos(THETA) + (Math.PI / 2)));
        float deltaY = positionComponent.pos.y - (RADIUS * angularVelY * (float) (Math.sin(THETA) + (Math.PI / 2)));

        positionComponent.pos.x += deltaX;
        positionComponent.pos.y += deltaY;
         */
    }
}
