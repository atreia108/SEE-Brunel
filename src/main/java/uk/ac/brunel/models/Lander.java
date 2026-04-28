package uk.ac.brunel.models;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.annotations.Attribute;
import org.see.skf.annotations.ObjectClass;
import org.see.skf.core.PropertyChangeSubject;
import org.see.skf.util.encoding.HLAunicodeStringCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.encoding.SpaceTimeCoordinateStateCoder;
import uk.ac.brunel.encoding.Vector3DCoder;
import uk.ac.brunel.federates.LanderFederate;
import uk.ac.brunel.federates.SpaceportFederate;
import uk.ac.brunel.interactions.MSGLanderTakeoff;
import uk.ac.brunel.interactions.MSGLandingRequest;
import uk.ac.brunel.listeners.DepartureRequestListener;
import uk.ac.brunel.listeners.SpaceportAssignmentListener;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Simulation model of a lunar lander.
 *
 * @author Hridyanshu Aatreya
 */
@ObjectClass(name = "HLAobjectRoot.PhysicalEntity.DynamicalEntity")
public class Lander extends PropertyChangeSubject implements SimEntity {
    private static final Logger logger = LoggerFactory.getLogger(Lander.class);

    public final Supplier<Vector3D> holdingWaypointGenerator = this::createHoldingWaypoint;

    private static final int COOLDOWN_TIME = 10;

    private static final double RNG_MIN_X_BOUND = -530.0;
    private static final double RNG_MAX_X_BOUND = 2010.0;
    private static final double RNG_MIN_Y_BOUND = -5710.0;
    private static final double RNG_MAX_Y_BOUND = 5000.0;
    private static final double RNG_MIN_Z_BOUND = -5710.0;
    private static final double RNG_MAX_Z_BOUND = -4850.0;

    // The velocity of the lander expressed in m/s.
    // For a soft-moon landing, the lander should decelerate to less than 100 mph.
    private static final double VELOCITY = 33.3333;
    private static final double DISTANCE_THRESHOLD = 35.0;
    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;

    /* SpaceFOM DynamicalEntity-specific fields */
    @Attribute(name = "name", coder = HLAunicodeStringCoder.class)
    private String name;

    @Attribute(name = "type", coder = HLAunicodeStringCoder.class)
    private String type;

    @Attribute(name = "status", coder = HLAunicodeStringCoder.class)
    private String status;

    @Attribute(name = "parent_reference_frame", coder = HLAunicodeStringCoder.class)
    private String parentReferenceFrame;

    @Attribute(name = "state",  coder = SpaceTimeCoordinateStateCoder.class)
    private SpaceTimeCoordinateState state;

    @Attribute(name = "acceleration", coder = Vector3DCoder.class)
    private Vector3D acceleration;

    private Random rng;
    private List<Spaceport> spaceports;
    private LanderFederate federate;
    private FlightComputer flightComputer;
    private OperationalState operationalState;

    private int cooldownTime;

    public Lander() {
        this.name = "";
        this.type = "";
        this.status = "";
        this.parentReferenceFrame = "";
        this.state = new SpaceTimeCoordinateState();
        this.acceleration = Vector3D.of(0, 0, 0);
    }

    public Lander(String landerName, LanderFederate landerFederate) {
        this();

        federate = landerFederate;
        rng = new Random();
        spaceports = new ArrayList<>();
        flightComputer = new FlightComputer(this);
        cooldownTime = 0;

        initLanderMetadata(landerName);
        createListeners();
    }

    private void initLanderMetadata(String landerName) {
        name = landerName;
        status = OperationalState.APPROACHING.name();
        type = "Lander";
        parentReferenceFrame = "AitkenBasinLocalFixed";
        acceleration = Vector3D.of(0, 0, LUNAR_GRAVITATIONAL_PULL);

        Vector3D spawnPoint = createHoldingWaypoint();
        state.setPosition(spawnPoint);

        operationalState = OperationalState.APPROACHING;
    }

    /*
    public Lander(Builder builder) {
        rng = new Random();
        spaceports = new ArrayList<>();
        federate = builder.ldFederate;
        flightComputer = new FlightComputer();
        cooldownTime = 0;

        initLanderMetadata(builder);
        createListeners();
    }

    private void initLanderMetadata(Builder builder) {
        setName(builder.ldName);
        setStatus("Approaching");
        setType("Lander");
        setParentReferenceFrame(builder.ldParentReferenceFrame);
        setAcceleration(Vector3D.of(0, 0, LUNAR_GRAVITATIONAL_PULL));

        Vector3D spawnPoint = createHoldingWaypoint();
        getState().setPosition(spawnPoint);

        operationalState = OperationalState.APPROACHING;
    }
     */

    private void createListeners() {
        federate.addInteractionListener(new SpaceportAssignmentListener(this));
        federate.addInteractionListener(new DepartureRequestListener(this));
    }

    private Vector3D createHoldingWaypoint() {
        double x = rng.nextDouble(RNG_MIN_X_BOUND, RNG_MAX_X_BOUND);
        double y = rng.nextDouble(RNG_MIN_Y_BOUND, RNG_MAX_Y_BOUND);
        double z = rng.nextDouble(RNG_MIN_Z_BOUND, RNG_MAX_Z_BOUND);

        return Vector3D.of(x, y, z);
    }

    private void designateSpaceportWaypoint(Vector3D waypoint) {
        flightComputer.chartRoute(waypoint);
        logger.info("Destination for Lander <{}> has been set to {}", getName(), waypoint);
    }

    public synchronized void acceptedBy(String spaceportName) {
        if (operationalState == OperationalState.APPROACHING) {
            Object o = federate.queryRemoteObjectInstance(spaceportName);

            if (o instanceof Spaceport spaceport) {
                Vector3D spaceportPosition = spaceport.getState().getPosition();
                designateSpaceportWaypoint(spaceportPosition);

                nextState();
            }
        }
    }

    public synchronized void depart(String spaceportName) {
        if (operationalState == OperationalState.SERVICING) {
            Vector3D departureWaypoint = createHoldingWaypoint();
            flightComputer.chartRoute(departureWaypoint);

            try {
                MSGLanderTakeoff takeoffNotification = new MSGLanderTakeoff(getName(), spaceportName);
                federate.sendInteraction(takeoffNotification);

                nextState();
            } catch (FederateNotExecutionMember | SaveInProgress | RTIinternalError | InteractionParameterNotDefined |
                     InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | RestoreInProgress e) {
                throw new IllegalStateException("Failed to dispatch departure notification", e);
            }
        }
    }

    @Override
    public void update() {
        // WARNING: The spaceport federate must be running FIRST with the spaceport object(s) created. Otherwise, lander
        // creation will result in a nasty NullPointerException.
        if (spaceports.isEmpty()) {
            queryRemoteSpaceportInstances();
        }

        if (operationalState == OperationalState.APPROACHING) {
            if (cooldownTime < 1) {
                obtainLandingClearance();
            } else {
                --cooldownTime;
            }
        } else if (operationalState == OperationalState.LANDING || operationalState == OperationalState.DEPARTING) {
            flightComputer.navigate();
        }
    }

    private void queryRemoteSpaceportInstances() {
        for (int i = 1; i == SpaceportFederate.SPACEPORT_COUNT; ++i) {
            Object o = federate.queryRemoteObjectInstance(SpaceportFederate.SPACEPORT_NAME_SEQUENCE + i);
            if (o instanceof Spaceport spaceport) {
                spaceports.add(spaceport);
            }
        }
    }

    private void obtainLandingClearance() {
        for (Spaceport s : spaceports) {
            if (s.getStatus().equals("Available")) {
                try {
                    MSGLandingRequest landingRequest = new MSGLandingRequest(getName(), s.getName());
                    federate.sendInteraction(landingRequest);
                    cooldownTime = COOLDOWN_TIME;

                    return;
                } catch (FederateNotExecutionMember | SaveInProgress | RTIinternalError |
                         InteractionParameterNotDefined |
                         InteractionClassNotDefined | InteractionClassNotPublished | NotConnected |
                         RestoreInProgress e) {
                    throw new IllegalStateException("Failed to dispatch landing request to spaceport <" + s.getName() + ">.", e);
                }
            }
        }
    }

    private class FlightComputer {
        private Vector3D destination;
        private Vector3D positionDelta;

        private final Lander lander;

        private FlightComputer(Lander lander) {
            this.lander = lander;
        }

        public void chartRoute(Vector3D waypoint) {
            destination = waypoint;

            SpaceTimeCoordinateState landerState = getState();
            Vector3D landerPosition = landerState.getPosition();
            int xPolarity = axisPolarity(destination.getX(), landerPosition.getX());
            int yPolarity = axisPolarity(destination.getY(), landerPosition.getY());
            int zPolarity = axisPolarity(destination.getZ(), landerPosition.getZ());

            double xWaypointDiff = destination.getX() - landerPosition.getX();
            double yWaypointDiff = destination.getY() - landerPosition.getY();
            double theta = Math.atan2(yWaypointDiff, xWaypointDiff);

            double deltaVXAxis = xPolarity * VELOCITY;
            double deltaVYAxis = yPolarity * VELOCITY;
            double deltaVZAxis = zPolarity * VELOCITY;
            landerState.setVelocity(Vector3D.of(deltaVXAxis, deltaVYAxis, deltaVZAxis));

            Vector3D acceleration = getAcceleration();
            double deltaSXAxis = deltaVXAxis * Math.abs(Math.cos(theta));
            double deltaSYAxis = deltaVYAxis * Math.abs(Math.sin(theta));
            double deltaSZAxis = deltaVZAxis + (0.5 * acceleration.getZ());
            positionDelta = Vector3D.of(deltaSXAxis, deltaSYAxis, deltaSZAxis);
        }

        private int axisPolarity(double n1, double n2) {
            return (n1 > n2) ? 1 : -1;
        }

        public void navigate() {
            boolean changed = false;
            Vector3D landerPosition = getState().getPosition();
            // Adjustments to the X-axis component.
            if (isCoordinateOutsideThreshold(landerPosition.getX(), destination.getX())) {
                Vector3D newPosition = getState().getPosition().add(Vector3D.of(positionDelta.getX(), 0, 0));
                getState().setPosition(newPosition);
                changed = true;
            }

            // Adjustments to the Y-axis component.
            if (isCoordinateOutsideThreshold(landerPosition.getY(), destination.getY())) {
                Vector3D newPosition = getState().getPosition().add(Vector3D.of(0, positionDelta.getY(), 0));
                getState().setPosition(newPosition);
                changed = true;
            }

            // Adjustments to the Z-axis component.
            if (isCoordinateOutsideThreshold(landerPosition.getZ(), destination.getZ())) {
                Vector3D newPosition = getState().getPosition().add(Vector3D.of(0, 0, positionDelta.getZ()));
                getState().setPosition(newPosition);
                changed = true;
            }

            // The lander will never precisely reach its destination because that would require some serious precision.
            // So, if it fits within a specified threshold distance of the target point, we assume it has reached there.
            if (atDestination()) {
                getState().setPosition(destination);
                getState().setVelocity(Vector3D.of(0, 0, 0));
                nextState();

                logger.info("Lander <{}> has reached its destination {}.", getName(), getState().getPosition());
                changed = true;
            }

            logger.info("Lander <{}> position: {}.", getName(), getState().getPosition());

            if (changed) {
                federate.updateObjectInstance(lander);
            }
        }

        private boolean isCoordinateOutsideThreshold(double a, double b) {
            return Math.abs(a - b) >= DISTANCE_THRESHOLD;
        }

        private boolean atDestination() {
            Vector3D landerPosition = getState().getPosition();
            double xDiff = Math.abs(destination.getX() - landerPosition.getX());
            double yDiff = Math.abs(destination.getY() - landerPosition.getY());
            double zDiff = Math.abs(destination.getZ() - landerPosition.getZ());

            double xDiffSquared = Math.pow(xDiff, 2);
            double yDiffSquared = Math.pow(yDiff, 2);
            double zDiffSquared = Math.pow(zDiff, 2);

            double root = Math.sqrt(xDiffSquared + yDiffSquared + zDiffSquared);

            return (root <= DISTANCE_THRESHOLD);
        }
    }

    private enum OperationalState {
        APPROACHING,
        LANDING,
        SERVICING,
        DEPARTING
    }

    private void nextState() {
        switch (operationalState) {
            case LANDING:
                operationalState = OperationalState.SERVICING;
                setStatus("Servicing");
                break;
            case SERVICING:
                operationalState = OperationalState.DEPARTING;
                setStatus("Departing");
                break;
            case DEPARTING:
                operationalState = OperationalState.APPROACHING;
                setStatus("Approaching");
                break;
            default:
                operationalState = OperationalState.LANDING;
                setStatus("Landing");
                break;
        }

        federate.updateObjectInstance(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParentReferenceFrame() {
        return parentReferenceFrame;
    }

    public void setParentReferenceFrame(String parentReferenceFrame) {
        this.parentReferenceFrame = parentReferenceFrame;
    }

    public SpaceTimeCoordinateState getState() {
        return state;
    }

    public void setState(SpaceTimeCoordinateState state) {
        this.state = state;
    }

    public Vector3D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3D acceleration) {
        this.acceleration = acceleration;
    }
}
