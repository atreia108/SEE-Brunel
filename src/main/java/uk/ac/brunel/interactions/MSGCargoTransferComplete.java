package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGCargoTransferComplete interaction class from the MSG FOM.
 * Purpose: Sent when an object capable of loading/unloading payload to/from a rover has completed the transfer process.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGRoverInteractionRoot.MSGCargoTransferComplete")
public class MSGCargoTransferComplete {
    @Parameter(name = "Rover", coder = HLAunicodeStringCoder.class)
    private String rover;

    @Parameter(name = "OperatorObject", coder = HLAunicodeStringCoder.class)
    private String operatorObject;

    public MSGCargoTransferComplete() {
        rover = "";
        operatorObject = "";
    }

    public MSGCargoTransferComplete(String rover, String operatorObject) {
        this.rover = rover;
        this.operatorObject = operatorObject;
    }

    public String getRover() {
        return rover;
    }

    public void setRover(String rover) {
        this.rover = rover;
    }

    public String getOperatorObject() {
        return operatorObject;
    }

    public void setOperatorObject(String operatorObject) {
        this.operatorObject = operatorObject;
    }
}
