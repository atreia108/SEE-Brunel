/*****************************************************************
 SEE Baseplate - A starter project template for the SEE HLA
 Starter Kit Framework.
 Copyright (c) 2026, Hridyanshu Aatreya - Modelling & Simulation
 Group (MSG) at Brunel University of London. All rights reserved.

 GNU Lesser General Public License (GNU LGPL).

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3.0 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library.
 If not, see http://http://www.gnu.org/licenses/
 *****************************************************************/

package org.see.baseplate.encoding;

import hla.rti1516_2025.encoding.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.quaternion.Quaternion;
import org.see.skf.core.Coder;
import org.see.skf.core.HLAUtilityFactory;
import org.see.baseplate.types.SpaceTimeCoordinateState;

public class SpaceTimeCoordinateStateCoder implements Coder<SpaceTimeCoordinateState> {
    private final HLAfixedRecord coder;

    // Translation state component.
    private final HLAfixedRecord translationalState;
    private final HLAfixedArray<HLAfloat64LE> position;
    private final HLAfixedArray<HLAfloat64LE> velocity;

    // Rotational state component.
    private final HLAfixedRecord rotationalState;
    private final HLAfixedArray<HLAfloat64LE> angularVelocity;
    private final HLAfixedRecord attitudeQuaternion;
    private final HLAfloat64LE scalar;
    private final HLAfixedArray<HLAfloat64LE> vector;

    // Simulated time component.
    private final HLAfloat64LE time;

    public SpaceTimeCoordinateStateCoder() {
        EncoderFactory encoderFactory = HLAUtilityFactory.INSTANCE.getEncoderFactory();
        coder = encoderFactory.createHLAfixedRecord();

        translationalState = encoderFactory.createHLAfixedRecord();
        position = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());
        velocity = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());
        translationalState.add(position);
        translationalState.add(velocity);

        rotationalState = encoderFactory.createHLAfixedRecord();
        angularVelocity = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());
        attitudeQuaternion = encoderFactory.createHLAfixedRecord();
        scalar = encoderFactory.createHLAfloat64LE();
        vector = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());
        attitudeQuaternion.add(scalar);
        attitudeQuaternion.add(vector);
        rotationalState.add(attitudeQuaternion);
        rotationalState.add(angularVelocity);

        time = encoderFactory.createHLAfloat64LE();

        coder.add(translationalState);
        coder.add(rotationalState);
        coder.add(time);
    }

    @Override
    public SpaceTimeCoordinateState decode(byte[] bytes) throws DecoderException {
        coder.decode(bytes);

        Vector3D decodedPosition = Vector3D.of(position.get(0).getValue(), position.get(1).getValue(), position.get(2).getValue());
        Vector3D decodedVelocity = Vector3D.of(velocity.get(0).getValue(), velocity.get(1).getValue(), velocity.get(2).getValue());

        double decodedScalar = scalar.getValue();
        Vector3D decodedVector = Vector3D.of(vector.get(0).getValue(), vector.get(1).getValue(), vector.get(2).getValue());
        Vector3D decodedAngularVelocity = Vector3D.of(angularVelocity.get(0).getValue(), angularVelocity.get(1).getValue(), angularVelocity.get(2).getValue());
        Quaternion decodedAttitudeQuaternion = Quaternion.of(decodedScalar, decodedVector.getX(), decodedVector.getY(), decodedVector.getZ());

        double decodedTime = time.getValue();

        SpaceTimeCoordinateState state = new SpaceTimeCoordinateState();
        state.setPosition(decodedPosition);
        state.setVelocity(decodedVelocity);
        state.setAttitudeQuaternion(decodedAttitudeQuaternion);
        state.setAngularVelocity(decodedAngularVelocity);
        state.setTime(decodedTime);

        return state;
    }

    @Override
    public byte[] encode(SpaceTimeCoordinateState state) {
        position.get(0).setValue(state.getPosition().getX());
        position.get(1).setValue(state.getPosition().getY());
        position.get(2).setValue(state.getPosition().getZ());

        velocity.get(0).setValue(state.getVelocity().getX());
        velocity.get(1).setValue(state.getVelocity().getY());
        velocity.get(2).setValue(state.getVelocity().getZ());

        scalar.setValue(state.getAttitudeQuaternion().getW());
        vector.get(0).setValue(state.getAttitudeQuaternion().getX());
        vector.get(1).setValue(state.getAttitudeQuaternion().getY());
        vector.get(2).setValue(state.getAttitudeQuaternion().getZ());

        angularVelocity.get(0).setValue(state.getAngularVelocity().getX());
        angularVelocity.get(1).setValue(state.getAngularVelocity().getY());
        angularVelocity.get(2).setValue(state.getAngularVelocity().getZ());

        time.setValue(state.getTime());

        return coder.toByteArray();
    }

    @Override
    public Class<SpaceTimeCoordinateState> getAllowedType() {
        return SpaceTimeCoordinateState.class;
    }
}
