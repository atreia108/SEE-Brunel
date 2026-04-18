package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;
import uk.ac.brunel.encoding.MaterialMapCoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the MSGLogisticsDeliveryRequest interaction class from the MSG FOM.
 * Purpose: Sent by a spaceport to the logistics system when it requires materials to replenish the resources aboard a
 * lander in preparation for its departure.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLogisticsInteractionRoot.MSGLogisticsDeliveryRequest")
public class MSGLogisticsDeliveryRequest {
    @Parameter(name = "Spaceport", coder = HLAunicodeStringCoder.class)
    private String spaceport;

    @Parameter(name = "Materials", coder = MaterialMapCoder.class)
    private Map<String, Integer> materials;

    public MSGLogisticsDeliveryRequest() {
        spaceport = "";
        materials = new HashMap<>();
    }

    public MSGLogisticsDeliveryRequest(String spaceport, Map<String, Integer> materials) {
        this.spaceport = spaceport;
        this.materials = materials;
    }

    public String getSpaceport() {
        return spaceport;
    }

    public void setSpaceport(String spaceport) {
        this.spaceport = spaceport;
    }

    public Map<String, Integer> getMaterials() {
        return materials;
    }

    public void setMaterials(Map<String, Integer> materials) {
        this.materials = materials;
    }
}
