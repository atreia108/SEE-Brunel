package uk.ac.brunel.archetypes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.github.atreia108.vega.core.IEntityArchetype;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.FederateMessageComponent;

public class FederateMessage implements IEntityArchetype {
    private final Engine engine = VegaUtilities.engine();
    @Override
    public Entity createEntity() {
        Entity federateMessage = engine.createEntity();
        FederateMessageComponent federateMessageComponent = engine.createComponent(FederateMessageComponent.class);

        federateMessage.add(federateMessageComponent);

        return federateMessage;
    }
}
