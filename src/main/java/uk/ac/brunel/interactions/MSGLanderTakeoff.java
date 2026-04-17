package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGLanderTakeoff interaction class from the MSG FOM.
 * Purpose: Sent by a lander once it has departed from a spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLanderInteractionRoot.MSGLanderTakeoff")
public class MSGLanderTakeoff {
    @Parameter(name = "LanderName", coder = HLAunicodeStringCoder.class)
    private String landerName;

    @Parameter(name = "SpaceportName", coder = HLAunicodeStringCoder.class)
    private String spaceportName;

    public MSGLanderTakeoff() {
        landerName = "";
        spaceportName = "";
    }

    public MSGLanderTakeoff(String landerName, String spaceportName) {
        this.landerName = landerName;
        this.spaceportName = spaceportName;
    }

    public String getLanderName() {
        return landerName;
    }

    public void setLanderName(String landerName) {
        this.landerName = landerName;
    }

    public String getSpaceportName() {
        return spaceportName;
    }

    public void setSpaceportName(String spaceportName) {
        this.spaceportName = spaceportName;
    }
}
