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
        vector = encoderFactory.createHLAfixedArray(encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE(),  encoderFactory.createHLAfloat64LE(), encoderFactory.createHLAfloat64LE());
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
