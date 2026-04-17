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

    private final HLAfixedRecord encodedMaterialMap;
    private final HLAunicodeString encodedMaterialName;
    private final HLAinteger32BE encodedMaterialQty;

    public MaterialMapCoder() {
        encoderFactory = HLAUtilityFactory.INSTANCE.getEncoderFactory();
        integerFactory = i -> encoderFactory.createHLAinteger32BE();
        stringFactory = j -> encoderFactory.createHLAunicodeString();

        encodedMaterialMap = encoderFactory.createHLAfixedRecord();
        encodedMaterialName = encoderFactory.createHLAunicodeString();
        encodedMaterialQty = encoderFactory.createHLAinteger32BE();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Integer> decode(byte[] buffer) throws DecoderException {
        encodedMaterialMap.decode(buffer);
        encodedMaterialMap.get(0);

        HLAvariableArray<HLAunicodeString> encodedMaterialNames = (HLAvariableArray<HLAunicodeString>) encodedMaterialMap.get(0);
        HLAvariableArray<HLAinteger32BE> encodedMaterialQuantities = (HLAvariableArray<HLAinteger32BE>) encodedMaterialMap.get(1);

        // We trust that the contract to keep the size of both the material name (keys) and material quantities (values)
        // the same (as explicitly stated in the MSG FOM) was honored by other federates when attempting the following:
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
        HLAvariableArray<HLAunicodeString> encodedMaterialNames = encoderFactory.createHLAvariableArray(stringFactory);
        HLAvariableArray<HLAinteger32BE> encodedMaterialQuantities = encoderFactory.createHLAvariableArray(integerFactory);

        for (Map.Entry<String, Integer> entry : materialMap.entrySet()) {
            encodedMaterialName.setValue(entry.getKey());
            encodedMaterialQty.setValue(entry.getValue());
            encodedMaterialNames.addElement(encodedMaterialName);
            encodedMaterialQuantities.addElement(encodedMaterialQty);
        }

        return encodedMaterialMap.toByteArray();
    }

    // Due to incompatibility with setting the return class type to the generic type assigned, the following returns
    // null to circumvent the issue altogether.
    @Override
    public Class<Map<String, Integer>> getAllowedType() {
        return null;
    }
}
