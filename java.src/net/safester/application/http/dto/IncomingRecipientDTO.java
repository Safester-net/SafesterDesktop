package net.safester.application.http.dto;

public final class IncomingRecipientDTO {

    private String recipientEmailAddr;
    private String recipientName;
    private int recipientType;

    /**
     * Default Constructor
     */
    public IncomingRecipientDTO() {

    }

    /**
     * @return the recipientEmailAddr
     */
    public String getRecipientEmailAddr() {
	return recipientEmailAddr;
    }

    /**
     * @param recipientEmailAddr the recipientEmailAddr to set
     */
    public void setRecipientEmailAddr(String recipientEmailAddr) {
	this.recipientEmailAddr = recipientEmailAddr;
    }

    /**
     * @return the recipientName
     */
    public String getRecipientName() {
	return recipientName;
    }

    /**
     * @param recipientName the recipientName to set
     */
    public void setRecipientName(String recipientName) {
	this.recipientName = recipientName;
    }

    /**
     * @return the recipientType
     */
    public int getRecipientType() {
	return recipientType;
    }

    /**
     * @param recipientType the recipientType to set
     */
    public void setRecipientType(int recipientType) {
	this.recipientType = recipientType;
    }

    @Override
    public String toString() {
	return "IncomingRecipientDTO [recipientEmailAddr=" + recipientEmailAddr + ", recipientName=" + recipientName
		+ ", recipientType=" + recipientType + "]";
    }

}
