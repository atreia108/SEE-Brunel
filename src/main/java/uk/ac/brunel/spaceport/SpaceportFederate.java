package uk.ac.brunel.spaceport;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;
import uk.ac.brunel.models.Spaceport;

import java.io.File;
import java.util.concurrent.CopyOnWriteArraySet;

public class SpaceportFederate extends SEELateJoinerFederate {
    private static final File confFile = new File("src/main/resources/spaceport.conf");

    public static final int SPACEPORT_COUNT = 3;
    public static final String SPACEPORT_NAME_SEQUENCE = "brunel_spaceport_";
    public static final String SPACEPORT_ARM_NAME_SEQUENCE = "brunel_spaceport_arm_";

    private static final Vector3D[] SPAWN_POINTS = new Vector3D[] {
            Vector3D.of(-536.303710683329, 4219.126819522129, -5648.62109375),
            Vector3D.of(736.18, 4097.91 ,-5608.40),
            Vector3D.of(2008.6595593598631, 3976.691211771549, -5584.8349609375),
    };

    private final CopyOnWriteArraySet<Spaceport> spaceports;

    protected SpaceportFederate(SEEFederateAmbassador federateAmbassador, FederateConfiguration federateConfiguration) {
        super(federateAmbassador, federateConfiguration);
        spaceports = new CopyOnWriteArraySet<>();
    }

    @Override
    public void declareClasses() throws FederateNotExecutionMember, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, NameNotFound, NotConnected, RTIinternalError, InvalidObjectClassHandle, SaveInProgress, InvalidInteractionClassHandle, InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM {

    }

    @Override
    public void declareObjectInstances() throws FederateNotExecutionMember, ObjectClassNotPublished, ObjectClassNotDefined, RestoreInProgress, ObjectInstanceNotKnown, IllegalName, ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, NotConnected, RTIinternalError, SaveInProgress {

    }

    @Override
    public void update() {
        spaceports.forEach(Spaceport::update);
    }

    public static void main(String[] args) {
        FederateConfiguration config = FederateConfiguration.Factory.create(confFile);
        uk.ac.brunel.federates.SpaceportFederate federate = new uk.ac.brunel.federates.SpaceportFederate(new SEEFederateAmbassador(), config);
        federate.configureAndStart();
    }
}
