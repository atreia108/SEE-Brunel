package uk.ac.brunel.core;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSimulationSystem implements SimulationSystem{
    private final AtomicBoolean running;

    protected AbstractSimulationSystem() {
        running = new AtomicBoolean(false);
    }

    @Override
    public void exec() {
        if (isEnabled()) {
            update();
        }
    }

    public abstract void update();

    @Override
    public void enable() {
        running.set(true);
    }

    @Override
    public void disable() {
        running.set(false);
    }

    @Override
    public boolean isEnabled() {
        return running.get();
    }
}
