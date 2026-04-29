package uk.ac.brunel.spaceport;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.spaceport.systems.CargoTransferSystem;
import uk.ac.brunel.spaceport.systems.PowerSystem;
import uk.ac.brunel.spaceport.systems.VehicleAssignmentRequestSystem;
import uk.ac.brunel.types.SpaceTimeCoordinateState;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.core.SimulationEntity;

public class Spaceport extends PhysicalEntity implements SimulationEntity, Powerable {
    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;

    // Power load of the spaceport that is incurred during its operational stages in kilowatts (kW).
    private static final double POWER_RATING = 0.092;

    private static final int LOW_PRIORITY_POWER_REQUEST  = 5;
    private static final int HIGH_PRIORITY_POWER_REQUEST = 0;

    private final SpaceportFSM spaceportStateMachine;
    private final SKBaseFederate federate;
    private final SpaceportArm arm;

    private final PowerSystem powerSystem;
    private final CargoTransferSystem cargoTransferSystem;
    private final VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem;

    private Spaceport(Builder builder) {
        federate = builder.federate;
        spaceportStateMachine = new SpaceportFSM();
        powerSystem = new PowerSystem(getName(), this, federate);
        arm = new SpaceportArm(builder.armName, powerSystem);

        cargoTransferSystem = new CargoTransferSystem(getName(), arm, federate);
        vehicleAssignmentRequestSystem = new VehicleAssignmentRequestSystem(getName(), getState().getPosition(), cargoTransferSystem, federate);

        initMetadata(builder);
    }

    private void initMetadata(Builder builder) {
        setName(builder.name);
        setType("Spaceport");
        setStatus(spaceportStateMachine.currentStateName());
        setParentReferenceFrame(builder.parentReferenceFrame);
        setState(builder.state);

        Vector3D accel = Vector3D.of(0, 0, LUNAR_GRAVITATIONAL_PULL);
        setAcceleration(accel);
    }

    @Override
    public void update() {
        powerSystem.consume(POWER_RATING);

        vehicleAssignmentRequestSystem.exec();
        cargoTransferSystem.exec();
        arm.update();

        powerSystem.exec();
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public double powerConsumption() {
        return spaceportStateMachine.current == OperationalState.LOADING_CARGO || spaceportStateMachine.current == OperationalState.UNLOADING_CARGO
                ? POWER_RATING + SpaceportArm.IDLE_POWER_RATING
                : POWER_RATING + SpaceportArm.PEAK_POWER_RATING;
    }

    @Override
    public int powerPriorityLevel() {
        return spaceportStateMachine.current == OperationalState.LOADING_CARGO || spaceportStateMachine.current == OperationalState.UNLOADING_CARGO
                ? HIGH_PRIORITY_POWER_REQUEST
                : LOW_PRIORITY_POWER_REQUEST;
    }

    public static class Builder {
        private SKBaseFederate federate;
        private String name;
        private String parentReferenceFrame;
        private SpaceTimeCoordinateState state;
        private String armName;

        public Builder federate(SpaceportFederate federate) {
            this.federate = federate;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parentReferenceFrame(String parentReferenceFrame) {
            this.parentReferenceFrame = parentReferenceFrame;
            return this;
        }

        public Builder spawnPoint(SpaceTimeCoordinateState state) {
            this.state = state;
            return this;
        }

        public Builder arm(String name) {
            this.armName = name;
            return this;
        }

        private void validate() {
            if (federate == null) {
                throw new IncompleteObjectDataException("Missing <federate> field for Spaceport object.");
            }

            if (name == null) {
                throw new IncompleteObjectDataException("Missing <name> field for Spaceport object.");
            }

            if (parentReferenceFrame == null) {
                throw new IncompleteObjectDataException("Missing <parentReferenceFrame> field for Spaceport object.");
            }

            if (state == null) {
                throw new IncompleteObjectDataException("Missing <spawnPoint> field for Spaceport object.");
            }

            if (armName == null) {
                throw new IncompleteObjectDataException("Missing <armName> field for Spaceport object.");
            }
        }

        public Spaceport build() {
            validate();
            return new Spaceport(this);
        }
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
        // SUSPENDED
    }

    private class SpaceportFSM {
        // private OperationalState previous;
        private OperationalState current;

        public SpaceportFSM() {
            current = OperationalState.AVAILABLE;
            // previous = OperationalState.AVAILABLE;
        }

        public synchronized void next() {
            switch (current) {
                case AVAILABLE -> current = OperationalState.AWAITING_LANDER_TOUCHDOWN;
                case AWAITING_LANDER_TOUCHDOWN -> current = OperationalState.AWAITING_PICKUP_ROVER_ALLOCATION;
                case AWAITING_PICKUP_ROVER_ALLOCATION -> current = OperationalState.AWAITING_PICKUP_ROVER_ARRIVAL;
                case AWAITING_PICKUP_ROVER_ARRIVAL -> current = OperationalState.UNLOADING_CARGO;
                case UNLOADING_CARGO -> current = OperationalState.AWAITING_PICKUP_ROVER_DEPARTURE;
                case AWAITING_PICKUP_ROVER_DEPARTURE -> current = OperationalState.AWAITING_DELIVERY_ROVER_ALLOCATION;
                case AWAITING_DELIVERY_ROVER_ALLOCATION -> current = OperationalState.AWAITING_DELIVERY_ROVER_ARRIVAL;
                case AWAITING_DELIVERY_ROVER_ARRIVAL -> current = OperationalState.LOADING_CARGO;
                case LOADING_CARGO -> current = OperationalState.AWAITING_DELIVERY_ROVER_DEPARTURE;
                case AWAITING_DELIVERY_ROVER_DEPARTURE -> current = OperationalState.HOSTING_LANDER_TAKEOFF;
                // case SUSPENDED -> resume();
                default -> current = OperationalState.AVAILABLE;
            }

            setStatus(spaceportStateMachine.currentStateName());
            federate.updateObjectInstance(this);
        }

        /*
        public synchronized void suspend() {
            if (current != OperationalState.SUSPENDED) {
                previous = current;
                current = OperationalState.SUSPENDED;
            }
        }

        private void resume() {
            if (current == OperationalState.SUSPENDED) {
                current = previous;
                previous = OperationalState.SUSPENDED;
            }
        }
         */

        public String currentStateName() {
            return switch (current) {
                case AVAILABLE -> "Available";
                case AWAITING_LANDER_TOUCHDOWN -> "Hosting Lander Arrival";
                case AWAITING_PICKUP_ROVER_ALLOCATION -> "Arranging Vehicle for Cargo Pickup";
                case AWAITING_PICKUP_ROVER_ARRIVAL -> "Cargo Pickup Vehicle en route";
                case UNLOADING_CARGO -> "Unloading Cargo";
                case AWAITING_PICKUP_ROVER_DEPARTURE, AWAITING_DELIVERY_ROVER_DEPARTURE -> "Awaiting Cargo Vehicle Departure";
                case AWAITING_DELIVERY_ROVER_ALLOCATION -> "Arranging Vehicle for Cargo Delivery";
                case AWAITING_DELIVERY_ROVER_ARRIVAL -> "Cargo Delivery Vehicle en route";
                case LOADING_CARGO -> "Loading Cargo";
                case HOSTING_LANDER_TAKEOFF -> "Hosting Lander Takeoff";
                // case SUSPENDED -> "Suspended due to Low Power Supply";
            };
        }
    }

    public PowerSystem getPowerSystem() {
        return powerSystem;
    }

    public SpaceportArm getArm() {
        return arm;
    }
}
