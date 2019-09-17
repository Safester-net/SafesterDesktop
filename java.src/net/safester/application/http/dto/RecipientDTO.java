package net.safester.application.http.dto;

/**
 * DTO for recipient
 * 
 * @author abecquereau
 *
 */
public final class RecipientDTO {

    private String recipientEmailAddr;
    private String recipientName;
    private int recipientPosition = 0;
    private int recipientType = 0;


    /**
     * Main constructor.
     * @param recipientEmailAddr
     * @param recipientName
     * @param recipientPosition
     * @param recipientType
     */
    public RecipientDTO(String recipientEmailAddr, String recipientName, int recipientPosition, int recipientType) {
	super();
	this.recipientEmailAddr = recipientEmailAddr;
	this.recipientName = recipientName;
	this.recipientPosition = recipientPosition;
	this.recipientType = recipientType;
    }

    /**
     * @return the recipientEmailAddr
     */
    public String getRecipientEmailAddr() {
	return recipientEmailAddr;
    }

    /**
     * @param recipientEmailAddr
     *            the recipientEmailAddr to set
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
     * @param recipientName
     *            the recipientName to set
     */
    public void setRecipientName(String recipientName) {
	this.recipientName = recipientName;
    }

    public int getRecipientPosition() {
        return recipientPosition;
    }

    public int getRecipientType() {
        return recipientType;
    }

    @Override
    public String toString() {
	return "RecipientDTO [recipientEmailAddr=" + recipientEmailAddr + ", recipientName=" + recipientName
		+ ", recipientPosition=" + recipientPosition + ", recipientType=" + recipientType + "]";
    }
   
    
}
