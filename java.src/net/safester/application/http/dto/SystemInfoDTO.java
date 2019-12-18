package net.safester.application.http.dto;

/**
 * DTO for Message
 * 
 * @author abecquereau
 *
 */
public final class SystemInfoDTO {

    private final String status = "OK";
    
    private String javaVersion = null;
    private String javaVendor = null;
    private String javaRuntimeName = null;

    public SystemInfoDTO() {

    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getJavaVendor() {
        return javaVendor;
    }

    public void setJavaVendor(String javaVendor) {
        this.javaVendor = javaVendor;
    }

    public String getJavaRuntimeName() {
        return javaRuntimeName;
    }

    public void setJavaRuntimeName(String javaRuntimeName) {
        this.javaRuntimeName = javaRuntimeName;
    }

    @Override
    public String toString() {
	return "SystemInfoDTO [status=" + status + ", javaVersion=" + javaVersion + ", javaVendor=" + javaVendor
		+ ", javaRuntimeName=" + javaRuntimeName + "]";
    }

    
}
