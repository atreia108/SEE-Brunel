package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

public class QuaternionComponent implements Component, Poolable {
    public double scalar = 0.0;
    public Vector3 vector = new Vector3(0.0f, 0.0f, 0.0f);

    @Override
    public void reset() {
        scalar = 0.0;
        vector = new Vector3(0.0f, 0.0f, 0.0f);
    }
}
