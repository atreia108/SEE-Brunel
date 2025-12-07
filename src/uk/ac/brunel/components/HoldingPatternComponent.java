package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

public class HoldingPatternComponent implements Component, Poolable {
    public Vector3 holdingPos = new Vector3();

    @Override
    public void reset() {
        holdingPos.setZero();
    }
}
