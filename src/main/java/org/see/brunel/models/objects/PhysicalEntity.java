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

package org.see.brunel.models.objects;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.quaternion.Quaternion;
import org.see.skf.annotations.Attribute;
import org.see.skf.annotations.ObjectClass;
import org.see.skf.impl.encoding.HLAunicodeStringCoder;
import org.see.skf.model.AccessLevel;
import org.see.brunel.encoding.QuaternionCoder;
import org.see.brunel.encoding.SpaceTimeCoordinateStateCoder;
import org.see.brunel.encoding.Vector3DCoder;
import org.see.brunel.types.SpaceTimeCoordinateState;
import org.see.skf.model.objects.UpdatableObjectInstance;

@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class PhysicalEntity extends UpdatableObjectInstance {
    private static final Vector3D LAUNCH_PAD_1 = Vector3D.of(100.2, 430.0, -5587.0);
    private static final Vector3D LAUNCH_PAD_2 = Vector3D.of(100.0, 400.0, -5587.0);

    private static final Vector3D POINT_CHARLIE = Vector3D.of(-500.0, -200.0, -5200.0);
    private static final Vector3D POINT_FOXTROT = Vector3D.of(600.0, 500.0, -4900.0);
    private static final Vector3D POINT_ROMEO = Vector3D.of(400.0, -100.0, -5000.0);

    @Attribute(name = "name", coder = HLAunicodeStringCoder.class, access = AccessLevel.PUBLISH_SUBSCRIBE)
    private String name;

    @Attribute(name = "type", coder = HLAunicodeStringCoder.class, access = AccessLevel.PUBLISH_SUBSCRIBE)
    private String type;

    @Attribute(name = "status", coder = HLAunicodeStringCoder.class, access = AccessLevel.NONE)
    private String status;

    @Attribute(name = "parent_reference_frame", coder = HLAunicodeStringCoder.class, access = AccessLevel.PUBLISH_SUBSCRIBE)
    private String parentReferenceFrame;

    @Attribute(name = "state",  coder = SpaceTimeCoordinateStateCoder.class, access = AccessLevel.PUBLISH_SUBSCRIBE)
    private SpaceTimeCoordinateState state;

    @Attribute(name = "acceleration", coder = Vector3DCoder.class, access = AccessLevel.NONE)
    private Vector3D acceleration;

    @Attribute(name = "rotational_acceleration", coder = Vector3DCoder.class, access = AccessLevel.NONE)
    private Vector3D rotationalAcceleration;

    @Attribute(name = "center_of_mass", coder = Vector3DCoder.class, access = AccessLevel.NONE)
    private Vector3D centerOfMass;

    @Attribute(name = "body_wrt_structural", coder = QuaternionCoder.class, access = AccessLevel.NONE)
    private Quaternion bodyWrtStructural;

    public PhysicalEntity() {
        this.name = "";
        this.type = "";
        this.status = "";
        this.parentReferenceFrame = "";
        this.state = new SpaceTimeCoordinateState();
        this.acceleration = Vector3D.of(0, 0, 0);
        this.rotationalAcceleration = Vector3D.of(0, 0, 0);
        this.centerOfMass = Vector3D.of(0, 0, 0);
        this.bodyWrtStructural = Quaternion.of(0, 0, 0, 0);
    }

    private PhysicalEntity(String name, String type, String status, String parentReferenceFrame, SpaceTimeCoordinateState state) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.parentReferenceFrame = parentReferenceFrame;
        this.state = state;
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

    static class Builder {
        private String name;
        private String type;
        private String status;
        private String parentReferenceFrame;
        private SpaceTimeCoordinateState state;

        private Builder() {}

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withParentReferenceFrame(String parentReferenceFrame) {
            this.parentReferenceFrame = parentReferenceFrame;
            return this;
        }

        public Builder withState(SpaceTimeCoordinateState state) {
            this.state = state;
            return this;
        }

        private void validate() {
            if (name == null || type == null || status == null || parentReferenceFrame == null || state == null) {
                throw new IllegalArgumentException("Failed to build PhysicalEntity object because one or more fields were not initialized.");
            }
        }

        public PhysicalEntity build() {
            validate();
            return new PhysicalEntity(name, type, status, parentReferenceFrame, state);
        }
    }
}
