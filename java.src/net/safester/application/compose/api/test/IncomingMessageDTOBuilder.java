/**
 * 
 */
package net.safester.application.compose.api.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import net.safester.application.compose.api.IncomingAttachementDTOUtil;
import net.safester.application.http.dto.IncomingAttachementDTO;
import net.safester.application.http.dto.IncomingMessageDTO;
import net.safester.application.http.dto.IncomingRecipientDTO;

/**
 * @author Nicolas de Pomereu
 *
 */
public class IncomingMessageDTOBuilder {

    /*

    "senderEmailAddr":"string",
    "recipients": [ 
        {
            "recipientEmailAddr":"string", 
            "recipientName":"string",
            "recipientPosition":int,
            "recipientType":int,

        }, 
        {
        ... 
        }
    ],
    "size":long,
    "subject":"base64 string",
    "body":"base64 string",
    "attachments": [ 
          {
            "attachPosition":int,
            "filename":"string", 
             "size":long,
          }, 
          {
             ... 
          }
    ]
     * 
     */
    
    private String senderEmailAddr = null;
    private List<IncomingRecipientDTO> recipients = null;
    private String subject = null;
    private String body = null;
    private List<File> enuncryptedFiles = null;
    private boolean printable = false;
    private boolean fowardable = false;
    private boolean anonymousNotification = false;
    
    
    /**
     * Constructor.
     * @param senderEmailAddr
     * @param recipients
     * @param subject
     * @param body
     * @param printable
     * @param fowardable
     * @param anonymousNotification
     * @param enuncryptedFiles
     */
    public IncomingMessageDTOBuilder(String senderEmailAddr, List<IncomingRecipientDTO> recipients, String subject,
	    String body, boolean printable, boolean fowardable, boolean anonymousNotification, List<File> enuncryptedFiles) {
	
	if (senderEmailAddr == null) throw new NullPointerException("senderEmailAddr is null!");
	if (recipients == null || recipients.isEmpty()) throw new NullPointerException("recipients is null or empty!");
	if (subject == null) throw new NullPointerException("subject is null!");
	if (body == null) throw new NullPointerException("body is null!");
	if (enuncryptedFiles == null ) throw new NullPointerException("enuncryptedFiles is null. Use empty List<File> for no attachments.");
	
	this.senderEmailAddr = senderEmailAddr;
	this.recipients = recipients;
	this.subject = subject;
	this.body = body;
	this.enuncryptedFiles = enuncryptedFiles;
	
	this.printable = printable;
	this.fowardable = fowardable;
	this.anonymousNotification = anonymousNotification;
	
    }

    /**
     * Builds the resulting Json string from the filled IncomingMessageDTO
     * @return the filled IncomingMessageDTO
     * @throws FileNotFoundException 
     */
    public IncomingMessageDTO build() throws FileNotFoundException {
	
	List<IncomingAttachementDTO> attachments = IncomingAttachementDTOUtil.getAttachmentsAddingPgpExt(this.enuncryptedFiles);
	
	IncomingMessageDTO incomingMessageDTO = new IncomingMessageDTO();
	
	long size = computeSize(body, enuncryptedFiles);
	
	incomingMessageDTO.setSenderEmailAddr(senderEmailAddr);
	incomingMessageDTO.setSize(size);
	incomingMessageDTO.setRecipients(recipients);
	incomingMessageDTO.setSubject(subject);
	incomingMessageDTO.setBody(body);
	incomingMessageDTO.setPrintable(printable);
	incomingMessageDTO.setFowardable(fowardable);
	incomingMessageDTO.setAnonymousNotification(anonymousNotification);
	incomingMessageDTO.setAttachments(attachments);
	return incomingMessageDTO;
    }
    
    private static long computeSize(String body, List<File> filesToAttach) {
	if (body == null) throw new NullPointerException("body is null!");
	if (filesToAttach == null ) throw new NullPointerException("enuncryptedFiles is null. Use empty List<File> for no attachments.");

	long size = body.length();
	for (File file : filesToAttach) {
	    size += file.length();
	}
	return size;
    }
        
}
