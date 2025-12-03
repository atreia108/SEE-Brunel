package uk.ac.brunel.archetypes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.github.atreia108.vega.core.IEntityArchetype;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.*;

public class PhysicalEntity implements IEntityArchetype {
    private final Engine engine = VegaUtilities.engine();

    @Override
    public Entity createEntity() {
        Entity physicalEntity = engine.createEntity();
        PositionComponent positionComponent = engine.createComponent(PositionComponent.class);
        QuaternionComponent quaternionComponent = engine.createComponent(QuaternionComponent.class);
        MovementComponent movementComponent = engine.createComponent(MovementComponent.class);
        ReferenceFrameComponent referenceFrameComponent = engine.createComponent(ReferenceFrameComponent.class);
        PhysicalEntityComponent physicalEntityComponent = engine.createComponent(PhysicalEntityComponent.class);

        physicalEntity.add(positionComponent);
        physicalEntity.add(quaternionComponent);
        physicalEntity.add(movementComponent);
        physicalEntity.add(referenceFrameComponent);
        physicalEntity.add(physicalEntityComponent);

        return physicalEntity;
    }
}
