package uk.ac.brunel.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.federates.LanderFederate;
import uk.ac.brunel.federates.SpaceportFederate;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhysicalEntityBuilderTest {
    private static FederateConfiguration config;

    @BeforeAll
    static void setupConfig() {
        File confFile = new File("src/main/resources/lander.conf");
        config = FederateConfiguration.Factory.create(confFile);
    }

    @Test
    void createLanderTest() {
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
        assertThrows(IncompleteObjectDataException.class, () -> {
            new Lander.Builder().build();
        });

        /* Name field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> {
            new Lander.Builder()
                    .federate(new LanderFederate(new SEEFederateAmbassador(), config))
                    .parentReferenceFrame("AitkenLocalBasinFixed")
                    .spaceTimeCoordinateState(new SpaceTimeCoordinateState())
                    .build();
        });

        /* Federate field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> {
            new Lander.Builder()
                    .name("Brunel_Lander")
                    .parentReferenceFrame("AitkenLocalBasinFixed")
                    .spaceTimeCoordinateState(new SpaceTimeCoordinateState())
                    .build();
        });

        /* SpaceTimeCoordinateState field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> {
            new Lander.Builder()
                    .name("Brunel_Lander")
                    .federate(new LanderFederate(new SEEFederateAmbassador(), config))
                    .parentReferenceFrame("AitkenLocalBasinFixed")
                    .build();
        });

        /* All fields are present */
        assertDoesNotThrow(() -> {
            new Lander.Builder()
                    .name("Brunel_Lander")
                    .federate(new LanderFederate(new SEEFederateAmbassador(), config))
                    .parentReferenceFrame("AitkenBasinLocalFixed")
                    .spaceTimeCoordinateState(new SpaceTimeCoordinateState())
                    .build();
        });
    }

    @Test
    void createSpaceportTest() {
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
        assertThrows(IncompleteObjectDataException.class, () -> {
            new Spaceport.Builder().build();
        });

        /* Name field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> {
            new Spaceport.Builder()
                    .federate(new SpaceportFederate(new SEEFederateAmbassador(), config))
                    .parentReferenceFrame("AitkenLocalBasinFixed")
                    .spaceTimeCoordinateState(new SpaceTimeCoordinateState())
                    .build();
        });

        /* Federate field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> {
            new Spaceport.Builder()
                    .name("Brunel_Spaceport")
                    .parentReferenceFrame("AitkenLocalBasinFixed")
                    .spaceTimeCoordinateState(new SpaceTimeCoordinateState())
                    .build();
        });

        /* SpaceTimeCoordinateState field is missing */
        assertThrows(IncompleteObjectDataException.class, () -> {
            new Spaceport.Builder()
                    .name("Brunel_Spaceport")
                    .federate(new SpaceportFederate(new SEEFederateAmbassador(), config))
                    .parentReferenceFrame("AitkenLocalBasinFixed")
                    .build();
        });

        /* All fields are present */
        assertDoesNotThrow(() -> {
            new Spaceport.Builder()
                    .name("Brunel_Spaceport")
                    .federate(new SpaceportFederate(new SEEFederateAmbassador(), config))
                    .parentReferenceFrame("AitkenBasinLocalFixed")
                    .spaceTimeCoordinateState(new SpaceTimeCoordinateState())
                    .build();
        });
    }
}
