/**
 * 
 */
package net.safester.application.http;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.awakefw.commons.api.client.RemoteException;

import com.google.api.client.util.Preconditions;
import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.http.dto.AddressBookEntryDTO;
import net.safester.application.http.dto.AddressBookEntryListDTO;
import net.safester.application.http.dto.ErrorDTO;
import net.safester.application.http.dto.ErrorFullDTO;
import net.safester.application.http.dto.GsonWsUtil;
import net.safester.application.http.dto.MessageCountDTO;
import net.safester.application.http.dto.MessageDTO;
import net.safester.application.http.dto.MessageHeaderDTO;
import net.safester.application.http.dto.MessageListDTO;
import net.safester.application.http.dto.SystemInfoDTO;
import net.safester.clientserver.ServerParms;

/**
 * Main Safester Web API class for Messages management.
 * @author Nicolas de Pomereu
 *
 */
public class ApiMessages {

    private static final boolean DEBUG = false;

    public static final int INBOX = 1;
    public static final int OUTBOX = 2;
    public static final int DRAFT = 3;

    protected KawanHttpClient kawanHttpClient = null;
    protected String username = null;
    protected String token = null;

    protected String errorMessage = null;
    protected String exceptionName = null;
    protected String exceptionStackTrace = null;

    /**
     * Constructor.
     * @param kawanHttpClient	the Http Client instance to use.
     * @param username
     * @param token
     */
    public ApiMessages(KawanHttpClient kawanHttpClient, String username, String token) {
	Preconditions.checkNotNull(kawanHttpClient, "kawanHttpClient is null!");
	Preconditions.checkNotNull(username, "username is null!");
	Preconditions.checkNotNull(token, "token is null!");
	
	this.kawanHttpClient = kawanHttpClient;
	this.username = username;
	this.token = token;
    }

   
     /**
     * Counts the list of Messages for the user in a directory
     * 
     * @param directoryId
     * @return the count of message for the uszr and directory id
     * @throws Exception
     */
    public int countMessages(int directoryId)
	    throws Exception {
	
	//NO! on Desktop there are many folders
	//Preconditions.checkArgument(directoryId >= 1 && directoryId <= 3, "Wrong directoryId!");
	
	String url = getUrlWithFinalSlash();
	url += "api/countMessages";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);
	parametersMap.put("directoryId", directoryId + "");

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);
	debug("jsonResult: " + jsonResult);
	
	MessageCountDTO messageCountDTO = null;

        int count = -1;
	if (resultAnalyzer.isStatusOk()) {
	    messageCountDTO = GsonWsUtil.fromJson(jsonResult, MessageCountDTO.class);
            count = messageCountDTO.getCount();
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
	    throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
	}

	return count;

    }
    
    
    /**
     * Gets the list of Messages for the user, with a limit and offset.
     * 
     * @param directoryId
     * @param limit
     * @param offset
     * @return list of Messages for the user, null if Exception raised.
     * @throws Exception
     */
    public MessageListDTO listMessages(int directoryId, int limit, int offset)
	    throws Exception {
	
	//NO! on Desktop there are many folders
	//Preconditions.checkArgument(directoryId >= 1 && directoryId <= 3, "Wrong directoryId!");
	
	String url = getUrlWithFinalSlash();
	url += "api/listMessages";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);
	parametersMap.put("directoryId", directoryId + "");
	parametersMap.put("limit", limit + "");
	parametersMap.put("offset", offset + "");

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);
	debug("jsonResult: " + jsonResult);
	
	MessageListDTO messageListDTO = null;

	if (resultAnalyzer.isStatusOk()) {
	    messageListDTO = GsonWsUtil.fromJson(jsonResult, MessageListDTO.class);
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
	    throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
	}

	return messageListDTO;

    }
    
    public MessageHeaderDTO getMessageHeader(int messageId) throws Exception {
	String url = getUrlWithFinalSlash();
	url += "api/getMessageHeader";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);
	parametersMap.put("messageId", messageId + "");

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);

	MessageHeaderDTO messageHeaderDTO = null;

	if (resultAnalyzer.isStatusOk()) {
	    messageHeaderDTO = GsonWsUtil.fromJson(jsonResult, MessageHeaderDTO.class);
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
            System.err.println("getMessageHeader errorMessage : " + this.errorMessage );
            System.err.println("getMessageHeader exceptionName: " + this.exceptionName );
            System.err.println("getMessageHeader exceptionStackTrace: " + this.exceptionStackTrace );
                        
	    throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
	}

	return messageHeaderDTO;
    }

        
    /**
     * Gets the message for an user.
     * @param messageId
     * @return
     * @throws Exception
     */
    public MessageDTO getMessage(int messageId)
	    throws Exception {

	String url = getUrlWithFinalSlash();
	url += "api/getMessage";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);
	parametersMap.put("messageId", messageId + "");

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);

	MessageDTO messageDTO = null;

	if (resultAnalyzer.isStatusOk()) {
	    messageDTO = GsonWsUtil.fromJson(jsonResult, MessageDTO.class);
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
	    throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
	}

	return messageDTO;

    }

    /**
     * Says that a message is read/unread
     * @param messageId
     * @param senderEmailAddress
     * @param messageUnread
     * @return
     * @throws Exception 
     */
    public boolean setMessageRead(int messageId, String senderEmailAddress, boolean messageUnread)
	    throws Exception {
	
		String url = getUrlWithFinalSlash();
		url += "api/setMessageRead";
	
		Map<String, String> parametersMap = new HashMap<>();
		parametersMap.put("username", username);
		parametersMap.put("token", token);
	        parametersMap.put("messageId", messageId + "");
		parametersMap.put("senderEmailAddress", senderEmailAddress);
		parametersMap.put("message_unread", messageUnread + "");
	        
		String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
		ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);
	
		if (resultAnalyzer.isStatusOk()) {
		    return true;
		} else {
		    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
		    this.errorMessage = errorFullDTO.getErrorMessage();
		    this.exceptionName = errorFullDTO.getExceptionName();
		    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
		    throw new RemoteException(errorMessage, new SQLException(this.exceptionName), exceptionStackTrace);
		}

    }
    /**
     * Deletes a message for an user. 
     * @param messageId     the message ID
     * @param directoryId   the folder ID
     * @return true id the message is deleted, else false
     * @throws Exception
     */
    public boolean deleteMessage(int messageId, int directoryId, boolean deleteForAll)
	    throws Exception {
	
	//NO! on Desktop there are many folders
	//Preconditions.checkArgument(directoryId >= 1 && directoryId <= 3, "Wrong directoryId!");
	
	String url = getUrlWithFinalSlash();
	url += "api/deleteMessage";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);
	parametersMap.put("messageId", messageId + "");
	parametersMap.put("directoryId", directoryId + "");
        parametersMap.put("deleteForAll", deleteForAll + "");
        
	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);

	if (resultAnalyzer.isStatusOk()) {
	    return true;
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
	    throw new RemoteException(errorMessage, new SQLException(this.exceptionName), exceptionStackTrace);
	}

    }
    
    /**
     * Returns  the list of Google Peope API Persons for the pass validation code.
     * @param googleCode    the Google People API validation code
     * @param displayFirstBeforeLast    if true, first name is before last
     * @return
     * @throws Exception 
     */
    public List<AddressBookEntryDTO> googleGetPersons(String googleCode, boolean displayFirstBeforeLast)
	    throws Exception {
	
	//NO! on Desktop there are many folders
	//Preconditions.checkArgument(directoryId >= 1 && directoryId <= 3, "Wrong directoryId!");
	
	String url = getUrlWithFinalSlash();
	url += "api/googleGetPersons";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);
	parametersMap.put("google_code", googleCode + "");
	parametersMap.put("display_first_before_last_name", displayFirstBeforeLast + "");
        
	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);

	AddressBookEntryListDTO addressBookEntryListDTO = null;

	if (resultAnalyzer.isStatusOk()) {
	    addressBookEntryListDTO = GsonWsUtil.fromJson(jsonResult, AddressBookEntryListDTO.class);
            List<AddressBookEntryDTO> personList = addressBookEntryListDTO.getAddressBookEntries();
            
            // Just in case
            if (personList == null) {
                personList = new ArrayList<>();
            }
            
            return personList;
	} else {
	    ErrorDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
            
            if (this.errorMessage.equals("Invalid Google Code.")) {
                return null;
            }
            
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
	    throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
	}

    }
    
        /**
     * Gets the PGP public key block for the passed email address. Return true if
     * success. Use the error/Exception getter for error.
     * @param userEmailAddr
     * 
     * @return
     * @throws Exception
     */
    public boolean verifyEmailAddrMx(String userEmailAddr) throws Exception {

	String url = ApiMessages.getUrlWithFinalSlash();
	url += "api/verifyEmailAddrMx";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);
	parametersMap.put("userEmailAddr", userEmailAddr);

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);

	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);
	if (resultAnalyzer.isStatusOk()) {
            return true;
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
	    // NO! Not in this method because we must accept non existing remote keys
            // TOTO: clean this.
            //throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
            return false;
	}

    }
    
    /**
     * Allows to get the remote java, to be sure of version.
     * @return
     * @throws Exception
     */
    public SystemInfoDTO getSystemInfo() throws Exception {
	String url = getUrlWithFinalSlash();
	url += "api/getSystemInfo";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);

	SystemInfoDTO systemInfoDTO = null;

	if (resultAnalyzer.isStatusOk()) {
	    systemInfoDTO = GsonWsUtil.fromJson(jsonResult, SystemInfoDTO.class);
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
            System.err.println("getMessageHeader errorMessage : " + this.errorMessage );
            System.err.println("getMessageHeader exceptionName: " + this.exceptionName );
            System.err.println("getMessageHeader exceptionStackTrace: " + this.exceptionStackTrace );
                        
	    throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
	}

	return systemInfoDTO;
    }
    
    /**
     * Says that a message is starred/not starred
     * @param messageId
     * @param isStarred
     * @return
     * @throws Exception 
     */
    public boolean setMessageStarred(final int messageId, final String senderEmailAddress, final boolean isStarred)
	    throws Exception {
	
		String url = getUrlWithFinalSlash();
		url += "api/setMessageStarred";
	
		Map<String, String> parametersMap = new HashMap<>();
		parametersMap.put("username", username);
		parametersMap.put("token", token);
	        parametersMap.put("messageId", messageId + "");
		parametersMap.put("senderEmailAddress", senderEmailAddress);
		parametersMap.put("messageIsStarred", isStarred + "");
	        
		String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
		ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);
	
		if (resultAnalyzer.isStatusOk()) {
		    return true;
		} else {
		    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
		    this.errorMessage = errorFullDTO.getErrorMessage();
		    this.exceptionName = errorFullDTO.getExceptionName();
		    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
		    throw new RemoteException(errorMessage, new SQLException(this.exceptionName), exceptionStackTrace);
		}

    }
    
    public MessageListDTO listMessagesStarred(int limit, int offset)
    	    throws Exception {
    	
    	//NO! on Desktop there are many folders
    	//Preconditions.checkArgument(directoryId >= 1 && directoryId <= 3, "Wrong directoryId!");
    	
    	String url = getUrlWithFinalSlash();
    	url += "api/listMessagesStarred";

    	Map<String, String> parametersMap = new HashMap<>();
    	parametersMap.put("username", username);
    	parametersMap.put("token", token);
    	parametersMap.put("directoryId", "-1");
    	parametersMap.put("limit", limit + "");
    	parametersMap.put("offset", offset + "");

    	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
    	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);
    	debug("jsonResult: " + jsonResult);
    	
    	MessageListDTO messageListDTO = null;

    	if (resultAnalyzer.isStatusOk()) {
    	    messageListDTO = GsonWsUtil.fromJson(jsonResult, MessageListDTO.class);
    	} else {
    	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
    	    this.errorMessage = errorFullDTO.getErrorMessage();
    	    this.exceptionName = errorFullDTO.getExceptionName();
    	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
    	    throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
    	}

    	return messageListDTO;

        }
    
    public static String getUrlWithFinalSlash() {
	String url = ServerParms.getHOST();
	if (!url.endsWith("/")) {
	    url += "/";
	}
	return url;
    }

    public String getToken() {
	return token;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public String getExceptionName() {
	return exceptionName;
    }

    public String getExceptionStackTrace() {
	return exceptionStackTrace;
    }

    protected static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new java.util.Date() + " " + ApiMessages.class.getName() + " " + s);
	}
    }

}
