package uk.ac.brunel.lander;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.interactions.*;
import uk.ac.brunel.spaceport.SpaceportFederate;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class LanderFederate extends SEELateJoinerFederate {
    private static final File confFile = new File("src/main/resources/lander.conf");

    private final Set<Lander> landers;

    private static final Vector3D[] SPAWN_POINTS = new Vector3D[] {
            Vector3D.of(-536.303710683329, 4219.126819522129, -5400.62109375),
            Vector3D.of(736.18, 4097.91 ,-5408.40),
            Vector3D.of(2008.6595593598631, 3976.691211771549, -5400.8349609375),
    };

    protected LanderFederate(SEEFederateAmbassador federateAmbassador, FederateConfiguration federateConfiguration) {
        super(federateAmbassador, federateConfiguration);
        landers = new CopyOnWriteArraySet<>();
    }

    @Override
    public void declareClasses() throws FederateNotExecutionMember, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, NameNotFound, NotConnected, RTIinternalError, InvalidObjectClassHandle, SaveInProgress, InvalidInteractionClassHandle, InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM {
        publishObjectClass(PhysicalEntity.class);
        subscribeObjectClass(PhysicalEntity.class);

        publishInteractionClass(MSGLandingRequest.class);
        publishInteractionClass(MSGLanderTouchdown.class);
        publishInteractionClass(MSGLanderTakeoff.class);
        publishInteractionClass(MSGSpaceportArrivalCommitted.class);

        subscribeInteractionClass(MSGLandingPermission.class);
        subscribeInteractionClass(MSGLanderDepartureRequest.class);
    }

    @Override
    public void declareObjectInstances() throws FederateNotExecutionMember, ObjectClassNotPublished, ObjectClassNotDefined, RestoreInProgress, ObjectInstanceNotKnown, IllegalName, ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, NotConnected, RTIinternalError, SaveInProgress {
        for (int i = 1; i < SpaceportFederate.SPACEPORT_COUNT + 1; ++i) {
            SpaceTimeCoordinateState defaultState = new SpaceTimeCoordinateState();
            defaultState.setPosition(SPAWN_POINTS[i - 1]);

            String landerName = Lander.DEFAULT_NAME_SEQUENCE + i;
            Lander l = new Lander.Builder()
                    .name(landerName)
                    .parentReferenceFrame("AitkenBasinLocalFixed")
                    .spawnPoint(defaultState)
                    .federate(this)
                    .build();

            registerObjectInstance(l, l.getName());
            landers.add(l);
        }
    }

    @Override
    public void update() {
        landers.forEach(Lander::update);
    }

    public static void main(String[] args) {
        FederateConfiguration config = FederateConfiguration.Factory.create(confFile);
        LanderFederate federate = new LanderFederate(new SEEFederateAmbassador(), config);
        federate.configureAndStart();
    }
}
