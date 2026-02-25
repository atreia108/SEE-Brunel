/*****************************************************************
 SEE Baseplate - A starter project template for the SEE HLA
 Starter Kit Framework.
 Copyright (c) 2026, Hridyanshu Aatreya - Modelling & Simulation
 Group (MSG) at Brunel University of London. All rights reserved.

 GNU Lesser General Public License (GNU LGPL).

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3.0 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library.
 If not, see http://http://www.gnu.org/licenses/
 *****************************************************************/

package org.see.baseplate.types;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.quaternion.Quaternion;

public class SpaceTimeCoordinateState {
    // Reference frame translation component.
    private Vector3D position;
    private Vector3D velocity;

    // Reference frame rotation component.
    private Quaternion attitudeQuaternion;
    private Vector3D angularVelocity;

    // Simulated physical time component.
    private double time;

    public SpaceTimeCoordinateState() {
        position = Vector3D.of(0, 0, 0);
        velocity = Vector3D.of(0, 0, 0);
        attitudeQuaternion = Quaternion.of(0, 0, 0, 0);
        angularVelocity = Vector3D.of(0, 0, 0);
        time = 0.0;
    }

    public SpaceTimeCoordinateState(Vector3D position, Vector3D velocity, Quaternion attitudeQuaternion, Vector3D angularVelocity, double time) {
        this.position = position;
        this.velocity = velocity;
        this.attitudeQuaternion = attitudeQuaternion;
        this.angularVelocity = angularVelocity;
        this.time = time;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Vector3D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3D velocity) {
        this.velocity = velocity;
    }

    public Quaternion getAttitudeQuaternion() {
        return attitudeQuaternion;
    }

    public void setAttitudeQuaternion(Quaternion attitudeQuaternion) {
        this.attitudeQuaternion = attitudeQuaternion;
    }

    public Vector3D getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(Vector3D angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
