package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

@InteractionClass(name = "HLAinteractionRoot.LoadSheddingEvent")
public class UCFLoadSheddingEvent {
    @Parameter(name = "targetID", coder = HLAunicodeStringCoder.class)
    private String targetID;

    @Parameter(name = "action", coder = HLAunicodeStringCoder.class)
    private String action;

    @Parameter(name = "reason", coder = HLAunicodeStringCoder.class)
    private String reason;

    public UCFLoadSheddingEvent() {
        targetID = "";
        action = "";
        reason = "";
    }
    public UCFLoadSheddingEvent(String targetID, String action, String reason) {
        this.targetID = targetID;
        this.action = action;
        this.reason = reason;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
