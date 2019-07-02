/**
 * 
 */
package net.safester.application.parms;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.safester.clientserver.SubscriptionLocal;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SubscriptionLocalGetterClient {

    
    public static SubscriptionLocal get(String login, Connection connection) throws SQLException {
	AwakeConnection awakeConnection = (AwakeConnection) connection;
	AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

	String methodRemote = "net.safester.server.hosts.newapi.UserSubscriptionInfo.getUserSubscription";
	//debug("methodRemote: " + methodRemote);

	String jsonString = null;
	try {
	    jsonString = awakeFileSession.call(methodRemote, login, connection);

	    Gson gsonOut = new Gson();
	    Type type = new TypeToken<SubscriptionLocal>() {
	    }.getType();

	    SubscriptionLocal subscriptionLocal = gsonOut.fromJson(jsonString, type);
	    return subscriptionLocal;
	}
        catch (Exception e)
        {
            throw new SQLException(e);
        }
	    
    }

}
