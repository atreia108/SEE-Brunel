package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.atreia108.vega.components.HLAInteractionComponent;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAInteractionManager;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.components.HoldingPatternComponent;

import java.util.Random;

public class LaunchPadRequestSystem extends IteratingSystem {
    private static final int RNG_THRESHOLD = 7;

    private final Random rand;
    private final WaypointAllocSystem allocSystem;

    public LaunchPadRequestSystem(WaypointAllocSystem allocSystem) {
        super(Family.all(HoldingPatternComponent.class, HLAObjectComponent.class).get());
        rand = new Random();
        this.allocSystem = allocSystem;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HLAObjectComponent objectComponent = VegaUtilities.objectComponentMapper().get(entity);

        int sum = rand.nextInt(0, 6) + rand.nextInt(0, 6) + rand.nextInt(0, 2);
        System.out.println("SUM: " + sum);

        if (sum > RNG_THRESHOLD && !allocSystem.isRegistered(entity)) {
            System.out.println("Processing entity: " + objectComponent.instanceName);
            Engine engine = VegaUtilities.engine();

            Entity interaction = engine.createEntity();
            HLAInteractionComponent interactionComponent = engine.createComponent(HLAInteractionComponent.class);
            interactionComponent.className = "HLAinteractionRoot.FederateMessage";

            FederateMessageComponent federateMessageComponent = engine.createComponent(FederateMessageComponent.class);
            federateMessageComponent.sender = objectComponent.instanceName;
            federateMessageComponent.receiver = "Spaceport";
            federateMessageComponent.type = "BRUNEL_LANDER_SPACEPORT_REQUEST_LANDING";
            federateMessageComponent.content = objectComponent.instanceName + " is requesting to land.";

            interaction.add(interactionComponent);
            interaction.add(federateMessageComponent);
            HLAInteractionManager.sendInteraction(interaction);

            allocSystem.register(entity);
        }
    }
}
