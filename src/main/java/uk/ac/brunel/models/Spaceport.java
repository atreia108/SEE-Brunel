package uk.ac.brunel.models;

import org.see.skf.annotations.ObjectClass;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.federates.SpaceportFederate;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class Spaceport extends PhysicalEntity {
    private final SpaceportFederate federate;

    private Spaceport(Builder builder) {
        this.federate = builder.spFederate;

        setName(builder.spName);
        setParentReferenceFrame(builder.spParentReferenceFrame);
        setState(builder.spState);
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
}
