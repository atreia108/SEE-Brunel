package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAfloat64BECoder;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

@InteractionClass(name = "HLAinteractionRoot.PowerAllocation")
public class UCFPowerAllocation {
    @Parameter(name = "federateID", coder = HLAunicodeStringCoder.class)
    private String federateID;

    @Parameter(name = "kw", coder = HLAfloat64BECoder.class)
    private Double kw;

    public UCFPowerAllocation() {
        federateID = "";
        kw = 0.0;
    }

    public UCFPowerAllocation(String federateID, Double kw) {
        this.federateID = federateID;
        this.kw = kw; }

    public String getFederateID() {
        return federateID;
    }

    public void setFederateID(String federateID) {
        this.federateID = federateID;
    }

    public Double getKw() {
        return kw;
    }

    public void setKw(Double kw) {
        this.kw = kw;
    }
}
