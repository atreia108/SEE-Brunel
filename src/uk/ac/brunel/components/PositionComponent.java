package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PositionComponent implements Component, Poolable {
    public Vector3 pos = new Vector3(0.0f, 0.0f, 0.0f);

    @Override
    public void reset() {
        pos = new Vector3(0.0f, 0.0f, 0.0f);
    }
}
