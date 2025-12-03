package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import io.github.atreia108.vega.components.HLAInteractionComponent;
import io.github.atreia108.vega.components.HLAObjectComponent;
import io.github.atreia108.vega.core.HLAInteractionManager;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.components.NavigationComponent;
import uk.ac.brunel.utils.ComponentMappers;
import uk.ac.brunel.utils.World;

public class WaypointDeallocSystem extends IteratingSystem {
    private final Engine engine;

    public WaypointDeallocSystem() {
        super(Family.all(NavigationComponent.class, HLAObjectComponent.class).get());
        engine = VegaUtilities.engine();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        NavigationComponent navComponent = ComponentMappers.navigation.get(entity);

        if (navComponent.fulfilled) {
            Entity interaction = engine.createEntity();
            HLAInteractionComponent interactionComponent = engine.createComponent(HLAInteractionComponent.class);
            interactionComponent.className = "HLAinteractionRoot.FederateMessage";

            HLAObjectComponent objectComponent = engine.createComponent(HLAObjectComponent.class);

            FederateMessageComponent federateMessageComponent = engine.createComponent(FederateMessageComponent.class);
            federateMessageComponent.sender = objectComponent.instanceName;
            federateMessageComponent.receiver = "brunel_spaceport";
            federateMessageComponent.type = "BRUNEL_LANDER_SPACEPORT_NOTIFY_DEPARTURE";
            federateMessageComponent.content = whichLaunchPad(navComponent.waypoint);

            interaction.add(interactionComponent);
            interaction.add(federateMessageComponent);
            HLAInteractionManager.sendInteraction(interaction);

            entity.remove(NavigationComponent.class);
        }
    }

    private String whichLaunchPad(Vector3 coordinates) {
        if (coordinates.epsilonEquals(World.LPAD_1)) {
            return "LPAD_1";
        } else {
            return "LPAD_2";
        }
    }
}
