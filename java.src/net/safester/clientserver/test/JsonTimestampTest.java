/**
 * 
 */
package net.safester.clientserver.test;

import java.sql.Timestamp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonTimestampTest {

    /**
     * 
     */
    public JsonTimestampTest() {
	// TODO Auto-generated constructor stub
    }

    public static void main(String[] args)
    {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        System.out.println(t);
        System.out.println(t.toLocaleString());
        String json = new Gson().toJson(t);
        System.out.println(json);
        json = new GsonBuilder()
                   .setDateFormat("yyyy-MM-dd hh:mm:ss.S")
                   .create()
                   .toJson(t);

        System.out.println(json);
    }  
}
