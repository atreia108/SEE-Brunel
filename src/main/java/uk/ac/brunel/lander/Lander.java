package uk.ac.brunel.lander;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.types.SpaceTimeCoordinateState;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.core.SimulationEntity;

public class Lander extends PhysicalEntity implements SimulationEntity {
    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;

    private final LanderFSM operationalState;
    private final SKBaseFederate federate;
    private final Vector3D spawnPoint;

    private Lander(Builder builder) {
        federate = builder.federate;
        operationalState = LanderFSM.APPROACHING;
        spawnPoint = builder.state.getPosition();

        initMetadata(builder);
        createEventListeners();
    }

    private void initMetadata(Builder builder) {
        setName(builder.name);
        setStatus(operationalState.toString());
        setType("Lander");
        setParentReferenceFrame(builder.parentReferenceFrame);
        setState(builder.state);

        setAcceleration(Vector3D.of(0, 0, LUNAR_GRAVITATIONAL_PULL));
    }

    private void createEventListeners() {

    }

    @Override
    public void update() {

    }

    public static class Builder {
        private SKBaseFederate federate;
        private String name;
        private String parentReferenceFrame;
        private SpaceTimeCoordinateState state;

        public Builder federate(LanderFederate federate) {
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

        private void validate() {
            if (federate == null) {
                throw new IncompleteObjectDataException("Missing field <federate> for Lander object.");
            }

            if (name == null) {
                throw new IncompleteObjectDataException("Missing field <name> for Lander object.");
            }

            if (parentReferenceFrame == null) {
                throw new IncompleteObjectDataException("Missing field <parentReferenceFrame> for Lander object.");
            }

            if (state == null) {
                throw new IncompleteObjectDataException("Missing field <spawnPoint> for Lander object.");
            }
        }

        public Lander build() {
            validate();
            return new Lander(this);
        }
    }
}
