package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;
import uk.ac.brunel.encoding.OperationalVerdictCoder;
import uk.ac.brunel.types.OperationalVerdict;

/**
 * Implementation of the MSGLogisticsDeliveryResponse interaction class from the MSG FOM.
 * Purpose: Sent by the logistics system in response to a logistics delivery request previously issued by a spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLogisticsInteractionRoot.MSGLogisticsDeliveryResponse")
public class MSGLogisticsDeliveryResponse {
    @Parameter(name = "Spaceport", coder = HLAunicodeStringCoder.class)
    private String spaceport;

    @Parameter(name = "Verdict", coder = OperationalVerdictCoder.class)
    private OperationalVerdict verdict;

    public MSGLogisticsDeliveryResponse() {
        spaceport = "";
        verdict = OperationalVerdict.REJECTED;
    }

    public MSGLogisticsDeliveryResponse(String spaceport, OperationalVerdict verdict) {
        this.spaceport = spaceport;
        this.verdict = verdict;
    }

    public String getSpaceport() {
        return spaceport;
    }

    public void setSpaceport(String spaceport) {
        this.spaceport = spaceport;
    }

    public OperationalVerdict getVerdict() {
        return verdict;
    }

    public void setVerdict(OperationalVerdict verdict) {
        this.verdict = verdict;
    }
}
