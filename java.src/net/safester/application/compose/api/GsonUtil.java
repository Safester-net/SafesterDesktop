package net.safester.application.compose.api;

import java.io.BufferedReader;
import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * GSON utility class
 * 
 * @author abecquereau
 *
 */
public final class GsonUtil {

    /**
     * Create json string representing object
     * 
     * @param obj
     * @return
     */
    public static String getJSonString(final Object obj) {
	final GsonBuilder builder = new GsonBuilder();
	final Gson gson = builder.setPrettyPrinting().create();
	return gson.toJson(obj, obj.getClass());
    }

    /**
     * Create Object from jsonString
     * 
     * @param jsonString
     * @param type
     * @return
     */
    public static <T extends Object> T fromJson(final String jsonString, final Class<T> type) {
	final GsonBuilder builder = new GsonBuilder();
	final Gson gson = builder.create();
	final BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonString));
	final T dTO = gson.fromJson(bufferedReader, type);
	return dTO;
    }
}
