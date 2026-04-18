package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;
import uk.ac.brunel.encoding.OperationalVerdictCoder;
import uk.ac.brunel.types.OperationalVerdict;

/**
 * Implementation of the MSGLandingPermission interaction class from the MSG FOM.
 * Purpose: Issued by a spaceport in response to the landing request sent by a lander.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLanderInteractionRoot.MSGLandingPermission")
public class MSGLandingPermission {
    @Parameter(name = "Spaceport", coder = HLAunicodeStringCoder.class)
    private String spaceport;

    @Parameter(name = "Lander", coder = HLAunicodeStringCoder.class)
    private String lander;

    @Parameter(name = "Verdict", coder = OperationalVerdictCoder.class)
    private OperationalVerdict verdict;

    public MSGLandingPermission() {
        spaceport = "";
        lander = "";
        verdict = OperationalVerdict.REJECTED;
    }

    public MSGLandingPermission(String spaceport, String lander, OperationalVerdict verdict) {
        this.spaceport = spaceport;
        this.lander = lander;
        this.verdict = verdict;
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

    public OperationalVerdict getVerdict() {
        return verdict;
    }

    public void setVerdict(OperationalVerdict verdict) {
        this.verdict = verdict;
    }
}
