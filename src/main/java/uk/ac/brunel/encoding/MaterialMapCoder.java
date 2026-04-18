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
