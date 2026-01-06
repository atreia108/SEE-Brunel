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
        System.out.println(lander.getName() + " was approved for arrival at" + launchPadId + ".");

        if (lander != null && waitList.contains(lander)) {
            if (launchPadId.equals("LPAD_1")) {
                lander.setWaypoint(cloneVec3D(LAUNCH_PAD_1));
            } else if (launchPadId.equals("LPAD_2")) {
                lander.setWaypoint(cloneVec3D(LAUNCH_PAD_2));
            } else {
                return;
            }

            lander.setMissionStage(Lander.MissionStage.ARRIVAL);
            pullFromWaitList(lander);
        }
    }

    void scheduleLanderDeparture(String landerName) {
        Lander lander = findLander(landerName);
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
