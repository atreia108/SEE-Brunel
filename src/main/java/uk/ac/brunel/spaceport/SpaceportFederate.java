package uk.ac.brunel.spaceport;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.interactions.*;
import uk.ac.brunel.core.PhysicalInterface;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

import java.io.File;
import java.util.concurrent.CopyOnWriteArraySet;

public class SpaceportFederate extends SEELateJoinerFederate {
    private static final File confFile = new File("src/main/resources/spaceport.conf");

    public static final int SPACEPORT_COUNT = 3;

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
        publishObjectClass(PhysicalEntity.class);
        publishObjectClass(PhysicalInterface.class);
        subscribeObjectClass(PhysicalEntity.class);

        publishInteractionClass(UCFPowerRequest.class);
        publishInteractionClass(MSGCargoPickupJob.class);
        publishInteractionClass(MSGCargoTransferComplete.class);
        publishInteractionClass(MSGLanderDepartureRequest.class);
        publishInteractionClass(MSGLandingPermission.class);

        subscribeInteractionClass(MSGCargoPickupJobAccepted.class);
        subscribeInteractionClass(MSGCargoPickupJobRejected.class);
        subscribeInteractionClass(MSGCargoTransferReady.class);
        subscribeInteractionClass(MSGLandingRequest.class);
        subscribeInteractionClass(MSGLanderTakeoff.class);
        subscribeInteractionClass(MSGLanderTouchdown.class);
        subscribeInteractionClass(UCFPowerAllocation.class);
        subscribeInteractionClass(UCFLoadSheddingEvent.class);
        subscribeInteractionClass(MSGSpaceportArrivalCommitted.class);
    }

    @Override
    public void declareObjectInstances() throws FederateNotExecutionMember, ObjectClassNotPublished, ObjectClassNotDefined, RestoreInProgress, ObjectInstanceNotKnown, IllegalName, ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, NotConnected, RTIinternalError, SaveInProgress {
        for (int i = 1; i < SPACEPORT_COUNT + 1; ++i) {
            SpaceTimeCoordinateState defaultState = new SpaceTimeCoordinateState();
            defaultState.setPosition(SPAWN_POINTS[i - 1]);

            String spaceportName = Spaceport.NAME_SEQUENCE + i;
            String spaceportArmName = SpaceportArm.NAME_SEQUENCE + i;

            Spaceport s = new Spaceport.Builder()
                    .federate(this)
                    .name(spaceportName)
                    .parentReferenceFrame("AitkenBasinLocalFixed")
                    .spawnPoint(defaultState)
                    .arm(spaceportArmName)
                    .build();

            registerObjectInstance(s, s.getName());

            SpaceportArm sArm = s.getArm();
            registerObjectInstance(sArm, sArm.getName());
            spaceports.add(s);
        }
    }

    @Override
    public void update() {
        spaceports.forEach(Spaceport::update);
    }

    public static void main(String[] args) {
        FederateConfiguration config = FederateConfiguration.Factory.create(confFile);
        SpaceportFederate federate = new SpaceportFederate(new SEEFederateAmbassador(), config);
        federate.configureAndStart();
    }
}
