package uk.ac.brunel.models;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.annotations.ObjectClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
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
public class Lander extends DynamicalEntity implements SimEntity {
    private static final Logger logger = LoggerFactory.getLogger(Lander.class);

    public final Supplier<Vector3D> holdingWaypointGenerator = this::createHoldingWaypoint;

    private static final double RNG_MIN_X_BOUND = -550.0;
    private static final double RNG_MAX_X_BOUND = 650.0;
    private static final double RNG_MIN_Y_BOUND = -350.0;
    private static final double RNG_MAX_Y_BOUND = 500.0;
    private static final double RNG_MIN_Z_BOUND = -5150.0;
    private static final double RNG_MAX_Z_BOUND = -4850.0;

    // The velocity of the lander expressed in m/s.
    // For a soft-moon landing, the lander should decelerate to less than 100 mph.
    private static final double VELOCITY = 33.3333;
    private static final double DISTANCE_THRESHOLD = 20.0;
    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;

    private final Random rng;
    private final List<Spaceport> spaceports;
    private final LanderFederate federate;
    private final FlightComputer flightComputer;
    private OperationalState operationalState;

    public Lander(Builder builder) {
        rng = new Random();
        spaceports = new ArrayList<>();
        federate = builder.ldFederate;
        flightComputer = new FlightComputer();

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
                nextState();

                Vector3D spaceportPosition = spaceport.getState().getPosition();
                designateSpaceportWaypoint(spaceportPosition);
            }
        }
    }

    public synchronized void depart(String spaceportName) {
        if (operationalState == OperationalState.SERVICING) {
            Vector3D departureWaypoint = createHoldingWaypoint();
            flightComputer.chartRoute(departureWaypoint);

            try {
                nextState();

                MSGLanderTakeoff takeoffNotification = new MSGLanderTakeoff(getName(), spaceportName);
                federate.sendInteraction(takeoffNotification);
            } catch (FederateNotExecutionMember | SaveInProgress | RTIinternalError | InteractionParameterNotDefined |
                     InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | RestoreInProgress e) {
                throw new IllegalStateException("Failed to dispatch departure notification", e);
            }
        }
    }

    @Override
    public void update() {
        if (operationalState == OperationalState.APPROACHING) {
            if (spaceports.size() < SpaceportFederate.SPACEPORT_COUNT) {
                queryRemoteSpaceportInstances();
            }

            for (Spaceport s : spaceports) {
                if (s.getStatus().equals("Available")) {
                    try {
                        MSGLandingRequest landingRequest = new MSGLandingRequest(getName(), s.getName());
                        federate.sendInteraction(landingRequest);
                    } catch (FederateNotExecutionMember | SaveInProgress | RTIinternalError | InteractionParameterNotDefined |
                            InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | RestoreInProgress e) {
                        throw new IllegalStateException("Failed to dispatch landing request to spaceport <" + s.getName() + ">.", e);
                    }
                }
            }
        } else if (operationalState == OperationalState.LANDING || operationalState == OperationalState.DEPARTING) {
            flightComputer.navigate();
        }
    }

    private void queryRemoteSpaceportInstances() {
        for (int i = 1; i <= SpaceportFederate.SPACEPORT_COUNT; ++i) {
            Object o = federate.queryRemoteObjectInstance(SpaceportFederate.DEFAULT_NAME_SEQUENCE + i);
            if (o instanceof Spaceport spaceport) {
                spaceports.add(spaceport);
            }
        }
    }

    private class FlightComputer {
        private Vector3D destination;
        private Vector3D positionDelta;

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
                getState().setPosition((Vector3D.of(positionDelta.getX(), 0, 0)));
                changed = true;
            }

            // Adjustments to the Y-axis component.
            if (isCoordinateOutsideThreshold(landerPosition.getY(), destination.getY())) {
                getState().setPosition((Vector3D.of(0, positionDelta.getY(), 0)));
                changed = true;
            }

            // Adjustments to the Z-axis component.
            if (isCoordinateOutsideThreshold(landerPosition.getZ(), destination.getZ())) {
                getState().setPosition((Vector3D.of(0, 0, positionDelta.getZ())));
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
                federate.updateObjectInstance(this);
            }
        }

        private boolean isCoordinateOutsideThreshold(double a, double b) {
            return Math.abs(a - b) > DISTANCE_THRESHOLD;
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

    public static class Builder {
        private LanderFederate ldFederate;
        private String ldName;
        private String ldParentReferenceFrame;

        public Builder federate(LanderFederate federate) {
            ldFederate = federate;
            return this;
        }

        public Builder name(String value) {
            ldName = value;
            return this;
        }

        public Builder parentReferenceFrame(String value) {
            ldParentReferenceFrame = value;
            return this;
        }

        public Lander build() {
            validate();
            return new Lander(this);
        }

        private void validate() {
            if (ldName == null) {
                throw new IncompleteObjectDataException("Missing field <name> for Lander object");
            }

            if (ldFederate == null) {
                throw new IncompleteObjectDataException("Missing field <federate> for Lander object \"" + ldName + "\"");
            }

            if (ldParentReferenceFrame == null) {
                throw new IncompleteObjectDataException("Missing field <parentReferenceFrame> for Lander object \"" + ldName + "\"");
            }
        }
    }
}
