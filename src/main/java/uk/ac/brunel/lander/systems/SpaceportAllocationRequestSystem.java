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

package uk.ac.brunel.lander.systems;

import hla.rti1516_2025.exceptions.*;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.interactions.MSGLandingRequest;
import uk.ac.brunel.lander.Lander;

import java.util.Set;

/**
 * The system used by a lander to send out requests asking for clearance to land at a spaceport.
 *
 * @author Hridyanshu Aatreya
 */
public class SpaceportAllocationRequestSystem extends AbstractSimulationSystem {
    private static final short COOLDOWN_LIMIT = 10;

    private final Lander lander;
    private final Set<PhysicalEntity> spaceports;
    private final SKBaseFederate federate;

    private short requestCooldownTimer;

    public SpaceportAllocationRequestSystem(Lander lander, Set<PhysicalEntity> spaceports, SKBaseFederate federate) {
        this.lander = lander;
        this.spaceports = spaceports;
        this.federate = federate;

        requestCooldownTimer = 0;
        enable();
    }

    @Override
    public void update() {
        if (embargoInEffect()) {
            requestCooldownTimer--;
            return;
        }

        requestSpaceportAllocation();
    }

    private boolean embargoInEffect() {
        return requestCooldownTimer > 0;
    }

    private void requestSpaceportAllocation() {
        for (PhysicalEntity spaceport : spaceports) {
            String landerName = lander.getName();
            String spaceportStatus = spaceport.getStatus();

            if (spaceportStatus.equals("Available")) {
                String spaceportName = spaceport.getName();
                MSGLandingRequest landingRequest = new MSGLandingRequest(landerName, spaceportName);
                dispatchInteraction(landingRequest);

                requestCooldownTimer = COOLDOWN_LIMIT;
                return;
            }
        }
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }
}
