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
import org.ejml.simple.SimpleMatrix;
import org.see.skf.core.Coder;
import org.see.skf.core.HLAUtilityFactory;

public class SimpleMatrixCoder implements Coder<SimpleMatrix> {
    private final HLAfixedArray<HLAfloat64LE> coder;

    public SimpleMatrixCoder() {
        EncoderFactory encoderFactory = HLAUtilityFactory.INSTANCE.getEncoderFactory();
        coder = encoderFactory.createHLAfixedArray(
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE(),
                encoderFactory.createHLAfloat64LE()
        );
    }

    @Override
    public SimpleMatrix decode(byte[] bytes) throws DecoderException {
        SimpleMatrix matrix = new SimpleMatrix(3, 3);
        matrix.set(0, coder.get(0).getValue());
        matrix.set(1, coder.get(1).getValue());
        matrix.set(2, coder.get(2).getValue());
        matrix.set(3, coder.get(3).getValue());
        matrix.set(4, coder.get(4).getValue());
        matrix.set(5, coder.get(5).getValue());
        matrix.set(6, coder.get(6).getValue());
        matrix.set(7, coder.get(7).getValue());
        matrix.set(8, coder.get(8).getValue());

        return matrix;
    }

    @Override
    public byte[] encode(SimpleMatrix matrix) {
        coder.get(0).setValue(matrix.get(0));
        coder.get(1).setValue(matrix.get(1));
        coder.get(2).setValue(matrix.get(2));
        coder.get(3).setValue(matrix.get(3));
        coder.get(4).setValue(matrix.get(4));
        coder.get(5).setValue(matrix.get(5));
        coder.get(6).setValue(matrix.get(6));
        coder.get(7).setValue(matrix.get(7));
        coder.get(8).setValue(matrix.get(8));

        return coder.toByteArray();
    }

    @Override
    public Class<SimpleMatrix> getAllowedType() {
        return SimpleMatrix.class;
    }
}
