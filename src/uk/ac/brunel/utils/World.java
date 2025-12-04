package uk.ac.brunel.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAObjectManager;
import io.github.atreia108.vega.core.IEntityArchetype;
import uk.ac.brunel.archetypes.PhysicalEntity;
import uk.ac.brunel.components.PhysicalEntityComponent;
import uk.ac.brunel.components.PositionComponent;
import uk.ac.brunel.components.ReferenceFrameComponent;

public class World {
    public static final double X = -817582.939286128;
    public static final double Y = -296194.936333636;
    public static final double Z = -1504977.52696795;

    public static final Vector3D POINT_CHARLIE =  new Vector3D(0.0, 0.0, 0.0);
    public static final Vector3D POINT_FOXTROT = new Vector3D(0.0, 0.0, 0.0);
    public static final Vector3D POINT_ROMEO = new Vector3D(0.0, 0.0, 0.0);

    public static final Vector3D LPAD_1 = new Vector3D(0.0, 0.0, 0.0);
    public static final Vector3D LPAD_2 = new Vector3D(0.0, 0.0, 0.0);

    private final PooledEngine engine;
    private final IEntityArchetype physicalEntityArchetype;

    public World(PooledEngine engine) {
        physicalEntityArchetype = new PhysicalEntity();
        this.engine = engine;
    }

    public Entity createLander(String name, String status, String parentReferenceFrame, double x, double y, double z) {
        Entity lander = physicalEntityArchetype.createEntity();
        setPhysicalEntityMetadata(lander, name, "Lander", status, parentReferenceFrame);
        placePhysicalEntity(lander, x, y, z);

        HLAObjectComponent objectComponent = engine.createComponent(HLAObjectComponent.class);
        objectComponent.className = "HLAobjectRoot.PhysicalEntity";
        objectComponent.instanceName = name;
        lander.add(objectComponent);

        HLAObjectManager.registerInstance(lander);
        engine.addEntity(lander);

        return lander;
    }

    private void setPhysicalEntityMetadata(Entity entity, String entityName, String type, String status, String parentReferenceFrame) {
        PhysicalEntityComponent physicalEntityComponent = ComponentMappers.physicalEntity.get(entity);
        physicalEntityComponent.name = entityName;
        physicalEntityComponent.type = type;
        physicalEntityComponent.status = status;

        ReferenceFrameComponent referenceFrameComponent = ComponentMappers.frame.get(entity);
        referenceFrameComponent.name = parentReferenceFrame;
    }

    private void placePhysicalEntity(Entity physicalEntity, double x, double y, double z) {
        PositionComponent positionComponent = ComponentMappers.position.get(physicalEntity);
        positionComponent.pos.x = x;
        positionComponent.pos.y = y;
        positionComponent.pos.z = z;
    }

    public static Vector3D toDONCoordinates(Vector3D coordinates) {
        return new Vector3D();
    }
}
