package uk.ac.brunel.lander;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for generating Lander objects.
 *
 * @author Hridyanshu Aatreya
 */
class LanderBuilderTest {
    private static LanderFederate federate;

    @BeforeAll
    static void setupConfig() {
        File confFile = new File("src/main/resources/lander.conf");
        FederateConfiguration config = FederateConfiguration.Factory.create(confFile);
        federate = new LanderFederate(new SEEFederateAmbassador(), config);
    }

    @Test
    void landerBuilderTest() {
        /* A lander requires the following fields to be supplied at the time of creation:
         * (1) Name
         * (2) Associated federate
         * (3) Parent reference frame
         * (4) SpaceTimeCoordinateState
         *
         * The absence of any one of them will result in a program exception. The following
         * tests will attempt a combination of valid and invalid cases with each of these fields
         * to ensure that a lander object from its builder object is initialized correctly.
         */

        /* All fields missing */
        assertThrows(IncompleteObjectDataException.class, () -> new Lander.Builder().build());

        /* Name field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> new Lander.Builder()
                .federate(federate)
                .parentReferenceFrame("AitkenLocalBasinFixed")
                .build());

        /* Federate field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> new Lander.Builder()
                .name("Brunel_Lander")
                .parentReferenceFrame("AitkenLocalBasinFixed")
                .build());

        /* Spawn point field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> new Lander.Builder()
                .name("Brunel_Lander")
                .federate(federate)
                .parentReferenceFrame("AitkenLocalBasinFixed")
                .build());

        /* All fields are present */
        assertDoesNotThrow(() ->
                new Lander.Builder()
                        .name("Brunel_Lander")
                        .federate(federate)
                        .parentReferenceFrame("AitkenBasinLocalFixed")
                        .spawnPoint(new SpaceTimeCoordinateState())
                        .build()
        );
    }
}
