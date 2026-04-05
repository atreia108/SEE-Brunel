package uk.ac.brunel.viz.animations;

import org.apache.commons.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAnimation {
    private final ArrayList<Vector3D> keyframes;

    protected AbstractAnimation() {
        keyframes = new ArrayList<>();
    }

    public List<Vector3D> getKeyframes() {
        return keyframes;
    }
}
