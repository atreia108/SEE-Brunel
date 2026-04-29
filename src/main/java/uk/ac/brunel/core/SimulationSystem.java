package uk.ac.brunel.core;

public interface SimulationSystem {
    void exec();
    void enable();
    void disable();
    boolean isEnabled();
}
