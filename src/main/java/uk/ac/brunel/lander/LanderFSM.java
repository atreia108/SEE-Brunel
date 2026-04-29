package uk.ac.brunel.lander;

public enum LanderFSM {
    APPROACHING,
    LANDING,
    SERVICING,
    DEPARTING;

    private LanderFSM current;

    public synchronized void nextState() {

    }

    @Override
    public String toString() {
        return switch (current) {
            case APPROACHING -> "Approaching";
            case LANDING -> "Landing";
            case SERVICING -> "Servicing";
            case DEPARTING -> "Departing";
        };
    }
}
