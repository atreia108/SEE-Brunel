package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGLandingRequest interaction class from the MSG FOM.
 * Purpose: While on its final approach to the lunar surface, a lander sends this interaction to obtain clearance to
 * land at a specific spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLanderInteractionRoot.MSGLandingRequest")
public final class MSGLandingRequest {
    @Parameter(name = "Lander", coder = HLAunicodeStringCoder.class)
    private String lander;

    @Parameter(name = "Spaceport", coder = HLAunicodeStringCoder.class)
    private String spaceport;

    public MSGLandingRequest() {
        lander = "";
        spaceport = "";
    }

    public MSGLandingRequest(String lander, String spaceport) {
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
