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
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.core.SKBaseFederate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.interactions.MSGCargoPickupJob;
import uk.ac.brunel.spaceport.Spaceport;
import uk.ac.brunel.types.CargoType;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Negotiates assignment of vehicles capable of transporting cargo (servicing the lander) to/from the spaceport.
 *
 * @author Hridyanshu Aatreya
 */
public class VehicleAssignmentRequestSystem extends AbstractSimulationSystem {
    private static final Logger logger = LoggerFactory.getLogger(VehicleAssignmentRequestSystem.class);

    private static final short COOLDOWN_LIMIT = 10;
    private static final Vector3D WAREHOUSE_COORDINATES = Vector3D.of(801, 3053, -5532);

    private final String spaceportName;
    private final Vector3D entityCoordinates;
    private final SKBaseFederate federate;

    private final AtomicInteger operatingMode;
    private short vehicleRequestCooldownTimer;

    public VehicleAssignmentRequestSystem(Spaceport spaceport, Vector3D spaceportCoordinates, SKBaseFederate federate) {
        this.entityCoordinates = spaceportCoordinates;
        this.federate = federate;

        vehicleRequestCooldownTimer = 0;
        spaceportName = spaceport.getName();
        operatingMode = spaceport.getOperatingMode();
    }

    @Override
    public void update() {
        if (isEmbargoInEffect()) {
            vehicleRequestCooldownTimer--;
            return;
        }

        dispatchCargoMission();
    }

    private void dispatchCargoMission() {
        String resourceName = CargoType.randomType().name();

        MSGCargoPickupJob job = new MSGCargoPickupJob();
        job.setRequestingObject(spaceportName);
        job.setCargoType(resourceName);

        if (operatingMode.get() == 0) {
            job.setPickupLocation(entityCoordinates);
            job.setDeliveryLocation(WAREHOUSE_COORDINATES);
        } else if (operatingMode.get() == 1){
            job.setPickupLocation(WAREHOUSE_COORDINATES);
            job.setDeliveryLocation(entityCoordinates);
        }

        dispatchInteraction(job);
        vehicleRequestCooldownTimer = COOLDOWN_LIMIT;

        if (operatingMode.get() == 0) {
            logger.info("<{}> dispatched a cargo pickup mission.", spaceportName);
        } else {
            logger.info("<{}> dispatched a cargo delivery mission.", spaceportName);
        }
    }

    private boolean isEmbargoInEffect() {
        return vehicleRequestCooldownTimer > 0;
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }

    public String getSpaceportName() {
        return spaceportName;
    }
}
