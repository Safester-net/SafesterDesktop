/**
 *
 */
package net.safester.application.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.awakefw.commons.api.client.RemoteException;

import com.google.api.client.util.Preconditions;
import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.http.dto.CouponDTO;
import net.safester.application.http.dto.ErrorFullDTO;
import net.safester.application.http.dto.GsonWsUtil;
import net.safester.clientserver.ServerParms;

/**
 * Main Safester Web API class for Messages management.
 *
 * @author Nicolas de Pomereu
 *
 */
public class ApiCoupon {

    public static final String ERROR_INVALID_COUPON = "error_invalid_coupon";

    private static final boolean DEBUG = true;

    protected KawanHttpClient kawanHttpClient = null;
    protected String username = null;
    protected String token = null;

    protected String errorMessage = null;
    protected String exceptionName = null;
    protected String exceptionStackTrace = null;

    /**
     * Constructor.
     *
     * @param kawanHttpClient	the Http Client instance to use.
     * @param username
     * @param token
     */
    public ApiCoupon(KawanHttpClient kawanHttpClient, String username, String token) {
        Preconditions.checkNotNull(kawanHttpClient, "kawanHttpClient is null!");
        Preconditions.checkNotNull(username, "username is null!");
        Preconditions.checkNotNull(token, "token is null!");

        this.kawanHttpClient = kawanHttpClient;
        this.username = username;
        this.token = token;
    }

    /**
     * Returns the coupon for the username. null is no coupon.
     *
     * @return the coupon for the username. null is no coupon.
     * @throws Exception
     */
    public String getCoupon()
            throws Exception {

        String url = getUrlWithFinalSlash();
        url += "api/getCoupon";

        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("username", username);
        parametersMap.put("token", token);

        String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
        ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);
        debug("jsonResult: " + jsonResult);

        CouponDTO couponDTO = null;

        String coupon = null;
        if (resultAnalyzer.isStatusOk()) {
            couponDTO = GsonWsUtil.fromJson(jsonResult, CouponDTO.class);
            coupon = couponDTO.getCoupon();
            if (coupon != null && coupon.equalsIgnoreCase("null")) {
                coupon = null;
            }
        } else {
            ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
            this.errorMessage = errorFullDTO.getErrorMessage();
            this.exceptionName = errorFullDTO.getExceptionName();
            this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();
            throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
        }

        return coupon;
    }

    /**
     * Stores the coupon for the username. null is accepted for coupon
     * deletion..
     *
     * @param coupon the coupon to store. Specifu null to suppress coupon.
     * @return true if success, else false/ ErrorMessage to be examinaed.
     * @throws Exception
     */
    public boolean storeCoupon(String coupon)
            throws Exception {

        String url = getUrlWithFinalSlash();
        url += "api/storeCoupon";

        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("username", username);
        parametersMap.put("token", token);
        parametersMap.put("coupon", coupon);

        String jsonResult = kawanHttpClient.callWithPost(new URL(url), parametersMap);
        ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);
        debug("jsonResult: " + jsonResult);

        if (resultAnalyzer.isStatusOk()) {
            return true;
        } else {
            ErrorFullDTO errorFullDTO = GsonWsUtil.fromJson(jsonResult, ErrorFullDTO.class);
            this.errorMessage = errorFullDTO.getErrorMessage();
            this.exceptionName = errorFullDTO.getExceptionName();
            this.exceptionStackTrace = errorFullDTO.getExceptionStackTrace();

            if (errorMessage.equals(ERROR_INVALID_COUPON)) {
                return false;
            } else {
                throw new RemoteException(errorMessage, new Exception(this.exceptionName), exceptionStackTrace);
            }
        }
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
            System.out.println(new java.util.Date() + " " + ApiCoupon.class.getName() + " " + s);
        }
    }
}
