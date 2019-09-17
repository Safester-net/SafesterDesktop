package net.safester.application.http.dto;

import java.util.List;

/**
 * DTO for Message
 * 
 * @author abecquereau
 *
 */
public final class MessageDTO {

    private final String status = "OK";
    private long message_id;
    private String body;
    private List<AttachmentInfoDTO> attachments;

    public MessageDTO() {

    }

    /**
     * @return the message_id
     */
    public long getMessage_id() {
	return message_id;
    }

    /**
     * @param message_id the message_id to set
     */
    public void setMessage_id(long message_id) {
	this.message_id = message_id;
    }

    /**
     * @return the body
     */
    public String getBody() {
	return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
	this.body = body;
    }

    /**
     * @return the attachments
     */
    public List<AttachmentInfoDTO> getAttachments() {
	return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<AttachmentInfoDTO> attachments) {
	this.attachments = attachments;
    }

    /**
     * @return the status
     */
    public String getStatus() {
	return status;
    }

    @Override
    public String toString() {
	return "MessageDTO [status=" + status + ", message_id=" + message_id + ", body=" + body + ", attachments="
		+ attachments + "]";
    }

}
