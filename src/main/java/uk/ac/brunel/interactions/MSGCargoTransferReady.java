package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGCargoTransferReady interaction class from the MSG FOM.
 * Purpose: Sent by a rover as a follow-up to a CargoPickupJobResponseAccepted interaction, signaling that it has
 * arrived at the previously requested location. It is assumed that the rover is interacting with objects in the same
 * SpaceFOM reference frame.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGRoverInteractionRoot.MSGCargoTransferReady")
public class MSGCargoTransferReady {
    @Parameter(name = "Rover", coder = HLAunicodeStringCoder.class)
    private String rover;

    @Parameter(name = "RequestingObject", coder = HLAunicodeStringCoder.class)
    private String requestingObject;

    public MSGCargoTransferReady() {
        rover = "";
        requestingObject = "";
    }

    public MSGCargoTransferReady(String rover, String requestingObject) {
        this.rover = rover;
        this.requestingObject = requestingObject;
    }

    public String getRover() {
        return rover;
    }

    public void setRover(String rover) {
        this.rover = rover;
    }

    public String getRequestingObject() {
        return requestingObject;
    }

    public void setRequestingObject(String requestingObject) {
        this.requestingObject = requestingObject;
    }
}
