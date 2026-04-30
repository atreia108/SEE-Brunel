package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGSpaceportArrivalCommitted interaction class from the MSG FOM.
 * Purpose: Notifies a spaceport that previously granted clearance to a lander that it is committed to arriving there.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLanderInteractionRoot.MSGSpaceportArrivalCommitted")
public class MSGSpaceportArrivalCommitted {
    @Parameter(name = "Lander", coder = HLAunicodeStringCoder.class)
    private String lander;

    @Parameter(name = "Spaceport", coder = HLAunicodeStringCoder.class)
    private String spaceport;

    public MSGSpaceportArrivalCommitted() {
    }

    public MSGSpaceportArrivalCommitted(String lander, String spaceport) {
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
