package org.see.brunel.models.objects;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Spaceport {
    private final ConcurrentMap<String, Integer> landerNameToPadNumber;
    private final ConcurrentMap<Integer, String> padNumberToLanderName;

    public Spaceport() {
        landerNameToPadNumber = new ConcurrentHashMap<>();
        padNumberToLanderName = new ConcurrentHashMap<>();
    }

    public void allocLaunchPad(int padNumber, String landerName) {
        landerNameToPadNumber.put(landerName, padNumber);
        padNumberToLanderName.put(padNumber, landerName);
    }

    public void evictLander(int padNumber) {
        String landerName = padNumberToLanderName.remove(padNumber);
        landerNameToPadNumber.remove(landerName);
    }

    public boolean isLaunchPadOccupied(int padNumber) {
        return !padNumberToLanderName.containsKey(padNumber);
    }
}
