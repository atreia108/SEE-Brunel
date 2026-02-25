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
import org.apache.commons.numbers.quaternion.Quaternion;
import org.see.skf.core.Coder;
import org.see.skf.core.HLAUtilityFactory;

public class QuaternionCoder implements Coder<Quaternion> {
    private final HLAfixedRecord coder;

    private final HLAfloat64LE scalar;
    private final HLAfixedArray<HLAfloat64LE> vector;

    public QuaternionCoder() {
        EncoderFactory encoderFactory = HLAUtilityFactory.INSTANCE.getEncoderFactory();
        coder = encoderFactory.createHLAfixedRecord();

        scalar = encoderFactory.createHLAfloat64LE();
        vector = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());
        coder.add(scalar);
        coder.add(vector);
    }

    @Override
    public Quaternion decode(byte[] bytes) throws DecoderException {
        coder.decode(bytes);
        return Quaternion.of(scalar.getValue(), vector.get(0).getValue(), vector.get(1).getValue(), vector.get(2).getValue());
    }

    @Override
    public byte[] encode(Quaternion quaternion) {
        scalar.setValue(quaternion.getW());
        vector.get(0).setValue(quaternion.getX());
        vector.get(1).setValue(quaternion.getY());
        vector.get(2).setValue(quaternion.getZ());

        return coder.toByteArray();
    }

    @Override
    public Class<Quaternion> getAllowedType() {
        return Quaternion.class;
    }
}
