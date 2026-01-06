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
                .withStatus("Holding")
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
