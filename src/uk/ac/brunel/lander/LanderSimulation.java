package uk.ac.brunel.lander;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import io.github.atreia108.vega.components.HLAInteractionComponent;
import io.github.atreia108.vega.core.ASpaceFomSimulation;
import io.github.atreia108.vega.core.HLAInteractionManager;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.systems.MovementSystem;
import uk.ac.brunel.systems.NavigationSystem;
import uk.ac.brunel.systems.WaypointAllocSystem;
import uk.ac.brunel.systems.WaypointDeallocSystem;
import uk.ac.brunel.utils.World;

import java.util.Random;

public class LanderSimulation extends ASpaceFomSimulation {
    private static final String CONFIG_FILE = "resources/Lander.xml";

    private PooledEngine engine;
    private World world;

    public LanderSimulation() {
        super(CONFIG_FILE);
        init();
    }

    @Override
    protected void onInit() {
        engine = VegaUtilities.engine();
        world = new World(engine);

        Entity lander1 = world.createLander("brunel_lander1", "Standby","AitkenBasinLocalFixed", 400.0f, -7200.0f, -1000.0f);
        Entity lander2 = world.createLander("brunel_lander2", "Standby", "AitkenBasinLocalFixed", 400.0f, -7200.0f, -1000.0f);
        Entity lander3 = world.createLander("brunel_lander2", "Standby", "AitkenBasinLocalFixed", 400.0f, -7200.0f, -1000.0f);

        engine.addEntity(lander1);
        engine.addEntity(lander2);
        engine.addEntity(lander3);

        engine.addSystem(new WaypointAllocSystem());
        engine.addSystem(new NavigationSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new WaypointDeallocSystem());
    }

    @Override
    protected void onRun() {
        // Run all systems.
        engine.update(1.0f);

        // Test if interactions are being issued to the Spaceport.
        /*
        Random rng = new Random();
        int sum = rng.nextInt(0, 7) + rng.nextInt(0, 7);

        if (sum >= 10) {
            Entity interaction = createDummyInteraction(rng.nextInt());
            HLAInteractionManager.sendInteraction(interaction);
        }
         */
    }

    private Entity createDummyInteraction(int randomNumber) {
        Entity interaction = engine.createEntity();
        HLAInteractionComponent interactionComponent = engine.createComponent(HLAInteractionComponent.class);
        interactionComponent.className = "HLAinteractionRoot.FederateMessage";

        FederateMessageComponent messageComponent = engine.createComponent(FederateMessageComponent.class);
        messageComponent.sender = "brunel_lander";
        messageComponent.type = "BRUNEL_SPACEPORT_BEACON_ACKNOWLEDGED";
        messageComponent.content = "Test" + randomNumber;
        messageComponent.receiver = "brunel_spaceport";

        interaction.add(messageComponent);
        interaction.add(interactionComponent);

        return interaction;
    }

    @Override
    protected void onShutdown() {

    }

    public static void main(String[] args) {
        new LanderSimulation();
    }
}
