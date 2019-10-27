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
import net.safester.application.http.dto.PrivKeyDTO;
import net.safester.application.http.dto.PubKeyDTO;

/**
 * Safester Web API class dedicated to key management.
 * @author Nicolas de Pomereu
 *
 */
public class ApiKeys extends ApiMessages {

    /**
     * @param kawanHttpClient
     * @param username
     * @param token
     */
    public ApiKeys(KawanHttpClient kawanHttpClient, String username, String token) {
	super(kawanHttpClient, username, token);
    }

    /**
     * Gets the PGP private key block for the passed email address. Return true if
     * success. Use the error/Exception getter for error.
     * 
     * @param username
     * @param token
     * @param userEmailAddr
     * @return
     * @throws Exception
     */
    public String getPrivateKey(String userEmailAddr) throws Exception {
	Preconditions.checkNotNull(username, "username is null!");
	Preconditions.checkNotNull(token, "token is null!");

	String url = ApiMessages.getUrlWithFinalSlash();
	url += "api/getPrivateKey";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);
	parametersMap.put("userEmailAddr", userEmailAddr);

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);

	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);

	String privateKeyBlock = null;
	if (resultAnalyzer.isStatusOk()) {
	    PrivKeyDTO privKeyDTO = GsonWsUtil.fromJson(jsonResult, PrivKeyDTO.class);
	    privateKeyBlock = privKeyDTO.getPrivateKey();
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
	    // NO! Not in this method because we must accept non existing remote keys
            // TOTO: clean this.
            //throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
	}

	return privateKeyBlock;
    }
    
    
    /**
     * Gets the PGP public key block for the passed email address. Return true if
     * success. Use the error/Exception getter for error.
     * @param userEmailAddr
     * 
     * @return
     * @throws Exception
     */
    public String getPublicKey(String userEmailAddr) throws Exception {

	String url = ApiMessages.getUrlWithFinalSlash();
	url += "api/getPublicKey";

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("token", token);
	parametersMap.put("userEmailAddr", userEmailAddr);

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);

	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);

	String publicKeyBlock = null;
	if (resultAnalyzer.isStatusOk()) {
	    PubKeyDTO pubKeyDTO = GsonWsUtil.fromJson(jsonResult, PubKeyDTO.class);
	    publicKeyBlock = pubKeyDTO.getPublicKey();
	} else {
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
	    // NO! Not in this method because we must accept non existing remote keys
            // TOTO: clean this.
            //throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
	}

	return publicKeyBlock;
    }
}
