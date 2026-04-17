package uk.ac.brunel.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAunicodeStringCoder;
import uk.ac.brunel.encoding.MaterialMapCoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the MSGLogisticsDeliveryRequest interaction class from the MSG FOM.
 * Purpose: Sent by a spaceport to the logistics system when it requires materials to refuel and replenish to prepare a
 * lander in preparation for its departure.
 *
 * @author Hridyanshu Aatreya
 */
@InteractionClass(name = "HLAinteractionRoot.MSGLogisticsInteractionRoot.MSGLogisticsDeliveryRequest")
public class MSGLogisticsDeliveryRequest {
    @Parameter(name = "SpaceportName", coder = HLAunicodeStringCoder.class)
    private String spaceportName;

    @Parameter(name = "Materials", coder = MaterialMapCoder.class)
    private Map<String, Integer> materialMap;

    public MSGLogisticsDeliveryRequest() {
        spaceportName = "";
        materialMap = new HashMap<>();
    }

    public MSGLogisticsDeliveryRequest(String spaceportName, Map<String, Integer> materialMap) {
        this.spaceportName = spaceportName;
        this.materialMap = materialMap;
    }

    public String getSpaceportName() {
        return spaceportName;
    }

    public void setSpaceportName(String spaceportName) {
        this.spaceportName = spaceportName;
    }

    public Map<String, Integer> getMaterialMap() {
        return materialMap;
    }

    public void setMaterialMap(Map<String, Integer> materialMap) {
        this.materialMap = materialMap;
    }
}
