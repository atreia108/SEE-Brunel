package uk.ac.brunel.types;

import java.util.Random;

public enum CargoType {
    /** Generic liquid coolant. */
    COOLANT,
    /** Generic propellant. */
    PROPELLANT,
    /** Battery or energy-storage unit. */
    BATTERY,
    /** Default sentinel for unrecognised or absent cargo type fields. */
    UNDEFINED;

    private static final Random rng = new Random();

    public static CargoType randomType() {
        int num = rng.nextInt(3);

        return switch (num) {
            case 0 -> CargoType.BATTERY;
            case 1 -> CargoType.COOLANT;
            default -> CargoType.PROPELLANT;
        };
    }
}
