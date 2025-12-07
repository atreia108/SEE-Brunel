package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import uk.ac.brunel.lander.NavigationDirection;

public class NavigationComponent implements Component, Poolable {
    public Vector3 waypoint = new Vector3(0.0f, 0.0f, 0.0f);
    public NavigationDirection direction = NavigationDirection.ARRIVAL;

    @Override
    public void reset() {
        waypoint.setZero();
        direction = NavigationDirection.ARRIVAL;
    }
}
