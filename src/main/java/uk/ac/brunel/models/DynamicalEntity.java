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

package uk.ac.brunel.models;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.ejml.simple.SimpleMatrix;
import org.see.skf.annotations.Attribute;
import org.see.skf.impl.encoding.HLAfloat64LECoder;
import org.see.skf.model.AccessLevel;
import uk.ac.brunel.encoding.SimpleMatrixCoder;
import uk.ac.brunel.encoding.Vector3DCoder;

public class DynamicalEntity extends PhysicalEntity {
    @Attribute(name = "force", coder = Vector3DCoder.class, access = AccessLevel.NONE)
    private Vector3D force;

    @Attribute(name = "torque", coder = Vector3DCoder.class, access = AccessLevel.NONE)
    private Vector3D torque;

    @Attribute(name = "mass", coder = HLAfloat64LECoder.class, access = AccessLevel.NONE)
    private double mass;

    @Attribute(name = "mass_rate", coder = HLAfloat64LECoder.class, access = AccessLevel.NONE)
    private double massRate;

    @Attribute(name = "inertia", coder = SimpleMatrixCoder.class, access = AccessLevel.NONE)
    private SimpleMatrix inertia;

    @Attribute(name = "inertia_rate", coder = SimpleMatrixCoder.class, access = AccessLevel.NONE)
    private SimpleMatrix inertiaRate;

    public DynamicalEntity() {
        force = Vector3D.of(0, 0, 0);
        torque = Vector3D.of(0, 0, 0);
        mass = 0.0;
        massRate = 0.0;
        inertia = new SimpleMatrix(3, 3);
        inertiaRate = new SimpleMatrix(3, 3);
    }

    public Vector3D getForce() {
        return force;
    }

    public void setForce(Vector3D force) {
        this.force = force;
    }

    public Vector3D getTorque() {
        return torque;
    }

    public void setTorque(Vector3D torque) {
        this.torque = torque;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getMassRate() {
        return massRate;
    }

    public void setMassRate(double massRate) {
        this.massRate = massRate;
    }

    public SimpleMatrix getInertia() {
        return inertia;
    }

    public void setInertia(SimpleMatrix inertia) {
        this.inertia = inertia;
    }

    public SimpleMatrix getInertiaRate() {
        return inertiaRate;
    }

    public void setInertiaRate(SimpleMatrix inertiaRate) {
        this.inertiaRate = inertiaRate;
    }
}
