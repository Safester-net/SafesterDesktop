/**
 * 
 */
package net.safester.application.parms;

import java.io.File;
import java.sql.Timestamp;

import net.safester.clientserver.SubscriptionLocal;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SubscriptionLocalManager {

    public static boolean isEsxpired(SubscriptionLocal subscriptionLocal) {

	if (subscriptionLocal.getTypeSubscription() == StoreParms.PRODUCT_FREE) {
	    return false;
	} else {
	    
	    if (isForceExpire()) {
		return true;
	    }
	    
	    Timestamp now = new Timestamp(System.currentTimeMillis());
	    Timestamp enddate = subscriptionLocal.getEnddate();
	    
	    if (now.after(enddate)) {
		return true;
	    }
	    else {
		return false;
	    }
	}
    }
    
    /**
     * Allow to force expire by creating a user.home/safester_force_expire.txt file.
     * @return true if we want to force expire
     */
    private static boolean isForceExpire() {

        String userHome = System.getProperty("user.home");
        File file = new File(userHome + File.separator + ".safester" + File.separator + "safester_force_expire.txt");

        return file.exists();
    }

}
