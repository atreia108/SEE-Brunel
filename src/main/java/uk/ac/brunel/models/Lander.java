package uk.ac.brunel.models;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.annotations.ObjectClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.federates.LanderFederate;
import uk.ac.brunel.listeners.SpaceportAssignmentListener;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

/**
 * Simulation model of a lunar lander.
 *
 * @author Hridyanshu Aatreya
 */
@ObjectClass(name = "HLAobjectRoot.PhysicalEntity.DynamicalEntity")
public class Lander extends DynamicalEntity implements SimEntity {
    private static final Logger logger = LoggerFactory.getLogger(Lander.class);

    // The velocity of the lander expressed in m/s.
    // For a soft-moon landing, the lander should decelerate to less than 100 mph.
    private static final double VELOCITY = 33.3333;
    private static final double LUNAR_GRAVITY = -1.625;
    private static final double DISTANCE_THRESHOLD = 20.0;

    private final LanderFederate federate;
    private final FlightComputer flightComputer;
    private OperationalState operationalState;

    public Lander(Builder builder) {
        setName(builder.ldName);
        setParentReferenceFrame(builder.ldParentReferenceFrame);
        setState(builder.ldState);
        this.federate = builder.ldFederate;

        flightComputer = new FlightComputer();
        init();
    }

    private void init() {
        operationalState = OperationalState.APPROACHING;
        federate.addInteractionListener(new SpaceportAssignmentListener(this, federate));
    }

    public void setWaypoint(Vector3D waypoint) {
        flightComputer.planRoute(waypoint);
        logger.info("Destination for Lander <{}> has been set to {}", getName(), waypoint);
    }

    @Override
    public void update() {
        if (!flightComputer.isRunning) {
            return;
        }

        boolean changed = false;
        Vector3D landerPosition = getState().getPosition();
        // Adjustments to the X-axis component.
        if (flightComputer.outsideThreshold(landerPosition.getX(), flightComputer.destination.getX())) {
            getState().setPosition((Vector3D.of(flightComputer.positionDelta.getX(), 0, 0)));
            changed = true;
        }

        // Adjustments to the Y-axis component.
        if (flightComputer.outsideThreshold(landerPosition.getY(), flightComputer.destination.getY())) {
            getState().setPosition((Vector3D.of(0, flightComputer.positionDelta.getY(), 0)));
            changed = true;
        }

        // Adjustments to the Z-axis component.
        if (flightComputer.outsideThreshold(landerPosition.getZ(), flightComputer.destination.getZ())) {
            getState().setPosition((Vector3D.of(0, 0, flightComputer.positionDelta.getZ())));
            changed = true;
        }

        // The lander will never precisely reach its destination because that would require some serious precision.
        // So, if it fits within a specified threshold distance of the target point, we assume it has reached there.
        if (flightComputer.atDestination()) {
            flightComputer.isRunning = false;
            getState().setPosition(flightComputer.destination);
            getState().setVelocity(Vector3D.of(0, 0, 0));
            logger.info("Lander <{}> has reached its destination {}.", getName(), getState().getPosition());
            changed = true;
        }

        logger.info("Lander <{}> position: {}.", getName(), getState().getPosition());

        if (changed) {
            federate.updateObjectInstance(this);
        }
    }

    private class FlightComputer {
        private boolean isRunning;
        private Vector3D destination;
        private Vector3D positionDelta;

        public void planRoute(Vector3D waypoint) {
            isRunning = true;
            destination = waypoint;

            Vector3D landerPosition = getState().getPosition();
            int xPolarity = axisPolarity(destination.getX(), landerPosition.getX());
            int yPolarity = axisPolarity(destination.getY(), landerPosition.getY());
            int zPolarity = axisPolarity(destination.getZ(), landerPosition.getZ());

            double xWaypointDiff = destination.getX() - landerPosition.getX();
            double yWaypointDiff = destination.getY() - landerPosition.getY();
            double theta = Math.atan2(yWaypointDiff, xWaypointDiff);

            double deltaVXAxis = xPolarity * VELOCITY;
            double deltaVYAxis = yPolarity * VELOCITY;
            double deltaVZAxis = zPolarity * VELOCITY;
            getState().setVelocity(Vector3D.of(deltaVXAxis, deltaVYAxis, deltaVZAxis));

            double deltaSXAxis = deltaVXAxis * Math.abs(Math.cos(theta));
            double deltaSYAxis = deltaVYAxis * Math.abs(Math.sin(theta));
            double deltaSZAxis = deltaVZAxis + (0.5 * LUNAR_GRAVITY);
            positionDelta = Vector3D.of(deltaSXAxis, deltaSYAxis, deltaSZAxis);
        }

        private int axisPolarity(double n1, double n2) {
            return (n1 > n2) ? 1 : -1;
        }

        private boolean outsideThreshold(double a, double b) {
            return (!(Math.abs(a - b) <= DISTANCE_THRESHOLD));
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

    public static class Builder {
        private LanderFederate ldFederate;
        private String ldName;
        private String ldParentReferenceFrame;
        private SpaceTimeCoordinateState ldState;

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

        public Builder spaceTimeCoordinateState(SpaceTimeCoordinateState value) {
            ldState = value;
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

            if (ldState == null) {
                throw new IncompleteObjectDataException("Missing field <state> for Lander object \"" + ldName + "\"");
            }
        }
    }
}
