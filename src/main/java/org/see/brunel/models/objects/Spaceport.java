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

package org.see.brunel.models.objects;

import hla.rti1516_2025.exceptions.*;
import org.see.brunel.models.interactions.FederateMessage;
import org.see.brunel.spaceport.SpaceportFederate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Spaceport {
    private final SpaceportFederate federate;

    private final ConcurrentMap<String, Integer> landerNameToPadNumber;
    private final ConcurrentMap<Integer, String> padNumberToLanderName;

    public Spaceport(SpaceportFederate federate) {
        this.federate = federate;
        landerNameToPadNumber = new ConcurrentHashMap<>();
        padNumberToLanderName = new ConcurrentHashMap<>();
    }

    public void allocLaunchPad(int padNumber, String landerName) {
        landerNameToPadNumber.put(landerName, padNumber);
        padNumberToLanderName.put(padNumber, landerName);
    }

    public void evictLander(String landerName) {
        if (landerNameToPadNumber.containsKey(landerName)) {
            int padNumber = landerNameToPadNumber.remove(landerName);
            padNumberToLanderName.remove(padNumber);
        }
    }

    public void clearLaunchPads() {
        landerNameToPadNumber.clear();
        padNumberToLanderName.clear();
    }

    public boolean isLaunchPadOccupied(int padNumber) {
        return padNumberToLanderName.containsKey(padNumber);
    }

    public boolean dispatchMessage(String receiverName, String messageType, String content) {
        FederateMessage message = new FederateMessage("Spaceport", receiverName, messageType, content);
        try {
            federate.sendInteraction(message);
            return true;
        } catch (FederateNotExecutionMember | NotConnected | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | RTIinternalError | SaveInProgress e) {
            return false;
        }
    }
}
