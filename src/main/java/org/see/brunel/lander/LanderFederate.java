package org.see.brunel.lander;

import hla.rti1516_2025.exceptions.*;
import org.see.brunel.models.interactions.FederateMessage;
import org.see.brunel.models.objects.Lander;
import org.see.brunel.models.objects.PhysicalEntity;
import org.see.brunel.models.objects.ReferenceFrame;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.InteractionListener;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;

import java.io.File;

public class LanderFederate extends SEELateJoinerFederate {
    private static final File confFile = new File("src/main/resources/lander.conf");

    private WaypointScheduler scheduler;
    private Lander lander;

    public LanderFederate(SEEFederateAmbassador federateAmbassador, FederateConfiguration federateConfiguration) {
        super(federateAmbassador, federateConfiguration);
        init();
    }

    private void init() {
        scheduler = new WaypointScheduler(this);
        lander = Lander.builder()
                .withName("Lander")
                .withType("Vehicle")
                .withStatus("Waiting")
                .withParentReferenceFrame("AitkenBasinLocalFixed")
                .withFederate(this)
                .build();
        scheduler.registerLander(lander);

        InteractionListener waypointListener = interaction -> {
            if (interaction instanceof FederateMessage) {
                FederateMessage message = (FederateMessage) interaction;
                if (message.getMessageType().equals("BRUNEL_SPACEPORT_LANDER_ARRIVAL_ACKNOWLEDGED")) {
                    // Message.receiver = Name of the lander.
                    // Message.content = Launch Pad ID (as a string)
                    scheduler.scheduleLanderArrival(message.getReceiver(), message.getContent());
                } else if (message.getMessageType().equals("BRUNEL_SPACEPORT_LANDER_REQUEST_DEPARTURE")) {
                    scheduler.scheduleLanderDeparture(message.getReceiver());
                }
            }
        };

        addInteractionListener(waypointListener);
    }

    @Override
    public void declareClasses() throws FederateNotExecutionMember, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, NameNotFound, NotConnected, RTIinternalError, InvalidObjectClassHandle, SaveInProgress, InvalidInteractionClassHandle, InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM {
        publishObjectClass(PhysicalEntity.class);
        subscribeObjectClass(ReferenceFrame.class);
        publishInteractionClass(FederateMessage.class);
        subscribeInteractionClass(FederateMessage.class);
    }

    @Override
    public void declareObjectInstances() throws FederateNotExecutionMember, ObjectClassNotPublished, ObjectClassNotDefined, RestoreInProgress, ObjectInstanceNotKnown, IllegalName, ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, NotConnected, RTIinternalError, SaveInProgress {
        createObjectInstance(lander, "Lander");
    }

    @Override
    public void update() {
        scheduler.process();

        if (lander.getMissionStage() == Lander.MissionStage.ARRIVAL || lander.getMissionStage() == Lander.MissionStage.DEPARTURE) {
            lander.move();
        }
    }

    public static void main(String[] args) {
        FederateConfiguration federateConfig = FederateConfiguration.Factory.create(confFile);
        LanderFederate landerFederate = new LanderFederate(new SEEFederateAmbassador(), federateConfig);
        landerFederate.configureAndStart();
    }
}
