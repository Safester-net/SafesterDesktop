package net.safester.application.http.dto;

import java.util.List;

/**
 * DTO for message header
 * 
 * @author abecquereau
 *
 */
public final class MessageHeaderDTO {

	private final String status = "OK";

	private long messageId;
	private int folderId;
	private String senderEmailAddr;
	private String senderName;
	private List<RecipientDTO> recipients;
	private long date;
	private long size;
	private String subject;
	private boolean hasAttachs;
	private boolean isRead;

	// New values as of 07/09/19
	private int senderUserNumber;
	private String priority;
	private boolean printable;
	private boolean fowardable;
	private boolean isEncrypted;
	private boolean isSigned;

	private boolean isStarred;

	public MessageHeaderDTO() {

	}

	/**
	 * @return the messageId
	 */
	public long getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(long messageId) {
		this.messageId = messageId;
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
	 * @return the senderName
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * @param senderName the senderName to set
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	/**
	 * @return the recipients
	 */
	public List<RecipientDTO> getRecipients() {
		return recipients;
	}

	/**
	 * @param recipients the recipients to set
	 */
	public void setRecipients(List<RecipientDTO> recipients) {
		this.recipients = recipients;
	}

	/**
	 * @return the date
	 */
	public long getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(long date) {
		this.date = date;
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
	 * @return the hasAttachs
	 */
	public boolean isHasAttachs() {
		return hasAttachs;
	}

	/**
	 * @param hasAttachs the hasAttachs to set
	 */
	public void setHasAttachs(boolean hasAttachs) {
		this.hasAttachs = hasAttachs;
	}

	/**
	 * @return the isRead
	 */
	public boolean isRead() {
		return isRead;
	}

	/**
	 * @param isRead the isRead to set
	 */
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public int getSenderUserNumber() {
		return senderUserNumber;
	}

	public void setSenderUserNumber(int senderUserNumber) {
		this.senderUserNumber = senderUserNumber;
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

	public boolean isStarred() {
		return isStarred;
	}

	public void setStarred(boolean isStarred) {
		this.isStarred = isStarred;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	@Override
	public String toString() {
		return "MessageHeaderDTO{" + "status=" + status + ", messageId=" + messageId + ", folderId=" + folderId + ", senderEmailAddr="
				+ senderEmailAddr + ", senderName=" + senderName + ", recipients=" + recipients + ", date=" + date
				+ ", size=" + size + ", subject=" + subject + ", hasAttachs=" + hasAttachs + ", isRead=" + isRead
				+ ", senderUserNumber=" + senderUserNumber + ", priority=" + priority + ", printable=" + printable
				+ ", fowardable=" + fowardable + ", isEncrypted=" + isEncrypted + ", isSigned=" + isSigned + '}';
	}

}
