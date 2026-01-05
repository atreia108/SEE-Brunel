package org.see.brunel.spaceport;

import com.formdev.flatlaf.FlatLightLaf;
import hla.rti1516_2025.RtiConfiguration;
import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.brunel.models.interactions.FederateMessage;
import org.see.brunel.models.objects.PhysicalEntity;
import org.see.brunel.models.objects.ReferenceFrame;
import org.see.brunel.spaceport.ui.CreateMessageFrame;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

public class SpaceportFederate extends SEELateJoinerFederate {
    private static final File confFile = new File("src/main/resources/spaceport.conf");

    public SpaceportFederate(SEEFederateAmbassador federateAmbassador, FederateConfiguration config) {
        super(federateAmbassador, config);
        Runnable r = this::createAndShowGUI;
        SwingUtilities.invokeLater(r);
    }

    private void createAndShowGUI() {
        FlatLightLaf.setup();

        JFrame frame = new JFrame("Brunel Spaceport Command Center");
        frame.setSize(1600, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WindowListener closeWindow = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        };
        frame.addWindowListener(closeWindow);

        CreateMessageFrame createMessageFrame = new CreateMessageFrame(this);
        createMessageFrame.setVisible(true);

        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem newMessage = new JMenuItem("Create message");
        newMessage.addActionListener(e -> {
            createMessageFrame.setVisible(true);
        });
        // TODO - Clear all
    }

    @Override
    public void declareClasses() throws FederateNotExecutionMember, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, NameNotFound, NotConnected, RTIinternalError, InvalidObjectClassHandle, SaveInProgress, InvalidInteractionClassHandle, InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM {
        publishObjectClass(PhysicalEntity.class);
        subscribeObjectClass(PhysicalEntity.class);
        subscribeObjectClass(ReferenceFrame.class);
        publishInteractionClass(FederateMessage.class);
        subscribeInteractionClass(FederateMessage.class);
    }

    @Override
    public void declareObjectInstances() throws FederateNotExecutionMember, ObjectClassNotPublished, ObjectClassNotDefined, RestoreInProgress, ObjectInstanceNotKnown, IllegalName, ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, NotConnected, RTIinternalError, SaveInProgress {
        // No object instances to be declared.
    }

    @Override
    public void update() {
        // The spaceport is static, tasks are only performed in response to interactions and already handled via
        // interaction listeners.
    }

    public boolean dispatchMessage(String receiverName, String messageType, String content) {
        FederateMessage message = new FederateMessage("Spaceport", receiverName, messageType, content);
        try {
            sendInteraction(message);
            return true;
        } catch (FederateNotExecutionMember | NotConnected | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | RTIinternalError | SaveInProgress e) {
            return false;
        }
    }

    public static void main(String[] args) {
        FederateConfiguration federateConfig = FederateConfiguration.Factory.create(confFile);
        String rtiAddress = federateConfig.rtiAddress();
        RtiConfiguration rtiConfig = RtiConfiguration.createConfiguration().withRtiAddress(rtiAddress);

        SpaceportFederate spaceportFederate = new SpaceportFederate(new SEEFederateAmbassador(), federateConfig);
        spaceportFederate.configureAndStart();
    }
}
