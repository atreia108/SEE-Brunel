package uk.ac.brunel.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PhysicalEntityComponent implements Component, Poolable {
    public String name = "";
    public String type = "";
    public String status = "";

    @Override
    public void reset() {
        name = "";
        type = "";
        status = "";
    }
}
