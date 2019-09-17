package net.safester.application.http.dto;

public final class IncomingAttachementDTO {

    private int attachPosition;
    private String filename;
    private long size;

    public IncomingAttachementDTO() {

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

    @Override
    public String toString() {
	return "IncomingAttachementDTO [attachPosition=" + attachPosition + ", filename=" + filename + ", size=" + size
		+ "]";
    }

}
