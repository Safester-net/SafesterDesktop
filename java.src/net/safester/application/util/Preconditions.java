/**
 * 
 */
package net.safester.application.util;

/**
 * Clone of Google Guava main check methods, because Guava does not work on Docker.
 * @author Nicolas de Pomereu
 *
 */
public class Preconditions {

    public static void checkNotNull(Object reference, String errorMessage) {
	if (reference == null) {
	    throw new NullPointerException(errorMessage);
	}
    }

    public static void checkArgument(boolean expression, String errorMessage) {
	if (!expression) {
	    throw new IllegalArgumentException(errorMessage);
	}
    }

    public static void checkState(boolean expression, String errorMessage) {
	if (!expression) {
	    throw new IllegalStateException(errorMessage);
	}
    }

}
