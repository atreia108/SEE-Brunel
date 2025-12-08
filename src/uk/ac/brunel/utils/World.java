package uk.ac.brunel.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAObjectManager;
import io.github.atreia108.vega.core.IEntityArchetype;
import uk.ac.brunel.archetypes.PhysicalEntity;
import uk.ac.brunel.components.*;

public class World {
    private final Engine engine;

    public static final Vector3 POINT_CHARLIE =  new Vector3(-500.0f, -200.0f, -5200.0f);
    public static final Vector3 POINT_FOXTROT = new Vector3(600.0f, 500.0f, -4900.0f);
    public static final Vector3 POINT_ROMEO = new Vector3(400.0f, -100.0f, -5000.0f);

    public static final Vector3 LPAD_1 = new Vector3(100.2f, 430.0f, -5587.0f);
    public static final Vector3 LPAD_2 = new Vector3(100.0f, 400.0f, -5587.0f);

    private final IEntityArchetype physicalEntityArchetype;

    public World(Engine engine) {
        physicalEntityArchetype = new PhysicalEntity();
        this.engine = engine;
    }

    public Entity createLander(String landerName, String parentReferenceFrame, Vector3 spawnPoint) {
        Entity newLander = physicalEntityArchetype.createEntity();
        setLanderMetadata(newLander, landerName, parentReferenceFrame);
        spawnLander(newLander, spawnPoint);

        HLAObjectComponent objectComponent = engine.createComponent(HLAObjectComponent.class);
        objectComponent.className = "HLAobjectRoot.PhysicalEntity";
        objectComponent.instanceName = landerName;
        newLander.add(objectComponent);

        HLAObjectManager.registerInstance(newLander);
        engine.addEntity(newLander);

        return newLander;
    }

    private void setLanderMetadata(Entity landerEntity, String landerName, String parentReferenceFrame) {
        PhysicalEntityComponent physicalEntityComponent = ComponentMappers.physicalEntity.get(landerEntity);
        physicalEntityComponent.name = landerName;
        physicalEntityComponent.type = "Lander";
        physicalEntityComponent.status = "Holding";

        ReferenceFrameComponent referenceFrameComponent = ComponentMappers.referenceFrame.get(landerEntity);
        referenceFrameComponent.name = parentReferenceFrame;
    }

    private void spawnLander(Entity landerEntity, Vector3 spawnPoint) {
        PositionComponent positionComponent = ComponentMappers.position.get(landerEntity);
        positionComponent.pos = spawnPoint.cpy();

        HoldingPatternComponent holdingPatternComponent = engine.createComponent(HoldingPatternComponent.class);
        landerEntity.add(holdingPatternComponent);
    }
}
