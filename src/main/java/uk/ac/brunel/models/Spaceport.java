package uk.ac.brunel.models;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.annotations.ObjectClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.federates.SpaceportFederate;
import uk.ac.brunel.interactions.MSGCargoTransferComplete;
import uk.ac.brunel.interactions.MSGLandingPermission;
import uk.ac.brunel.interactions.UCFPowerRequest;
import uk.ac.brunel.listeners.CargoTransferReadyListener;
import uk.ac.brunel.listeners.LandingRequestListener;
import uk.ac.brunel.listeners.PowerAllocationListener;
import uk.ac.brunel.listeners.TouchdownListener;
import uk.ac.brunel.types.OperationalVerdict;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

/**
 * Simulation model of a Lunar Spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class Spaceport extends PhysicalEntity implements SimEntity {
    private static final Logger logger = LoggerFactory.getLogger(Spaceport.class);

    // Power load of the spaceport that is incurred during its operational stages in kilowatts (kW).
    private static final double POWER_RATING = 0.092;
    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;

    private final SpaceportFederate federate;

    private double allocatedPower;
    private SpaceportArm arm;
    private String occupyingLander;

    private OperationalState preSuspendedOperationalState;
    private OperationalState currentOperationalState;

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
        occupyingLander = "";
        allocatedPower = 0;
        currentOperationalState = preSuspendedOperationalState = OperationalState.AVAILABLE;

        arm = new SpaceportArm(builder.spArmName, getName());
    }

    private void createListeners() {
        federate.addInteractionListener(new LandingRequestListener(this));
        federate.addInteractionListener(new PowerAllocationListener(this));
        federate.addInteractionListener(new TouchdownListener(this));
        federate.addInteractionListener(new CargoTransferReadyListener(this));
    }

    public synchronized void processLandingRequest(String landerName) {
        if (currentOperationalState == OperationalState.AVAILABLE && !occupyingLander.isEmpty()) {
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

    public synchronized void onLanderTouchDown(String landerName) {
        if (landerName.equals(occupyingLander)) {
            nextState();
        }
    }

    public synchronized void setAllocatedPower(double amount) {
        allocatedPower = amount;
    }

    public synchronized void initiateCargoTransfer() {
        arm.start();
    }

    @Override
    public void update() {
        requestPower();

        if (isServicePowerRequirementSatisfied()) {
            if (currentOperationalState == OperationalState.SUSPENDED) {
                resumeOperations();
            }

            operateArm();
        } else {
            if (currentOperationalState == OperationalState.LOADING_CARGO || currentOperationalState == OperationalState.UNLOADING_CARGO) {
                suspendOperations();
            }
        }
    }

    private void operateArm() {
        if (arm.transferInProgress) {
            arm.update();
        }
    }

    private void requestPower() {
        double powerRequirement = powerConsumption();
        try {
            UCFPowerRequest powerRequest = new UCFPowerRequest(getName(), powerRequirement, 0);
            federate.sendInteraction(powerRequest);
        } catch (FederateNotExecutionMember | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | RTIinternalError |
                 SaveInProgress e) {
            throw new IllegalStateException("Failed to dispatch REJECTED landing permission notification.", e);
        }
    }

    private double powerConsumption() {
        double powerUsage = POWER_RATING;

        if (currentOperationalState == OperationalState.LOADING_CARGO || currentOperationalState == OperationalState.UNLOADING_CARGO) {
            powerUsage += SpaceportArm.PEAK_POWER_RATING;
        } else {
            powerUsage += SpaceportArm.IDLE_POWER_RATING;
        }

        return powerUsage;
    }

    private boolean isServicePowerRequirementSatisfied() {
        return allocatedPower >= (SpaceportArm.PEAK_POWER_RATING + POWER_RATING);
    }

    private enum OperationalState {
        AVAILABLE,
        AWAITING_LANDER_TOUCHDOWN,
        // The first half of the sequence where a rover that can take away the lander's cargo is dispatched.
        AWAITING_PICKUP_ROVER_ALLOCATION,
        AWAITING_PICKUP_ROVER_ARRIVAL,
        UNLOADING_CARGO,
        AWAITING_PICKUP_ROVER_DEPARTURE,
        // The second half of the sequence where a rover carrying cargo for a lander is dispatched.
        AWAITING_DELIVERY_ROVER_ALLOCATION,
        AWAITING_DELIVERY_ROVER_ARRIVAL,
        LOADING_CARGO,
        AWAITING_DELIVERY_ROVER_DEPARTURE,
        HOSTING_LANDER_TAKEOFF,
        // When there is a sudden cut in power allocation during cargo loading/unloading, spaceport operations are
        // temporarily suspended until power supply is restored.
        SUSPENDED
    }

    private void nextState() {
        String newStatus;

        switch (currentOperationalState) {
            case AVAILABLE:
                newStatus = "Awaiting Lander Touchdown";
                currentOperationalState = OperationalState.AWAITING_LANDER_TOUCHDOWN;
                break;
            case AWAITING_LANDER_TOUCHDOWN:
                newStatus = "Awaiting Pickup Rover Allocation";
                currentOperationalState = OperationalState.AWAITING_PICKUP_ROVER_ALLOCATION;
                break;
            case AWAITING_PICKUP_ROVER_ALLOCATION:
                newStatus = "Awaiting Pickup Rover Arrival";
                currentOperationalState = OperationalState.AWAITING_PICKUP_ROVER_ARRIVAL;
                break;
            case AWAITING_PICKUP_ROVER_ARRIVAL:
                newStatus = "Unloading Cargo";
                currentOperationalState = OperationalState.UNLOADING_CARGO;
                break;
            case UNLOADING_CARGO:
                newStatus = "Awaiting Rover Departure";
                currentOperationalState = OperationalState.AWAITING_PICKUP_ROVER_DEPARTURE;
                break;
            case AWAITING_PICKUP_ROVER_DEPARTURE:
                newStatus = "Awaiting Delivery Rover Allocation";
                currentOperationalState = OperationalState.AWAITING_DELIVERY_ROVER_ALLOCATION;
                break;
            case AWAITING_DELIVERY_ROVER_ALLOCATION:
                newStatus = "Awaiting Delivery Rover Arrival";
                currentOperationalState = OperationalState.AWAITING_DELIVERY_ROVER_ARRIVAL;
                break;
            case AWAITING_DELIVERY_ROVER_ARRIVAL:
                newStatus = "Loading Cargo";
                currentOperationalState = OperationalState.LOADING_CARGO;
                break;
            case LOADING_CARGO:
                newStatus = "Awaiting Delivery Rover Departure";
                currentOperationalState = OperationalState.AWAITING_DELIVERY_ROVER_DEPARTURE;
                break;
            case AWAITING_DELIVERY_ROVER_DEPARTURE:
                newStatus = "Hosting Lander Takeoff";
                currentOperationalState = OperationalState.HOSTING_LANDER_TAKEOFF;
                break;
            default:
                currentOperationalState = OperationalState.AVAILABLE;
                newStatus = "Available";
                break;
        }

        setStatus(newStatus);
        federate.updateObjectInstance(this);
    }

    private void suspendOperations() {
        preSuspendedOperationalState = currentOperationalState;
        currentOperationalState = OperationalState.SUSPENDED;

        setStatus("Suspended");
        federate.updateObjectInstance(this);
        logger.debug("Cargo transfer operations on Spaceport <{}> has been temporarily suspended due to low power supply.", getName());
    }

    private void resumeOperations() {
        currentOperationalState = preSuspendedOperationalState;
        logger.debug("Power supply has been restored to Spaceport <{}>. Cargo transfer operations will now resume.", getName());
    }

    @ObjectClass(name = "HLAobjectRoot.PhysicalInterface")
    private class SpaceportArm extends PhysicalInterface implements SimEntity {
        // Power load of the arm that is incurred during its operational stages in kilowatts (kW).
        private static final double IDLE_POWER_RATING = 0.435;
        private static final double PEAK_POWER_RATING = 2.000;
        private static final int CARGO_TRANSFER_TIME = 10;

        private boolean transferInProgress;
        private int cargoTransferStep;

        private SpaceportArm(String name, String parentName) {
            setName(name);
            setParentName(parentName);
        }

        private void start() {
            transferInProgress = true;
            cargoTransferStep = CARGO_TRANSFER_TIME;
        }

        private void stop() {
            transferInProgress = false;
            try {
                MSGCargoTransferComplete transferComplete = new MSGCargoTransferComplete();
                federate.sendInteraction(transferComplete);
                logger.debug("Spaceport arm <{}> has completed transferring cargo.", getName());
            } catch (FederateNotExecutionMember | InteractionParameterNotDefined | RestoreInProgress |
                     InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | RTIinternalError |
                     SaveInProgress e) {
                throw new IllegalStateException("Failed to dispatch cargo transfer complete notification.", e);
            }
        }

        @Override
        public void update() {
            if (transferInProgress && cargoTransferStep > 0) {
                cargoTransferStep--;
            }

            if (cargoTransferStep <= 0) {
                stop();
            }
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
