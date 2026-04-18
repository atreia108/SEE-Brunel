package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGCargoPickupJobAccepted interaction class from the MSG FOM.
 * Purpose: Confirmation of the assignment of a rover to fulfill the requested transport mission. It is assumed that the
 * rover is interacting with objects in the same SpaceFOM reference frame.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGRoverInteractionRoot.MSGCargoPickupJobAccepted")
public class MSGCargoPickupJobAccepted {
    @Parameter(name = "RequestingObject", coder = HLAunicodeStringCoder.class)
    private String requestingObject;

    @Parameter(name = "AssignedRover", coder = HLAunicodeStringCoder.class)
    private String assignedRover;

    public MSGCargoPickupJobAccepted() {
        requestingObject = "";
        assignedRover = "";
    }

    public MSGCargoPickupJobAccepted(String requestingObject, String assignedRover) {
        this.requestingObject = requestingObject;
        this.assignedRover = assignedRover;
    }

    public String getRequestingObject() {
        return requestingObject;
    }

    public void setRequestingObject(String requestingObject) {
        this.requestingObject = requestingObject;
    }

    public String getAssignedRover() {
        return assignedRover;
    }

    public void setAssignedRover(String assignedRover) {
        this.assignedRover = assignedRover;
    }
}
