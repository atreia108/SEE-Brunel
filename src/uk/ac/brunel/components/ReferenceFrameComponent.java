package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ReferenceFrameComponent implements Component, Poolable {
    // Name of parent reference frame name
    public String name = "";

    // Time dimension for this reference frame
    public double simulatedPhysicalTime = 0.0;

    @Override
    public void reset() {
        name = "";
        simulatedPhysicalTime = 0.0;
    }
}
