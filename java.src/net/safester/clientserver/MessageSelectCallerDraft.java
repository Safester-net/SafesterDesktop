/**
 * 
 */
package net.safester.clientserver;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.safester.application.Main.DEBUG;
import net.safester.application.MessageDecryptor;

import org.apache.commons.io.FileUtils;

import net.safester.application.compose.api.GsonUtil;
import net.safester.application.compose.api.drafts.MessageDraftManager;
import net.safester.application.compose.api.drafts.MessageDraftsStore;
import net.safester.application.http.dto.IncomingMessageDTO;
import net.safester.application.http.dto.IncomingRecipientDTO;
import net.safester.application.parms.Parms;
import net.safester.noobs.clientserver.AttachmentLocal;
import net.safester.noobs.clientserver.MessageLocal;
import net.safester.noobs.clientserver.RecipientLocal;

/**
 * @author Nicolas de Pomereu
 *
 */
public class MessageSelectCallerDraft {

    /**
     * The user number
     */
    private int userNumber = 0;
    private char[] passphrase = null;
    private Connection connection = null;

    /**
     * Constructor for count.
     * @param userNumber 
     */
    MessageSelectCallerDraft(int userNumber) {
	this.userNumber = userNumber;
    }
        
    /**
     * Constructor for decryption of messages.
     * @param userNumber
     * @param passphrase
     * @param connection 
     */
    public MessageSelectCallerDraft(int userNumber, char[] passphrase, Connection connection) {
	this.userNumber = userNumber;
        this.passphrase = passphrase;
        this.connection = connection;
    }



    
    public MessageLocalStore getMessageLocalStore() throws IOException, SQLException {

	List<IncomingMessageDTO> incomingMessageDTOList = new ArrayList<>();
	
	File safesterDraftsTxt = MessageDraftManager.getSafesterDraftsTxt(userNumber);
	if (safesterDraftsTxt != null && safesterDraftsTxt.exists()) {
	    String jsonString = FileUtils.readFileToString(safesterDraftsTxt);
	    MessageDraftsStore messageDraftsStore = GsonUtil.fromJson(jsonString, MessageDraftsStore.class);
	    incomingMessageDTOList = messageDraftsStore.getIncomingMessageDTOList();
	}
	
	MessageLocalStore messageLocalStore = new MessageLocalStore();
        MessageDecryptor messageDecryptor = new MessageDecryptor(userNumber, passphrase, connection);
        
	for (IncomingMessageDTO incomingMessageDTO : incomingMessageDTOList) {

	    debug("+++ incomingMessageDTO: " + incomingMessageDTO);
	    
	    int message_id = incomingMessageDTO.getMessageId();
	    String priority = incomingMessageDTO.getPriority();
	    boolean is_with_attachment = incomingMessageDTO.getFileToAttachList().isEmpty() ? false:true;
	    boolean is_encrypted = incomingMessageDTO.isEncrypted();
	    boolean is_signed = incomingMessageDTO.isSigned();
	    Timestamp date_message = new Timestamp(System.currentTimeMillis());

	    boolean printable = incomingMessageDTO.isPrintable();
	    boolean fowardable = incomingMessageDTO.isFowardable();

	    int folder_id = Parms.DRAFT_ID;
	    //int sender_user_number = message.getSenderUserNumber();

	    String senderEmail = incomingMessageDTO.getSenderEmailAddr();
	    String senderUserName = senderEmail;

	    MessageLocal messageLocal = new MessageLocal();
	    messageLocal.setMessageId(message_id);
	    messageLocal.setPriority(priority);
	    messageLocal.setIsWithAttachment(is_with_attachment);
	    messageLocal.setIsEncrypted(is_encrypted);
	    messageLocal.setIsSigned(is_signed);
	    messageLocal.setDateMessage(date_message);

	    messageLocal.setSenderUserEmail(senderEmail);
	    messageLocal.setSenderUserName(senderUserName);

            // SUBJECT & BODY
	    
            try {
                messageLocal.setSubject(messageDecryptor.decrypt(incomingMessageDTO.getSubject()));
                messageLocal.setBody(messageDecryptor.decrypt(incomingMessageDTO.getBody()));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
            
	    long size_message = incomingMessageDTO.getSize();
	    messageLocal.setSizeMessage(size_message);

	    messageLocal.setPrintable(printable);
	    messageLocal.setFowardable(fowardable);
	    //messageLocal.setSenderUserNumber(sender_user_number);

	    messageLocal.setFolderId(folder_id);
	    messageLocal.setIsRead(true);

	    messageLocal.setRecipientLocal(getRecipientLocalList(incomingMessageDTO.getRecipients()));

            // Files to attach: dedicatd treatment
            messageLocal.setAttachmentLocal(getAttachmentLocalList(incomingMessageDTO.getFileToAttachList()));
	    messageLocalStore.put(message_id, messageLocal);

	}

	return messageLocalStore;

    }
    
    
    private List<AttachmentLocal> getAttachmentLocalList(List<String> filesToAttach) {
        
        List<AttachmentLocal> AttachmentLocalList = new ArrayList<>();
        int i = 0;
        for (String file : filesToAttach) {
            AttachmentLocal attachmentLocal = new AttachmentLocal();
            attachmentLocal.setAttachPosition(i++);
            attachmentLocal.setFileName(file); // In fact full path
            attachmentLocal.setFileSize(9999); // Do not care
            AttachmentLocalList.add(attachmentLocal);
        }
        return AttachmentLocalList;
    }
    
    private List<RecipientLocal> getRecipientLocalList(List<IncomingRecipientDTO> recipients) {

        List<RecipientLocal> recpipientLocalList = new ArrayList<>();
        int i = 1;
        for (final IncomingRecipientDTO recipient : recipients) {
            RecipientLocal recipientLocal = new RecipientLocal();
            recipientLocal.setEmail(recipient.getRecipientEmailAddr());
            
            if (recipient.getRecipientName() == null || recipient.getRecipientName().isEmpty()) {
                recipientLocal.setNameRecipient(recipient.getRecipientEmailAddr());
            } else {
                recipientLocal.setNameRecipient(recipient.getRecipientName());
            }   

            recipientLocal.setTypeRecipient(recipient.getRecipientType());
            recipientLocal.setRecipientPosition(i++);
            recpipientLocalList.add(recipientLocal);
        }
        return recpipientLocalList;
    }

    int count() throws IOException {
	List<IncomingMessageDTO> incomingMessageDTOList = new ArrayList<>();
	
	File safesterDraftsTxt = MessageDraftManager.getSafesterDraftsTxt(userNumber);
	if (safesterDraftsTxt != null && safesterDraftsTxt.exists()) {
	    String jsonString = FileUtils.readFileToString(safesterDraftsTxt);
	    MessageDraftsStore messageDraftsStore = GsonUtil.fromJson(jsonString, MessageDraftsStore.class);
	    incomingMessageDTOList = messageDraftsStore.getIncomingMessageDTOList();
	}
        
        return incomingMessageDTOList.size();
    }
    
    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }


}
