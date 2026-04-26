package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAfloat64BECoder;
import org.see.skf.util.encoding.HLAinteger32BECoder;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

@InteractionClass(name = "HLAinteractionRoot.PowerRequest")
public class UCFPowerRequest {
    @Parameter(name = "federateID", coder = HLAunicodeStringCoder.class)
    private String federateID;
    @Parameter(name = "requestedKw", coder = HLAfloat64BECoder.class)
    private Double requestedKw;
    @Parameter(name = "priority", coder = HLAinteger32BECoder.class)
    private Integer priority;
    public UCFPowerRequest() {
        federateID = "";
        requestedKw = 0.0;
        priority = 0;
    }
    public UCFPowerRequest(String federateID, Double requestedKw, Integer priority) {
        this.federateID = federateID;
        this.requestedKw = requestedKw;
        this.priority = priority;
    }

    public String getFederateID() {
        return federateID;
    }

    public void setFederateID(String federateID) {
        this.federateID = federateID;
    }
    public Double getRequestedKw() {
        return requestedKw;
    }

    public void setRequestedKw(Double requestedKw) {
        this.requestedKw = requestedKw;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
