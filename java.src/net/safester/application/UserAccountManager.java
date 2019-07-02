/**
 * 
 */
package net.safester.application;

import java.util.Set;

/**
 * @author Nicolas de Pomereu
 *
 */
public class UserAccountManager {

    public static UserAccount getAccount(String keyId, Set<UserAccount> userAccounts) {

	if (keyId == null) {
	    throw new NullPointerException("keyId is null!");
	}

	if (userAccounts == null) {
	    throw new NullPointerException("userAccounts is null!");
	}
	
	for (UserAccount userAccount : userAccounts) {
	    if (keyId.equals(userAccount.getKeyId())) {
		return userAccount;
	    }
	}

	throw new IllegalArgumentException("No UserAccount for keyId:" + keyId);
    }

    static boolean containsAccountForKey(String keyId, Set<UserAccount> userAccounts) {
	if (keyId == null) {
	    throw new NullPointerException("keyId is null!");
	}

	if (userAccounts == null) {
	    throw new NullPointerException("userAccounts is null!");
	}
	
	for (UserAccount userAccount : userAccounts) {
	    if (keyId.equals(userAccount.getKeyId())) {
		return true;
	    }
	}
        
        return false;
        
    }

}
