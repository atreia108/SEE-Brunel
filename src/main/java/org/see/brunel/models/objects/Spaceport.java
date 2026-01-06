package org.see.brunel.models.objects;

import hla.rti1516_2025.exceptions.*;
import org.see.brunel.models.interactions.FederateMessage;
import org.see.brunel.spaceport.SpaceportFederate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Not an object instance because it's stationary in DON, and only really processes instructions via GUI; not really
// warranting the need for a specific object instance. In short, the federate IS the spaceport.
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
        int padNumber = landerNameToPadNumber.remove(landerName);
        padNumberToLanderName.remove(padNumber);
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
