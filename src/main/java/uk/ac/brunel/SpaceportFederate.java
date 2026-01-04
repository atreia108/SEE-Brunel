package uk.ac.brunel;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;

public class SpaceportFederate extends SEELateJoinerFederate {
    public SpaceportFederate(SEEFederateAmbassador federateAmbassador, FederateConfiguration config) {
        super(federateAmbassador, config);
    }

    @Override
    public void declareClasses() throws FederateNotExecutionMember, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, NameNotFound, NotConnected, RTIinternalError, InvalidObjectClassHandle, SaveInProgress, InvalidInteractionClassHandle, InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM {

    }

    @Override
    public void declareObjectInstances() throws FederateNotExecutionMember, ObjectClassNotPublished, ObjectClassNotDefined, RestoreInProgress, ObjectInstanceNotKnown, IllegalName, ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, NotConnected, RTIinternalError, SaveInProgress {

    }

    @Override
    public void update() {

    }

    public static void main(String[] args) {

    }
}
