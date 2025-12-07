package uk.ac.brunel.lander;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import io.github.atreia108.vega.core.ASpaceFomSimulation;
import io.github.atreia108.vega.core.HLAObjectManager;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.systems.*;
import uk.ac.brunel.utils.World;

public class LanderSimulation extends ASpaceFomSimulation {
    private static final String CONFIG_FILE = "resources/Lander.xml";

    private PooledEngine engine;

    private boolean sentAttributes = false;
    private Entity lander;

    public LanderSimulation() {
        super(CONFIG_FILE);
        init();
    }

    @Override
    protected void onInit() {
        engine = VegaUtilities.engine();
        World world = new World(engine);

        // At -5387 Z-units, the lander should begin making its descent onto the launch pad.
        // lander = world.createLander("Lander", "AitkenBasinLocalFixed", "Stationary", World.POINT_CHARLIE.x, World.POINT_CHARLIE.y, World.POINT_CHARLIE.z);
        lander = world.createLander("Lander", "AitkenBasinLocalFixed", World.POINT_CHARLIE);

        // TODO - Add systems here.
        WaypointAllocSystem waypointAllocSystem = new WaypointAllocSystem();
        engine.addSystem(new LaunchPadRequestSystem(waypointAllocSystem));
        engine.addSystem(waypointAllocSystem);
        engine.addSystem(new MovementSystem());
        engine.addSystem(new WaypointCompletionSystem());
    }

    @Override
    protected void onRun() {
        if (!sentAttributes) {
            HLAObjectManager.sendInstanceUpdate(lander);
            sentAttributes = true;
        }

        // Run all systems.
        engine.update(1.0f);
    }

    @Override
    protected void onShutdown() {

    }

    public static void main(String[] args) {
        new LanderSimulation();
    }
}
