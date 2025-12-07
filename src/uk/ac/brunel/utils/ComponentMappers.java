package uk.ac.brunel.utils;

import com.badlogic.ashley.core.ComponentMapper;
import uk.ac.brunel.components.*;

public class ComponentMappers {
    public static ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static ComponentMapper<ReferenceFrameComponent> referenceFrame = ComponentMapper.getFor(ReferenceFrameComponent.class);
    public static ComponentMapper<MovementComponent> movement = ComponentMapper.getFor(MovementComponent.class);
    public static ComponentMapper<PhysicalEntityComponent> physicalEntity = ComponentMapper.getFor(PhysicalEntityComponent.class);
    public static ComponentMapper<QuaternionComponent> quaternion = ComponentMapper.getFor(QuaternionComponent.class);
    public static ComponentMapper<FederateMessageComponent> federateMessage = ComponentMapper.getFor(FederateMessageComponent.class);
    public static ComponentMapper<NavigationComponent> navigation = ComponentMapper.getFor(NavigationComponent.class);
    public static ComponentMapper<HoldingPatternComponent> holdingPattern = ComponentMapper.getFor(HoldingPatternComponent.class);
}
