/*-
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Brunel University of London
 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * 	  contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
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

package uk.ac.brunel.encoding;

import hla.rti1516_2025.encoding.DecoderException;
import hla.rti1516_2025.encoding.EncoderFactory;
import hla.rti1516_2025.encoding.HLAinteger16BE;
import org.see.skf.core.Coder;
import org.see.skf.core.HLAUtilityFactory;
import uk.ac.brunel.types.OperationalVerdict;

/**
 * Encoding/decoding mechanism for the OperationalVerdict data type defined in the MSG FOM.
 *
 * @author Hridyanshu Aatreya
 */
public class OperationalVerdictCoder implements Coder<OperationalVerdict> {
    private final HLAinteger16BE encodedVerdict;

    public OperationalVerdictCoder() {
        EncoderFactory encoderFactory = HLAUtilityFactory.INSTANCE.getEncoderFactory();
        encodedVerdict = encoderFactory.createHLAinteger16BE();
    }

    @Override
    public OperationalVerdict decode(byte[] buffer) throws DecoderException {
        encodedVerdict.decode(buffer);
        return OperationalVerdict.query(encodedVerdict.getValue());
    }

    @Override
    public byte[] encode(OperationalVerdict verdict) {
        encodedVerdict.setValue(verdict.getValue());
        return encodedVerdict.toByteArray();
    }

    @Override
    public Class<OperationalVerdict> getAllowedType() {
        return OperationalVerdict.class;
    }
}
