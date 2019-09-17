/**
 * 
 */
package net.safester.application.http.test;

import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.http.ApiLogin;
import net.safester.application.http.ApiMessages;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.application.http.dto.MessageDTO;
import net.safester.application.http.dto.MessageListDTO;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ApiLoginTest {

    /**
     * 
     */
    public ApiLoginTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	
	KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.build(null);
	ApiLogin apiLogin = new ApiLogin(kawanHttpClient);
	String username = "ndepomereu@gmail.com";
	boolean logged = apiLogin.login(username, "xxxxxxxx".toCharArray(), null);
	
	System.out.println("logged : " + logged);
	System.out.println("token  : " + apiLogin.getToken());
	System.out.println("product: " + apiLogin.getProduct());
	
	ApiMessages apiMessages = new ApiMessages(kawanHttpClient, username, apiLogin.getToken());
	int offset = 1;
	int limit = 20;
	MessageListDTO messageListDTO = apiMessages.listMessages(ApiMessages.INBOX, limit, offset);
	
	System.out.println("messageListDTO: " + messageListDTO);
	
	int messageId = 46527;
	MessageDTO messageDTO = apiMessages.getMessage(messageId);
	System.out.println("messageDTO: " + messageDTO);
	
	
    }

}
