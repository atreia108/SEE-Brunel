package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

@InteractionClass(name = "HLAinteractionRoot.MSGinteractionRoot.MSGLandingRequest")
public class MSGLandingRequest {
    @Parameter(name = "landerName", coder = HLAunicodeStringCoder.class)
    private String landerName;

    @Parameter(name = "spaceportName", coder = HLAunicodeStringCoder.class)
    private String spaceportName;

    public MSGLandingRequest() {}

    public MSGLandingRequest(String landerName, String spaceportName) {
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
