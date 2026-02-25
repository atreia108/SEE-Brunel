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

package org.see.baseplate.models;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.quaternion.Quaternion;
import org.ejml.simple.SimpleMatrix;
import org.see.baseplate.encoding.QuaternionCoder;
import org.see.baseplate.encoding.SimpleMatrixCoder;
import org.see.baseplate.encoding.SpaceTimeCoordinateStateCoder;
import org.see.baseplate.encoding.Vector3DCoder;
import org.see.baseplate.types.SpaceTimeCoordinateState;
import org.see.skf.annotations.Attribute;
import org.see.skf.annotations.ObjectClass;
import org.see.skf.core.PropertyChangeSubject;
import org.see.skf.util.encoding.HLAfloat64LECoder;
import org.see.skf.util.encoding.HLAunicodeStringCoder;

@ObjectClass(name = "HLAobjectRoot.PhysicalEntity.DynamicalEntity")
public class DynamicalEntity extends PropertyChangeSubject {
    @Attribute(name = "name", coder = HLAunicodeStringCoder.class)
    private String name;

    @Attribute(name = "type", coder = HLAunicodeStringCoder.class)
    private String type;

    @Attribute(name = "status", coder = HLAunicodeStringCoder.class)
    private String status;

    @Attribute(name = "parent_reference_frame", coder = HLAunicodeStringCoder.class)
    private String parentReferenceFrame;

    @Attribute(name = "state",  coder = SpaceTimeCoordinateStateCoder.class)
    private SpaceTimeCoordinateState state;

    @Attribute(name = "acceleration", coder = Vector3DCoder.class)
    private Vector3D acceleration;

    @Attribute(name = "rotational_acceleration", coder = Vector3DCoder.class)
    private Vector3D rotationalAcceleration;

    @Attribute(name = "center_of_mass", coder = Vector3DCoder.class)
    private Vector3D centerOfMass;

    @Attribute(name = "body_wrt_structural", coder = QuaternionCoder.class)
    private Quaternion bodyWrtStructural;

    @Attribute(name = "force", coder = Vector3DCoder.class)
    private Vector3D force;

    @Attribute(name = "torque", coder = Vector3DCoder.class)
    private Vector3D torque;

    @Attribute(name = "mass", coder = HLAfloat64LECoder.class)
    private double mass;

    @Attribute(name = "mass_rate", coder = HLAfloat64LECoder.class)
    private double massRate;

    @Attribute(name = "inertia", coder = SimpleMatrixCoder.class)
    private SimpleMatrix inertia;

    @Attribute(name = "inertia_rate", coder = SimpleMatrixCoder.class)
    private SimpleMatrix inertiaRate;

    public DynamicalEntity() {
        this.name = "";
        this.type = "";
        this.status = "";
        this.parentReferenceFrame = "";
        this.state = new SpaceTimeCoordinateState();
        this.acceleration = Vector3D.of(0, 0, 0);
        this.rotationalAcceleration = Vector3D.of(0, 0, 0);
        this.centerOfMass = Vector3D.of(0, 0, 0);
        this.bodyWrtStructural = Quaternion.of(0, 0, 0, 0);
        force = Vector3D.of(0, 0, 0);
        torque = Vector3D.of(0, 0, 0);
        mass = 0.0;
        massRate = 0.0;
        inertia = new SimpleMatrix(3, 3);
        inertiaRate = new SimpleMatrix(3, 3);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParentReferenceFrame() {
        return parentReferenceFrame;
    }

    public void setParentReferenceFrame(String parentReferenceFrame) {
        this.parentReferenceFrame = parentReferenceFrame;
    }

    public SpaceTimeCoordinateState getState() {
        return state;
    }

    public void setState(SpaceTimeCoordinateState state) {
        this.state = state;
    }

    public Vector3D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3D acceleration) {
        this.acceleration = acceleration;
    }

    public Vector3D getRotationalAcceleration() {
        return rotationalAcceleration;
    }

    public void setRotationalAcceleration(Vector3D rotationalAcceleration) {
        this.rotationalAcceleration = rotationalAcceleration;
    }

    public Vector3D getCenterOfMass() {
        return centerOfMass;
    }

    public void setCenterOfMass(Vector3D centerOfMass) {
        this.centerOfMass = centerOfMass;
    }

    public Quaternion getBodyWrtStructural() {
        return bodyWrtStructural;
    }

    public void setBodyWrtStructural(Quaternion bodyWrtStructural) {
        this.bodyWrtStructural = bodyWrtStructural;
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
