package org.see.brunel.models.interactions;

import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.impl.encoding.HLAunicodeStringCoder;
import org.see.skf.model.AccessLevel;

@InteractionClass(name = "HLAinteractionRoot.FederateMessage", access = AccessLevel.PUBLISH_SUBSCRIBE)
public class FederateMessage {
    @Parameter(name = "Sender", coder = HLAunicodeStringCoder.class)
    private String sender;

    @Parameter(name = "Receiver", coder = HLAunicodeStringCoder.class)
    private String receiver;

    @Parameter(name = "MessageType", coder = HLAunicodeStringCoder.class)
    private String messageType;

    @Parameter(name = "Content", coder = HLAunicodeStringCoder.class)
    private String content;

    public FederateMessage() {
        this.sender = "";
        this.receiver = "";
        this.messageType = "";
        this.content = "";
    }

    public FederateMessage(String sender, String receiver, String messageType, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageType = messageType;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
