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
import uk.ac.brunel.federates.SpaceportFederate;
import uk.ac.brunel.interactions.MSGCargoPickupJob;
import uk.ac.brunel.interactions.MSGCargoTransferComplete;
import uk.ac.brunel.interactions.MSGLandingPermission;
import uk.ac.brunel.interactions.UCFPowerRequest;
import uk.ac.brunel.listeners.*;
import uk.ac.brunel.types.CargoType;
import uk.ac.brunel.types.OperationalVerdict;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

/**
 * Simulation model of a Lunar Spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class Spaceport extends PropertyChangeSubject implements SimEntity {
    private static final Logger logger = LoggerFactory.getLogger(Spaceport.class);

    // Power load of the spaceport that is incurred during its operational stages in kilowatts (kW).
    private static final double POWER_RATING = 0.092;
    private static final short IDLE_LEVEL_PRIORY = 5;
    private static final short PEAK_LEVEL_PRIORITY = 0;
    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;

    private static final short COOLDOWN_TIME_LIMIT = 10;
    private static final Vector3D WAREHOUSE_COORDINATES = Vector3D.of(801, 3053, -5532);

    private SpaceportFederate federate;
    private SpaceportArm arm;

    private double allocatedPower;
    private String occupyingLander;

    private String assignedRover;

    private short cargoRequestCooldownTimer;
    private short powerRequestCooldownTimer;
    private short departureCooldownTimer;
    private OperationalState preSuspendedOperationalState;
    private OperationalState currentOperationalState;

    /* SpaceFOM PhysicalObject fields exposed to the RTI. */
    @Attribute(name = "name", coder = HLAunicodeStringCoder.class)
    private String name;

    @Attribute(name = "type", coder = HLAunicodeStringCoder.class)
    private String type;

    @Attribute(name = "status", coder = HLAunicodeStringCoder.class)
    private String status;

    @Attribute(name = "parent_reference_frame", coder = HLAunicodeStringCoder.class)
    private String parentReferenceFrame;

    @Attribute(name = "state", coder = SpaceTimeCoordinateStateCoder.class)
    private SpaceTimeCoordinateState state;

    @Attribute(name = "acceleration", coder = Vector3DCoder.class)
    private Vector3D acceleration;

    public Spaceport() {
        name = "";
        type = "";
        status = "";
        parentReferenceFrame = "";
        state = new SpaceTimeCoordinateState();
        acceleration = Vector3D.of(0, 0, 0);
    }

    public Spaceport(String spaceportName, SpaceTimeCoordinateState spaceportState, SpaceportFederate spaceportFederate, String armName) {
        this();

        this.federate = spaceportFederate;
        initSpaceportMetadata(spaceportName, spaceportState);
        createListeners();

        arm = new SpaceportArm(armName, name);
    }

    private void initSpaceportMetadata(String spName, SpaceTimeCoordinateState spState) {
        name = spName;
        status = "Available";
        type = "Spaceport";
        parentReferenceFrame = "AitkenBasinLocalFixed";
        state = spState;
        acceleration = Vector3D.of(0, 0, LUNAR_GRAVITATIONAL_PULL);

        assignedRover = "";
        occupyingLander = "";
        allocatedPower = 10.0;
        powerRequestCooldownTimer = 0;
        currentOperationalState = preSuspendedOperationalState = OperationalState.AWAITING_PICKUP_ROVER_ALLOCATION;
        departureCooldownTimer = 0;
    }

    private void createListeners() {
        federate.addInteractionListener(new LandingRequestListener(this));
        federate.addInteractionListener(new PowerAllocationListener(this));
        federate.addInteractionListener(new TouchdownListener(this));
        federate.addInteractionListener(new CargoTransferReadyListener(this));
        federate.addInteractionListener(new RoverAllocationListener(this));
    }

    public synchronized void processLandingRequest(String landerName) {
        OperationalVerdict verdict;

        if (currentOperationalState == OperationalState.AVAILABLE && occupyingLander.isEmpty()) {
            occupyingLander = landerName;
            verdict = OperationalVerdict.ACCEPTED;
            nextState();
        } else {
            verdict = OperationalVerdict.REJECTED;
        }

        try {
            MSGLandingPermission permission = new MSGLandingPermission(name, landerName, verdict);
            federate.sendInteraction(permission);
        } catch (FederateNotExecutionMember | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | RTIinternalError |
                 SaveInProgress e) {
            throw new IllegalStateException("Failed to dispatch landing permission REJECT notification.", e);
        }
    }

    public synchronized void onLanderTouchDown(String landerName) {
        if (landerName.equals(occupyingLander)) {
            nextState();
        }
    }

    public synchronized void onPowerAllocation(double amount) {
        allocatedPower = amount;
        logger.debug("Allocated {} kW of power", amount);

        if (amount < powerConsumption()) {
            powerRequestCooldownTimer = COOLDOWN_TIME_LIMIT;
        }
    }

    public synchronized void onCargoPickupJobAccepted(String roverName) {
        assignedRover = roverName;
        nextState();
    }

    public synchronized void initiateCargoTransfer() {
        arm.start();
        nextState();
    }

    public synchronized void landerTakeoff() {

    }

    @Override
    public void update() {
        if (currentOperationalState == OperationalState.SUSPENDED && isServicePowerRequirementSatisfied()) {
            resumeOperations();
        } else if (!powerEmbargoInEffect()) {
            requestPower();
        }

        if (currentOperationalState == OperationalState.AWAITING_PICKUP_ROVER_DEPARTURE
                || currentOperationalState == OperationalState.AWAITING_DELIVERY_ROVER_DEPARTURE) {
            if (departureCooldownTimer < 1) {
                nextState();
            } else {
                departureCooldownTimer--;
            }

            return;
        }

        if (currentOperationalState == OperationalState.LOADING_CARGO || currentOperationalState == OperationalState.UNLOADING_CARGO) {
            if (!isServicePowerRequirementSatisfied()) {
                suspendOperations();
            } else {
                operateArm();
            }

            return;
        } else if (currentOperationalState == OperationalState.AWAITING_PICKUP_ROVER_ALLOCATION && cargoRequestLimiterInEffect()) {
            requestCargoPickup(0);
        } else if (currentOperationalState == OperationalState.AWAITING_DELIVERY_ROVER_ALLOCATION && cargoRequestLimiterInEffect()) {
            requestCargoPickup(1);
        }

        allocatedPower = 10.0;
    }

    private void operateArm() {
        if (arm.transferInProgress) {
            arm.update();
        }
    }

    private boolean powerEmbargoInEffect() {
        if (powerRequestCooldownTimer > 0) {
            powerRequestCooldownTimer--;
            return true;
        } else {
            return false;
        }
    }

    private boolean cargoRequestLimiterInEffect() {
        if (cargoRequestCooldownTimer > 0) {
            cargoRequestCooldownTimer--;
            return false;
        } else {
            return true;
        }
    }

    private void requestCargoPickup(int type) {
        String resourceName = CargoType.randomType().name();

        try {

            MSGCargoPickupJob pickupJob = createCargoPickupJob(type, resourceName);
            cargoRequestCooldownTimer = COOLDOWN_TIME_LIMIT;

            federate.sendInteraction(pickupJob);
        } catch (FederateNotExecutionMember | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | RTIinternalError |
                 SaveInProgress e) {
            throw new IllegalStateException("Failed to dispatch cargo pickup request.", e);
        }
    }

    private MSGCargoPickupJob createCargoPickupJob(int type, String resourceName) {
        MSGCargoPickupJob pickupJob = new MSGCargoPickupJob();
        pickupJob.setRequestingObject(name);
        pickupJob.setCargoType(resourceName);

        if (type == 0) {
            pickupJob.setPickupLocation(state.getPosition());
            pickupJob.setDeliveryLocation(WAREHOUSE_COORDINATES);
        } else {
            pickupJob.setPickupLocation(WAREHOUSE_COORDINATES);
            pickupJob.setDeliveryLocation(state.getPosition());
        }
        return pickupJob;
    }

    private void requestPower() {
        double powerRequirement = powerConsumption();

        try {
            int priority;

            if (currentOperationalState ==  OperationalState.LOADING_CARGO || currentOperationalState == OperationalState.UNLOADING_CARGO) {
                priority = IDLE_LEVEL_PRIORY;
            } else {
                priority = PEAK_LEVEL_PRIORITY;
            }

            UCFPowerRequest powerRequest = new UCFPowerRequest(getName(), powerRequirement, priority);
            federate.sendInteraction(powerRequest);

            powerRequestCooldownTimer = COOLDOWN_TIME_LIMIT;
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
                departureCooldownTimer = COOLDOWN_TIME_LIMIT;
                break;
            case AWAITING_PICKUP_ROVER_DEPARTURE:
                newStatus = "Awaiting Delivery Rover Allocation";
                assignedRover = "";
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
                departureCooldownTimer = COOLDOWN_TIME_LIMIT;
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
        logger.debug("Spaceport <{}> status: \"{}\"", getName(), newStatus);
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
            super.setName(name);
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
                logger.debug("Spaceport arm <{}> has completed transferring cargo.", name);

                nextState();
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

    public Object getArmObject() {
        return arm;
    }

    public String getOccupyingLander() {
        return occupyingLander;
    }

    public String getAssignedRover() {
        return assignedRover;
    }
}
