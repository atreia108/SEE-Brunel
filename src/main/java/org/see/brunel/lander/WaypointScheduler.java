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
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.brunel.models.interactions.FederateMessage;
import org.see.brunel.models.objects.Lander;
import org.see.skf.core.SKFederateInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.see.brunel.utils.Cloner.cloneVec3D;

public class WaypointScheduler {
    private static final Vector3D LAUNCH_PAD_1 = Vector3D.of(100.2, 430.0, -5587.0);
    private static final Vector3D LAUNCH_PAD_2 = Vector3D.of(100.0, 400.0, -5587.0);
    private static final int RNG_THRESHOLD = 5;

    private SKFederateInterface federate;
    private final CopyOnWriteArraySet<Lander> landers;
    private final CopyOnWriteArraySet<Lander> waitList;
    private final ConcurrentMap<Lander, Integer> landerToWaitingPeriod;

    private final Random rand;

    public WaypointScheduler(SKFederateInterface federate) {
        this.federate = federate;
        landers = new CopyOnWriteArraySet<>();
        waitList = new CopyOnWriteArraySet<>();
        landerToWaitingPeriod = new ConcurrentHashMap<>();
        rand = new Random();
    }

    void registerLander(Lander lander) {
        landers.add(lander);
    }

    void process() {
        scheduleRequests();
        evalWaitingPeriods();
    }

    private void scheduleRequests() {
        for (var lander : landers) {
            int sum = rand.nextInt(6) + rand.nextInt(6) + rand.nextInt(2);

            try {
                if (!waitList.contains(lander) && sum > RNG_THRESHOLD && lander.getMissionStage() == Lander.MissionStage.AWAITING_SCHEDULER_PROCESSING) {
                    FederateMessage message = new FederateMessage(lander.getName(), "Spaceport",
                            "BRUNEL_LANDER_SPACEPORT_REQUEST_LANDING",
                            lander.getName() + " is requesting to land.");
                    federate.sendInteraction(message);

                    putOnWaitList(lander);
                    lander.setMissionStage(Lander.MissionStage.AWAITING_SPACEPORT_ACK);
                }
            } catch (FederateNotExecutionMember | SaveInProgress | RTIinternalError | NotConnected |
                     InteractionClassNotPublished | InteractionClassNotDefined | RestoreInProgress |
                     InteractionParameterNotDefined e) {
                throw new IllegalStateException("Error encountered while trying to send interaction.", e);
            }
        }
    }

    void scheduleLanderArrival(String landerName, String launchPadId) {
        Lander lander = findLander(landerName);

        if (lander != null && waitList.contains(lander)) {
            if (launchPadId.equals("LPAD_1")) {
                lander.setWaypoint(cloneVec3D(LAUNCH_PAD_1));
            } else if (launchPadId.equals("LPAD_2")) {
                lander.setWaypoint(cloneVec3D(LAUNCH_PAD_2));
            } else {
                return;
            }

            lander.setMissionStage(Lander.MissionStage.ARRIVAL);
            lander.setStatus("Arriving");
            pullFromWaitList(lander);
        }
    }

    void scheduleLanderDeparture(String landerName) {
        Lander lander = findLander(landerName);

        if (lander != null && lander.getMissionStage() == Lander.MissionStage.LANDED) {
            lander.setMissionStage(Lander.MissionStage.DEPARTURE);
            lander.setStatus("Departing");
        }
    }

    private void evalWaitingPeriods() {
        for (var entry : landerToWaitingPeriod.entrySet()) {
            Lander lander = entry.getKey();
            int elapsedPeriod = entry.getValue();
            // Since the spaceport didn't give us a response in time, put the lander back into the required state for
            // processing. Maybe we'll get lucky this time.
            if (elapsedPeriod > 10) {
                pullFromWaitList(lander);
                lander.setMissionStage(Lander.MissionStage.AWAITING_SCHEDULER_PROCESSING);
            } else {
                landerToWaitingPeriod.replace(entry.getKey(), ++elapsedPeriod);
            }
        }
    }

    private void putOnWaitList(Lander lander) {
        waitList.add(lander);
        landerToWaitingPeriod.put(lander, 0);
    }

    private void pullFromWaitList(Lander lander) {
        waitList.remove(lander);
        landerToWaitingPeriod.remove(lander);
    }

    private Lander findLander(String landerName) {
        Optional<Lander> lander = landers.stream().filter(l -> l.getName().equals(landerName)).findFirst();
        return lander.orElse(null);
    }
}
