package uk.ac.brunel.models;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LanderTest {
    private static final File confFile = new File("src/main/resources/lander.conf");

    // RNG bounds per axis.
    private static final double RNG_MIN_X_BOUND = -550.0;
    private static final double RNG_MAX_X_BOUND = 650.0;
    private static final double RNG_MIN_Y_BOUND = -350.0;
    private static final double RNG_MAX_Y_BOUND = 500.0;
    private static final double RNG_MIN_Z_BOUND = -5150.0;
    private static final double RNG_MAX_Z_BOUND = -4850.0;

    @Disabled
    @Test
    void waypointCreationTest() {
        Lander lander = createTestLander();

        Vector3D spawnPoint1 = lander.holdingWaypointGenerator.get();
        boolean x1BoundsSatisfied = spawnPoint1.getX() >= RNG_MIN_X_BOUND && spawnPoint1.getX() <= RNG_MAX_X_BOUND;
        boolean y1BoundsSatisfied = spawnPoint1.getY() >= RNG_MIN_Y_BOUND && spawnPoint1.getY() <= RNG_MAX_Y_BOUND;
        boolean z1BoundsSatisfied = spawnPoint1.getZ() >= RNG_MIN_Z_BOUND && spawnPoint1.getZ() <= RNG_MAX_Z_BOUND;

        assertTrue(x1BoundsSatisfied);
        assertTrue(y1BoundsSatisfied);
        assertTrue(z1BoundsSatisfied);

        Vector3D spawnPoint2 = lander.holdingWaypointGenerator.get();
        boolean x2BoundsExceeded = spawnPoint2.getX() < -550 || spawnPoint2.getX() > 650;
        boolean y2BoundsExceeded = spawnPoint2.getY() < -350 || spawnPoint2.getY() > 500;
        boolean z2BoundsExceeded = spawnPoint2.getZ() < -5150 || spawnPoint2.getZ() > -4850;

        assertFalse(x2BoundsExceeded);
        assertFalse(y2BoundsExceeded);
        assertFalse(z2BoundsExceeded);
    }

    Lander createTestLander() {
        FederateConfiguration config = FederateConfiguration.Factory.create(confFile);
        LanderFederate federate = new LanderFederate(new SEEFederateAmbassador(), config);
        return new Lander("Brunel_Lander", federate);
    }
}
