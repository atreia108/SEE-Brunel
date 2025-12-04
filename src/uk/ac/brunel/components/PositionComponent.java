package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import uk.ac.brunel.utils.Vector3D;

public class PositionComponent implements Component, Poolable {
    public Vector3D pos = new Vector3D(0, 0, 0);

    @Override
    public void reset() {
        pos.setZero();
    }
}
