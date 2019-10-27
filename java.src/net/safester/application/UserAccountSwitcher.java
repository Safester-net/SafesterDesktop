/**
 * 
 */
package net.safester.application;

import java.util.Set;

/**
 * @author Nicolas de Pomereu
 *
 */
public class UserAccountSwitcher {
    
    private String keyId = null;
    private Set<UserAccount> userAccounts = null;

    /**
     * Constructor.
     * @param keyId
     * @param userAccounts
     */
    public UserAccountSwitcher(String keyId, Set<UserAccount> userAccounts) {

	if (keyId ==null) {
	    throw new NullPointerException("keyId is null!");
	}
	
	if (userAccounts ==null) {
	    throw new NullPointerException("userAccounts is null!");
	}
	
	this.keyId = keyId;
	this.userAccounts = userAccounts;
    }

    /**
     * Start Main with Account selected by user
     */
    public void switchTo() {
	
	UserAccount newUserAccount = UserAccountManager.getAccount(keyId, userAccounts);
	
        Main mainNew = new Main(newUserAccount.getConnection(), keyId, newUserAccount.getUserNumber(), 
        	newUserAccount.getPassphrase(), newUserAccount.getTypeSubscription(), userAccounts);
        mainNew.setVisible(true);
    }
   
}
