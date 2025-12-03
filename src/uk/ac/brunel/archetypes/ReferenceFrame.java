package uk.ac.brunel.archetypes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.github.atreia108.vega.core.IEntityArchetype;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.*;

public class ReferenceFrame implements IEntityArchetype {
    private final Engine engine = VegaUtilities.engine();

    @Override
    public Entity createEntity() {
        Entity referenceFrame = engine.createEntity();
        ReferenceFrameComponent referenceFrameComponent = engine.createComponent(ReferenceFrameComponent.class);
        PhysicalEntityComponent physicalEntityComponent = engine.createComponent(PhysicalEntityComponent.class);
        PositionComponent positionComponent = engine.createComponent(PositionComponent.class);
        MovementComponent movementComponent = engine.createComponent(MovementComponent.class);
        QuaternionComponent quaternionComponent = engine.createComponent(QuaternionComponent.class);

        referenceFrame.add(referenceFrameComponent);
        referenceFrame.add(physicalEntityComponent);
        referenceFrame.add(positionComponent);
        referenceFrame.add(movementComponent);
        referenceFrame.add(quaternionComponent);

        return referenceFrame;
    }
}
