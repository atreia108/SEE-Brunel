package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import uk.ac.brunel.utils.Vector3D;

public class NavigationComponent implements Component, Poolable {
    public Vector3D waypoint = new Vector3D(0.0, 0.0, 0.0);
    public boolean fulfilled = false;

    @Override
    public void reset() {
        waypoint.setZero();
        fulfilled = false;
    }
}
