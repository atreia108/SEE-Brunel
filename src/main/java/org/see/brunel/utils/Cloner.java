package org.see.brunel.utils;

import org.apache.commons.geometry.euclidean.threed.Vector3D;

public class Cloner {
    public static Vector3D cloneVec3D(Vector3D vector) {
        return Vector3D.of(vector.getX(), vector.getY(), vector.getZ());
    }
}
