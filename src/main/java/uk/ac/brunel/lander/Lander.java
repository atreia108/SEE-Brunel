package uk.ac.brunel.lander;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.annotations.ObjectClass;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.lander.listeners.LanderDepartureRequestListener;
import uk.ac.brunel.lander.listeners.LandingPermissionListener;
import uk.ac.brunel.lander.systems.NavigationSystem;
import uk.ac.brunel.lander.systems.SpaceportAllocationRequestSystem;
import uk.ac.brunel.spaceport.SpaceportFederate;
import uk.ac.brunel.types.SpaceTimeCoordinateState;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.core.SimulationEntity;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class Lander extends PhysicalEntity implements SimulationEntity {
    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;

    private final SKBaseFederate federate;
    private final Vector3D spawnPoint;
    private final AtomicInteger operatingMode;

    private final Set<PhysicalEntity> spaceports;

    private final SpaceportAllocationRequestSystem spaceportAllocationRequestSystem;
    private final NavigationSystem navigationSystem;

    private Lander(Builder builder) {
        setName(builder.name);
        setType("Lander");
        setStatus("Approaching");
        setParentReferenceFrame(builder.parentReferenceFrame);
        setState(builder.state);

        setAcceleration(Vector3D.of(0, 0, LUNAR_GRAVITATIONAL_PULL));

        federate = builder.federate;
        spawnPoint = Vector3D.of(builder.state.getPosition().getX(), builder.state.getPosition().getY(), builder.state.getPosition().getZ());
        operatingMode = new AtomicInteger(0);
        spaceports = new CopyOnWriteArraySet<>();

        spaceportAllocationRequestSystem = new SpaceportAllocationRequestSystem(this, spaceports, federate);
        navigationSystem = new NavigationSystem(this, spaceports, spaceportAllocationRequestSystem, federate);

        createEventListeners();
    }

    private void createEventListeners() {
        federate.addInteractionListener(new LandingPermissionListener(this, federate));
        federate.addInteractionListener(new LanderDepartureRequestListener(this));
    }

    @Override
    public void update() {
        // WARNING: The spaceport federate must be running FIRST with the spaceport object(s) created. Otherwise, lander
        // creation will result in a nasty NullPointerException.
        if (spaceports.isEmpty()) {
            queryRemoteSpaceportInstances();
        }

        spaceportAllocationRequestSystem.exec();
        navigationSystem.exec();
    }

    private void queryRemoteSpaceportInstances() {
        String spaceportNameSequence = "brunel_spaceport_";

        for (int i = 1; i < SpaceportFederate.SPACEPORT_COUNT + 1; ++i) {
            Object entity = federate.queryRemoteObjectInstance(spaceportNameSequence + i);
            PhysicalEntity spaceport = (PhysicalEntity) entity;
            spaceports.add(spaceport);
        }
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

    public AtomicInteger getOperatingMode() {
        return operatingMode;
    }

    public Vector3D getSpawnPoint() {
        return spawnPoint;
    }

    public void flipOperatingMode() {
        operatingMode.set( (operatingMode.get() == 0) ? 1 : 0 );
    }

    public SpaceportAllocationRequestSystem getSpaceportAllocationRequestSystem() {
        return spaceportAllocationRequestSystem;
    }

    public NavigationSystem getNavigationSystem() {
        return navigationSystem;
    }
}
