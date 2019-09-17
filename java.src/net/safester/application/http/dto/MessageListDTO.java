package net.safester.application.http.dto;

import java.util.List;

/**
 * DTO for message list
 * 
 * @author abecquereau
 *
 */
public final class MessageListDTO {

    private final String status = "OK";
    private List<MessageHeaderDTO> messages;

    public MessageListDTO(final List<MessageHeaderDTO> messages) {
	this.messages = messages;
    }

    /**
     * @return the messages
     */
    public List<MessageHeaderDTO> getMessages() {
	return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(List<MessageHeaderDTO> messages) {
	this.messages = messages;
    }

    /**
     * @return the status
     */
    public String getStatus() {
	return status;
    }

    @Override
    public String toString() {
	return "MessageListDTO [status=" + status + ", messages=" + messages + "]";
    }

}
