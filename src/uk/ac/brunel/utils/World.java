package uk.ac.brunel.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAObjectManager;
import io.github.atreia108.vega.core.IEntityArchetype;
import uk.ac.brunel.archetypes.PhysicalEntity;
import uk.ac.brunel.components.PhysicalEntityComponent;
import uk.ac.brunel.components.PositionComponent;
import uk.ac.brunel.components.QuaternionComponent;
import uk.ac.brunel.components.ReferenceFrameComponent;

public class World {
    public static final Vector3 POINT_CHARLIE =  new Vector3(0.0f, 0.0f, 0.0f);
    public static final Vector3 POINT_FOXTROT = new Vector3(0.0f, 0.0f, 0.0f);
    public static final Vector3 POINT_ROMEO = new Vector3(0.0f, 0.0f, 0.0f);

    public static final Vector3 LPAD_1 = new Vector3(0.0f, 0.0f, 0.0f);
    public static final Vector3 LPAD_2 = new Vector3(0.0f, 0.0f, 0.0f);

    private final PooledEngine engine;
    private final IEntityArchetype physicalEntityArchetype;

    public World(PooledEngine engine) {
        physicalEntityArchetype = new PhysicalEntity();
        this.engine = engine;
    }

    public Entity createSpaceport(String name, String status, String parentReferenceFrame, float x, float y, float z) {
        Entity spaceport = physicalEntityArchetype.createEntity();
        setPhysicalEntityMetadata(spaceport, name, "Spaceport", status, parentReferenceFrame);
        placePhysicalEntity(spaceport, x, y, z);

        HLAObjectComponent objectComponent = engine.createComponent(HLAObjectComponent.class);
        objectComponent.className = "HLAobjectRoot.PhysicalEntity";
        objectComponent.instanceName = name;

        spaceport.add(objectComponent);

        HLAObjectManager.registerInstance(spaceport);
        engine.addEntity(spaceport);

        return spaceport;
    }

    public Entity createLander(String name, String status, String parentReferenceFrame, float x, float y, float z) {
        Entity lander = physicalEntityArchetype.createEntity();
        setPhysicalEntityMetadata(lander, name, "Lander", status, parentReferenceFrame);
        placePhysicalEntity(lander, x, y, z);

        HLAObjectComponent objectComponent = engine.createComponent(HLAObjectComponent.class);
        objectComponent.className = "HLAobjectRoot.PhysicalEntity";
        objectComponent.instanceName = name;
        lander.add(objectComponent);

        /*
        QuaternionComponent quaternionComponent = lander.getComponent(QuaternionComponent.class);
        quaternionComponent.scalar = -0.79079044533773;
        quaternionComponent.vector.x = 0.554597207736449f;
        quaternionComponent.vector.y = 0.148705030888344f;
        quaternionComponent.vector.z = -0.212035899134992f;
         */

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

    private void placePhysicalEntity(Entity physicalEntity, float x, float y, float z) {
        PositionComponent positionComponent = ComponentMappers.position.get(physicalEntity);
        positionComponent.pos.x = x;
        positionComponent.pos.y = y;
        positionComponent.pos.z = z;
    }
}
