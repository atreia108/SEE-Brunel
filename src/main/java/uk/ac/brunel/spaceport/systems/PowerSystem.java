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
import uk.ac.brunel.interactions.UCFPowerRequest;
import uk.ac.brunel.spaceport.Powerable;

/**
 * Handles the power management aspects of the spaceport and objects bound to it.
 *
 * @author Hridyanshu Aatreya
 */
public class PowerSystem extends AbstractSimulationSystem {
    private static final Logger logger = LoggerFactory.getLogger(PowerSystem.class);

    private static final double BASE_POWER_VALUE = 45.0;
    private static final short COOLDOWN_LIMIT = 8;

    private final String entityName;
    private final Powerable entity;
    private final SKBaseFederate federate;

    private double reserveAmount;
    private short powerRequestCooldownTimer;

    public PowerSystem(String entityName, Powerable entity, SKBaseFederate federate) {
        this.entityName = entityName;
        this.entity = entity;
        this.federate = federate;

        reserveAmount = BASE_POWER_VALUE;
        powerRequestCooldownTimer = 0;
        enable();
    }

    @Override
    public void update() {
        if (embargoInEffect()) {
            powerRequestCooldownTimer--;
            return;
        }

        if (reserveAmount < (0.5 * entity.powerConsumption())) {
            requestPower();
            powerRequestCooldownTimer = COOLDOWN_LIMIT;
        }
    }

    private boolean embargoInEffect() {
        return powerRequestCooldownTimer > 0;
    }

    public synchronized double consume(double amount) {
        double threshold = 0.3 * reserveAmount;

        // Continue reserving until a new power request allocation brings in more power.
        if ((reserveAmount - amount) > threshold) {
            reserveAmount -= amount;
            return amount;
        } else {
            return 0;
        }
    }

    public synchronized void recharge(double amount) {
        logger.info("{} was allocated <{}> kW.", entityName, amount);
        reserveAmount += amount;
    }

    private void requestPower() {
        double amount = entity.powerConsumption();
        int priorityLevel = entity.powerPriorityLevel();
        UCFPowerRequest powerRequest = new UCFPowerRequest(entityName, amount, priorityLevel);
        dispatchInteraction(powerRequest);

        logger.info("Requested <{}> kW of power with priority level {}. Residual amount: {}.", amount, priorityLevel, reserveAmount);
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }

    public String getEntityName() {
        return entityName;
    }
}
