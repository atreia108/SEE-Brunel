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
    @Parameter(name = "SpaceportName", coder = HLAunicodeStringCoder.class)
    private String spaceportName;

    @Parameter(name = "LanderName", coder = HLAunicodeStringCoder.class)
    private String landerName;

    @Parameter(name = "Verdict", coder = OperationalVerdictCoder.class)
    private OperationalVerdict verdict;

    public MSGLandingPermission() {
        spaceportName = "";
        landerName = "";
        verdict = OperationalVerdict.REJECTED;
    }

    public MSGLandingPermission(String spaceportName, String landerName, OperationalVerdict verdict) {
        this.spaceportName = spaceportName;
        this.landerName = landerName;
        this.verdict = verdict;
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

    public OperationalVerdict getVerdict() {
        return verdict;
    }

    public void setVerdict(OperationalVerdict verdict) {
        this.verdict = verdict;
    }
}
