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

import hla.rti1516_2025.encoding.*;
import org.see.skf.core.Coder;
import org.see.skf.core.HLAUtilityFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Encoding/decoding mechanism for the MaterialMap data type defined in the MSG FOM.
 *
 * @author Hridyanshu Aatreya
 */
public class MaterialMapCoder implements Coder<Map<String, Integer>> {
    private final EncoderFactory encoderFactory;
    private final DataElementFactory<HLAinteger32BE> integerFactory;
    private final DataElementFactory<HLAunicodeString> stringFactory;

    public MaterialMapCoder() {
        encoderFactory = HLAUtilityFactory.INSTANCE.getEncoderFactory();
        integerFactory = i -> encoderFactory.createHLAinteger32BE();
        stringFactory = j -> encoderFactory.createHLAunicodeString();
    }

    @Override
    public Map<String, Integer> decode(byte[] buffer) throws DecoderException {
        final HLAfixedRecord encodedMaterialMap = encoderFactory.createHLAfixedRecord();
        HLAvariableArray<HLAunicodeString> encodedMaterialNames = encoderFactory.createHLAvariableArray(stringFactory);
        HLAvariableArray<HLAinteger32BE> encodedMaterialQuantities = encoderFactory.createHLAvariableArray(integerFactory);
        encodedMaterialMap.add(encodedMaterialNames);
        encodedMaterialMap.add(encodedMaterialQuantities);

        encodedMaterialMap.decode(buffer);
        // In attempting the following, we trust that the contract to keep the size of both the material name (keys) and
        // material quantities (values) the same (as explicitly stated in the MSG FOM) was honored by other federates:
        Map<String, Integer> decodedMaterialMap = new HashMap<>() {
        };
        for (int k = 0; k < encodedMaterialNames.size(); ++k) {
            String decodedMaterialName = encodedMaterialNames.get(k).getValue();
            int decodedMaterialQty = encodedMaterialQuantities.get(k).getValue();

            decodedMaterialMap.put(decodedMaterialName, decodedMaterialQty);
        }

        return decodedMaterialMap;
    }

    @Override
    public byte[] encode(Map<String, Integer> materialMap) {
        final HLAfixedRecord encodedMaterialMap = encoderFactory.createHLAfixedRecord();

        final HLAvariableArray<HLAunicodeString> encodedMaterialNames = encoderFactory.createHLAvariableArray(stringFactory);
        final HLAvariableArray<HLAinteger32BE> encodedMaterialQuantities = encoderFactory.createHLAvariableArray(integerFactory);

        for (Map.Entry<String, Integer> entry : materialMap.entrySet()) {
            HLAunicodeString encodedMaterialName = encoderFactory.createHLAunicodeString();
            HLAinteger32BE encodedMaterialQty = encoderFactory.createHLAinteger32BE();

            encodedMaterialName.setValue(entry.getKey());
            encodedMaterialQty.setValue(entry.getValue());
            encodedMaterialNames.addElement(encodedMaterialName);
            encodedMaterialQuantities.addElement(encodedMaterialQty);
        }

        encodedMaterialMap.add(encodedMaterialNames);
        encodedMaterialMap.add(encodedMaterialQuantities);

        return encodedMaterialMap.toByteArray();
    }

    // Due to incompatibility with setting the return class type to the generic type assigned, the following returns
    // null to circumvent the issue altogether.
    @Override
    public Class<Map<String, Integer>> getAllowedType() {
        return null;
    }
}
