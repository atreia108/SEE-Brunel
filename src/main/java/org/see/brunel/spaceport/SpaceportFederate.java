/*-
 * Copyright (c) 2026 Hridyanshu Aatreya
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package org.see.brunel.spaceport;

import com.formdev.flatlaf.FlatLightLaf;
import hla.rti1516_2025.exceptions.*;
import org.see.brunel.models.interactions.FederateMessage;
import org.see.brunel.models.objects.PhysicalEntity;
import org.see.brunel.models.objects.Spaceport;
import org.see.brunel.spaceport.ui.CreateMessageFrame;
import org.see.brunel.spaceport.ui.MessageInteractionTable;
import org.see.skf.conf.FederateConfiguration;
import org.see.skf.core.InteractionListener;
import org.see.skf.core.SEEFederateAmbassador;
import org.see.skf.core.SEELateJoinerFederate;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

public class SpaceportFederate extends SEELateJoinerFederate {
    private static final File confFile = new File("src/main/resources/spaceport.conf");

    private Spaceport spaceport;
    private MessageInteractionTable tablePane;

    public SpaceportFederate(SEEFederateAmbassador federateAmbassador, FederateConfiguration config) {
        super(federateAmbassador, config);
        init();

        Runnable r = this::createAndShowGUI;
        SwingUtilities.invokeLater(r);
    }

    private void init() {
        spaceport = new Spaceport(this);
        InteractionListener federateMessageListener = interaction -> {
            if (interaction instanceof FederateMessage) {
                FederateMessage federateMessage = (FederateMessage) interaction;
                if (!federateMessage.getSender().isEmpty() && !federateMessage.getReceiver().isEmpty() && !federateMessage.getMessageType().isEmpty() && !federateMessage.getContent().isEmpty()) {
                    String landerName = federateMessage.getSender();
                    String messageType = federateMessage.getMessageType();

                    if (messageType.equals("BRUNEL_LANDER_SPACEPORT_DEPARTURE_COMPLETED")) {
                        spaceport.evictLander(landerName);
                        tablePane.showNotification(landerName + " has departed from the platform.");
                    } else if (messageType.equals("BRUNEL_LANDER_SPACEPORT_TOUCHDOWN")) {
                        tablePane.showNotification(landerName + " has landed on the platform.");
                    } else {
                        tablePane.getModel().addRow(landerName, messageType, federateMessage.getContent());
                    }
                }
            }
        };

        addInteractionListener(federateMessageListener);
    }

    private void createAndShowGUI() {
        FlatLightLaf.setup();

        JFrame frame = new JFrame("Brunel Spaceport Command Center");
        frame.setSize(1600, 900);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        tablePane = new MessageInteractionTable(spaceport);
        tablePane.setOpaque(true);

        WindowListener closeWindow = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        };
        frame.addWindowListener(closeWindow);

        CreateMessageFrame createMessageFrame = new CreateMessageFrame(spaceport);
        createMessageFrame.setVisible(false);

        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem newMessage = new JMenuItem("Create message");
        newMessage.addActionListener(e -> createMessageFrame.setVisible(true));

        JMenuItem clearAll = new JMenuItem("Clear messages");
        clearAll.addActionListener(e -> tablePane.getModel().removeAllRows());

        JMenuItem deallocLaunchPads = new JMenuItem("Deallocate launch pads");
        deallocLaunchPads.addActionListener(e -> spaceport.clearLaunchPads());

        optionsMenu.add(newMessage);
        optionsMenu.add(clearAll);
        optionsMenu.add(deallocLaunchPads);

        menuBar.add(optionsMenu);
        frame.setJMenuBar(menuBar);
        frame.setContentPane(tablePane);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
    }

    @Override
    public void declareClasses() throws FederateNotExecutionMember, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, NameNotFound, NotConnected, RTIinternalError, InvalidObjectClassHandle, SaveInProgress, InvalidInteractionClassHandle, InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM {
        publishObjectClass(PhysicalEntity.class);
        subscribeObjectClass(PhysicalEntity.class);
        publishInteractionClass(FederateMessage.class);
        subscribeInteractionClass(FederateMessage.class);
    }

    @Override
    public void declareObjectInstances() throws FederateNotExecutionMember, ObjectClassNotPublished, ObjectClassNotDefined, RestoreInProgress, ObjectInstanceNotKnown, IllegalName, ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, NotConnected, RTIinternalError, SaveInProgress {
        // The spaceport is not an object instance because it's stationary in DON, and only really processes
        // instructions via GUI; thus not warranting the need for a specific object instance. In short, the federate IS
        // the spaceport.
    }

    @Override
    public void update() {
        // Nothing to do here, tasks are already handled via an interaction listener.
    }

    public static void main(String[] args) {
        FederateConfiguration federateConfig = FederateConfiguration.Factory.create(confFile);
        SpaceportFederate spaceportFederate = new SpaceportFederate(new SEEFederateAmbassador(), federateConfig);
        spaceportFederate.configureAndStart();
    }
}
