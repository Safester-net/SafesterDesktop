/**
 * 
 */
package net.safester.application.compose.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.awakefw.commons.api.client.AwakeProgressManager;

import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.http.ApiMessages;
import net.safester.application.http.dto.IncomingMessageDTO;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ApiMessageSender {

    private KawanHttpClient kawanHttpClient = null;
    private String userId = null;
    private String token = null;

    private List<File> unencryptedFiles = null;

    private AwakeProgressManager awakeProgressManager = null;
    private IncomingMessageDTO incomingMessageDTO = null;

    /**
     * Constructor.
     * 
     * @param kawanHttpClient
     * @param userId
     * @param token
     * @param incomingMessageDTO
     * @param unencryptedFiles     must be non null
     * @param awakeProgressManager
     * @throws FileNotFoundException
     */
    public ApiMessageSender(KawanHttpClient kawanHttpClient, String userId, String token,
	    IncomingMessageDTO incomingMessageDTO, List<File> unencryptedFiles,
	    AwakeProgressManager awakeProgressManager) throws FileNotFoundException {

	if (kawanHttpClient == null)
	    throw new NullPointerException("kawanHttpClient is null!");
	if (userId == null)
	    throw new NullPointerException("userId is null!");
	if (token == null)
	    throw new NullPointerException("token is null!");
	if (incomingMessageDTO == null)
	    throw new NullPointerException("incomingMessageDTO is null!");
	if (unencryptedFiles == null) {
	    throw new NullPointerException(
		    "unencryptedFiles is null!. Pass empty List<File> if no unencryptedFiles to attach.");
	}

	this.kawanHttpClient = kawanHttpClient;
	this.userId = userId;
	this.token = token;
	this.incomingMessageDTO = incomingMessageDTO;
	this.unencryptedFiles = unencryptedFiles;
	this.awakeProgressManager = awakeProgressManager;

	for (File file : unencryptedFiles) {
	    if (!file.exists()) {
		throw new FileNotFoundException("File to encrypt does not exist: " + file);
	    }
	}
    }

    /**
     * Calls https://www.runsafester.net/api/sendMessage
     * 
     * @return
     * @throws IOException
     */
    public String sendMessage() throws IOException {

	List<File> encryptedFiles = getEncryptedFiles(unencryptedFiles);

	for (File encryptedFile : encryptedFiles) {
	    if (!encryptedFile.exists()) {
		throw new FileNotFoundException("OpenPGP encrypted does not exist: " + encryptedFile);
	    }
	}

	long filesLength = 0;
	for (File file : encryptedFiles) {
	    filesLength += file.length();
	}

	if (awakeProgressManager != null) {
	    awakeProgressManager.setLengthToTransfer(filesLength);
	}

        String url = ApiMessages.getUrlWithFinalSlash();
	url += "api/sendMessage";
        
	URL theUrl = new URL(url);
	Map<String, String> requestParams = new HashMap<String, String>();
	requestParams.put("username", userId);
	requestParams.put("token", token);
	requestParams.put("jsonMessageElements", GsonUtil.getJSonString(incomingMessageDTO));

	String jsonResult = kawanHttpClient.callWithPostMultiPart(theUrl, requestParams, encryptedFiles,
		awakeProgressManager);
	return jsonResult;
    }

    private static List<File> getEncryptedFiles(List<File> files) throws FileNotFoundException {
	List<File> encryptedFiles = new ArrayList<>();
	for (File unencryptedfile : files) {

	    if (! unencryptedfile.exists() ) {
		throw new FileNotFoundException("File to encrypt does not exist: "  + unencryptedfile);
	    }
	    
	    File encryptedFile = new File(unencryptedfile + ".pgp");
	    encryptedFiles.add(encryptedFile);
	}
	return encryptedFiles;
    }

    public AwakeProgressManager getAwakeProgressManager() {
	return awakeProgressManager;
    }

    /**
     * Returns the current uploading file name.
     * 
     * @return
     */
    public String getCurrentFilename() {
	if (kawanHttpClient == null) {
	    return null;
	} else {
	    return kawanHttpClient.getCurrentFilename();
	}

    }

    public List<File> getUnencryptedFiles() {
        return unencryptedFiles;
    }

    
}
