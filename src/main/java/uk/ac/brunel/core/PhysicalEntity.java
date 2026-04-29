package uk.ac.brunel.core;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.quaternion.Quaternion;
import org.see.skf.annotations.Attribute;
import org.see.skf.annotations.ObjectClass;
import org.see.skf.core.PropertyChangeSubject;
import org.see.skf.runtime.ScopeLevel;
import org.see.skf.util.encoding.HLAunicodeStringCoder;
import uk.ac.brunel.encoding.QuaternionCoder;
import uk.ac.brunel.encoding.SpaceTimeCoordinateStateCoder;
import uk.ac.brunel.encoding.Vector3DCoder;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class PhysicalEntity extends PropertyChangeSubject {
    @Attribute(name = "name", coder = HLAunicodeStringCoder.class)
    private String name;

    @Attribute(name = "type", coder = HLAunicodeStringCoder.class)
    private String type;

    @Attribute(name = "status", coder = HLAunicodeStringCoder.class)
    private String status;

    @Attribute(name = "parent_reference_frame", coder = HLAunicodeStringCoder.class)
    private String parentReferenceFrame;

    @Attribute(name = "state", coder = SpaceTimeCoordinateStateCoder.class)
    private SpaceTimeCoordinateState state;

    @Attribute(name = "acceleration", coder = Vector3DCoder.class)
    private Vector3D acceleration;

    @Attribute(name = "rotational_acceleration", coder = Vector3DCoder.class, scope = ScopeLevel.NONE)
    private Vector3D rotationalAcceleration;

    @Attribute(name = "center_of_mass", coder = Vector3DCoder.class, scope = ScopeLevel.NONE)
    private Vector3D centerOfMass;

    @Attribute(name = "body_wrt_structural", coder = QuaternionCoder.class, scope = ScopeLevel.NONE)
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
}