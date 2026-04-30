package uk.ac.brunel.spaceport;

/**
 * A simulated object that consumes power. Entities implementing this interface can have their power management
 * capabilities handled by the PowerSystem.
 * @author Hridyanshu Aatreya
 */
public interface Powerable {
    double powerConsumption();
    int powerPriorityLevel();
}
