package net.safester.application.http.dto;

/**
 * DTO for Attachments infos
 * @author abecquereau
 *
 */
public final class AttachmentInfoDTO {

	private int attachPosition;
	private String filename;
	private long size;
	
	// Temporary patch: Add Remote file name     
	private String remoteFilename; 
	
	public AttachmentInfoDTO() {

	}

	/**
	 * @return the attachPosition
	 */
	public int getAttachPosition() {
		return attachPosition;
	}

	/**
	 * @param attachPosition the attachPosition to set
	 */
	public void setAttachPosition(int attachPosition) {
		this.attachPosition = attachPosition;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
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
	
	public String getRemoteFilename() {
	    return remoteFilename;
	}

	public void setRemoteFilename(String remoteFilename) {
	    this.remoteFilename = remoteFilename;
	}

	@Override
	public String toString() {
	    return "AttachmentInfoDTO [attachPosition=" + attachPosition + ", filename=" + filename + ", size=" + size
		    + ", remoteFilename=" + remoteFilename + "]";
	}

	
}
