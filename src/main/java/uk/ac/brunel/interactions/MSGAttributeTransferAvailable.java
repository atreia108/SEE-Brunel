package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

/**
 * Implementation of the MSGAttributeTransferAvailable interaction class from the MSG FOM.
 * Purpose: Notifies all participating federates that the federate mentioned in this interaction is ready to relinquish
 * ownership of certain object attributes that it currently owns.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGAttributeTransferAvailable")
public class MSGAttributeTransferAvailable {
    @Parameter(name = "ProprietorFederate", coder = HLAunicodeStringCoder.class)
    private String proprietorFederate;

    @Parameter(name = "TargetObject", coder = HLAunicodeStringCoder.class)
    private String targetObject;

    public MSGAttributeTransferAvailable() {
        proprietorFederate = "";
        targetObject = "";
    }

    public MSGAttributeTransferAvailable(String proprietorFederate, String targetObject) {
        this.proprietorFederate = proprietorFederate;
        this.targetObject = targetObject;
    }

    public String getProprietorFederate() {
        return proprietorFederate;
    }

    public void setProprietorFederate(String proprietorFederate) {
        this.proprietorFederate = proprietorFederate;
    }

    public String getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }
}
