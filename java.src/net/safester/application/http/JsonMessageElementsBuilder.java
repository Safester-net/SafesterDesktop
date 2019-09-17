/**
 * 
 */
package net.safester.application.http;

import java.util.List;

import net.safester.application.http.dto.GsonWsUtil;
import net.safester.application.http.dto.IncomingAttachementDTO;
import net.safester.application.http.dto.IncomingMessageDTO;
import net.safester.application.http.dto.IncomingRecipientDTO;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonMessageElementsBuilder {

    /*
     * 
     * "senderEmailAddr":"string", "recipients": [ { "recipientEmailAddr":"string",
     * "recipientName":"string", "recipientPosition":int, "recipientType":int,
     * 
     * }, { ... } ], "size":long, "subject":"base64 string", "body":"base64 string",
     * "attachments": [ { "attachPosition":int, "filename":"string", "size":long, },
     * { ... } ]
     * 
     */

    private String senderEmailAddr = null;
    private List<IncomingRecipientDTO> recipients = null;
    private long size = -1;
    private String subject = null;
    private String body = null;
    private List<IncomingAttachementDTO> attachments = null;

    /**
     * Constructor.
     * 
     * @param senderEmailAddr
     * @param recipients
     * @param size
     * @param subject
     * @param body
     * @param attachments
     */
    public JsonMessageElementsBuilder(String senderEmailAddr, List<IncomingRecipientDTO> recipients, long size,
	    String subject, String body, List<IncomingAttachementDTO> attachments) {

	if (senderEmailAddr == null)
	    throw new NullPointerException("senderEmailAddr is null!");
	if (recipients == null || recipients.isEmpty())
	    throw new NullPointerException("recipients is null or empty!");
	if (subject == null)
	    throw new NullPointerException("subject is null!");
	if (body == null)
	    throw new NullPointerException("body is null!");

	if (attachments == null)
	    throw new NullPointerException(
		    "attachments is null. Use empty List<IncomingAttachementDTO> for no attachs.");

	this.senderEmailAddr = senderEmailAddr;
	this.recipients = recipients;
	this.size = size;
	this.subject = subject;
	this.body = body;
	this.attachments = attachments;

    }

    /**
     * Builds the resulting Json string from the filled IncomingMessageDTO
     * 
     * @return the filled IncomingMessageDTO
     */
    public String build() {

	IncomingMessageDTO incomingMessageDTO = new IncomingMessageDTO();

	incomingMessageDTO.setSenderEmailAddr(senderEmailAddr);
	incomingMessageDTO.setSize(size);
	incomingMessageDTO.setRecipients(recipients);
	incomingMessageDTO.setSubject(subject);
	incomingMessageDTO.setBody(body);
	incomingMessageDTO.setAttachments(attachments);
	return GsonWsUtil.getJSonString(incomingMessageDTO);
    }

}
