package uk.ac.brunel.models;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.annotations.ObjectClass;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.federates.SpaceportFederate;
import uk.ac.brunel.interactions.MSGLandingPermission;
import uk.ac.brunel.listeners.LandingRequestListener;
import uk.ac.brunel.types.OperationalVerdict;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

/**
 * Simulation model of a Lunar Spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class Spaceport extends PhysicalEntity implements SimEntity {
    // Power load by the spaceport that is incurred during its operational stages in kilowatts (kW).
    // These values are essential to the UCF Power Systems federate.
    private static final double IDLE_POWER_RATING = 0.527;
    private static final double PEAK_POWER_RATING = 2.092;
    private static final double COMPUTE_MIN_POWER = 0.092;
    private static final double ARM_MIN_POWER = 0.435;

    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;
    private static final int CARGO_TRANSFER_TIME = 10;

    private final SpaceportFederate federate;

    private OperationalState operationalState;
    private String occupyingLander;
    private double powerConsumption;
    private int cargoLoadingTimer;

    private Spaceport(Builder builder) {
        this.federate = builder.spFederate;

        initSpaceportMetadata(builder);
        createListeners();
    }

    private void initSpaceportMetadata(Builder builder) {
        setName(builder.spName);
        setStatus("Available");
        setType("Spaceport");
        setParentReferenceFrame(builder.spParentReferenceFrame);
        setState(builder.spState);
        setAcceleration(Vector3D.of(0, 0, LUNAR_GRAVITATIONAL_PULL));

        occupyingLander = "";
        powerConsumption = IDLE_POWER_RATING;
        operationalState = OperationalState.AVAILABLE;
        cargoLoadingTimer = 0;
    }

    private void createListeners() {
        federate.addInteractionListener(new LandingRequestListener(this));
    }

    public synchronized void processLandingRequest(String landerName) {
        if (occupyingLander.isEmpty()) {
            occupyingLander = landerName;
            nextState();
        } else {
            MSGLandingPermission permissionRejected = new MSGLandingPermission(getName(), landerName, OperationalVerdict.REJECTED);
            try {
                federate.sendInteraction(permissionRejected);
            } catch (FederateNotExecutionMember | InteractionParameterNotDefined | RestoreInProgress |
                     InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | RTIinternalError |
                     SaveInProgress e) {
                throw new IllegalStateException("Failed to dispatch landing permission REJECT notification.", e);
            }
        }
    }

    public synchronized void landerTouchedDown(String landerName) {
        if (landerName.equals(occupyingLander)) {
            nextState();
        }
    }

    @Override
    public void update() {
        requestPower();
    }

    private void requestPower() {
        // TODO - Send a PowerRequest interaction.
    }

    public static class Builder {
        private SpaceportFederate spFederate;
        private String spName;
        private String spParentReferenceFrame;
        private SpaceTimeCoordinateState spState;

        public Builder federate(SpaceportFederate federate) {
            spFederate = federate;
            return this;
        }

        public Builder name(String value) {
            spName = value;
            return this;
        }

        public Builder parentReferenceFrame(String value) {
            spParentReferenceFrame = value;
            return this;
        }

        public Builder spaceTimeCoordinateState(SpaceTimeCoordinateState value) {
            spState = value;
            return this;
        }

        public Spaceport build () {
            validate();
            return new Spaceport(this);
        }

        private void validate() {
            if (spName == null) {
                throw new IncompleteObjectDataException("Missing field <name> for Spaceport object");
            }

            if (spFederate == null) {
                throw new IncompleteObjectDataException("Missing field <federate> for Spaceport object \"" + spName + "\"");
            }

            if (spParentReferenceFrame == null) {
                throw new IncompleteObjectDataException("Missing field <parentReferenceFrame> for Spaceport object \"" + spName + "\"");
            }

            if (spState == null) {
                throw new IncompleteObjectDataException("Missing field <state> for Spaceport object \"" + spName + "\"");
            }
        }
    }

    private enum OperationalState {
        AVAILABLE,
        AWAITING_LANDER_TOUCHDOWN,
        // The first half of the sequence where a rover that can take away the lander's cargo is dispatched.
        AWAITING_ROVER_ALLOCATION_1,
        AWAITING_ROVER_ARRIVAL_1,
        UNLOADING_CARGO,
        // The second half of the sequence where a rover carrying cargo for a lander is dispatched.
        AWAITING_ROVER_ALLOCATION_2,
        AWAITING_ROVER_ARRIVAL_2,
        LOADING_CARGO,
        AWAITING_LANDER_DEPARTURE,
        REFUELING
    }

    private void nextState() {
        switch (operationalState) {
            case AVAILABLE:
                operationalState = OperationalState.AWAITING_LANDER_TOUCHDOWN;
                setStatus("Busy");
                federate.updateObjectInstance(this);
                break;
            case AWAITING_LANDER_TOUCHDOWN:
                operationalState = OperationalState.AWAITING_ROVER_ALLOCATION_1;
                break;
            case AWAITING_ROVER_ALLOCATION_1:
                operationalState = OperationalState.AWAITING_ROVER_ARRIVAL_1;
                break;
            case AWAITING_ROVER_ARRIVAL_1:
                operationalState = OperationalState.UNLOADING_CARGO;
                break;
            case UNLOADING_CARGO:
                operationalState = OperationalState.AWAITING_ROVER_ALLOCATION_2;
                break;
            case AWAITING_ROVER_ALLOCATION_2:
                operationalState = OperationalState.AWAITING_ROVER_ARRIVAL_2;
                break;
            case AWAITING_ROVER_ARRIVAL_2:
                operationalState = OperationalState.LOADING_CARGO;
                break;
            case LOADING_CARGO:
                /* Deliberately skipped for now until UCF logistics system implementation is ready to handle this
                 mission state on their end. */
                // operationalState = OperationalState.REFUELING;
                operationalState = OperationalState.AWAITING_LANDER_DEPARTURE;
                setStatus("Awaiting Lander Departure");
                break;
            case REFUELING:
                operationalState = OperationalState.AWAITING_LANDER_DEPARTURE;
                break;
            default:
                operationalState = OperationalState.AVAILABLE;
                federate.updateObjectInstance(this);
                setStatus("Available");
        }
    }
}
