package uk.ac.brunel.utils;

/**
 * Alternative Vector3-DOUBLE class to the Vector3 provided in Vega which uses
 * floats instead. We don't have enough precision to match coordinates in DON
 * with that class.
 *
 * @author Hridyanshu Aatreya
 */
public class Vector3D {
    public double x;
    public double y;
    public double z;

    public Vector3D() {
        this.x =  0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setZero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }
}
