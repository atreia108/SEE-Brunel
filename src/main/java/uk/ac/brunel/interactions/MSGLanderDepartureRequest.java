package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGLanderDepartureRequest interaction class from the MSG FOM.
 * Purpose: Sent by a spaceport to advise the lander currently occupying it to depart immediately.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLanderInteractionRoot.MSGLanderDepartureRequest")
public class MSGLanderDepartureRequest {
    @Parameter(name = "Spaceport", coder = HLAunicodeStringCoder.class)
    private String spaceport;

    @Parameter(name = "Lander", coder = HLAunicodeStringCoder.class)
    private String lander;

    public MSGLanderDepartureRequest() {
        spaceport = "";
        lander = "";
    }

    public MSGLanderDepartureRequest(String spaceport, String lander) {
        this.spaceport = spaceport;
        this.lander = lander;
    }

    public String getSpaceport() {
        return spaceport;
    }

    public void setSpaceport(String spaceport) {
        this.spaceport = spaceport;
    }

    public String getLander() {
        return lander;
    }

    public void setLander(String lander) {
        this.lander = lander;
    }
}
