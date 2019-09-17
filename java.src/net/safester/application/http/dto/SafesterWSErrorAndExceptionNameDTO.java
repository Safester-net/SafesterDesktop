package net.safester.clientserver.serverapi;

import com.safelogic.utilx.Debug;

/**
 * DTO for errors with Exception Names
 * 
 * @author N. de Pomereu
 *
 */
public final class SafesterWSErrorAndExceptionNameDTO {

    private final String status = "KO";
    private String errorMessage = null;
    private String exceptionStackTrace = null;
    private String exceptionName = null;

    /**
     * Constructor
     * 
     * @param errorMessage
     * @param e
     */
    public SafesterWSErrorAndExceptionNameDTO(final String errorMessage, final Exception e)  {
	this.errorMessage = errorMessage;

	if (e != null) {
	    this.exceptionName = e.getClass().getName();
	    this.exceptionStackTrace = Debug.GetStackTrace(e);
	}
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    /**
     * @return the exceptionStackTrace
     */
    public String getExceptionStackTrace() {
	return exceptionStackTrace;
    }

    /**
     * @param exceptionStackTrace the exceptionStackTrace to set
     */
    public void setExceptionStackTrace(String exceptionStackTrace) {
	this.exceptionStackTrace = exceptionStackTrace;
    }

    /**
     * @return the status
     */
    public String getStatus() {
	return status;
    }

    public String getExceptionName() {
        return exceptionName;
    }
    
    

}
