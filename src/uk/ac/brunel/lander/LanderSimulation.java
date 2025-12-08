package uk.ac.brunel.lander;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import io.github.atreia108.vega.core.ASpaceFomSimulation;
import io.github.atreia108.vega.core.HLAInteractionQueue;
import io.github.atreia108.vega.core.HLAObjectManager;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.systems.*;
import uk.ac.brunel.utils.World;

import java.util.HashSet;
import java.util.Set;

public class LanderSimulation extends ASpaceFomSimulation {
    private static final String CONFIG_FILE = "resources/Lander.xml";

    private PooledEngine engine;
    public static final float VELOCITY = 20.0f;

    private boolean sentAttributes = false;
    private final Set<Entity> landers = new HashSet<>();

    public LanderSimulation() {
        super(CONFIG_FILE);
        init();
    }

    @Override
    protected void onInit() {
        engine = VegaUtilities.engine();
        World world = new World(engine);

        // At -5387 Z-units, the lander should begin making its descent onto the launch pad.
        Entity lander = world.createLander("Lander", "AitkenBasinLocalFixed", World.POINT_CHARLIE);
        landers.add(lander);

        // TODO - Add systems here.
        WaypointAllocSystem waypointAllocSystem = new WaypointAllocSystem(this);
        engine.addSystem(new LaunchPadRequestSystem(waypointAllocSystem));
        engine.addSystem(waypointAllocSystem);
        engine.addSystem(new MovementSystem());
        engine.addSystem(new WaypointCompletionSystem());
    }

    @Override
    protected void onRun() {
        if (!sentAttributes) {
            sendLatestAttributes();
            sentAttributes = true;
        }

        // Run all systems.
        engine.update(1.0f);
        HLAInteractionQueue.clear();
    }

    private void sendLatestAttributes() {
        for (Entity entity : landers) {
            HLAObjectManager.sendInstanceUpdate(entity);
        }
    }

    @Override
    protected void onShutdown() {
        // Do nothing.
    }

    public Set<Entity> getLanders() {
        return landers;
    }

    public static void main(String[] args) {
        new LanderSimulation();
    }
}
