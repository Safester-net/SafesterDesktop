package net.safester.application.http.dto;

import java.util.List;

public final class IncomingMessageDTO {

    // BEGIN Needed on local side only for Drafts identification
    private int messageId = 1;
    private List<String> fileToAttachList;
    // END Needed on local side only for Drafts identification
    
    private String senderEmailAddr;
    private List<IncomingRecipientDTO> recipients;
    private long size;
    private String subject;
    private String body;
    private List<IncomingAttachementDTO> attachments;

    // New values as of 07/09/19
    private String priority;
    private boolean printable;
    private boolean fowardable;
    private boolean isEncrypted;
    private boolean isSigned;
    
    // new value as of 14/10/19
    private boolean anonymousNotification;
    
    // New info that says message comes from Desktop
    private boolean isDesktopCreation = false;

    /**
     * Default Constructor
     */
    public IncomingMessageDTO() {

    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public List<String> getFileToAttachList() {
        return fileToAttachList;
    }

    public void setFileToAttachList(List<String> fileToAttachList) {
        this.fileToAttachList = fileToAttachList;
    }
   
    /**
     * @return the senderEmailAddr
     */
    public String getSenderEmailAddr() {
	return senderEmailAddr;
    }

    /**
     * @param senderEmailAddr the senderEmailAddr to set
     */
    public void setSenderEmailAddr(String senderEmailAddr) {
	this.senderEmailAddr = senderEmailAddr;
    }

    /**
     * @return the recipients
     */
    public List<IncomingRecipientDTO> getRecipients() {
	return recipients;
    }

    /**
     * @param recipients the recipients to set
     */
    public void setRecipients(List<IncomingRecipientDTO> recipients) {
	this.recipients = recipients;
    }

    /**
     * @return the size
     */
    public long getSize() {
	return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size) {
	this.size = size;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
	return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
	this.subject = subject;
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
    public List<IncomingAttachementDTO> getAttachments() {
	return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<IncomingAttachementDTO> attachments) {
	this.attachments = attachments;
    }

    public String getPriority() {
	return priority;
    }

    public void setPriority(String priority) {
	this.priority = priority;
    }

    public boolean isPrintable() {
	return printable;
    }

    public void setPrintable(boolean printable) {
	this.printable = printable;
    }

    public boolean isFowardable() {
	return fowardable;
    }

    public void setFowardable(boolean fowardable) {
	this.fowardable = fowardable;
    }

    public boolean isEncrypted() {
	return isEncrypted;
    }

    public void setEncrypted(boolean isEncrypted) {
	this.isEncrypted = isEncrypted;
    }

    public boolean isSigned() {
	return isSigned;
    }

    public void setSigned(boolean isSigned) {
	this.isSigned = isSigned;
    }
    
    public boolean isDesktopCreation() {
        return isDesktopCreation;
    }

    public void setDesktopCreation(boolean isDesktopCreation) {
        this.isDesktopCreation = isDesktopCreation;
    }

    public boolean isAnonymousNotification() {
        return anonymousNotification;
    }

    public void setAnonymousNotification(boolean anonymousNotification) {
        this.anonymousNotification = anonymousNotification;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.messageId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IncomingMessageDTO other = (IncomingMessageDTO) obj;
        if (this.messageId != other.messageId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IncomingMessageDTO{" + "messageId=" + messageId + ", senderEmailAddr=" + senderEmailAddr + ", recipients=" + recipients + ", size=" + size + ", subject=" + subject + ", body=" + body + ", attachments=" + attachments + ", priority=" + priority + ", printable=" + printable + ", fowardable=" + fowardable + ", isEncrypted=" + isEncrypted + ", isSigned=" + isSigned + ", anonymousNotification=" + anonymousNotification + ", isDesktopCreation=" + isDesktopCreation + '}';
    }
}
