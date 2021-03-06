/**
 * 
 */
package net.safester.application.http;

import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.util.Preconditions;
import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.http.dto.ErrorFullDTO;
import net.safester.application.http.dto.GsonWsUtil;
import net.safester.application.http.dto.LoginOkNew;
import net.safester.application.util.crypto.PassphraseUtil;
import net.safester.application.util.crypto.PassphraseUtilLegacy;
import net.safester.clientserver.ServerParms;

/**
 * Main Safester Web API clas for Login.
 * @author Nicolas de Pomereu
 *
 */
public class ApiLogin {

    private static final boolean DEBUG = false;

    public static final int INBOX = 1;
    public static final int OUTBOX = 2;
    public static final int DRAFT = 3;

    protected KawanHttpClient kawanHttpClient = null;

    protected String token = null;
    
    protected int userNumber = -1;
    protected int product = -1;
    protected Timestamp endDate = null;
    
    protected String errorMessage = null;
    protected String exceptionName = null;
    protected String exceptionStackTrace = null;

    /**
     * Constructor.
     * @param kawanHttpClient
     */
    public ApiLogin(KawanHttpClient kawanHttpClient) {
	Preconditions.checkNotNull(kawanHttpClient, "kawanHttpClient is null!");
	this.kawanHttpClient = kawanHttpClient;
    }

    /**
     * Logins. Return true if success. Use the error/Exception getter for error.
     * 
     * @param username
     * @param password
     * @param doublefaCode (opitional)
     * @return true if success, else false.
     * @throws Exception
     */
    public boolean login(String username, char[] password, String doublefaCode) throws Exception {
	Preconditions.checkNotNull(username, "username is null!");
	Preconditions.checkNotNull(password, "password is null!");

	String url = getBaseUrlWithFinalSlash();
	url += "api/loginNew";

	String connectionPassword = PassphraseUtil.computeHashAndSaltedPassphrase(username, password);

	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("passphrase", connectionPassword);
	parametersMap.put("2faCode", doublefaCode);

	String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
	debug("jsonResult: " + jsonResult);
	
	ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);

	if (resultAnalyzer.isStatusOk()) {
	    LoginOkNew loginOk = GsonWsUtil.fromJson(jsonResult, LoginOkNew.class);
	    this.token = loginOk.getToken();
	    this.product = loginOk.getProduct();
            this.userNumber = loginOk.getUserNumber();
            this.endDate = new Timestamp(loginOk.getEndDate());
	} else {
            
            // Try with the legacy < 5.6.7 computeHashAndSaltedPassphrase that was 
            // different in Java from C#
            connectionPassword = PassphraseUtilLegacy.computeHashAndSaltedPassphrase(username, password);
            parametersMap.put("passphrase", connectionPassword);
        
            String jsonResultLegacy = kawanHttpClient.callWithPost(new URL(url), parametersMap);
            debug("jsonResult: " + jsonResult);

            ResultAnalyzer resultAnalyzerLegacy = new ResultAnalyzer(jsonResultLegacy);
            if (resultAnalyzerLegacy.isStatusOk()) {
                LoginOkNew loginOk = GsonWsUtil.fromJson(jsonResultLegacy, LoginOkNew.class);
                this.token = loginOk.getToken();
                this.product = loginOk.getProduct();
                this.userNumber = loginOk.getUserNumber();
                this.endDate = new Timestamp(loginOk.getEndDate());
                return true; //
            }
            
            // Back no regular error flow
	    ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
	    this.errorMessage = errorFullDTO.getErrorMessage();
	    this.exceptionName = errorFullDTO.getExceptionName();
	    this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
	}

	return resultAnalyzer.isStatusOk();
    }

    public static String getBaseUrlWithFinalSlash() {
	String url = ServerParms.getHOST();
	if (!url.endsWith("/")) {
	    url += "/";
	}
	return url;
    }

    public String getToken() {
	return token;
    }

    public int getProduct() {
	return product;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public Timestamp getEndDate() {
        return endDate;
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
	    System.out.println(new java.util.Date() + " " + ApiLogin.class.getName() + " "+ s);
	}
    }

}
