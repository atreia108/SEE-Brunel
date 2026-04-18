package uk.ac.brunel.interactions;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;
import uk.ac.brunel.encoding.Vector3DCoder;

/**
 * Implementation of the MSGCargoPickupJob interaction class from the MSG FOM.
 * Purpose: Requests a rover to be dispatched for a transport mission from a pickup location to a delivery location.
 * It is assumed that the rover is interacting with objects in the same SpaceFOM reference frame.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGRoverInteractionRoot.MSGCargoPickupJob")
public class MSGCargoPickupJob {
    @Parameter(name = "RequestingObject", coder = HLAunicodeStringCoder.class)
    private String requestingObject;

    @Parameter(name = "CargoType", coder = HLAunicodeStringCoder.class)
    private String cargoType;

    @Parameter(name = "PickupLocation", coder = Vector3DCoder.class)
    private Vector3D pickupLocation;

    @Parameter(name = "DeliveryLocation", coder = Vector3DCoder.class)
    private Vector3D deliveryLocation;

    public MSGCargoPickupJob() {
        requestingObject = "";
        cargoType = "";
        pickupLocation = Vector3D.of(0, 0, 0);
        deliveryLocation = Vector3D.of(0, 0, 0);
    }

    public MSGCargoPickupJob(String requestingObject, String cargoType, Vector3D pickupLocation, Vector3D deliveryLocation) {
        this.requestingObject = requestingObject;
        this.cargoType = cargoType;
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
    }

    public String getRequestingObject() {
        return requestingObject;
    }

    public void setRequestingObject(String requestingObject) {
        this.requestingObject = requestingObject;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public Vector3D getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Vector3D pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Vector3D getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(Vector3D deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }
}
