package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGLanderDepartureRequest interaction class from the MSG FOM.
 * Purpose: Sent by a spaceport advising the lander currently occupying it to depart immediately.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLanderInteractionRoot.MSGLanderDepartureRequest")
public class MSGLanderDepartureRequest {
    @Parameter(name = "SpaceportName", coder = HLAunicodeStringCoder.class)
    private String spaceportName;

    @Parameter(name = "LanderName", coder = HLAunicodeStringCoder.class)
    private String landerName;

    public MSGLanderDepartureRequest() {
        spaceportName = "";
        landerName = "";
    }

    public MSGLanderDepartureRequest(String spaceportName, String landerName) {
        this.spaceportName = spaceportName;
        this.landerName = landerName;
    }

    public String getSpaceportName() {
        return spaceportName;
    }

    public void setSpaceportName(String spaceportName) {
        this.spaceportName = spaceportName;
    }

    public String getLanderName() {
        return landerName;
    }

    public void setLanderName(String landerName) {
        this.landerName = landerName;
    }
}
