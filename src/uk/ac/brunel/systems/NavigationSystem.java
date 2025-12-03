package uk.ac.brunel.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import uk.ac.brunel.components.NavigationComponent;

public class NavigationSystem extends IteratingSystem {
    public NavigationSystem() {
        super(Family.all(NavigationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {

    }
}
