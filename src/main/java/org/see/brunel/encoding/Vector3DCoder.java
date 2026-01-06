/*-
 * Copyright (c) 2026 Hridyanshu Aatreya
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

import hla.rti1516_2025.encoding.DecoderException;
import hla.rti1516_2025.encoding.EncoderFactory;
import hla.rti1516_2025.encoding.HLAfixedArray;
import hla.rti1516_2025.encoding.HLAfloat64LE;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.core.Coder;
import org.see.skf.core.HLAUtilityFactory;

public class Vector3DCoder implements Coder<Vector3D> {
    private final HLAfixedArray<HLAfloat64LE> coder;

    public Vector3DCoder() {
        EncoderFactory encoderFactory = HLAUtilityFactory.INSTANCE.getEncoderFactory();
        coder = encoderFactory.createHLAfixedArray(
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE()
        );
    }

    @Override
    public Vector3D decode(byte[] bytes) throws DecoderException {
        coder.decode(bytes);
        return Vector3D.of(coder.get(0).getValue(), coder.get(1).getValue(), coder.get(2).getValue());
    }

    @Override
    public byte[] encode(Vector3D vector) {
        coder.get(0).setValue(vector.getX());
        coder.get(1).setValue(vector.getY());
        coder.get(2).setValue(vector.getZ());

        return coder.toByteArray();
    }

    @Override
    public Class<Vector3D> getAllowedType() {
        return Vector3D.class;
    }
}
