package uk.ac.brunel.converters;

import com.badlogic.ashley.core.Entity;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAunicodeString;
import io.github.atreia108.vega.core.IMultiDataConverter;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.components.PhysicalEntityComponent;
import uk.ac.brunel.components.ReferenceFrameComponent;
import uk.ac.brunel.utils.ComponentMappers;

public class HLAunicodeStringConverter implements IMultiDataConverter {
    private HLAunicodeString string;

    private FederateMessageComponent federateMessageComponent;
    private PhysicalEntityComponent physicalEntityComponent;
    private ReferenceFrameComponent referenceFrameComponent;

    @Override
    public void decode(Entity entity, EncoderFactory encoderFactory, byte[] buffer, int trigger) throws DecoderException {
        if (string == null) {
            string = encoderFactory.createHLAunicodeString();
        }

        string.decode(buffer);

        switch (trigger) {
            case 0:
                physicalEntityComponent = ComponentMappers.physicalEntity.get(entity);
                if (physicalEntityComponent != null) {
                    physicalEntityComponent.name = string.getValue();
                }
                break;
            case 1:
                physicalEntityComponent = ComponentMappers.physicalEntity.get(entity);
                if (physicalEntityComponent != null) {
                    physicalEntityComponent.type = string.getValue();
                }
                break;
            case 2:
                physicalEntityComponent = ComponentMappers.physicalEntity.get(entity);
                if (physicalEntityComponent != null) {
                    physicalEntityComponent.status = string.getValue();
                }
                break;
            case 3:
                // We're borrowing this component to store the ReferenceFrame name field.
                physicalEntityComponent = ComponentMappers.physicalEntity.get(entity);
                if (physicalEntityComponent != null) {
                    physicalEntityComponent.name = string.getValue();
                }
                break;
            case 4:
                referenceFrameComponent = ComponentMappers.frame.get(entity);
                if (referenceFrameComponent != null) {
                    referenceFrameComponent.name = string.getValue();
                }
                break;
            case 5:
                federateMessageComponent = ComponentMappers.federateMessage.get(entity);
                if (federateMessageComponent != null) {
                    federateMessageComponent.sender = string.getValue();
                }
                break;
            case 6:
                federateMessageComponent = ComponentMappers.federateMessage.get(entity);
                if (federateMessageComponent != null) {
                    federateMessageComponent.receiver = string.getValue();
                }
                break;
            case 7:
                federateMessageComponent = ComponentMappers.federateMessage.get(entity);
                if (federateMessageComponent != null) {
                    federateMessageComponent.type =  string.getValue();
                }
                break;
            case 8:
                federateMessageComponent = ComponentMappers.federateMessage.get(entity);
                if (federateMessageComponent != null) {
                    federateMessageComponent.content = string.getValue();
                }
                break;
        }
    }

    @Override
    public byte[] encode(Entity entity, EncoderFactory encoderFactory, int trigger) {
        if (string == null) {
            string = encoderFactory.createHLAunicodeString();
        }

        switch (trigger) {
            case 0:
                physicalEntityComponent = ComponentMappers.physicalEntity.get(entity);
                if (physicalEntityComponent != null && !physicalEntityComponent.name.isEmpty()) {
                    string.setValue(physicalEntityComponent.name);
                }
                break;
            case 1:
                physicalEntityComponent = ComponentMappers.physicalEntity.get(entity);
                if (physicalEntityComponent != null && !physicalEntityComponent.type.isEmpty()) {
                    string.setValue(physicalEntityComponent.type);
                }
                break;
            case 2:
                physicalEntityComponent = ComponentMappers.physicalEntity.get(entity);
                if (physicalEntityComponent != null && !physicalEntityComponent.status.isEmpty()) {
                    string.setValue(physicalEntityComponent.status );
                }
                break;
            case 3:
                // We're borrowing this component to store the ReferenceFrame name field.
                physicalEntityComponent = ComponentMappers.physicalEntity.get(entity);
                if (physicalEntityComponent != null && !physicalEntityComponent.name.isEmpty()) {
                    string.setValue(physicalEntityComponent.status );
                }
                break;
            case 4:
                referenceFrameComponent = ComponentMappers.frame.get(entity);
                if (referenceFrameComponent != null && !referenceFrameComponent.name.isEmpty()) {
                    string.setValue(referenceFrameComponent.name);
                }
                break;
            case 5:
                federateMessageComponent = ComponentMappers.federateMessage.get(entity);
                if (federateMessageComponent != null && !federateMessageComponent.sender.isEmpty()) {
                    string.setValue(federateMessageComponent.sender);
                }
                break;
            case 6:
                federateMessageComponent = ComponentMappers.federateMessage.get(entity);
                if (federateMessageComponent != null && !federateMessageComponent.receiver.isEmpty()) {
                    string.setValue(federateMessageComponent.receiver);
                }
                break;
            case 7:
                federateMessageComponent = ComponentMappers.federateMessage.get(entity);
                if (federateMessageComponent != null && !federateMessageComponent.type.isEmpty()) {
                    string.setValue(federateMessageComponent.type);
                }
                break;
            case 8:
                federateMessageComponent = ComponentMappers.federateMessage.get(entity);
                if (federateMessageComponent != null && !federateMessageComponent.content.isEmpty()) {
                    string.setValue(federateMessageComponent.content);
                }
                break;
        }

        return string.toByteArray();
    }
}
