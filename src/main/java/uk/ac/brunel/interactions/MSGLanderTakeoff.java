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
    @Parameter(name = "Lander", coder = HLAunicodeStringCoder.class)
    private String lander;

    @Parameter(name = "Spaceport", coder = HLAunicodeStringCoder.class)
    private String spaceport;

    public MSGLanderTakeoff() {
        lander = "";
        spaceport = "";
    }

    public MSGLanderTakeoff(String lander, String spaceport) {
        this.lander = lander;
        this.spaceport = spaceport;
    }

    public String getLander() {
        return lander;
    }

    public void setLander(String lander) {
        this.lander = lander;
    }

    public String getSpaceport() {
        return spaceport;
    }

    public void setSpaceport(String spaceport) {
        this.spaceport = spaceport;
    }
}
