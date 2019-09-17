/**
 * 
 */
package net.safester.application.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import com.google.api.client.util.Preconditions;
import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.http.dto.ErrorFullDTO;
import net.safester.application.http.dto.GsonWsUtil;
import net.safester.application.http.dto.SuccessDTO;
import net.safester.application.util.crypto.PassphraseUtil;
import net.safester.clientserver.ServerParms;

/**
 * Main Safester Web API class for Register.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ApiRegister {

    private static final boolean DEBUG = true;
    protected KawanHttpClient kawanHttpClient = null;
    
    protected String errorMessage = null;
    protected String exceptionName = null;
    protected String exceptionStackTrace = null;

    /**
     * Constructor.
     * @param kawanHttpClient
     */
    public ApiRegister(KawanHttpClient kawanHttpClient) {
	Preconditions.checkNotNull(kawanHttpClient, "kawanHttpClient is null!");
	this.kawanHttpClient = kawanHttpClient;
    }

    /*
     * String userEmail = req.getParameter(SafesterWSRequestParam.EMAIL_ADDRESS);
     * final String name = req.getParameter(SafesterWSRequestParam.NAME); final
     * String hashPassphrase =
     * req.getParameter(SafesterWSRequestParam.HASH_PASSPHRASE); final String
     * privKey = req.getParameter(SafesterWSRequestParam.PRIV_KEY); final String
     * pubKey = req.getParameter(SafesterWSRequestParam.PUB_KEY); final String
     * coupon = req.getParameter(SafesterWSRequestParam.COUPON);
     */

    /**
     * Registers.
     * @param emailAddress
     * @param name
     * @param passphrase
     * @param privKey
     * @param pubKey
     * @return
     * @throws Exception 
     */
    public boolean register(String emailAddress, String name, char [] passphrase, String privKey, String pubKey)
	    throws Exception {
	Preconditions.checkNotNull(emailAddress, "emailAddress is null!");
	Preconditions.checkNotNull(name, "name is null!");
	Preconditions.checkNotNull(passphrase, "passphrase is null!");
	Preconditions.checkNotNull(privKey, "privKey is null!");
	Preconditions.checkNotNull(pubKey, "pubKey is null!");
	
	String url = getUrlWithFinalSlash();
	url += "api/register";

	String hashPassphrase = PassphraseUtil.computeHashAndSaltedPassphrase(emailAddress, passphrase);

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("emailAddress", emailAddress);
	parametersMap.put("name", name);
	parametersMap.put("hashPassphrase", hashPassphrase);
	parametersMap.put("privKey", privKey);
	parametersMap.put("pubKey", pubKey);

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
	debug("jsonResult: " + jsonResult);

	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);

	if (resultAnalyzer.isStatusOk()) {
	    SuccessDTO successDTO = GsonWsUtil.fromJson(jsonResult, SuccessDTO.class);
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
            // No! must be analyzed outside!
	    //throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
	}

	return resultAnalyzer.isStatusOk();
    }

    protected static String getUrlWithFinalSlash() {
	String url = ServerParms.getHOST();
	if (!url.endsWith("/")) {
	    url += "/";
	}
	return url;
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
	    System.out.println(new java.util.Date() + " " + ApiRegister.class.getName() + " " + s);
	}
    }

}
