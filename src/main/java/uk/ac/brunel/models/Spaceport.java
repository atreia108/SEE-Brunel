package uk.ac.brunel.models;

import org.see.skf.annotations.ObjectClass;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.federates.SpaceportFederate;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

/**
 * Simulation model of a Lunar Spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class Spaceport extends PhysicalEntity {
    private final SpaceportFederate federate;
    private OperationalState operationalState;

    private Spaceport(Builder builder) {
        this.federate = builder.spFederate;

        setName(builder.spName);
        setParentReferenceFrame(builder.spParentReferenceFrame);
        setState(builder.spState);

        operationalState = OperationalState.IDLE;
    }

    public synchronized void acceptLander() {
        // ...
        // If it's a no-go, send an interaction back DENYING passage.
        operationalState = OperationalState.AWAITING_LANDER_TOUCHDOWN;
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
        IDLE,
        AWAITING_ROVER_ALLOCATION,
        AWAITING_ROVER_ARRIVAL,
        PROCESSING_CARGO,
        AWAITING_LANDER_TOUCHDOWN,
        AWAITING_LANDER_DEPARTURE,
        REFUELING
    }
}
