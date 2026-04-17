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
