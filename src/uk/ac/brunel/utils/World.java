package uk.ac.brunel.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.sun.jna.platform.win32.WinDef;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAObjectManager;
import io.github.atreia108.vega.core.IEntityArchetype;
import uk.ac.brunel.archetypes.PhysicalEntity;
import uk.ac.brunel.components.*;

public class World {
    private final Engine engine;

    public static final Vector3 POINT_CHARLIE =  new Vector3(-500.0f, -700.0f, -4900.0f);
    public static final Vector3 POINT_FOXTROT = new Vector3(0.0f, 0.0f, 0.0f);
    public static final Vector3 POINT_ROMEO = new Vector3(0.0f, 0.0f, 0.0f);

    public static final Vector3 LPAD_1 = new Vector3(100.2f, 430.0f, -5587.0f);
    public static final Vector3 LPAD_2 = new Vector3(100.0f, 400.0f, -5587.0f);

    private final IEntityArchetype physicalEntityArchetype;
    private int[] availableHoldingPositions;

    public World(Engine engine) {
        physicalEntityArchetype = new PhysicalEntity();
        this.engine = engine;
        availableHoldingPositions = new int[] {0, 0, 0};
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

        /*
        // Conveniently place it for circling around the spawn point.
        float centerX = spawnPoint.x;
        float centerY = spawnPoint.y;
        float radius = 50.0f;
        float theta = (float) Math.toRadians(45);
        float startX = centerX + (radius * (float) Math.cos(theta));
        float startY = centerY - (radius * (float) Math.sin(theta));

        PositionComponent positionComponent = ComponentMappers.position.get(landerEntity);
        positionComponent.pos.x = startX;
        positionComponent.pos.y = startY;
        positionComponent.pos.z = spawnPoint.z;

        final float LANDER_START_ANGULAR_VEL = 0.1f;
        MovementComponent movementComponent = ComponentMappers.movement.get(landerEntity);
        movementComponent.angularVel.x = LANDER_START_ANGULAR_VEL;
        movementComponent.angularVel.y = LANDER_START_ANGULAR_VEL;

        HoldingPatternComponent holdingPatternComponent = engine.createComponent(HoldingPatternComponent.class);
        landerEntity.add(holdingPatternComponent);
         */
    }

    public Vector3 assignHoldingPosition() {
        for (int i = 0; i < availableHoldingPositions.length; i++) {
            if (availableHoldingPositions[i] == 0) {
                availableHoldingPositions[i] = 1;
                return holdingPosKeyToValue(i);
            }
        }

        return null;
    }

    public Vector3 holdingPosKeyToValue(int i) {
        return switch (i) {
            case 0 -> POINT_CHARLIE;
            case 1 -> POINT_FOXTROT;
            case 2 -> POINT_ROMEO;
            default -> null;
        };
    }

    /*
    public Entity createLander(String name, String parentReferenceFrameName, String status, float x, float y, float z) {
        Entity lander = physicalEntityArchetype.createEntity();
        setPhysicalEntityMetadata(lander, name, parentReferenceFrameName, "Lander", status);
        placePhysicalEntity(lander, x, y, z);

        HLAObjectComponent objectComponent = engine.createComponent(HLAObjectComponent.class);
        objectComponent.className = "HLAobjectRoot.PhysicalEntity";
        objectComponent.instanceName = name;
        lander.add(objectComponent);

        HLAObjectManager.registerInstance(lander);
        engine.addEntity(lander);

        return lander;
    }

    private void setPhysicalEntityMetadata(Entity entity, String entityName, String parentReferenceFrameName, String type, String status) {

    }

    private void placePhysicalEntity(Entity physicalEntity, float x, float y, float z) {
        PositionComponent positionComponent = ComponentMappers.position.get(physicalEntity);
        positionComponent.pos.x = x;
        positionComponent.pos.y = y;
        positionComponent.pos.z = z;
    }

     */
}
