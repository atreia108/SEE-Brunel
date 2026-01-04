/*-
 * Copyright (c) 2025 Hridyanshu Aatreya
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package uk.ac.brunel.types;

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
