package uk.ac.brunel.models;

import org.see.skf.annotations.ObjectClass;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.federates.LanderFederate;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

@ObjectClass(name = "HLAobjectRoot.PhysicalEntity.DynamicalEntity")
public class Lander extends DynamicalEntity {
    private final LanderFederate federate;
    private FlightStage flightStage;

    public Lander(Builder builder) {
        this.federate = builder.ldFederate;

        setName(builder.ldName);
        setParentReferenceFrame(builder.ldParentReferenceFrame);
        setState(builder.ldState);

        flightStage = FlightStage.APPROACHING;
    }

    private enum FlightStage {
        APPROACHING,
        LANDING,
        ASCENT,
        IDLE
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
