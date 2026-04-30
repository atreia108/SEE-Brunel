/*-
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Brunel University of London
 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * 	  contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
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

package uk.ac.brunel.spaceport.systems;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.interactions.MSGCargoTransferComplete;
import uk.ac.brunel.spaceport.LanderLiaison;
import uk.ac.brunel.spaceport.Spaceport;
import uk.ac.brunel.spaceport.SpaceportArm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages the transfer of cargo on/off the spaceport via the onboard arm.
 *
 * @author Hridyanshu Aatreya
 */
public class CargoTransferSystem extends AbstractSimulationSystem {
    private static final Logger logger = LoggerFactory.getLogger(CargoTransferSystem.class);

    private final Spaceport spaceport;
    private final String spaceportName;
    private final SpaceportArm arm;
    private final LanderLiaison landerLiaison;
    private final SKBaseFederate federate;

    private final AtomicInteger operatingMode;
    private String assignedVehicle;

    public CargoTransferSystem(Spaceport spaceport, SpaceportArm arm, SKBaseFederate federate) {
        this.spaceport = spaceport;
        this.arm = arm;
        this.federate = federate;

        assignedVehicle = "";
        spaceportName = spaceport.getName();
        landerLiaison = spaceport.getLanderLiaison();
        operatingMode = spaceport.getOperatingMode();
    }

    @Override
    public void update() {
        if (!arm.isRunning() && !arm.cleanupPeriodActive()) {
            dispatchTransferCompletionNotification();
            postExecutionTasks();
            disable();
        }
    }

    private void dispatchTransferCompletionNotification() {
        MSGCargoTransferComplete transferComplete = new MSGCargoTransferComplete(assignedVehicle, spaceportName);
        dispatchInteraction(transferComplete);
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }

    private void postExecutionTasks() {
        if (operatingMode.get() == 0) {
            VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem = spaceport.getVehicleAssignmentRequestSystem();
            vehicleAssignmentRequestSystem.enable();
        } else {
            landerLiaison.initiateDeparture();
        }

        spaceport.flipOperatingMode();
    }

    public synchronized void vehicleAssigned(String vehicleName) {
        assignedVehicle = vehicleName;

        logger.info("<{}> has accepted cargo mission from {}.", vehicleName, spaceportName);
    }

    public synchronized void initiateCargoTransfer() {
        arm.start();
        enable();

        logger.info("<{}> is beginning the cargo transfer process.", spaceportName);
    }

    public String getSpaceportName() {
        return spaceportName;
    }

    public String getAssignedVehicle() {
        return assignedVehicle;
    }
}
