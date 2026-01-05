/*-
 * Copyright (c) 2025 Hridyanshu Aatreya
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package org.see.brunel.encoding;

import hla.rti1516_2025.encoding.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.quaternion.Quaternion;
import org.see.skf.core.Coder;
import org.see.skf.core.HLAUtilityFactory;
import org.see.brunel.types.SpaceTimeCoordinateState;

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
