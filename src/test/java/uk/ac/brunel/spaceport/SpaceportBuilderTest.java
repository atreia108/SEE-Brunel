package uk.ac.brunel.spaceport;

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
 * Tests for generating spaceport objects.
 *
 * @author Hridyanshu Aatreya
 */
class SpaceportBuilderTest {
    private static SpaceportFederate federate;

    @BeforeAll
    static void setupConfig() {
        File confFile = new File("src/main/resources/lander.conf");
        FederateConfiguration config = FederateConfiguration.Factory.create(confFile);
        federate = new SpaceportFederate(new SEEFederateAmbassador(), config);
    }

    @Test
    void spaceportBuilderTest() {
        /* A Spaceport requires the following fields to be supplied at the time of creation:
         * (1) Name
         * (2) Associated federate
         * (3) Parent reference frame
         * (4) SpaceTimeCoordinateState
         *
         * The absence of any one of them will result in a program exception. The following
         * tests will attempt a combination of valid and invalid cases with each of these fields
         * to ensure that a spaceport object from its builder object is initialized correctly.
         */

        /* All fields missing */
        assertThrows(IncompleteObjectDataException.class, () -> new Spaceport.Builder().build());

        /* Name field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> new Spaceport.Builder()
                .federate(federate)
                .parentReferenceFrame("AitkenLocalBasinFixed")
                .spawnPoint(new SpaceTimeCoordinateState())
                .build());

        /* Federate field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> new Spaceport.Builder()
                .name("Brunel_Spaceport")
                .parentReferenceFrame("AitkenLocalBasinFixed")
                .spawnPoint(new SpaceTimeCoordinateState())
                .build());

        /* SpaceTimeCoordinateState field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> new Spaceport.Builder()
                .name("Brunel_Spaceport")
                .federate(federate)
                .parentReferenceFrame("AitkenLocalBasinFixed")
                .build());

        /* Arm name field is missing */
        assertThrows(IncompleteObjectDataException.class, () ->
                new Spaceport.Builder()
                        .name("Brunel_Spaceport")
                        .federate(federate)
                        .parentReferenceFrame("AitkenBasinLocalFixed")
                        .spawnPoint(new SpaceTimeCoordinateState())
                        .build()
        );

        /* All fields are present */
        assertDoesNotThrow(() ->
                new Spaceport.Builder()
                        .name("Brunel_Spaceport")
                        .federate(federate)
                        .parentReferenceFrame("AitkenBasinLocalFixed")
                        .spawnPoint(new SpaceTimeCoordinateState())
                        .arm("Brunel_Spaceport_Arm")
                        .build()
        );
    }
}
