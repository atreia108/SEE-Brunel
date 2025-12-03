package uk.ac.brunel.converters;

import com.badlogic.ashley.core.Entity;
import hla.rti1516e.encoding.*;
import io.github.atreia108.vega.core.IDataConverter;

import uk.ac.brunel.components.*;
import uk.ac.brunel.utils.ComponentMappers;

public class SpaceTimeCoordinateStateConverter implements IDataConverter {
    private HLAfixedRecord state;

    private HLAfixedRecord translationalState;
    private HLAfixedArray<HLAfloat64LE> position;
    private HLAfixedArray<HLAfloat64LE> velocity;

    private HLAfixedRecord rotationalState;
    private HLAfixedRecord attitudeQuaternion;
    private HLAfloat64LE scalar;
    private HLAfixedArray<HLAfloat64LE> vector;
    private HLAfixedArray<HLAfloat64LE> angularVelocity;

    private HLAfloat64LE time;

    @Override
    public void decode(Entity entity, EncoderFactory encoderFactory, byte[] buffer) throws DecoderException {
        state = encoderFactory.createHLAfixedRecord();

        PositionComponent positionComponent = ComponentMappers.position.get(entity);
        MovementComponent movementComponent = ComponentMappers.movement.get(entity);
        QuaternionComponent quaternionComponent = ComponentMappers.quaternion.get(entity);
        ReferenceFrameComponent referenceFrameComponent = ComponentMappers.frame.get(entity);

        translationalState = encoderFactory.createHLAfixedRecord();
        position = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());
        velocity = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());

        rotationalState = encoderFactory.createHLAfixedRecord();
        attitudeQuaternion = encoderFactory.createHLAfixedRecord();
        scalar = encoderFactory.createHLAfloat64LE();
        vector = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());
        angularVelocity = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());

        time = encoderFactory.createHLAfloat64LE();

        translationalState.add(position);
        translationalState.add(velocity);

        attitudeQuaternion.add(scalar);
        attitudeQuaternion.add(vector);
        rotationalState.add(attitudeQuaternion);
        rotationalState.add(angularVelocity);

        state.add(translationalState);
        state.add(rotationalState);
        state.add(time);

        state.decode(buffer);

        positionComponent.pos.x = (float) position.get(0).getValue();
        positionComponent.pos.y = (float) position.get(1).getValue();
        positionComponent.pos.z = (float) position.get(2).getValue();

        movementComponent.vel.x = (float) velocity.get(0).getValue();
        movementComponent.vel.y = (float) velocity.get(1).getValue();
        movementComponent.vel.z = (float) velocity.get(2).getValue();

        quaternionComponent.scalar = scalar.getValue();
        quaternionComponent.vector.x = (float) vector.get(0).getValue();
        quaternionComponent.vector.y = (float) vector.get(1).getValue();
        quaternionComponent.vector.z = (float) vector.get(2).getValue();

        referenceFrameComponent.simulatedPhysicalTime = time.getValue();
    }

    @Override
    public byte[] encode(Entity entity, EncoderFactory encoderFactory) {
        state = encoderFactory.createHLAfixedRecord();

        PositionComponent positionComponent = ComponentMappers.position.get(entity);
        MovementComponent movementComponent = ComponentMappers.movement.get(entity);
        QuaternionComponent quaternionComponent = ComponentMappers.quaternion.get(entity);
        ReferenceFrameComponent referenceFrameComponent = ComponentMappers.frame.get(entity);

        translationalState = encoderFactory.createHLAfixedRecord();

        position = encoderFactory.createHLAfixedArray(
                encoderFactory.createHLAfloat64LE(positionComponent.pos.x),
                encoderFactory.createHLAfloat64LE(positionComponent.pos.y),
                encoderFactory.createHLAfloat64LE(positionComponent.pos.z));

        // A plausible case is if an entity is stationary and so it won't have a MovementComponent.
        // We're using a fallback values for when this occurs, thus mitigating any nasty Null Pointer exceptions.
        if (movementComponent != null)
        {
            velocity = encoderFactory.createHLAfixedArray(
                    encoderFactory.createHLAfloat64LE(movementComponent.vel.x),
                    encoderFactory.createHLAfloat64LE(movementComponent.vel.y),
                    encoderFactory.createHLAfloat64LE(movementComponent.vel.z));

            angularVelocity = encoderFactory.createHLAfixedArray(
                    encoderFactory.createHLAfloat64LE(movementComponent.angularVel.x),
                    encoderFactory.createHLAfloat64LE(movementComponent.angularVel.y),
                    encoderFactory.createHLAfloat64LE(movementComponent.angularVel.z));
        }
        else
        {
            velocity = encoderFactory.createHLAfixedArray(
                    encoderFactory.createHLAfloat64LE(0.0),
                    encoderFactory.createHLAfloat64LE(0.0),
                    encoderFactory.createHLAfloat64LE(0.0));

            angularVelocity = encoderFactory.createHLAfixedArray(
                    encoderFactory.createHLAfloat64LE(0.0),
                    encoderFactory.createHLAfloat64LE(0.0),
                    encoderFactory.createHLAfloat64LE(0.0));
        }

        rotationalState = encoderFactory.createHLAfixedRecord();
        attitudeQuaternion = encoderFactory.createHLAfixedRecord();
        scalar = encoderFactory.createHLAfloat64LE(quaternionComponent.scalar);
        vector = encoderFactory.createHLAfixedArray(
                encoderFactory.createHLAfloat64LE(quaternionComponent.vector.x),
                encoderFactory.createHLAfloat64LE(quaternionComponent.vector.y),
                encoderFactory.createHLAfloat64LE(quaternionComponent.vector.z));

        time = encoderFactory.createHLAfloat64LE(referenceFrameComponent.simulatedPhysicalTime);

        translationalState.add(position);
        translationalState.add(velocity);

        attitudeQuaternion.add(scalar);
        attitudeQuaternion.add(vector);
        rotationalState.add(attitudeQuaternion);
        rotationalState.add(angularVelocity);

        state.add(translationalState);
        state.add(rotationalState);
        state.add(time);

        return state.toByteArray();
    }
}
