package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MovementComponent implements Component, Poolable {
    public Vector3 vel = new Vector3(0.0f, 0.0f, 0.0f);
    public Vector3 angularVel = new Vector3(0.0f, 0.0f, 0.0f);

    @Override
    public void reset() {
        vel = new Vector3(0.0f, 0.0f, 0.0f);
        angularVel = new Vector3(0.0f, 0.0f, 0.0f);
    }
}
