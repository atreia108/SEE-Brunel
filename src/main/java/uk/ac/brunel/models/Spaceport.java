package uk.ac.brunel.models;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.annotations.ObjectClass;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.federates.SpaceportFederate;
import uk.ac.brunel.interactions.MSGLandingPermission;
import uk.ac.brunel.interactions.UCFPowerRequest;
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
    // Power load of the spaceport that is incurred during its operational stages in kilowatts (kW).
    private static final double POWER_RATING = 0.092;

    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;
    private static final int CARGO_TRANSFER_TIME = 10;

    private final SpaceportFederate federate;

    private SpaceportArm arm;
    private String occupyingLander;
    private int cargoLoadingTimer;

    private OperationalState preSuspendedState;
    private OperationalState operationalState;

    private Spaceport(Builder builder) {
        federate = builder.spFederate;

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

        arm = new SpaceportArm(builder.spArmName, getName());
        occupyingLander = "";
        cargoLoadingTimer = 0;

        operationalState = OperationalState.AVAILABLE;
        preSuspendedState = OperationalState.AVAILABLE;
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
        double powerRequirement = powerConsumption();
        try {
            UCFPowerRequest powerRequest = new UCFPowerRequest(getName(), powerRequirement, 0);
            federate.sendInteraction(powerRequest);
        } catch (FederateNotExecutionMember | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | RTIinternalError |
                 SaveInProgress e) {
            throw new IllegalStateException("Failed to dispatch landing permission REJECT notification.", e);
        }
    }

    private double powerConsumption() {
        double powerUsage = POWER_RATING;

        if (operationalState == OperationalState.LOADING_CARGO || operationalState == OperationalState.UNLOADING_CARGO) {
            powerUsage += SpaceportArm.PEAK_POWER_RATING;
        } else {
            powerUsage += SpaceportArm.IDLE_POWER_RATING;
        }

        return powerUsage;
    }

    public synchronized void powerAllocation(double allocatedAmount) {
        // TODO - Handle constraints imposed by the amount of power allocated.
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
        // A specific case where the sudden cut in power allocation can impede the spaceport from carrying out
        // a high-demand operation.
        SUSPENDED
    }

    private void nextState() {
        switch (operationalState) {
            case AVAILABLE:
                shiftOperationalState(OperationalState.AVAILABLE, OperationalState.AWAITING_LANDER_TOUCHDOWN);
                setStatus("Busy");
                federate.updateObjectInstance(this);
                break;
            case AWAITING_LANDER_TOUCHDOWN:
                shiftOperationalState(OperationalState.AWAITING_LANDER_TOUCHDOWN, OperationalState.AWAITING_ROVER_ALLOCATION_1);
                // operationalState = OperationalState.AWAITING_ROVER_ALLOCATION_1;
                break;
            case AWAITING_ROVER_ALLOCATION_1:
                shiftOperationalState(OperationalState.AWAITING_ROVER_ALLOCATION_1, OperationalState.AWAITING_ROVER_ARRIVAL_1);
                // operationalState = OperationalState.AWAITING_ROVER_ARRIVAL_1;
                break;
            case AWAITING_ROVER_ARRIVAL_1:
                shiftOperationalState(OperationalState.AWAITING_ROVER_ARRIVAL_1, OperationalState.UNLOADING_CARGO);
                // operationalState = OperationalState.UNLOADING_CARGO;
                break;
            case UNLOADING_CARGO:
                shiftOperationalState(OperationalState.UNLOADING_CARGO, OperationalState.AWAITING_ROVER_ALLOCATION_2);
                // operationalState = OperationalState.AWAITING_ROVER_ALLOCATION_2;
                break;
            case AWAITING_ROVER_ALLOCATION_2:
                shiftOperationalState(OperationalState.AWAITING_ROVER_ALLOCATION_2, OperationalState.AWAITING_ROVER_ARRIVAL_2);
                // operationalState = OperationalState.AWAITING_ROVER_ARRIVAL_2;
                break;
            case AWAITING_ROVER_ARRIVAL_2:
                shiftOperationalState(OperationalState.AWAITING_ROVER_ARRIVAL_2, OperationalState.LOADING_CARGO);
                // operationalState = OperationalState.LOADING_CARGO;
                break;
            case LOADING_CARGO:
                shiftOperationalState(OperationalState.LOADING_CARGO, OperationalState.AWAITING_LANDER_DEPARTURE);
                // operationalState = OperationalState.AWAITING_LANDER_DEPARTURE;
                setStatus("Awaiting Lander Departure");
                break;
            default:
                operationalState = OperationalState.AVAILABLE;
                federate.updateObjectInstance(this);
                setStatus("Available");
        }
    }

    private void shiftOperationalState(OperationalState currentState, OperationalState nextState) {
        preSuspendedState = currentState;
        operationalState = nextState;
    }

    public static class SpaceportArm extends PhysicalInterface {
        // Power load of the arm that is incurred during its operational stages in kilowatts (kW).
        private static final double IDLE_POWER_RATING = 0.435;
        private static final double PEAK_POWER_RATING = 2.000;

        private SpaceportArm(String name, String parentName) {
            setName(name);
            setParentName(parentName);
        }

        private void initiateCargoTransfer() {

        }
    }

    public static class Builder {
        private SpaceportFederate spFederate;
        private String spName;
        private String spParentReferenceFrame;
        private SpaceTimeCoordinateState spState;
        private String spArmName;

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

        public Builder arm(String armName) {
            spArmName = armName;
            return this;
        }

        public Spaceport build () {
            validate();
            return new Spaceport(this);
        }

        private void validate() {
            if (spName == null) {
                throw new IncompleteObjectDataException("Missing field <name> for Spaceport");
            }

            if (spFederate == null) {
                throw new IncompleteObjectDataException("Missing field <federate> for Spaceport \"" + spName + "\"");
            }

            if (spParentReferenceFrame == null) {
                throw new IncompleteObjectDataException("Missing field <parentReferenceFrame> for Spaceport \"" + spName + "\"");
            }

            if (spState == null) {
                throw new IncompleteObjectDataException("Missing field <state> for Spaceport \"" + spName + "\"");
            }

            if (spArmName == null) {
                throw new IncompleteObjectDataException("Missing name for Spaceport's arm object \"" + spName + "\"");
            }
        }
    }

    public Object getArmObject() {
        return arm;
    }
}
