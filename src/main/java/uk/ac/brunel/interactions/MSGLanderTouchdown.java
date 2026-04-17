package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGLanderTouchdown interaction class from the MSG FOM.
 * Purpose: Sent by a lander when it has just landed at a spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLanderInteractionRoot.MSGLanderTouchdown")
public class MSGLanderTouchdown {
    @Parameter(name = "LanderName", coder = HLAunicodeStringCoder.class)
    private String landerName;

    @Parameter(name = "SpaceportName", coder = HLAunicodeStringCoder.class)
    private String spaceportName;

    public MSGLanderTouchdown() {
        landerName = "";
        spaceportName = "";
    }

    public MSGLanderTouchdown(String landerName, String spaceportName) {
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
