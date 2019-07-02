/**
 * 
 */
package net.safester.application;

import java.sql.Connection;
import java.util.Arrays;
import net.safester.application.parms.StoreParms;

/**
 * 
 * A holder for an account, to be used for multi account login.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class UserAccount implements Comparable<UserAccount> {
    private Connection connection = null;
    private String keyId = null;
    private int userNumber = -1;
    private char[] passphrase = null;
    private int typeSubscription = StoreParms.PRODUCT_FREE;

    /**
     * Constructor.
     * 
     * @param connection
     * @param keyId
     * @param userNumber
     * @param passphrase
     * @param typeSubscription
     * 
     */
    public UserAccount(Connection connection, String keyId, int userNumber, char[] passphrase, int typeSubscription) {
	
	if (connection ==null) {
	    throw new NullPointerException("connection is null!");
	}

	if (keyId ==null) {
	    throw new NullPointerException("keyId is null!");
	}
	
	if (passphrase ==null) {
	    throw new NullPointerException("passphrase is null!");
	}
	
	this.connection = connection;
	this.keyId = keyId;
	this.userNumber = userNumber;
	this.passphrase = passphrase;
        this.typeSubscription = typeSubscription;
    }

    public Connection getConnection() {
	return connection;
    }

    public String getKeyId() {
	return keyId;
    }

    public int getUserNumber() {
	return userNumber;
    }

    public char[] getPassphrase() {
	return passphrase;
    }

    public int getTypeSubscription() {
        return typeSubscription;
    }
    
   
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((keyId == null) ? 0 : keyId.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	UserAccount other = (UserAccount) obj;
	if (keyId == null) {
	    if (other.keyId != null)
		return false;
	} else if (!keyId.equals(other.keyId))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "UserAccount [connection=" + connection + ", keyId=" + keyId + ", userNumber=" + userNumber
		+ ", passphrase=" + Arrays.toString(passphrase) + "]";
    }

    @Override
    public int compareTo(UserAccount o) {

	if (o ==null) {
	    throw new NullPointerException("Can not compare, o object is null!");
	}
	
	if (! (o instanceof UserAccount)) {
	    throw new NullPointerException("Can not compare, first object is not an UserAccount object!");
	}
	
	String s1 = ((UserAccount) this).getKeyId();
	String s2 = ((UserAccount) o).getKeyId();
	
	return s1.compareTo(s2);
    }

}
