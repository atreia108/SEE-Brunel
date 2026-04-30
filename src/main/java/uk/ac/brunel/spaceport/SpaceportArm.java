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

package uk.ac.brunel.spaceport;

import org.see.skf.annotations.ObjectClass;
import uk.ac.brunel.core.PhysicalInterface;
import uk.ac.brunel.core.SimulationEntity;
import uk.ac.brunel.spaceport.systems.PowerSystem;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simulated model of an arm onboard the spaceport. Represented as a SpaceFOM PhysicalInterface parented to the
 * spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@ObjectClass(name = "HLAobjectRoot.PhysicalInterface")
public class SpaceportArm extends PhysicalInterface implements SimulationEntity {
    public static final String NAME_SEQUENCE = "brunel_spaceport_arm_";

    // Power load of the arm that is incurred during its operational stages in kilowatts (kW).
    public static final double IDLE_POWER_RATING = 0.435;
    public static final double PEAK_POWER_RATING = 2.000;
    private static final int TRANSFER_OPERATION_TIME  = 10;
    private static final int CLEANUP_PERIOD = 10;

    private final PowerSystem powerSystem;
    private final AtomicBoolean running;

    private short cleanupPeriodCounter;
    private short transferOperationCounter;

    public SpaceportArm(String name, PowerSystem powerSystem) {
        this.powerSystem = powerSystem;

        running = new AtomicBoolean(false);
        cleanupPeriodCounter = 0;
        transferOperationCounter = 0;

        setName(name);
    }

    public void start() {
        running.set(true);
        transferOperationCounter = TRANSFER_OPERATION_TIME;
    }

    public void stop() {
        running.set(false);
        cleanupPeriodCounter = CLEANUP_PERIOD;
    }

    public boolean cleanupPeriodActive() {
        return cleanupPeriodCounter > 0;
    }

    @Override
    public void update() {
        if (running.get()) {
            runTransferOperation();
        } else {
            powerSystem.consume(idleConsumptionAmount());
        }

        if (cleanupPeriodActive()) {
            cleanupPeriodCounter--;
        }
    }

    private void runTransferOperation() {
        double consumptionAmount = peakConsumptionAmount();
        double allocatedWattage = powerSystem.consume(consumptionAmount);

        if (allocatedWattage >= consumptionAmount) {
            if (transferOperationCounter > 0) {
                transferOperationCounter--;
            } else {
                stop();
            }
        }
    }

    private double peakConsumptionAmount() {
        return 0.2 * PEAK_POWER_RATING;
    }

    private double idleConsumptionAmount() {
        return 0.2 * IDLE_POWER_RATING;
    }

    public boolean isRunning() {
        return running.get();
    }
}
